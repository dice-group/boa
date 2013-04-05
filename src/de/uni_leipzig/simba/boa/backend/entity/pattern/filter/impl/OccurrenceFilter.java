package de.uni_leipzig.simba.boa.backend.entity.pattern.filter.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.GeneralizedPattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.filter.PatternFilter;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;

/**
 * 
 * @author Daniel Gerber
 */
public class OccurrenceFilter implements PatternFilter {

//	private final NLPediaLogger logger = new NLPediaLogger(OccurrenceFilter.class);
	
	@Override
	public void filterPattern(PatternMapping patternMapping) {

		// collect all patterns which do not fit the filters, can't modify list while iteration
		Set<GeneralizedPattern> correctPatterns = new HashSet<GeneralizedPattern>();
		
		for ( GeneralizedPattern p : patternMapping.getGeneralizedPatterns() ) {
			
			// skip this evaluation, because it was characterized as not suitable in a previous evaluation
			if ( p.isUseForPatternEvaluation() ) {
				
				// discard only patterns which might occur randomly
				p.setUseForPatternEvaluation(
						
						p.getNumberOfOccurrences() >= NLPediaSettings.getIntegerSetting("number.of.occurrence.threshold") 
				);
				
				int counter = 0;
				
				if ( p.isUseForPatternEvaluation() ) {
					
					// look if there are pairs available between the pattern occurs more than NUMBER_OF_UNIQUE_OCCURRENCES_THRESHOLD
					Map<String,Integer> learnedFrom = p.getLearnedFrom();
					for (Entry<String,Integer> entry : learnedFrom.entrySet()) {
						
						if ( entry.getValue() >= NLPediaSettings.getIntegerSetting("number.of.unique.occurrence.threshold") ) counter++;
					}
					
					if ( counter < 1 ) p.setUseForPatternEvaluation(false);
					
					if ( p.isUseForPatternEvaluation() ) {
						
						// look if there are more than NUMBER_OF_LEARNED_PAIRS pairs the pattern was learned from
						p.setUseForPatternEvaluation(
							
								learnedFrom.size() >= NLPediaSettings.getIntegerSetting("number.of.learned.pairs")
						);
					}
				}
			}
			// check after filter were applied if pattern is still suitable
			if ( p.isUseForPatternEvaluation() ) {
				
				correctPatterns.add(p);
			}
		}
		// replace them with the ones which survived the filtering
		patternMapping.setGeneralizedPatterns(correctPatterns);
	}
	
//	public static void main(String[] args) {
//		
//		String nlr = "was his her he fault World in 9 March 2012 the";
//		System.out.println(nlr + ": " + PatternMappingManager.generalize(nlr));
//		nlr = "when he";
//		System.out.println(nlr + ": " + PatternMappingManager.generalize(nlr));
//		nlr = "when he ,";
//		System.out.println(nlr + ": " + PatternMappingManager.generalize(nlr));
//		nlr = "written by Steve Smith and";
//		System.out.println(nlr + ": " + PatternMappingManager.generalize(nlr));
//		nlr = "years old ,";
//		System.out.println(nlr + ": " + PatternMappingManager.generalize(nlr));
//		nlr = ", Baden-Württemberg";
//		System.out.println(nlr + ": " + PatternMappingManager.generalize(nlr));
//		nlr = ", Baden-Württemberg Baden-Württemberg";
//		System.out.println(nlr + ": " + PatternMappingManager.generalize(nlr));
//		nlr = ", Baden-Württemberg in Baden-Württemberg";
//		System.out.println(nlr + ": " + PatternMappingManager.generalize(nlr));
//		nlr = ", Baden-Württemberg in Baden-Württemberg ``";
//		System.out.println(nlr + ": " + PatternMappingManager.generalize(nlr));
//		nlr = ", Baden-Württemberg in Baden-Württemberg , ``";
//		System.out.println(nlr + ": " + PatternMappingManager.generalize(nlr));
//		nlr = "`` , Baden-Württemberg in Baden-Württemberg ``";
//		System.out.println(nlr + ": " + PatternMappingManager.generalize(nlr));
//	}
}
