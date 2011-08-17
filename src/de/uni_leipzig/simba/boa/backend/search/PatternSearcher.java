package de.uni_leipzig.simba.boa.backend.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
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

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.nlp.PosTagger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;

/**
 * 
 * @author Daniel Gerber
 */
public class PatternSearcher {
	
//	private final static int maxPatternChunkLength = new Integer(NLPediaSettings.getInstance().getSetting("maxPatternLenght")).intValue();
//	private final static int minPatternChunkLenght = new Integer(NLPediaSettings.getInstance().getSetting("minPatternLenght")).intValue();

	private PosTagger posTagger;
	
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
	private List<SearchResult> results;
	private ScoreDoc[] hits;

//	private Pattern p1 = null;
//	private Matcher m1 = null;
//	private Pattern p2 = null;
//	private Matcher m2 = null;

	public PatternSearcher(String indexDir) throws IOException, ParseException {

		this.directory = FSDirectory.open(new File(indexDir));
		this.analyzer = new WhitespaceAnalyzer();

		// create index searcher in read only mode
		this.indexSearcher = new IndexSearcher(directory, true);
		this.parser = new QueryParser(Version.LUCENE_30, "sentence", this.analyzer);

		this.results = new ArrayList<SearchResult>();
		this.hits = null;
		
		this.posTagger = new PosTagger();
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

		String subjectLabel = this.removeBrackets(triple.getSubject().getLabel());
		String objectLabel = this.removeBrackets(triple.getObject().getLabel());
		
		Query query = parser.parse("+sentence:\"" + QueryParser.escape(subjectLabel) + "\" && +sentence:\"" + QueryParser.escape(objectLabel) + "\"");

		int maxNumberOfDocuments = Integer.valueOf(NLPediaSettings.getInstance().getSetting("maxNumberOfDocuments"));

		hits = indexSearcher.search(query, null, maxNumberOfDocuments).scoreDocs;
		
//		p1 = Pattern.compile("(\\b" + Pattern.quote(subjectLabel) + "\\b)(.*?)(\\b" + Pattern.quote(objectLabel) + "\\b)", Pattern.CASE_INSENSITIVE);
//		p2 = Pattern.compile("(\\b" + Pattern.quote(objectLabel) + "\\b)(.*?)(\\b" + Pattern.quote(subjectLabel) + "\\b)", Pattern.CASE_INSENSITIVE);

		for (int i = 0; i < hits.length; i++) {

			String sentence = indexSearcher.doc(hits[i].doc).get("sentence");
			Map<Integer,String> currentMatches = new HashMap<Integer,String>();
			
			// the subject of the triple is the domain of the property so, replace every occurrence with ?D?
			if ( triple.getSubject().getType().equals(triple.getProperty().getRdfsDomain()) ) {
				
				String[] match1 = StringUtils.substringsBetween(sentence, subjectLabel, objectLabel);
				if ( match1 != null ) {
					
					for (int j = 0 ; j < match1.length ; j++ ) {
						
						match1[j] = "?D? " + match1[j].trim() + " ?R?";
						currentMatches.put(hits[i].doc, match1[j]);
					}
				}
				
				String[] match2 = StringUtils.substringsBetween(sentence, objectLabel, subjectLabel);
				if ( match2 != null ) {
					
					for (int j = 0 ; j < match2.length ; j++ ) { 
						
						match2[j] = "?R? " + match2[j].trim() + " ?D?";
						currentMatches.put(hits[i].doc, match2[j]);
					}
				}
				
//				m1 = p1.matcher(sentence);
//				m2 = p2.matcher(sentence);
//				// collect the matches
//				while (m1.find()) currentMatches.put(hits[i].doc, m1.group());
//				while (m2.find()) currentMatches.put(hits[i].doc, m2.group());
				
				// replace the entity labels with ?D? and ?R?
				for (String match : currentMatches.values() ) {
					
//					match = match.replaceAll("(?i)" + subjectLabel, "?D?");
//					match = match.replaceAll("(?i)" + objectLabel, "?R?");
					
					// but only for those who are suitable
					if ( !match.isEmpty() && this.isPatternSuitable(match) ) {
						
						SearchResult result = new SearchResult();
						result.setProperty(triple.getProperty().getUri());
						result.setNaturalLanguageRepresentation(match);
						result.setRdfsRange(triple.getProperty().getRdfsRange());
						result.setRdfsDomain(triple.getProperty().getRdfsDomain());
						result.setFirstLabel(triple.getSubject().getLabel());
						result.setSecondLabel(triple.getObject().getLabel());
						result.setIndexId(hits[i].doc);
						result.setPosTags(this.posTagger.getPosTagsForSentence(match.substring(0, match.length() - 3).substring(3), subjectLabel, objectLabel));
						this.results.add(result);
					}
				}
			}
			else {
				
				String[] match1 = StringUtils.substringsBetween(sentence, subjectLabel, objectLabel);
				if ( match1 != null ) {
					
					for (int j = 0 ; j < match1.length ; j++ ) {
						
						match1[j] = "?R? " + match1[j].trim() + " ?D?";
						currentMatches.put(hits[i].doc, match1[j]);
					}
				}
				
				String[] match2 = StringUtils.substringsBetween(sentence, objectLabel, subjectLabel);
				if ( match2 != null ) {
					
					for (int j = 0 ; j < match2.length ; j++ ) { 
						
						match2[j] = "?D? " + match2[j].trim() + " ?R?";
						currentMatches.put(hits[i].doc, match2[j]);
					}
				}
				
//				m1 = p1.matcher(sentence);
//				m2 = p2.matcher(sentence);
//
//				while (m1.find()) currentMatches.put(hits[i].doc, m1.group());
//				while (m2.find()) currentMatches.put(hits[i].doc, m2.group());
				
				for ( String match : currentMatches.values() ) {
					
//					match = match.replaceAll("(?i)" + subjectLabel, "?R?");
//					match = match.replaceAll("(?i)" + objectLabel, "?D?");
					
					// but only for those who are suitable
					if ( !match.isEmpty() && this.isPatternSuitable(match) ) {
						
						SearchResult result = new SearchResult();
						result.setProperty(triple.getProperty().getUri());
						result.setNaturalLanguageRepresentation(match);
						result.setRdfsRange(triple.getProperty().getRdfsRange());
						result.setRdfsDomain(triple.getProperty().getRdfsDomain());
						result.setFirstLabel(triple.getObject().getLabel());
						result.setSecondLabel(triple.getSubject().getLabel());
						result.setIndexId(hits[i].doc);
						result.setPosTags(this.posTagger.getPosTagsForSentence(match.substring(0, match.length() - 3).substring(3), subjectLabel, objectLabel));
						this.results.add(result);
					}
				}
			}
		}
	}
	
	private String removeBrackets(String label) {

		return label.substring(0, label.indexOf("(") - 1);
	}

	private boolean isPatternSuitable(String naturalLanguageRepresentation) {

		// patterns are only allowed to have 256 characters
		if (naturalLanguageRepresentation.length() > 256 || naturalLanguageRepresentation.isEmpty()) 
			return false;
		
		// pattern need to start with either ?D? or ?R? and have to end with ?D? or ?R?
		if ( (!naturalLanguageRepresentation.startsWith("?D?") && !naturalLanguageRepresentation.startsWith("?R?")) 
				|| (!naturalLanguageRepresentation.endsWith("?D?") && !naturalLanguageRepresentation.endsWith("?R?")) ) 
			return false;
		
		// patterns need to have only one domain and only one range
		if ( StringUtils.countMatches(naturalLanguageRepresentation, "?D?") != 1 || StringUtils.countMatches(naturalLanguageRepresentation, "?R?") != 1 )			
			return false;
		
		// patterns need to be bigger/equal than min chunk size and smaller/equal then max chunk size
//		String[] naturalLanguageRepresentationChunks = naturalLanguageRepresentation.substring(0, naturalLanguageRepresentation.length() - 3).substring(3).trim().split(" ");
//		if ( naturalLanguageRepresentationChunks.length > maxPatternChunkLength || naturalLanguageRepresentationChunks.length < minPatternChunkLenght )
//			return false;
		
		// true or correct if the number of stop-words in the pattern is not equal to the number of tokens
		// patterns containing only stop-words can't be used, because they are way to general 
//		int numberOfStopWordsInPattern = 0;
//		for ( String token : naturalLanguageRepresentationChunks ) {
//			if ( stopwords.contains(token) ) numberOfStopWordsInPattern++;
//		}
//		if ( naturalLanguageRepresentationChunks.length == numberOfStopWordsInPattern )
//			return false;
		
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

		ScoreDoc[] hits = indexSearcher.search(this.parser.parse("\""+QueryParser.escape(keyphrase)+"\""), null, maxNumberOfDocuments).scoreDocs;
		TreeSet<String> list = new TreeSet<String>();
		
		// reverse order because longer sentences come last, longer sentences most likely contain less it,he,she 
		for (int i = hits.length - 1; i >= 0; i--) {
			
			// get the indexed string and put it in the result
			list.add(indexSearcher.doc(hits[i].doc).get("sentence"));
		}
		return list;
	}
}
