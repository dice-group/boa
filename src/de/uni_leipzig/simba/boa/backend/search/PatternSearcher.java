package de.uni_leipzig.simba.boa.backend.search;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
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

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.nlp.NamedEntityRecognizer;
import de.uni_leipzig.simba.boa.backend.nlp.PosTagger;

/**
 * 
 * @author Daniel Gerber
 */
public class PatternSearcher {

	private Directory directory				= null;
	private Analyzer analyzer				= null;
	private Searcher indexSearcher 			= null;
	
	private QueryParser parser;
	private List<String> results;
	private ScoreDoc[] hits;
	private Document hitDoc = null;
	private Pattern p1 = null;
	private Matcher m1 = null;
	private Pattern p2 = null;
	private Matcher m2 = null;
	
	public PatternSearcher(String indexDir) throws IOException, ParseException {
		
		this.directory	= FSDirectory.open(new File(indexDir));
		this.analyzer 	= new StandardAnalyzer(Version.LUCENE_30);
		
		// create index searcher in read only mode
		this.indexSearcher = new IndexSearcher(directory, true);
		this.parser = new QueryParser(Version.LUCENE_30, "sentence", analyzer);
		
		this.results = new ArrayList<String>();
		this.hits = null;
	}

	public Set<String> getSentencesWithString(String keyphrase, int maxNumberOfDocuments) throws ParseException, IOException {

		Query query = parser.parse("+sentence:'" + keyphrase + "'");
		ScoreDoc[] hits = indexSearcher.search(query, null, maxNumberOfDocuments).scoreDocs;
		
//		System.out.println("keyphrase: " + keyphrase);
//		System.out.println("parsed-keyphrase: " + QueryParser.escape(keyphrase));
//		System.out.println("query: " + query);
		
		Set<String> list = new TreeSet<String>();
		for (int i = 0 ; i < hits.length ; i++ ) {
					
			String sentence = indexSearcher.doc(hits[i].doc).get("sentence");
			
			// how is exact matching possible? right now filter for exact string afterwards
			if ( sentence.contains(keyphrase) && !sentence.contains("/") ) {
				
				list.add(sentence);
			}
		}
		return list;
	}
	
	/**
	 * Query the index with two search terms.
	 * 
	 * @param string
	 * @param string2
	 * @throws ParseException, IOException 
	 */
	public void queryPattern(String label1, String label2, String property, String range, String domain, boolean isLabel1Subject) throws ParseException, IOException {

		Query query = parser.parse("+sentence:\"" + QueryParser.escape(label1) + "\" && +sentence:\"" + QueryParser.escape(label2) + "\"");
		
		int maxNumberOfDocuments = Integer.valueOf(NLPediaSettings.getInstance().getSetting("maxNumberOfDocuments"));
		
		hits = indexSearcher.search(query, null, 10).scoreDocs;
	    
	    for (int i = 0; i < hits.length; i++) {
	    	
	    	if ( i == maxNumberOfDocuments ) break;
	    	hitDoc = indexSearcher.doc(hits[i].doc);

//	    	if ( label1.equals("Council of the European Union") && label2.equals("Justus Lipsius building") ) {
//	    	if ( property.equals("http://dbpedia.org/ontology/subsidiary") ) {
//	    		if ( hitDoc.get("sentence").equals("The European Council uses the same building as the Council of the European Union , i.e. , the Justus Lipsius building .") || 
//		    			hitDoc.get("sentence").equals("The Justus Lipsius building is a building in Brussels -LRB- Belgium -RRB- that has been the headquarters of the Council of the European Union since 1995 .") ) {
		    	
	    			String naturalLanguageRepresentation = "";
	    			if ( isLabel1Subject ) {
	    				
	    				p1 = Pattern.compile("(\\b"+label1+"\\b)(.*?)(\\b"+label2+"\\b)", Pattern.CASE_INSENSITIVE);
		    	    	m1 = p1.matcher(hitDoc.get("sentence"));
		    	    	p2 = Pattern.compile("(\\b"+label2+"\\b)(.*?)(\\b"+label1+"\\b)", Pattern.CASE_INSENSITIVE);
		    	    	m2 = p2.matcher(hitDoc.get("sentence"));
		    	    	
		    	    	String match1 = "";
		    	    	while (m1.find()) match1 = m1.group();
		    	    	String match2 = "";
		    	    	while (m2.find()) match2 = m2.group();
		    	    	
		    	    	if ( !match1.isEmpty() ) {  // java.util.regex.PatternSyntaxException: Dangling meta character '?' near index 4 (?i)?
		    	    		
		    	    		naturalLanguageRepresentation = match1.replaceFirst("(?i)"+label1, "?Y?");
			    	    	naturalLanguageRepresentation = naturalLanguageRepresentation.replaceAll("(?i)"+label2, "?X?");
		    	    	}
		    	    	if ( !match2.isEmpty() ) {
		    	    		
		    	    		naturalLanguageRepresentation = match2.replaceFirst("(?i)"+label1, "?Y?");
		    	    		naturalLanguageRepresentation = naturalLanguageRepresentation.replaceAll("(?i)"+label2, "?X?");
		    	    	}
	    			}
	    			else {
	    				
	    				p1 = Pattern.compile("(\\b"+label1+"\\b)(.*?)(\\b"+label2+"\\b)", Pattern.CASE_INSENSITIVE);
		    	    	m1 = p1.matcher(hitDoc.get("sentence"));
		    	    	p2 = Pattern.compile("(\\b"+label2+"\\b)(.*?)(\\b"+label1+"\\b)", Pattern.CASE_INSENSITIVE);
		    	    	m2 = p2.matcher(hitDoc.get("sentence"));
		    	    	
		    	    	String match1 = "";
		    	    	while (m1.find()) match1 = m1.group();
		    	    	String match2 = "";
		    	    	while (m2.find()) match2 = m2.group();
		    	    	
		    	    	if ( !match1.isEmpty() ) {  // java.util.regex.PatternSyntaxException: Dangling meta character '?' near index 4 (?i)?
		    	    		
//		    	    		System.out.println("match1: " + match1);
		    	    		naturalLanguageRepresentation = match1.replaceFirst("(?i)"+label1, "?X?");
			    	    	naturalLanguageRepresentation = naturalLanguageRepresentation.replaceAll("(?i)"+label2, "?Y?");
		    	    	}
		    	    	if ( !match2.isEmpty() ) {
		    	    		
//		    	    		System.out.println("match2: " + match2);
		    	    		naturalLanguageRepresentation = match2.replaceFirst("(?i)"+label1, "?X?");
		    	    		naturalLanguageRepresentation = naturalLanguageRepresentation.replaceAll("(?i)"+label2, "?Y?");
		    	    	}
		    	    	
//		    	    	System.out.println(label1 +" "+ property + " " + label2);
	    			}
//	    	    	System.out.println(naturalLanguageRepresentation);
//	    	    	System.out.println(hitDoc.get("sentence"));
//	    	    	System.out.println("LABEL1: " + label1);
//	    	    	System.out.println("LABEL2: " + label2);
//	    	    	System.out.println(property);
//	    	    	System.out.println();
	    	    	
	    	    	if ( StringUtils.countOccurrencesOf(naturalLanguageRepresentation, "?X?") != 1 || StringUtils.countOccurrencesOf(naturalLanguageRepresentation, "?Y?") != 1) {
	    	    		
	    	    		continue; // be sure there are no patterns without x or y in the pattern list
	    	    	}
	    	    	// add only those patterns we do have a property for an all patterns with length higher than 256 are more or less useless
	    	    	if ( property.length() > 0 && naturalLanguageRepresentation.length() < 256 ){
	    	    		
	    	    		this.results.add(property + "-;-" + naturalLanguageRepresentation + "-;-" + range + "-;-" + domain + "-;-" + label1 + "-;-" + label2 + "-;-" + hits[i].doc);
	    	    	}
//		    	}
//	    	}
	    }
	}
	
	public List<String> getResults() {
		
		return this.results;
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		
		this.indexSearcher.close();
	}
}
