package de.uni_leipzig.simba.boa.backend.entity.pattern.filter.impl;

import java.util.Map;
import java.util.Map.Entry;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.filter.PatternFilter;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

/**
 * 
 * @author Daniel Gerber
 */
public class OccurrenceFilter implements PatternFilter {

	private static final int NUMBER_OF_OCCURRENCES_THRESHOLD = Integer.valueOf(NLPediaSettings.getInstance().getSetting("number.of.occurrence.threshold"));
	private static final int NUMBER_OF_UNIQUE_OCCURRENCES_THRESHOLD = Integer.valueOf(NLPediaSettings.getInstance().getSetting("number.of.unique.occurrence.threshold"));
	private static final int NUMBER_OF_LEARNED_PAIRS = Integer.valueOf(NLPediaSettings.getInstance().getSetting("number.of.learned.pairs"));
	
	private final NLPediaLogger logger = new NLPediaLogger(OccurrenceFilter.class);
	
	@Override
	public void filterPattern(PatternMapping patternMapping) {

		for ( Pattern p : patternMapping.getPatterns() ) {
			
			// skip this evaluation, because it was characterized as not suitable in a previous evaluation
			if ( p.isUseForPatternEvaluation() ) {
				
				// discard only patterns which might occur randomly
				p.setUseForPatternEvaluation(
						
						p.getNumberOfOccurrences() >= OccurrenceFilter.NUMBER_OF_OCCURRENCES_THRESHOLD 
				);
				
				int counter = 0;
				
				if ( p.isUseForPatternEvaluation() ) {
					
					// look if there are pairs available between the pattern occurs more than NUMBER_OF_UNIQUE_OCCURRENCES_THRESHOLD
					Map<String,Integer> learnedFrom = p.getLearnedFrom();
					for (Entry<String,Integer> entry : learnedFrom.entrySet()) {
						
						if ( entry.getValue() > OccurrenceFilter.NUMBER_OF_UNIQUE_OCCURRENCES_THRESHOLD ) counter++;
					}
					
					if ( counter < 1 ) p.setUseForPatternEvaluation(false);
					
					if ( p.isUseForPatternEvaluation() ) {
						
						// look if there are more than NUMBER_OF_LEARNED_PAIRS pairs the pattern was learned from
						p.setUseForPatternEvaluation(
							
								p.getLearnedFrom().size() >= OccurrenceFilter.NUMBER_OF_LEARNED_PAIRS
						);
					}
				}
			}
		}
	}
}
