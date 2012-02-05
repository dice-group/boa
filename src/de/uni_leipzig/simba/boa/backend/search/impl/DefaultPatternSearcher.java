package de.uni_leipzig.simba.boa.backend.search.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.BackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.lucene.LowerCaseWhitespaceAnalyzer;
import de.uni_leipzig.simba.boa.backend.lucene.LuceneIndexHelper;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.NaturalLanguageProcessingToolFactory;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.partofspeechtagger.PartOfSpeechTagger;
import de.uni_leipzig.simba.boa.backend.search.PatternSearcher;
import de.uni_leipzig.simba.boa.backend.search.result.SearchResult;

/**
 * 
 * @author Daniel Gerber
 */
public class DefaultPatternSearcher implements PatternSearcher {

	private final static int MAX_PATTERN_CHUNK_LENGTH = new Integer(NLPediaSettings.getInstance().getSetting("maxPatternLenght")).intValue();
	private final static int MIN_PATTERN_CHUNK_LENGTH = new Integer(NLPediaSettings.getInstance().getSetting("minPatternLenght")).intValue();
	private final static int MAX_NUMBER_OF_DOCUMENTS = Integer.valueOf(NLPediaSettings.getInstance().getSetting("maxNumberOfDocuments"));

	private PartOfSpeechTagger posTagger;

	private Directory directory = null;
	private Analyzer analyzer = null;
	private IndexSearcher indexSearcher = null;

	private QueryParser parser;
	private ScoreDoc[] hits;
	
	private final NLPediaLogger logger = new NLPediaLogger(DefaultPatternSearcher.class);

	public DefaultPatternSearcher() {

		// create index searcher in read only mode
		this.directory = LuceneIndexHelper.openIndex(NLPediaSettings.BOA_DATA_DIRECTORY + "index/corpus/");
		this.analyzer = new LowerCaseWhitespaceAnalyzer();
		this.indexSearcher = LuceneIndexHelper.openIndexSearcher(directory, true);
		this.parser = new QueryParser(Version.LUCENE_34, "sentence", this.analyzer);

		this.hits = null;
	}
	
	public DefaultPatternSearcher(String indexDir) {

		this.directory = LuceneIndexHelper.openIndex(NLPediaSettings.BOA_DATA_DIRECTORY + "index/corpus/");
		this.analyzer = new LowerCaseWhitespaceAnalyzer();

		// create index searcher in read only mode
		this.indexSearcher = LuceneIndexHelper.openIndexSearcher(directory, true);
		this.parser = new QueryParser(Version.LUCENE_34, "sentence", this.analyzer);

		this.hits = null;
	}
	
	public DefaultPatternSearcher(Directory indexDir) {

		this.directory = indexDir;
		this.analyzer = new LowerCaseWhitespaceAnalyzer();

		// create index searcher in read only mode
		this.indexSearcher = LuceneIndexHelper.openIndexSearcher(directory, true);
		this.parser = new QueryParser(Version.LUCENE_34, "sentence", this.analyzer);

		this.hits = null;
	}
	
	/**
	 * Returns the sentence index by the given id.
	 * 
	 * @param id
	 * @return
	 */
	public String getSentencesByID(Integer id) {

		try {

			return this.indexSearcher.doc(id).get("sentence");
		}
		catch (CorruptIndexException e) {
			
			e.printStackTrace();
			String error = "Could not get document with id: " + id + " from index.";
			this.logger.error(error, e);
			throw new RuntimeException(error, e);
		}
		catch (IOException e) {
			
			e.printStackTrace();
			String error = "Could not get document with id: " + id + " from index.";
			this.logger.error(error, e);
			throw new RuntimeException(error, e);
		}
	}

	/**
	 * Returns the sentences from the index with the given ids. Uses the method
	 * DefaultPatternSearcher.getSentencesByID() to query the index
	 * 
	 * @param listOfIds
	 * @return
	 */
	public List<String> getSentencesByIds(List<Integer> ids) {

		List<String> sentences = new ArrayList<String>();
		for (Integer id : ids) {

			sentences.add(this.getSentencesByID(id));
		}
		return sentences;
	}

	/**
	 * Query the index with two search terms.
	 * 
	 * @param string
	 * @param string2
	 * @return 
	 * @throws ParseException
	 *             , IOException
	 */
	public Collection<SearchResult> queryBackgroundKnowledge(BackgroundKnowledge backgroundKnowledge) {

		boolean inverse = false;
		List<SearchResult> results = new ArrayList<SearchResult>();
		
		Set<String> firstLabels = backgroundKnowledge.getSubjectSurfaceForms();
		Set<String> secondLabels = backgroundKnowledge.getObjectSurfaceForms();

		// switch the labels in case we have more subject labels than object labels
		if ( firstLabels.size() > secondLabels.size() ) {
			
			inverse = true;
			
			firstLabels = backgroundKnowledge.getObjectSurfaceForms();
			secondLabels = backgroundKnowledge.getSubjectSurfaceForms();
		}
		
		// go through all surface form combinations
		for (String firstLabel : firstLabels) {

			// check if we find at least sentences with the first token, if not we can skip this search word combination
			TotalHitCountCollector collector = new TotalHitCountCollector();
			this.searchIndex(this.parseQuery("+sentence:\"" + QueryParser.escape(firstLabel) + "\""), collector);
			if (collector.getTotalHits() == 0 ) continue; 
			
			for (String secondLabel : secondLabels) {
				
				Query query = this.parseQuery("+sentence:\"" + QueryParser.escape(firstLabel) + "\" && +sentence:\"" + QueryParser.escape(secondLabel) + "\"");
				hits = this.searchIndex(query, null, MAX_NUMBER_OF_DOCUMENTS);
				
				for (int i = 0; i < hits.length; i++) {

					String sentence				= this.getIndexDocument(hits[i], "sentence");
					String sentenceLowerCase	= sentence.toLowerCase();
					String firstLabelLowerCase	= firstLabel;
					String secondLabelLowerCase	= secondLabel;

					Map<Integer, String> currentMatches = new HashMap<Integer, String>();
					
					// the switching is neccessary because it could be possible that we change the label sets, see above 					
					String[] match1; 
					if ( !inverse ) {
						match1 = StringUtils.substringsBetween(sentenceLowerCase, firstLabelLowerCase, secondLabelLowerCase);
					}
					else {
						
						match1 = StringUtils.substringsBetween(sentenceLowerCase, secondLabelLowerCase, firstLabelLowerCase);
					}
					
					if (match1 != null) {

						for (int j = 0; j < match1.length; j++) {

							match1[j] = "?D? " + match1[j].trim() + " ?R?";
							currentMatches.put(hits[i].doc, match1[j]);
						}
					}

					// the switching is neccessary because it could be possible that we change the label sets, see above 
					String[] match2; 
					if ( !inverse ) {
						match2 = StringUtils.substringsBetween(sentenceLowerCase, secondLabelLowerCase, firstLabelLowerCase);
					}
					else {
						
						match2 = StringUtils.substringsBetween(sentenceLowerCase, firstLabelLowerCase, secondLabelLowerCase);
					}
					if (match2 != null) {

						for (int j = 0; j < match2.length; j++) {

							match2[j] = "?R? " + match2[j].trim() + " ?D?";
							currentMatches.put(hits[i].doc, match2[j]);
						}
					}
					this.addSearchResults(results, currentMatches, backgroundKnowledge, hits[i], sentence);
				}
			}
		}
		
		return results;
	}

	/**
	 * Create the search results
	
	 * @param results 
	 * @param currentMatches
	 * @param triple
	 * @param hit
	 * @param isSubject
	 */
	private void addSearchResults(List<SearchResult> results, Map<Integer, String> currentMatches, BackgroundKnowledge backgroundKnowledge, ScoreDoc hit, String sentenceNormalCase) {

		for (String match : currentMatches.values()) {

			String nlr = this.getCorrectCaseNLR(sentenceNormalCase.toLowerCase(), sentenceNormalCase, match);

			// but only for those who are suitable
			if (!match.isEmpty() && this.isPatternSuitable(match)) {

				SearchResult result = new SearchResult();
				result.setProperty(backgroundKnowledge.getProperty().getUri());
				result.setSentence(sentenceNormalCase);
				result.setNaturalLanguageRepresentation(nlr);
				result.setRdfsRange(backgroundKnowledge.getProperty().getRdfsRange());
				result.setRdfsDomain(backgroundKnowledge.getProperty().getRdfsDomain());
				// the subject of the triple is the domain of the property so,
				// replace every occurrence with ?D?
				if (nlr.startsWith("?D?")) {
					result.setFirstLabel(backgroundKnowledge.getSubject().getLabel());
					result.setSecondLabel(backgroundKnowledge.getObject().getLabel());
				}
				else {
					result.setFirstLabel(backgroundKnowledge.getObject().getLabel());
					result.setSecondLabel(backgroundKnowledge.getSubject().getLabel());
				}
				if ( this.posTagger == null ) this.posTagger = NaturalLanguageProcessingToolFactory.getInstance().createDefaultPartOfSpeechTagger();
				result.setPosTags(this.posTagger.getAnnotations(result.getNaturalLanguageRepresentationWithoutVariables()));
				results.add(result);
			}
		}
	}

	private String getCorrectCaseNLR(String lowerCase, String normalCase, String pattern) {

		String firstVariable = pattern.substring(0, 3);
		String secondVariable = pattern.substring(pattern.length() - 3, pattern.length());

		// remove ?D? and ?R?
		pattern = pattern.substring(0, pattern.length() - 3).substring(3).trim();
		// get the start of the pattern and got until its end
		int start = lowerCase.indexOf(pattern);
		int end = start + pattern.length();
		return firstVariable + " " + normalCase.substring(start, end) + " " + secondVariable;
	}

	public static void main(String[] args) {

		String lowerCase = "anarchist themes can be found in the works of taoist sages laozi and zhuangzi .";
		String upperCase = "Anarchist themes can be found in the works of Taoist sages Laozi and Zhuangzi .";
		String pattern = "?D? can be found in ?R?";

		String test = "it lies 123 km south of tirana , the capital of albania .";

		String[] matches = StringUtils.substringsBetween(test, "albania", "tirana");

		if (matches != null) {
			for (String s : matches) {

				System.out.println(s);
				;
			}
		}
		System.out.println();
		matches = StringUtils.substringsBetween(test, "tirana", "albania");
		if (matches != null) {
			for (String s : matches) {

				System.out.println(s);
				;
			}
		}

		// System.out.println(getCorrectCaseNLR(lowerCase,upperCase,pattern));

	}

	public boolean isPatternSuitable(String naturalLanguageRepresentation) {

		String patternWithoutVariables = naturalLanguageRepresentation.substring(0, naturalLanguageRepresentation.length() - 3).substring(3).trim();

		// patterns are only allowed to have 256 characters
		if (naturalLanguageRepresentation.length() > 256 || naturalLanguageRepresentation.isEmpty())
			return false;

		// pattern need to start with either ?D? or ?R? and have to end with ?D?
		// or ?R?
		if ((!naturalLanguageRepresentation.startsWith("?D?") && !naturalLanguageRepresentation.startsWith("?R?"))
				|| (!naturalLanguageRepresentation.endsWith("?D?") && !naturalLanguageRepresentation.endsWith("?R?")))
			return false;

		// patterns need to have only one domain and only one range
		if (StringUtils.countMatches(naturalLanguageRepresentation, "?D?") != 1 || StringUtils.countMatches(naturalLanguageRepresentation, "?R?") != 1)
			return false;

		// patterns need to be bigger/equal than min chunk size and
		// smaller/equal then max chunk size
		// true or correct if the number of stop-words in the pattern is not
		// equal to the number of tokens
		Set<String> naturalLanguageRepresentationChunks = new HashSet<String>(Arrays.asList(patternWithoutVariables.toLowerCase().split(" ")));
		if (naturalLanguageRepresentationChunks.size() >= MAX_PATTERN_CHUNK_LENGTH || naturalLanguageRepresentationChunks.size() <= MIN_PATTERN_CHUNK_LENGTH)
			return false;

		// patterns containing only stop-words can't be used, because they are
		// way to general
		naturalLanguageRepresentationChunks.removeAll(Constants.STOP_WORDS);
		if (naturalLanguageRepresentationChunks.size() == 0)
			return false;

		// patterns shall not start with "and" or "and ," because this is the
		// conjunction of sentences and does not carry meaning
		if (patternWithoutVariables.startsWith("and ") || patternWithoutVariables.startsWith("and,") || patternWithoutVariables.startsWith("and ,"))
			return false;

		return true;
	}

	/**
	 * Closes the current pattern searcher.
	 */
	public void close() {

		try {
			
			this.indexSearcher.close();
		}
		catch (IOException e) {
			
			e.printStackTrace();
			String error = "Could not close index!";
			this.logger.error(error, e);
			throw new RuntimeException(error, e);
		}
	}

	public Set<String> getExactMatchSentences(String keyphrase, int maxNumberOfDocuments) {

		ScoreDoc[] hits = this.searchIndex(this.parseQuery("+sentence:\"" + QueryParser.escape(keyphrase) + "\""), null, maxNumberOfDocuments);
		TreeSet<String> list = new TreeSet<String>();

		// reverse order because longer sentences come last, longer sentences
		// most likely contain less it,he,she
		for (int i = hits.length - 1; i >= 0; i--) {

			// get the indexed string and put it in the result
			list.add(this.getIndexDocument(hits[i], "sentence"));
		}
		return list;
	}
	
	public Set<String> getExactMatchSentencesForLabels(String label1, String label2, int maxNumberOfDocuments) {
		
		Query query = this.parseQuery("+sentence:\"" + QueryParser.escape(label1) + "\" && +sentence:\"" + QueryParser.escape(label2) + "\"");
		hits = this.searchIndex(query, null, maxNumberOfDocuments);
		TreeSet<String> list = new TreeSet<String>();
		
		// reverse order because longer sentences come last, longer sentences
		// most likely contain less it,he,she
		for (int i = hits.length - 1; i >= 0; i--) {

			// get the indexed string and put it in the result
			list.add(this.getIndexDocument(hits[i], "sentence"));
		}
		return list;
	}
	
	/**
	 * Get's the documents value for field "fieldname" from the index.
	 * 
	 * @param document - the found document
	 * @param fieldname - the fieldname which should be used to get the document value
	 * @return the documents value for the fieldname
	 */
	private String getIndexDocument(ScoreDoc document, String fieldname) {

		try {
			
			return indexSearcher.doc(document.doc).get(fieldname);
		}
		catch (CorruptIndexException e) {
			
			e.printStackTrace();
			String error = "Could not get document " + document.doc + "(field:"+fieldname+") from index.";
			this.logger.error(error, e);
			throw new RuntimeException(error, e);
		}
		catch (IOException e) {
			
			e.printStackTrace();
			String error = "Could not get document " + document.doc + "(field:"+fieldname+") from index.";
			this.logger.error(error, e);
			throw new RuntimeException(error, e);
		}
	}
	
	/**
	 * Method to query a Lucene Index.
	 * 
	 * @param query - the query to query
	 * @param filter - a Lucene filter
	 * @param maxNumberOfDocuments - max number of returned documents
	 * @return a list of all matching documents
	 */
	private ScoreDoc[] searchIndex(Query query, Filter filter, int maxNumberOfDocuments) {
		
		try {
			
			return indexSearcher.search(query, filter, maxNumberOfDocuments).scoreDocs;
		}
		catch (IOException e) {
			
			e.printStackTrace();
			String error = "Could not query index for query: \"" + query.toString() + "\"";
			this.logger.error(error, e);
			throw new RuntimeException(error, e);
		}
	}
	
	/**
	 * Query an index to know how many hits we have 
	 * 
	 * @param query - the query to execute
	 * @param collector - the document collector
	 */
	private void searchIndex(Query query, TotalHitCountCollector collector) {

		try {
			
			indexSearcher.search(query, collector);
		}
		catch (IOException e) {
			
			e.printStackTrace();
			String error = "Could not execute query: \"" + query.toString() + "\"";
			this.logger.error(error, e);
			throw new RuntimeException(error, e);
		}
	}
	
	/**
	 * Method to parse a given string to a Lucene query
	 * 
	 * @param query - the query to parse
	 * @return returns a parsed query for the given string
	 * @throws RuntimeException if something goes wrong
	 */
	private Query parseQuery(String query){
		
		try {
			
			return this.parser.parse(query);
		}
		catch (ParseException e) {
			
			e.printStackTrace();
			String error = "Could not parse query: \"" + query + "\"";
			this.logger.error(error, e);
			throw new RuntimeException(error, e);
		}
	}
}
