package de.uni_leipzig.simba.boa.backend.entity.pattern.evaluation.impl;

import de.uni_leipzig.simba.boa.backend.configuration.Initializeable;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.evaluation.PatternEvaluator;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;


/**
 * This class filters all patterns with begin with a certain string, for example "and"
 * because they do not represent a "meaningful" natural language representation
 * 
 * @author Daniel Gerber
 */
public class StartsWithEvaluator extends Initializeable implements PatternEvaluator {

	private final NLPediaLogger logger = new NLPediaLogger(StartsWithEvaluator.class);

	@Override
	public void initialize() {

		// nothing to do here		
	}
	
	/**
	 * 
	 */
	@Override public void evaluatePattern(PatternMapping patternMapping) {

		for ( Pattern p : patternMapping.getPatterns() ) {
		
			if ( p.isUseForPatternEvaluation() ) {
				
				String pattern = p.getNaturalLanguageRepresentation();
				pattern = p.getNaturalLanguageRepresentation().substring(0, p.getNaturalLanguageRepresentation().length() - 3).substring(3).trim();

				// patterns with "and" at the beginning to not have a real meaning
				p.setUseForPatternEvaluation(!pattern.startsWith("and ") && !pattern.startsWith("and,"));
				
				if ( !p.isUseForPatternEvaluation() ) {
					
					System.out.println("Pattern " + p.getNaturalLanguageRepresentation() + " begins with \"and\".");
					this.logger.debug("Pattern " + p.getNaturalLanguageRepresentation() + " begins with \"and\".");
				}

			}
		}
	}
}
