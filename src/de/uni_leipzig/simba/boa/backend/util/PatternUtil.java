package de.uni_leipzig.simba.boa.backend.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.lucene.queryParser.ParseException;

import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.search.PatternSearcher;


public class PatternUtil {
	
	/**
	 * This is the strategy for collecting the patterns in the getTopNPattern method 
	 */
	public enum PatternSelectionStrategy { 
		
		/**
		 * means there are no limitations, we will take all patterns despite their score
		 */
		ALL,
		
		/**
		 * <code>
		 * if ( patternList.size() < 3 ) return Collections.<Pattern>emptyList();
			else {
				
				if ( patternList.size() >= 3 && patternList.size() < 5) topN = 1;
				if ( patternList.size() >= 5 && patternList.size() < 10 ) topN = 2;
				if ( patternList.size() >= 10 && patternList.size() < 20 ) topN = 3;
				if ( patternList.size() >= 20 ) topN = 5;
			}
			</code>
		 */
		SAFE
	}
	
	/**
	 * 
	 * @param mapping
	 * @param strategy
	 * @param topN
	 * @param scoreThreshold - score threshold for patterns, inclusive
	 * @return
	 */
	public static List<Pattern> getTopNPattern(PatternMapping mapping, PatternSelectionStrategy strategy, Integer topN, Double scoreThreshold) {

		List<Pattern> patternList = new ArrayList<Pattern>();
		
		// if there is a threshold given, only use those patterns which abide it
		if ( scoreThreshold != null ) {

			for (Pattern p : mapping.getPatterns()) {

				if ( p.getConfidence() >= scoreThreshold ) {

					patternList.add(p);
				}
			}
		}
		// if not just take all patterns
		else patternList = new ArrayList<Pattern>(mapping.getPatterns());
				
		if ( strategy == PatternUtil.PatternSelectionStrategy.ALL ) {
			
			// topN = topN; we don't need to change the topN value 
		}
		else if ( strategy == PatternUtil.PatternSelectionStrategy.SAFE ) {
			
			// too few patterns so we wont create anything for this pattern
			if ( patternList.size() < 3 ) return Collections.<Pattern>emptyList();
			else {
				
				if ( patternList.size() >= 3 && patternList.size() < 5) topN = 1;
				if ( patternList.size() >= 5 && patternList.size() < 10 ) topN = 2;
				if ( patternList.size() >= 10 && patternList.size() < 20 ) topN = 3;
				if ( patternList.size() >= 20 ) topN = 5;
			}
		}
		
		// sort the pattern by their confidence in descending order
		Collections.sort(patternList, new Comparator<Pattern>() {

			@Override
			public int compare(Pattern pattern1, Pattern pattern2) {

				return pattern2.getConfidence().compareTo(pattern1.getConfidence());
			}
		});
		
		// return the list from 0 to topN or the whole list if the list is smaller-equal than topN
		// since subList's "to" is exclusive this means from 0 to 3 -> top3 pattern 
		return patternList.size() > topN ? patternList.subList(0, topN) : patternList;
	}
	
	/**
	 * 
	 * @param indexDir
	 * @param pattern
	 * @param maxHits
	 * @return
	 */
	public static Set<String> exactQueryIndex(String indexDir, Pattern pattern, int maxHits){

		try {
			
			return PatternSearcher.getInstance(indexDir).getExactMatchSentences(pattern.getNaturalLanguageRepresentationWithoutVariables(), maxHits);
		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new HashSet<String>();
	}
	
	public static List<String> getLuceneDocuments(String indexDir, List<Integer> luceneDocIds) {
		
		return PatternSearcher.getInstance(indexDir).getSentences(luceneDocIds);
	}
}
