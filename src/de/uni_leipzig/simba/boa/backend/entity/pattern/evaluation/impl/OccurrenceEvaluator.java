package de.uni_leipzig.simba.boa.backend.entity.pattern.evaluation.impl;

import java.util.Map;

import java.util.Map.Entry;

import de.uni_leipzig.simba.boa.backend.configuration.Initializeable;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.evaluation.PatternEvaluator;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;


public class OccurrenceEvaluator extends Initializeable implements PatternEvaluator {

	private static final int NUMBER_OF_OCCURRENCES_THRESHOLD = 2;
	private static final int NUMBER_OF_UNIQUE_OCCURRENCES_THRESHOLD = 2;
	private final NLPediaLogger logger = new NLPediaLogger(OccurrenceEvaluator.class);
	
	@Override
	public void evaluatePattern(PatternMapping patternMapping) {

		for ( Pattern p : patternMapping.getPatterns() ) {
			
			// skip this evaluation, because it was characterized as not suitable in a previous evaluation
			if ( p.isUseForPatternEvaluation() ) {
				
				// this patterns is possibly random, so discard its
				p.setUseForPatternEvaluation(
						
						p.getNumberOfOccurrences() 
						>= 
						OccurrenceEvaluator.NUMBER_OF_OCCURRENCES_THRESHOLD 
				);
				
				int counter = 0;
				
				if ( p.isUseForPatternEvaluation() ) {
					
					Map<String,Integer> learnedFrom = p.getLearnedFrom();
					for (Entry<String,Integer> entry : learnedFrom.entrySet()) {
						
						if ( entry.getValue() > OccurrenceEvaluator.NUMBER_OF_UNIQUE_OCCURRENCES_THRESHOLD ) counter++;
					}
					
					if ( counter < 1 ) p.setUseForPatternEvaluation(false);
					else {
						
						
					}
				}
				
				if ( !p.isUseForPatternEvaluation() ) {
					
					if ( counter <= 1 ) {
						
						System.out.println("Pattern " + p.getNaturalLanguageRepresentation() + " does not occur often enough with the same entites.");
						this.logger.debug("Pattern " +  p.getNaturalLanguageRepresentation() + " does not occur often enough with the same entites.");
					}
					else {
						
						System.out.println("Pattern " + p.getNaturalLanguageRepresentation() + " does not occur often enough.");
						this.logger.debug("Pattern " +  p.getNaturalLanguageRepresentation() + " does not occur often enough.");
					}
				}
			}
		}
	}

	@Override
	public void initialize() {

		// nothing to do here
	}
}
