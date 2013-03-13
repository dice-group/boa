/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.indexing.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.util.Version;
import org.jsoup.Jsoup;

import com.github.gerbsen.file.BufferedFileReader;
import com.github.gerbsen.file.FileUtil;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.lucene.LowerCaseWhitespaceAnalyzer;
import de.uni_leipzig.simba.boa.backend.lucene.LuceneIndexHelper;
import de.uni_leipzig.simba.boa.backend.lucene.LuceneIndexHelper.LuceneIndexType;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.NaturalLanguageProcessingToolFactory;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.namedentityrecognition.NamedEntityRecognition;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.sentenceboundarydisambiguation.SentenceBoundaryDisambiguation;
import de.uni_leipzig.simba.boa.backend.pipeline.module.AbstractPipelineModule;

/**
 * @author gerb
 *
 */
public class DefaultWikiIndexingModule extends AbstractPipelineModule {

	private final NLPediaLogger logger		= new NLPediaLogger(DefaultWikiIndexingModule.class);
	
	private final String RAW_DATA_DIRECTORY	= NLPediaSettings.BOA_DATA_DIRECTORY + "raw/";
	private final String INDEX_DIRECTORY	= NLPediaSettings.BOA_DATA_DIRECTORY + "index/corpus/";
	private final int RAM_BUFFER_MAX_SIZE	= NLPediaSettings.getIntegerSetting("ramBufferMaxSizeInMb");
	private final boolean OVERWRITE_INDEX	= this.overrideData;
	
	private BlockingQueue<IndexingThread> queue = new LinkedBlockingQueue<IndexingThread>(Runtime.getRuntime().availableProcessors());
	
	// remember how many files get indexed
	private int indexDocumentCount = 0;
	
	@Override
	public String getName() {

		return "Default Wiki Indexing Module (de/en)";
	}
	
	@Override
	public boolean isDataAlreadyAvailable() {

	    return LuceneIndexHelper.isIndexExisting(INDEX_DIRECTORY);
	}
	
	@Override
	public void loadAlreadyAvailableData() {

		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void updateModuleInterchangeObject() {
		
		// nothing to do here
	}
	
	@Override
	public String getReport() {

		return "A total of " + indexDocumentCount + " documents has been indexed!";
	}
	
	@Override
	/**
	 * This methods goes through all files specified in the rawSentenceDirectory and presumes that
	 * these are of the Wikipedia XML format:
	 * 		<doc id="wikipediaUrl">
	 * 			some text
	 * 		</doc
	 * 		<doc...
	 * 
	 * There is no real root element.
	 * Then it indexes all sentences for all documents with Lucene and a LowerCaseWhitespaceAnalyzer.
	 */
	public void run() {
		
		// create the index writer configuration and create a new index writer
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_36, new LowerCaseWhitespaceAnalyzer());
		indexWriterConfig.setRAMBufferSizeMB(RAM_BUFFER_MAX_SIZE);
		indexWriterConfig.setOpenMode(OVERWRITE_INDEX || !LuceneIndexHelper.isIndexExisting(INDEX_DIRECTORY) ? OpenMode.CREATE : OpenMode.APPEND);
		IndexWriter writer = LuceneIndexHelper.createIndex(INDEX_DIRECTORY, indexWriterConfig, LuceneIndexType.DIRECTORY_INDEX);

		BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<Runnable>(Runtime.getRuntime().availableProcessors() / 2);
	    RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
	    ExecutorService executorService = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() / 2, Runtime.getRuntime().availableProcessors() / 2, 0L, TimeUnit.MILLISECONDS, blockingQueue, rejectedExecutionHandler);
	    
	    System.out.println("Available Processors: " + Runtime.getRuntime().availableProcessors());

		// go through all files which are not hidden in the raw sentence directory
		for (File file : FileUtils.listFiles(new File(RAW_DATA_DIRECTORY), HiddenFileFilter.VISIBLE, TrueFileFilter.INSTANCE)) {
			
			this.logger.info("Indexing file " + file + " (" + (double) file.length() / (1024 * 1024) + " MB)");

			BufferedFileReader br = FileUtil.openReader(file.getAbsolutePath(), "UTF-8");
			List<IndexDocument> documents = new ArrayList<IndexDocument>();

			IndexDocument document = new IndexDocument();
			
			String line;
			while ((line = br.readLine()) != null) {

				// new document found so set the uri
				if (line.startsWith("<doc")) document.uri = line.substring(line.lastIndexOf("url=\"") + 5, line.lastIndexOf("\">"));
				else {
					// new line is the end of article
					if (line.startsWith("</doc>")) {

						documents.add(document); // document finished 
						document = new IndexDocument();
					}
					else document.text.append(line); // line belongs to current document
				}
				// since we don't want to have all wikipedia entries we collect 10000 docs and then start again
				if (documents.size() == 1000) {
					
					this.logger.debug("Starting IndexingThread");
					this.logger.debug("BlockingQueue-Size: " + blockingQueue.size());
					executorService.submit(new IndexingThread(writer, documents));
					documents = new ArrayList<IndexDocument>();
				}
			}
			// index the remaining x documents
			if ( documents.size() > 0 ) { 

				executorService.submit(new IndexingThread(writer, documents));
				indexDocumentCount += documents.size();
			}
		}
		executorService.shutdown();
		LuceneIndexHelper.closeIndexWriter(writer);
	}

	private class IndexingThread implements Runnable {

		private List<IndexDocument> documents;
		private IndexWriter writer;
		
		protected SentenceBoundaryDisambiguation sentenceBoundaryDisambiguation = NaturalLanguageProcessingToolFactory.getInstance().createDefaultSentenceBoundaryDisambiguation();
		protected NamedEntityRecognition nerTagger = NaturalLanguageProcessingToolFactory.getInstance().createDefaultNamedEntityRecognition();

		public IndexingThread(IndexWriter writer, List<IndexDocument> documents) {
			
			this.documents	= documents;
			this.writer		= writer;
		}

		@Override
		public void run() {
			
			// go through every document
			for (IndexDocument doc : this.documents)
				// get every sentence from this document
				for (String sentence : sentenceBoundaryDisambiguation.getSentences(Jsoup.parse(doc.text.toString()).text()) ) {

					String taggedSentence = nerTagger.getAnnotatedString(sentence);
					
					// add it to the index
				    LuceneIndexHelper.indexDocument(writer, 
				    		createLuceneDocument(
				    				doc.uri, 
				    				sentence, 
				    				taggedSentence, 
				    				new HashSet<String>(getEntities(this.mergeTagsInSentences(taggedSentence)))));
				}
			
			indexDocumentCount += this.documents.size();			
			logger.info("Finished indexing of " + indexDocumentCount + " documents!");
		}
		
	    /**
	     * 
	     * @param mergedTaggedSentence
	     * @return
	     */
	    private List<String> getEntities(List<String> mergedTaggedSentence){
	        
	        List<String> entities = new ArrayList<String>();
	        for (String entity :  mergedTaggedSentence) {

                if (entity.endsWith("_PERSON") ) entities.add(entity.replace("_PERSON", ""));
                if (entity.endsWith("_MISC")) entities.add(entity.replace("_MISC", ""));
                if (entity.endsWith("_PLACE")) entities.add(entity.replace("_PLACE", ""));
                if (entity.endsWith("_ORGANIZATION")) entities.add(entity.replace("_ORGANIZATION", ""));
	        }
	        
	        return entities;
	    }
	    
	    /**
	     * 
	     */
	    public List<String> mergeTagsInSentences(String nerTaggedSentence) {

	        List<String> tokens = new ArrayList<String>();
	        String lastToken = "";
	        String lastTag = "";
	        String currentTag = "";
	        String newToken = "";
	        
	        for (String currentToken : nerTaggedSentence.split(" ")) {

	            currentTag = currentToken.substring(currentToken.lastIndexOf("_") + 1);

	            // we need to check for the previous token's tag
	            if (!currentToken.endsWith("_OTHER")) {

	                // we need to merge the cell
	                if (currentTag.equals(lastTag)) {

	                    newToken = lastToken.substring(0, lastToken.lastIndexOf("_")) + " " + currentToken;
	                    tokens.set(tokens.size() - 1, newToken);
	                }
	                // different tag found so just add it
	                else
	                    tokens.add(currentToken);
	            }
	            else {

	                // add the current token
	                tokens.add(currentToken);
	            }
	            // update for next iteration
	            lastToken = tokens.get(tokens.size() - 1);
	            lastTag = currentTag;
	        }
	        return tokens;
	    }
		
		/**
		 * Indexes a document as follow:
		 * 
		 * - uri: Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO
		 * - sentence: Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO
		 * 
		 * the field name are "uri" and "sentence".
		 * 
		 * @param uri - the uri of the wiki entry
		 * @param sentence - a single sentence from the wiki entry
		 * @return a Lucene Document
		 */
		protected Document createLuceneDocument(String uri, String sentence, String taggedSentence, Set<String> entities) {

			Document luceneDocument = new Document();
			luceneDocument.add(new Field("uri", uri, Field.Store.YES, Field.Index.NOT_ANALYZED, Field.TermVector.NO));
			luceneDocument.add(new Field("sentence", sentence, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO));
			luceneDocument.add(new Field("ner", taggedSentence, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO));
			
			for ( String entity : entities )
                luceneDocument.add(new Field("entity", entity, Field.Store.YES, Field.Index.NOT_ANALYZED, Field.TermVector.NO));
                
			return luceneDocument;
		}
	}
	
	/**
	 * Only used internally to represent a single wikipedia entry.
	 * One entry consists of a uri and text.
	 * 
	 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
	 */
	protected class IndexDocument {

		protected String uri = "";
		protected StringBuffer text = new StringBuffer();
	}
}
