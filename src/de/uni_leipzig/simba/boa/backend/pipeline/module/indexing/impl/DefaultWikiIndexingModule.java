/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.indexing.impl;

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
import org.apache.lucene.index.IndexReader;
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
import de.uni_leipzig.simba.boa.backend.lucene.LuceneIndexHelper;
import de.uni_leipzig.simba.boa.backend.lucene.LuceneIndexHelper.LuceneIndexType;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.NaturalLanguageProcessingToolFactory;
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
		
		// load the sentence boundary dismabiguation
		SentenceBoundaryDisambiguation sbd = NaturalLanguageProcessingToolFactory.getInstance().createDefaultSentenceBoundaryDisambiguation();

		// create the index writer configuration and create a new index writer
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_34, new LowerCaseWhitespaceAnalyzer());
		indexWriterConfig.setRAMBufferSizeMB(RAM_BUFFER_MAX_SIZE);
		indexWriterConfig.setOpenMode(OVERWRITE_INDEX || !LuceneIndexHelper.isIndexExisting(INDEX_DIRECTORY) ? OpenMode.CREATE : OpenMode.APPEND);
		IndexWriter writer = LuceneIndexHelper.createIndex(INDEX_DIRECTORY, indexWriterConfig, LuceneIndexType.DIRECTORY_INDEX);

		// go through all files which are not hidden in the raw sentence directory
		for (File file : FileUtils.listFiles(new File(RAW_DATA_DIRECTORY), HiddenFileFilter.VISIBLE, TrueFileFilter.INSTANCE)) {
			
			this.logger.info("Indexing file " + file + " (" + (double) file.length() / (1024 * 1024) + " MB)");

			BufferedFileReader br = FileUtil.openReader(file.getAbsolutePath(), Constants.UTF_8_ENCODING);
			List<IndexDocument> documents = new ArrayList<IndexDocument>();

			IndexDocument document = new IndexDocument(sbd);
			
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
					this.logger.debug("\tIndexed " + indexDocumentCount);
					indexDocuments(writer, documents);
					documents = new ArrayList<IndexDocument>();
				}
			}
			// index the remaining x documents
			if ( documents.size() > 0 ) { 
				
				indexDocuments(writer, documents);
				indexDocumentCount += documents.size();
			}
		}
		LuceneIndexHelper.closeIndexWriter(writer);
	}

	/**
	 * Writes all sentences of all sentences in the given list to the Lucene index.
	 * 
	 * @param writer - the writer to write the sentences
	 * @param documents - all documents to be processed
	 */
	protected void indexDocuments(IndexWriter writer, List<IndexDocument> documents) {
		
	    // go through every document
		for (IndexDocument doc : documents)
			// get every sentence from this document
			for (String sentence : doc.getSentences() )
				// add it to the index
			    LuceneIndexHelper.indexDocument(writer, this.createLuceneDocument(doc.uri, sentence));
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
	protected Document createLuceneDocument(String uri, String sentence) {

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
	protected class IndexDocument {

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
}
