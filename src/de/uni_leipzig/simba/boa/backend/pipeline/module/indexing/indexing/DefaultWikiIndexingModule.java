/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.indexing.indexing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
import org.jsoup.Jsoup;

import de.danielgerber.Constants;
import de.danielgerber.file.BufferedFileReader;
import de.danielgerber.file.FileUtil;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.lucene.LowerCaseWhitespaceAnalyzer;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.NaturalLanguageProcessingToolFactory;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.sentenceboundarydisambiguation.SentenceBoundaryDisambiguation;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.sentenceboundarydisambiguation.impl.StanfordNLPSentenceBoundaryDisambiguation;
import de.uni_leipzig.simba.boa.backend.pipeline.module.AbstractPipelineModule;

/**
 * @author gerb
 *
 */
public final class DefaultWikiIndexingModule extends AbstractPipelineModule {

	private final NLPediaLogger logger		= new NLPediaLogger(DefaultWikiIndexingModule.class);
	
	private final String RAW_DATA_DIRECTORY	= NLPediaSettings.BOA_DATA_DIRECTORY + NLPediaSettings.getInstance().getSetting("rawSentenceDirectory");
	private final String INDEX_DIRECTORY	= NLPediaSettings.BOA_DATA_DIRECTORY + NLPediaSettings.getInstance().getSetting("indexSentenceDirectory");
	private final int RAM_BUFFER_MAX_SIZE	= new Integer(NLPediaSettings.getInstance().getSetting("ramBufferMaxSizeInMb")).intValue();
	private final boolean OVERWRITE_INDEX	= new Boolean(NLPediaSettings.getInstance().getSetting("overwriteIndex")).booleanValue();
	
	@Override
	public String getName() {

		return "Default Wiki Indexing Module (de/en)";
	}
	
	@Override
	public void updateModuleInterchangeObject() {
		
		// nothing to do here
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
		
		// load the sentence boundary dismabiguation
		SentenceBoundaryDisambiguation sbd = NaturalLanguageProcessingToolFactory.getInstance().createSentenceBoundaryDisambiguation(StanfordNLPSentenceBoundaryDisambiguation.class);

		// create the index writer configuration and create a new index writer
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_34, new LowerCaseWhitespaceAnalyzer());
		indexWriterConfig.setRAMBufferSizeMB(RAM_BUFFER_MAX_SIZE);
		indexWriterConfig.setOpenMode(OVERWRITE_INDEX ? OpenMode.CREATE : OpenMode.APPEND);
		IndexWriter writer = this.createIndex(new File(INDEX_DIRECTORY), indexWriterConfig);

		// go through all files which are not hidden in the raw sentence directory
		for (File file : FileUtils.listFiles(new File(RAW_DATA_DIRECTORY), HiddenFileFilter.VISIBLE, TrueFileFilter.INSTANCE)) {
			
			this.logger.info("Indexing file " + file + " (" + (double) file.length() / (1024 * 1024) + " MB)");

			BufferedFileReader br = FileUtil.openReader(file.getAbsolutePath(), Constants.UTF_8_ENCODING);
			List<IndexDocument> documents = new ArrayList<IndexDocument>();

			IndexDocument document = new IndexDocument(sbd);
			int indexDocumentCount = 0;
			
			String line;
			while ((line = br.readLine()) != null) {

				// new document found so set the uri
				if (line.startsWith("<doc")) document.uri = line.substring(line.lastIndexOf("url=\"") + 5, line.lastIndexOf("\">"));
				else {
					// new line is the end of article
					if (line.startsWith("</doc>")) {

						documents.add(document); // document finished 
						document = new IndexDocument(sbd);
					}
					else document.text.append(line); // line belongs to current document
				}
				// since we don't want to have all wikipedia entries we collect 10000 docs and then start again
				if (documents.size() == 10000) {
					
					indexDocumentCount += 10000;
					System.out.println("Indexed " + indexDocumentCount);
					indexDocuments(writer, documents);
					documents = new ArrayList<IndexDocument>();
				}
			}
			// index the remaining x documents
			if ( documents.size() > 0 ) indexDocuments(writer, documents);
		}
		this.closeIndexWriter(writer);
	}

	/**
	 * Writes all sentences of all sentences in the given list to the Lucene index.
	 * 
	 * @param writer - the writer to write the sentences
	 * @param documents - all documents to be processed
	 */
	private void indexDocuments(IndexWriter writer, List<IndexDocument> documents) {
		
		try {
			
			// go through every document
			for (IndexDocument doc : documents)
				// get every sentence from this document
				for (String sentence : doc.getSentences() )
					// add it to the index
					writer.addDocument(this.createLuceneDocument(doc.uri, sentence));
		}
		catch (CorruptIndexException e) {
			
			this.logger.fatal("Could not index list of documents", e);
			e.printStackTrace();
			throw new RuntimeException("Could not index list of documents", e);
		}
		catch (IOException e) {
			
			this.logger.fatal("Could not index list of documents", e);
			e.printStackTrace();
			throw new RuntimeException("Could not index list of documents", e);
		}
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
	private Document createLuceneDocument(String uri, String sentence) {

		Document luceneDocument = new Document();
		luceneDocument.add(new Field("uri", uri, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO));
		luceneDocument.add(new Field("sentence", sentence, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO));
		return luceneDocument;
	}
	

	/**
	 * Only used internally to represent a single wikipedia entry.
	 * One entry consists of a uri and text.
	 * 
	 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
	 */
	private class IndexDocument {

		protected String uri = "";
		protected StringBuffer text = new StringBuffer();
		protected SentenceBoundaryDisambiguation sentenceBoundaryDisambiguation;

		/**
		 * Creates a new wiki-entry like document
		 * 
		 * @param sentenceBoundaryDisambiguation
		 */
		public IndexDocument(SentenceBoundaryDisambiguation sentenceBoundaryDisambiguation){
			
			this.sentenceBoundaryDisambiguation = sentenceBoundaryDisambiguation;
		}
		
		/**
		 * @return a list of all cleaned (no html, a's) sentences in the this documents text   
		 */
		public List<String> getSentences() {
			
			return this.sentenceBoundaryDisambiguation.getSentences(Jsoup.parse(this.text.toString()).text());
		}
	}
	
	/**
	 * Opens a Lucene IndexWriter with the specified settings.
	 * This method catches all Lucene exceptions.
	 * 
	 * @param file - the directory where to write the index
	 * @param indexWriterConfig - the config for the index writer
	 * @return an opened indexwriter for the specified settings.
	 * @throws RuntimeException if something goes wrong
	 */
	private IndexWriter createIndex(File file, IndexWriterConfig indexWriterConfig) {

		try {
			
			return new IndexWriter(FSDirectory.open(new File(INDEX_DIRECTORY)), indexWriterConfig);
		}
		catch (CorruptIndexException e) {
			
			this.logger.fatal("Could not create index", e);
			e.printStackTrace();
			throw new RuntimeException("Could not create index", e);
		}
		catch (LockObtainFailedException e) {
			
			this.logger.fatal("Could not create index", e);
			e.printStackTrace();
			throw new RuntimeException("Could not create index", e);
		}
		catch (IOException e) {
			
			this.logger.fatal("Could not create index", e);
			e.printStackTrace();
			throw new RuntimeException("Could not create index", e);
		}
	}
	
	/**
	 * Closes a given index writer without Lucene exceptions.
	 * This method performs a optimze step before closing.
	 * 
	 * @param writer the writer to close
	 * @throws RuntimeException if something goes wrong
	 */
	private void closeIndexWriter(IndexWriter writer) {

		try {
			
			// close the index and do a full optmize
			writer.optimize();
			writer.close();
		}
		catch (CorruptIndexException e) {
			
			this.logger.fatal("Could not close/optimize index", e);
			e.printStackTrace();
			throw new RuntimeException("Could not close/optimize index", e);
		}
		catch (IOException e) {
			
			this.logger.fatal("Could not close/optimize index", e);
			e.printStackTrace();
			throw new RuntimeException("Could not close/optimize index", e);
		}
	}
}
