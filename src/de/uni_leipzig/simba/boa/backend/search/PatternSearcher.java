package de.uni_leipzig.simba.boa.backend.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.partofspeechtagger.PartOfSpeechTagger;
import de.uni_leipzig.simba.boa.backend.nlp.PosTagger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;

/**
 * 
 * @author Daniel Gerber
 */
public class PatternSearcher {

	private final static int MAX_PATTERN_CHUNK_LENGTH = new Integer(NLPediaSettings.getInstance().getSetting("maxPatternLenght")).intValue();
	private final static int MIN_PATTERN_CHUNK_LENGTH = new Integer(NLPediaSettings.getInstance().getSetting("minPatternLenght")).intValue();
	private final static int MAX_NUMBER_OF_DOCUMENTS = Integer.valueOf(NLPediaSettings.getInstance().getSetting("maxNumberOfDocuments"));

	private PartOfSpeechTagger posTagger;

	private Directory directory = null;
	private Analyzer analyzer = null;
	private IndexSearcher indexSearcher = null;

	private QueryParser parser;
	private List<SearchResult> results;
	private ScoreDoc[] hits;
	
	private static String indexDir = "";
	private static PatternSearcher INSTANCE;
	
	private final NLPediaLogger logger = new NLPediaLogger(PatternSearcher.class);

	public PatternSearcher() throws IOException, ParseException {

		this.directory = FSDirectory.open(new File(NLPediaSettings.BOA_DATA_DIRECTORY + NLPediaSettings.getInstance().getSetting("sentenceIndexDirectory")));
		this.analyzer = new WhitespaceAnalyzer();

		// create index searcher in read only mode
		this.indexSearcher = new IndexSearcher(directory, true);
		this.parser = new QueryParser(Version.LUCENE_30, "sentence-lc", this.analyzer);

		this.results = new ArrayList<SearchResult>();
		this.hits = null;
	}
	
	public PatternSearcher(String indexDir) throws IOException, ParseException {

		this.directory = FSDirectory.open(new File(indexDir));
		this.analyzer = new WhitespaceAnalyzer();

		// create index searcher in read only mode
		this.indexSearcher = new IndexSearcher(directory, true);
		this.parser = new QueryParser(Version.LUCENE_30, "sentence-lc", this.analyzer);

		this.results = new ArrayList<SearchResult>();
		this.hits = null;
	}
	
	public PatternSearcher(Directory indexDir) throws IOException, ParseException {

		this.directory = indexDir;
		this.analyzer = new WhitespaceAnalyzer();

		// create index searcher in read only mode
		this.indexSearcher = new IndexSearcher(directory, true);
		this.parser = new QueryParser(Version.LUCENE_30, "sentence-lc", this.analyzer);

		this.results = new ArrayList<SearchResult>();
		this.hits = null;
	}
	
	public static PatternSearcher getInstance(String indexDir) {
		
		if ( PatternSearcher.INSTANCE == null || !PatternSearcher.indexDir.equals(indexDir) ) {
			
			try {
				
				PatternSearcher.INSTANCE = new PatternSearcher(indexDir);
			}
			catch (IOException e) {
				
				e.printStackTrace();
			}
			catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		return PatternSearcher.INSTANCE;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Returns the sentences from the index with the given ids. Uses the method
	 * PatternSearcher.getSentencesByID() to query the index
	 * 
	 * @param listOfIds
	 * @return
	 */
	public List<String> getSentences(List<Integer> ids) {

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
	 * @throws ParseException
	 *             , IOException
	 */
	public void queryPattern(Triple triple) throws ParseException, IOException {

		boolean inverse = false;
		
//		Set<String> firstLabels = triple.getSubject().retrieveLabels();
//		Set<String> secondLabels = triple.getObject().retrieveLabels();
		Set<String> firstLabels = new HashSet<String>(Arrays.asList(triple.getSubject().getLabel().toLowerCase()));//retrieveLabels();
		Set<String> secondLabels = new HashSet<String>(Arrays.asList(triple.getObject().getLabel().toLowerCase()));//.retrieveLabels();

		// switch the labels in case we have more subject labels than object labels
		if ( firstLabels.size() > secondLabels.size() ) {
			
			inverse = true;
			
			firstLabels = triple.getObject().retrieveLabels();
			secondLabels = triple.getSubject().retrieveLabels();
		}
		
		// go through all surface form combinations
		for (String firstLabel : firstLabels) {

			// check if we find at least sentences with the first token, if not we can skip this search word combination
			TotalHitCountCollector collector = new TotalHitCountCollector();
			indexSearcher.search(parser.parse("+sentence-lc:\"" + QueryParser.escape(firstLabel) + "\""), collector);
			if (collector.getTotalHits() == 0 ) continue; 
			
			for (String secondLabel : secondLabels) {

				Query query = parser.parse("+sentence-lc:\"" + QueryParser.escape(firstLabel) + "\" && +sentence-lc:\"" + QueryParser.escape(secondLabel) + "\"");
				hits = indexSearcher.search(query, null, MAX_NUMBER_OF_DOCUMENTS).scoreDocs;
				
				for (int i = 0; i < hits.length; i++) {

					String sentenceLowerCase = indexSearcher.doc(hits[i].doc).get("sentence-lc");

					Map<Integer, String> currentMatches = new HashMap<Integer, String>();
					
					// the switching is neccessary because it could be possible that we change the label sets, see above 					
					String[] match1; 
					if ( !inverse ) {
						match1 = StringUtils.substringsBetween(sentenceLowerCase, firstLabel, secondLabel);
					}
					else {
						
						match1 = StringUtils.substringsBetween(sentenceLowerCase, secondLabel, firstLabel);
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
						match2 = StringUtils.substringsBetween(sentenceLowerCase, secondLabel, firstLabel);
					}
					else {
						
						match2 = StringUtils.substringsBetween(sentenceLowerCase, firstLabel, secondLabel);
					}
					if (match2 != null) {

						for (int j = 0; j < match2.length; j++) {

							match2[j] = "?R? " + match2[j].trim() + " ?D?";
							currentMatches.put(hits[i].doc, match2[j]);
						}
					}
					this.addSearchResults(currentMatches, triple, hits[i], sentenceLowerCase, indexSearcher.doc(hits[i].doc).get("sentence"));
				}
			}
		}
	}

	/**
	 * Create the search results
	 * 
	 * @param currentMatches
	 * @param triple
	 * @param hit
	 * @param isSubject
	 */
	private void addSearchResults(Map<Integer, String> currentMatches, Triple triple, ScoreDoc hit, String sentenceLowerCase, String sentenceNormalCase) {

		for (String match : currentMatches.values()) {

			String nlr = this.getCorrectCaseNLR(sentenceLowerCase, sentenceNormalCase, match);

			// but only for those who are suitable
			if (!match.isEmpty() && this.isPatternSuitable(match)) {

				SearchResult result = new SearchResult();
				result.setProperty(triple.getProperty().getUri());
				result.setNaturalLanguageRepresentation(nlr);
				result.setRdfsRange(triple.getProperty().getRdfsRange());
				result.setRdfsDomain(triple.getProperty().getRdfsDomain());
				// the subject of the triple is the domain of the property so,
				// replace every occurrence with ?D?
				if (nlr.startsWith("?D?")) {
					result.setFirstLabel(triple.getSubject().getLabel());
					result.setSecondLabel(triple.getObject().getLabel());
				}
				else {
					result.setFirstLabel(triple.getObject().getLabel());
					result.setSecondLabel(triple.getSubject().getLabel());
				}
				result.setIndexId(hit.doc);
				if ( this.posTagger == null ) this.posTagger = new PosTagger();
				result.setPosTags(this.posTagger.getPosTagsForSentence(match.substring(0, match.length() - 3).substring(3), triple.getSubject().getLabel(), triple.getObject().getLabel()));
				this.results.add(result);
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

	private boolean isPatternSuitable(String naturalLanguageRepresentation) {

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

	public List<SearchResult> getResults() {

		return this.results;
	}

	/**
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {

		this.indexSearcher.close();
	}

	public Set<String> getExactMatchSentences(String keyphrase, int maxNumberOfDocuments) throws ParseException, IOException {

		ScoreDoc[] hits = indexSearcher.search(this.parser.parse("+sentence-lc:\"" + QueryParser.escape(keyphrase.toLowerCase()) + "\""), null, maxNumberOfDocuments).scoreDocs;
		System.out.println(this.parser.parse("+sentence-lc:\"" + QueryParser.escape(keyphrase.toLowerCase()) + "\""));
		TreeSet<String> list = new TreeSet<String>();

		// reverse order because longer sentences come last, longer sentences
		// most likely contain less it,he,she
		for (int i = hits.length - 1; i >= 0; i--) {

			// get the indexed string and put it in the result
			list.add(indexSearcher.doc(hits[i].doc).get("sentence"));
		}
		return list;
	}
	
	public Set<String> getExactMatchSentencesForLabels(String label1, String label2, int numberOfDocuments) throws ParseException, IOException {
		
		Query query = parser.parse("+sentence-lc:\"" + QueryParser.escape(label1) + "\" && +sentence-lc:\"" + QueryParser.escape(label2) + "\"");
		hits = indexSearcher.search(query, null, numberOfDocuments).scoreDocs;
		TreeSet<String> list = new TreeSet<String>();
		
		// reverse order because longer sentences come last, longer sentences
		// most likely contain less it,he,she
		for (int i = hits.length - 1; i >= 0; i--) {

			// get the indexed string and put it in the result
			list.add(indexSearcher.doc(hits[i].doc).get("sentence"));
		}
		return list;
	}
}
