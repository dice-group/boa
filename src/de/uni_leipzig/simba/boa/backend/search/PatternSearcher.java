package de.uni_leipzig.simba.boa.backend.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.util.StringUtils;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;

/**
 * 
 * @author Daniel Gerber
 */
public class PatternSearcher {
	
	private final static int maxPatternChunkLength = 10;//new Integer(NLPediaSettings.getInstance().getSetting("maxPatternLenght")).intValue();
	private final static int minPatternChunkLenght = 2;//new Integer(NLPediaSettings.getInstance().getSetting("minPatternLenght")).intValue();

	public static final Set<String> stopwords = new HashSet<String>();
	static {
		Collections.addAll(stopwords, 	"I","a","about","an","are","as","at","be","by","com","for",
										"from","how","in","is","it","of","on","or","that","the",
										"this","to","was","what","when","where","who","will","with",
										"the","www","before",",","after",";","like","and","such",
										"-LRB-","-RRB-","aber","als","am","an","auch","auf","aus",
										"bei","bin","bis","bist","da","dadurch","daher","darum",
										"das","daß","dass","dein","deine","dem","den","der","des",
										"dessen","deshalb","die","dies","dieser","dieses","doch",
										"dort","du","durch","ein","eine","einem","einen","einer",
										"eines","er","es","euer","eure","für","hatte","hatten",
										"hattest","hattet","hier","hinter","ich","ihr","ihre","im",
										"in","ist","ja","jede","jedem","jeden","jeder","jedes",
										"jener","jenes","jetzt","kann","kannst","können","könnt",
										"machen","mein","meine","mit","muß","mußt","musst","müssen",
										"müßt","nach","nachdem","nein","nicht","nun","oder","seid",
										"sein","seine","sich","sie","sind","soll","sollen","sollst",
										"sollt","sonst","soweit","sowie","und","unser unsere","unter",
										"vom","von","vor","wann","warum","was","weiter","weitere",
										"wenn","wer","werde","werden","werdet","weshalb","wie",
										"wieder","wieso","wir","wird","wirst","wo","woher","wohin",
										"zu","zum","zur","über");
	}
	
	
	private Directory directory = null;
	private Analyzer analyzer = null;
	private Searcher indexSearcher = null;

	private QueryParser parser;
	private QueryParser exactMatchParser = null;
	private List<SearchResult> results;
	private ScoreDoc[] hits;
	private Document hitDoc = null;
	private Pattern p1 = null;
	private Matcher m1 = null;
	private Pattern p2 = null;
	private Matcher m2 = null;

	public PatternSearcher(String indexDir) throws IOException, ParseException {

		this.directory = FSDirectory.open(new File(indexDir));
		this.analyzer = new StandardAnalyzer(Version.LUCENE_30);

		// create index searcher in read only mode
		this.indexSearcher = new IndexSearcher(directory, true);
		this.parser = new QueryParser(Version.LUCENE_30,"sentence", this.analyzer);
		this.exactMatchParser = new QueryParser(Version.LUCENE_30, "sentence", new SimpleAnalyzer());

		this.results = new ArrayList<SearchResult>();
		this.hits = null;
	}

	public Set<String> getSentencesWithString(String keyphrase, int maxNumberOfDocuments) throws ParseException, IOException {

		Query query = parser.parse("+sentence:'" + QueryParser.escape(keyphrase) + "'");
		ScoreDoc[] hits = indexSearcher.search(query, null, maxNumberOfDocuments).scoreDocs;

		Set<String> list = new TreeSet<String>();
		for (int i = 0; i < hits.length; i++) {

			String sentence = indexSearcher.doc(hits[i].doc).get("sentence");

			// how is exact matching possible? right now filter for exact string
			// afterwards
			if (sentence.contains(keyphrase) && !sentence.contains("/")) {

				list.add(sentence);
			}
		}
		return ((TreeSet) list).descendingSet();
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
	public void queryPattern(String label1, String label2, String property, String range, String domain, boolean isLabel1Subject) throws ParseException, IOException {

		Query query = parser.parse("+sentence:\"" + QueryParser.escape(label1) + "\" && +sentence:\"" + QueryParser.escape(label2) + "\"");

		int maxNumberOfDocuments = Integer.valueOf(NLPediaSettings.getInstance().getSetting("maxNumberOfDocuments"));

		hits = indexSearcher.search(query, null, maxNumberOfDocuments).scoreDocs;

		for (int i = 0; i < hits.length; i++) {

			hitDoc = indexSearcher.doc(hits[i].doc);

			String naturalLanguageRepresentation = "";
			if (isLabel1Subject) {

				p1 = Pattern.compile("(\\b" + label1 + "\\b)(.*?)(\\b" + label2 + "\\b)", Pattern.CASE_INSENSITIVE);
				m1 = p1.matcher(hitDoc.get("sentence"));
				p2 = Pattern.compile("(\\b" + label2 + "\\b)(.*?)(\\b" + label1 + "\\b)", Pattern.CASE_INSENSITIVE);
				m2 = p2.matcher(hitDoc.get("sentence"));

				String match1 = "";
				while (m1.find())
					match1 = m1.group();
				String match2 = "";
				while (m2.find())
					match2 = m2.group();

				if (!match1.isEmpty()) { 
					
					naturalLanguageRepresentation = match1.replaceFirst("(?i)" + label1, "?D?");
					naturalLanguageRepresentation = naturalLanguageRepresentation.replaceAll("(?i)" + label2, "?R?");
				}
				if (!match2.isEmpty()) {

					naturalLanguageRepresentation = match2.replaceFirst("(?i)" + label1, "?D?");
					naturalLanguageRepresentation = naturalLanguageRepresentation.replaceAll("(?i)" + label2, "?R?");
				}
			}
			else {

				p1 = Pattern.compile("(\\b" + label1 + "\\b)(.*?)(\\b" + label2 + "\\b)", Pattern.CASE_INSENSITIVE);
				m1 = p1.matcher(hitDoc.get("sentence"));
				p2 = Pattern.compile("(\\b" + label2 + "\\b)(.*?)(\\b" + label1 + "\\b)", Pattern.CASE_INSENSITIVE);
				m2 = p2.matcher(hitDoc.get("sentence"));

				String match1 = "";
				while (m1.find())
					match1 = m1.group();
				String match2 = "";
				while (m2.find())
					match2 = m2.group();

				if (!match1.isEmpty()) {
					
					naturalLanguageRepresentation = match1.replaceFirst("(?i)" + label1, "?R?");
					naturalLanguageRepresentation = naturalLanguageRepresentation.replaceAll("(?i)" + label2, "?D?");
				}
				if (!match2.isEmpty()) {

					naturalLanguageRepresentation = match2.replaceFirst("(?i)" + label1, "?R?");
					naturalLanguageRepresentation = naturalLanguageRepresentation.replaceAll("(?i)" + label2, "?D?");
				}
			}

			if ( property.length() > 0 && this.isPatternSuitable(naturalLanguageRepresentation) ) {
				
				SearchResult result = new SearchResult();
				result.setProperty(property);
				result.setNaturalLanguageRepresentation(naturalLanguageRepresentation);
				result.setRdfsRange(range);
				result.setRdfsDomain(domain);
				result.setFirstLabel(label1);
				result.setSecondLabel(label2);
				result.setIndexId(hits[i].doc);
				this.results.add(result);
			}
		}
	}
	
//	public static void main(String[] args) {
//
//		System.out.println(isPatternSuitable("?D? and is a ?R?"));
//		System.out.println(isPatternSuitable("?R? and is a better ?D?"));
//		System.out.println(isPatternSuitable("?E? and ?R?"));
//		System.out.println(isPatternSuitable("?? and ?R?"));
//		System.out.println(isPatternSuitable("?D? and which has way too manny string between the one and the other thing ?R?"));
//		System.out.println(isPatternSuitable("?? and ?R?"));
//		System.out.println(isPatternSuitable("an ?D? and ?R?"));
//		System.out.println(isPatternSuitable("an ?D? and ?R? and ?D?"));
//	}

	private boolean isPatternSuitable(String naturalLanguageRepresentation) {

		// patterns are only allowed to have 256 characters
		if (naturalLanguageRepresentation.length() > 256) 
			return false;
		
		// pattern need to start with either ?D? or ?R? and have to end with ?D? or ?R?
		if ( (!naturalLanguageRepresentation.startsWith("?D?") && !naturalLanguageRepresentation.startsWith("?R?")) 
				|| (!naturalLanguageRepresentation.endsWith("?D?") && !naturalLanguageRepresentation.endsWith("?R?")) ) 
			return false;
		
		// patterns need to have only one domain and only one range
		if ( StringUtils.countOccurrencesOf(naturalLanguageRepresentation, "?D?") != 1 || StringUtils.countOccurrencesOf(naturalLanguageRepresentation, "?R?") != 1 )			
			return false;
		
		// patterns need to be bigger/equal than min chunk size and smaller/equal then max chunk size
		String[] naturalLanguageRepresentationChunks = naturalLanguageRepresentation.substring(0, naturalLanguageRepresentation.length() - 3).substring(3).trim().split(" ");
		if ( naturalLanguageRepresentationChunks.length > maxPatternChunkLength || naturalLanguageRepresentationChunks.length < minPatternChunkLenght )
			return false;
		
		// true or correct if the number of stop-words in the pattern is not equal to the number of tokens
		// patterns containing only stop-words can't be used, because they are way to general 
		int numberOfStopWordsInPattern = 0;
		for ( String token : naturalLanguageRepresentationChunks ) {
			if ( stopwords.contains(token) ) numberOfStopWordsInPattern++;
		}
		if ( naturalLanguageRepresentationChunks.length == numberOfStopWordsInPattern )
			return false;
		
		// patterns shall not start with "and" or "and ," because this is the conjunction of sentences and does not carry meaning
		if ( naturalLanguageRepresentation.startsWith("and ") || naturalLanguageRepresentation.startsWith("and,") || naturalLanguageRepresentation.startsWith("and ,") ) 
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

	public Set<String> getExactMatchSentences(String keyphrase, int maxNumberOfDocuments)  throws ParseException, IOException {

		ScoreDoc[] hits = indexSearcher.search(exactMatchParser.parse("\""+QueryParser.escape(keyphrase)+"\""), null, maxNumberOfDocuments).scoreDocs;
		TreeSet<String> list = new TreeSet<String>();

		for (int i = hits.length - 1; i >= 0; i--) {
			
			// get the indexed string
			String sentence = indexSearcher.doc(hits[i].doc).get("sentence");
			
			// convert to UTF8
			byte[] bytes = sentence.getBytes("UTF-8");
			String testString = new String(bytes, "UTF-8");
			
			// only add it if also "," are contained (remember lucene does not match ",")
			if ( testString.contains(keyphrase)) list.add(testString);
		}
		return list;
	}
}
