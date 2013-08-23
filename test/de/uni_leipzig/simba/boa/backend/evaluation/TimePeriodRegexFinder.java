/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.evaluation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.util.Version;

import com.github.gerbsen.encoding.Encoder.Encoding;
import com.github.gerbsen.file.BufferedFileWriter;
import com.github.gerbsen.file.BufferedFileWriter.WRITER_WRITE_MODE;
import com.github.gerbsen.math.Frequency;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.lucene.LowerCaseWhitespaceAnalyzer;
import de.uni_leipzig.simba.boa.backend.lucene.LuceneIndexHelper;

/**
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 *
 */
public class TimePeriodRegexFinder {

	private static final int MAXIMUM_MATCH_LENGTH = 5;
	
	static Analyzer analyzer         = new LowerCaseWhitespaceAnalyzer();
	static QueryParser parser        = new QueryParser(Version.LUCENE_34, "sentence", analyzer);
	static IndexSearcher indexSearcher = null;
	

	/**
	 * @param args
	 * @throws CorruptIndexException 
	 * @throws ParseException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws CorruptIndexException, IOException, ParseException  {
		
		NLPediaSetup  s = new NLPediaSetup(false);
		
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_34, new LowerCaseWhitespaceAnalyzer());
        indexWriterConfig.setOpenMode(OpenMode.APPEND);
        
        Frequency freq = new Frequency();
        indexSearcher = LuceneIndexHelper.getIndexSearcher("/Users/gerb/Development/workspaces/experimental/boa/qa/en/index/corpus");

        BufferedFileWriter writer = new BufferedFileWriter("/Users/gerb/Development/workspaces/experimental/defacto/mltemp/eval/intervals.txt", Encoding.UTF_8, WRITER_WRITE_MODE.OVERRIDE);
        
		List<YearMatch> matches = new ArrayList<YearMatch>();
		for (int i = 1990; i < 2014 ; i++) {
			for (int j = 1990; j < 2014 ; j++) {
				
				if ( i >= j ) continue;
				
				List<YearMatch> patterns = getPatterns(i+"", j+"");
				for ( YearMatch m : patterns) {
					
					for ( String before : m.beforeMatch ) freq.addValue(before);
					for ( String match : m.beforeMatch ) freq.addValue(match);
					for ( String after : m.beforeMatch ) freq.addValue(after);
					
					writer.write(m.toString());
				}
				matches.addAll(patterns);
				
				System.out.println(i+"/"+j + ": " + matches.size());
			}
			int n = 0;
			for ( Map.Entry<Comparable<?>, Long> sortByValue : freq.sortByValue() ) {
				
				if ( n++ == 100 )break;
				System.out.println(sortByValue.getKey() + ":\t" + sortByValue.getValue());
			}
		}
		
		writer.close();
	}
	
	public static List<YearMatch> getPatterns(String first, String second) throws CorruptIndexException, IOException, ParseException {
		
		Query q =  parser.parse("sentence:("+first+") AND sentence:("+second+")");

		List<YearMatch> currentMatches = new ArrayList<TimePeriodRegexFinder.YearMatch>();
		
		// go through all sentences and surface form combinations 
		for ( ScoreDoc hit : indexSearcher.search(q, 5000).scoreDocs ) {
		
			String sentence     = indexSearcher.doc(hit.doc).get("sentence");
//			System.out.println(sentence);
			currentMatches.addAll(findYearMatches(sentence, first, second));
		}
		
//		for ( YearMatch match : currentMatches ) {
//
//			System.out.println(match.beforeMatch + " " + match.startYear + " " + match.match + " " + match.endYear + " " + match.afterMatch);
//		}
		
		return currentMatches;
	}
	
    
	public static List<YearMatch> findYearMatches(String textToTag, String start, String end){
		
		List<String> words = Arrays.asList(textToTag.split(" "));
		List<YearMatch> matches = new ArrayList<YearMatch>();
		
		YearMatch match = new YearMatch();
		
		for ( int i = 0; i < words.size() ; i++ ){
			
			String token = words.get(i);
			
			// we found a year
			if ( token.matches(start) ) {
				
				match.startYear = Integer.valueOf(token);
				if ( i - 3 >= 0 ) match.beforeMatch.add(words.get(i - 3));
				if ( i - 2 >= 0 ) match.beforeMatch.add(words.get(i - 2));
				if ( i - 1 >= 0 ) match.beforeMatch.add(words.get(i - 1));
			}
			// we found a normal token
			else {
				
				// end 
				if ( token.matches(end) ) {
					
					match.endYear = Integer.valueOf(token);
					
					if ( i + 1 < words.size() ) match.afterMatch.add(words.get(i + 1));
					if ( i + 2 < words.size() ) match.afterMatch.add(words.get(i + 2));
					if ( i + 3 < words.size() ) match.afterMatch.add(words.get(i + 3));
					
					if ( match.size() <= MAXIMUM_MATCH_LENGTH ){
						
						if (match.startYear != -1 ) matches.add(match);
					}
						
					// make the new start the old end
					match = new YearMatch();
				}
				// nothing
				else {
					
					// so far no match
					if ( match.startYear == -1 ) {
						
						continue;
					}
					else {
						
						match.add(token);
					}
				}
			}
		}
		return matches;
	}
	
	public static List<YearMatch> findYearMatches(String textToTag){
		
		List<String> words = Arrays.asList(textToTag.split(" "));
		List<YearMatch> matches = new ArrayList<YearMatch>();
		
		YearMatch match = new YearMatch();
		
		for ( int i = 0; i < words.size() ; i++ ){
			
			String token = words.get(i);
			
			// we found a year
			if ( token.matches("[12][0-9]{3}") ) {
				
				// we found the start of a match
				if ( match.startYear == -1 ) {
					
					match.startYear = Integer.valueOf(token);
					if ( i - 3 >= 0 ) match.beforeMatch.add(words.get(i - 3));
					if ( i - 2 >= 0 ) match.beforeMatch.add(words.get(i - 2));
					if ( i - 1 >= 0 ) match.beforeMatch.add(words.get(i - 1));
				}
				// we found the end of a match
				else {
					
					match.endYear = Integer.valueOf(token);
					
					if ( i + 1 < words.size() ) match.afterMatch.add(words.get(i + 1));
					if ( i + 2 < words.size() ) match.afterMatch.add(words.get(i + 2));
					if ( i + 3 < words.size() ) match.afterMatch.add(words.get(i + 3));
					
					if ( match.size() <= MAXIMUM_MATCH_LENGTH ) matches.add(match);
						
					// make the new start the old end
					match = new YearMatch();
				}
			}
			// we found a normal token
			else {
				
				// so far no match
				if ( match.startYear == -1 ) {
					
					continue;
				}
				else {
					
					match.add(token);
				}
			}
		}
		return matches;
	}
	
	
	public static class YearMatch {
		
		public List<String> beforeMatch = new ArrayList<String>();
		public List<String> afterMatch = new ArrayList<String>();
		public List<String> match = new ArrayList<String>();
		public int startYear = -1;
		public int endYear = -1;
		public YearMatch(String start, String end) {
			
			startYear = Integer.valueOf(start);
			endYear = Integer.valueOf(end);
		}
		public YearMatch() {
			// TODO Auto-generated constructor stub
		}
		public boolean isEmpty() {
			return match.isEmpty();
		}
		public int size() {
			return match.size();
		}
		public void add(String token) {
			
			match.add(token);
		}
		
		public String toString(){
			
			return StringUtils.join(beforeMatch, " ") +"\t" + "XXX" + "\t" + StringUtils.join(match, " ") + "\t" + "XXX" + "\t" + StringUtils.join(afterMatch, " "); 
		}
	}
}
