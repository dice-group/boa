package de.uni_leipzig.simba.boa.backend.wordnet.similarity.io;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

/**
 * 
 * <p>
 * Title: Java WordNet Similarity
 * </p>
 * <p>
 * Description: Assesses the semantic similarity between a pair of words as
 * described in Seco, N., Veale, T., Hayes, J. (2004) "An Intrinsic Information
 * Content Metric for Semantic Similarity in WordNet". In Proceedings of the
 * European Conference of Artificial Intelligence
 * </p>
 * <p>
 * This Class interfaces with the index files facilitating word and synset
 * lookups
 * </p>
 * <p>
 * Copyright: Nuno Seco Copyright (c) 2004
 * </p>
 * 
 * @author Nuno Seco
 * @version 1.0
 */

public class IndexBroker {

	private final NLPediaLogger logger = new NLPediaLogger(IndexBroker.class);
	
	/**
	 * A static constant that represents the field name that holds the offset
	 * value of each document.
	 */
	public static final String SYNSET = "synset";

	/**
	 * A static constant that represents the field name that holds the list of
	 * words of each document.
	 */
	public static final String WORDS = "word";

	/**
	 * A static constant that represents the field name that holds the list of
	 * hypernym offsets of each document. This list also contains the offset of
	 * the documented in which it is contained.
	 */
	public static final String HYPERNYM = "hypernym";

	/**
	 * A static constant that represents the field name that holds the
	 * information Content value of each document.
	 */
	public static final String INFORMATION_CONTENT = "ic";

	/**
	 * The directory where the broker will look for the Lucene index.
	 */
	private Directory INDEX_DIR = null;

	/**
	 * Holds a reference to an instance of a Searcher that allows searches to be
	 * conducted in the opened index.
	 */
	private Searcher searcher;

	/**
	 * Holds a reference to an instance of a Parser; a parser parses the query.
	 */
	private QueryParser parser;

	/**
	 * A static reference to an instance of an Index Broker. This variable
	 * guarantees that only one instance of the broker will be allowed for each
	 * Java Virtual Machine launched.
	 */
//	private static IndexBroker INSTANCE;

	/**
	 * The Constructor. Has private access to allow the implementation of the
	 * singleton design pattern. Points the searcher to the index directory,
	 * sets the default field to lookup and the defualt operator that is to be
	 * assumed when more than one token is given.
	 */
	public IndexBroker() {

		try {

			INDEX_DIR = FSDirectory.open(new File(NLPediaSettings.getInstance().getSetting("wordnet.index.directory")));
			searcher = new IndexSearcher(INDEX_DIR);
			parser = new QueryParser(Version.LUCENE_30, WORDS, new WhitespaceAnalyzer());
			parser.setDefaultOperator(QueryParser.AND_OPERATOR);
		}
		catch (IOException ex) {
			ex.printStackTrace();
			System.err.println("");
			System.err.println("Please place the " + INDEX_DIR + " in the working directory.");
		}
	}

	/**
	 * Static method that allows other objects to aquire a reference to an
	 * existing broker. If no broker exists than a new one is created.
	 * 
	 * @return IndexBroker
	 */
//	public static IndexBroker getInstance() {
//
//		if (INSTANCE == null) {
//			INSTANCE = new IndexBroker();
//		}
//
//		return INSTANCE;
//	}

	/**
	 * Returns the list of documents that fulfill the given query.
	 * 
	 * @param query
	 *            String The query to be searched
	 * @return Hits A list of hits
	 */
	public TopDocs getHits(String query) {

		Query q;
		try {
			q = parser.parse(query);
			return searcher.search(q, 10);
		}
		catch (NullPointerException npe){
			
			this.logger.debug(npe.getMessage());
		}
		catch (ParseException ex) {
			
			this.logger.debug(ex.getMessage());
		}
		catch (IOException ex) {
			
			this.logger.debug(ex.getMessage());
		}
		catch (ArrayIndexOutOfBoundsException aiooe) {
			
			this.logger.debug(aiooe.getMessage());
		}
		catch (StringIndexOutOfBoundsException sioobe) {
			
			System.out.println(query);
			this.logger.debug(sioobe.getMessage());
		}
		return null;
	}
	
	/**
	 * @return the searcher
	 */
	public Searcher getSearcher() {
	
		return searcher;
	}

	
	/**
	 * @param searcher the searcher to set
	 */
	public void setSearcher(Searcher searcher) {
	
		this.searcher = searcher;
	}
}
