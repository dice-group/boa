package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;

import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.Feature;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.search.impl.DefaultPatternSearcher;


public class TotalOccurrenceFeature implements Feature {

	private NLPediaLogger logger = new NLPediaLogger(TotalOccurrenceFeature.class);
	
	@Override
	public void score(List<PatternMapping> mappings) {

		// dont do anything here
	}

	@Override
	public void scoreMapping(PatternMapping mapping) {
		
		// this is for junit testing
		this.scoreMapping(mapping, null);
	}
	
	public void scoreMapping(PatternMapping mapping, DefaultPatternSearcher searcher) {

		String patternToQuery = "";
		searcher = searcher != null ? searcher : new DefaultPatternSearcher();
		
		for ( Pattern pattern : mapping.getPatterns() ) {
			
			patternToQuery = pattern.getNaturalLanguageRepresentationWithoutVariables().toLowerCase();
				
			int totalOccurrences = searcher.getExactMatchSentences(patternToQuery, 10000).size();
			pattern.getFeatures().put(
				de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature.TOTAL_OCCURRENCE, Double.valueOf(totalOccurrences));
		}
	}
}
