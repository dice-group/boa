package de.uni_leipzig.simba.boa.backend.entity.pattern.filter.impl;

import java.util.Map;
import java.util.Map.Entry;

import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.filter.PatternFilter;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

/**
 * 
 * @author Daniel Gerber
 */
public class OccurrenceFilter implements PatternFilter {

	private static final int NUMBER_OF_OCCURRENCES_THRESHOLD = 3;
	private static final int NUMBER_OF_UNIQUE_OCCURRENCES_THRESHOLD = 2;
	private final NLPediaLogger logger = new NLPediaLogger(OccurrenceFilter.class);
	
	@Override
	public void filterPattern(PatternMapping patternMapping) {

		for ( Pattern p : patternMapping.getPatterns() ) {
			
			// skip this evaluation, because it was characterized as not suitable in a previous evaluation
			if ( p.isUseForPatternEvaluation() ) {
				
				// this patterns is possibly random, so discard its
				p.setUseForPatternEvaluation(
						
						p.getNumberOfOccurrences() 
						>= 
						OccurrenceFilter.NUMBER_OF_OCCURRENCES_THRESHOLD 
				);
				
				int counter = 0;
				
				if ( p.isUseForPatternEvaluation() ) {
					
					Map<String,Integer> learnedFrom = p.getLearnedFrom();
					for (Entry<String,Integer> entry : learnedFrom.entrySet()) {
						
						if ( entry.getValue() > OccurrenceFilter.NUMBER_OF_UNIQUE_OCCURRENCES_THRESHOLD ) counter++;
					}
					
					if ( counter < 1 ) p.setUseForPatternEvaluation(false);
				}
			}
		}
	}
}
