package de.uni_leipzig.simba.boa.backend.pipeline.module.indexing.impl;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;

import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.NaturalLanguageProcessingToolFactory;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.josatagger.impl.KoreanJosaTagger;

public class KoreanWikiIndexingModule extends DefaultWikiIndexingModule {
	private final NLPediaLogger logger		= new NLPediaLogger(KoreanWikiIndexingModule.class);
	private final KoreanJosaTagger kjt		= (KoreanJosaTagger) NaturalLanguageProcessingToolFactory.getInstance().createDefaultJosaTagger();	// Somehow will be implemented.

	
	@Override
	public String getName() {

		return "Korean Wiki Indexing Module (ko)";
	}
	
	/**
	 * Writes all sentences of all sentences in the given list to the Lucene index.
	 * 
	 * @param writer - the writer to write the sentences
	 * @param documents - all documents to be processed
	 */
	@Override
	protected void indexDocuments(IndexWriter writer, List<IndexDocument> documents) {
		
		try {
			
			// go through every document
			for (IndexDocument doc : documents)
				// get every sentence from this document
				for (String sentence : doc.getSentences() ){
					// add it to the index					
					String JosaSeparatedSentence	= kjt.getJosaSeparatedSentence(sentence);
					writer.addDocument(this.createLuceneDocument(doc.uri, sentence, JosaSeparatedSentence));
				}
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
	
	protected Document createLuceneDocument(String uri, String origSentence, String sentence) {

		Document luceneDocument = new Document();
		luceneDocument.add(new Field("uri", uri, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO));
		luceneDocument.add(new Field("sentence", sentence, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO));
		luceneDocument.add(new Field("originalsentence", origSentence, Field.Store.YES, Field.Index.NOT_ANALYZED, Field.TermVector.NO));
		return luceneDocument;
	}
	

}
