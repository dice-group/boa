/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.entity.pattern.evaluation.impl;

import de.uni_leipzig.simba.boa.backend.configuration.Initializeable;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.evaluation.PatternEvaluator;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;


/**
 * @author Daniel Gerber
 *
 */
public class PatternLengthEvaluator extends Initializeable implements PatternEvaluator {

	private final NLPediaLogger logger		= new NLPediaLogger(PatternLengthEvaluator.class);
	private final int maxPatternChunkLength = new Integer(NLPediaSettings.getInstance().getSetting("maxPatternLenght")).intValue();
	private final int minPatternChunkLenght = 3;
	
	public PatternLengthEvaluator() {}
	
	/**
	 * 
	 */
	@Override public void initialize() {
		
		// nothing to be done here
	}
	
	/**
	 * Evaluates a pattern mapping if the length of its pattern length is suitable. 
	 * This means, if a pattern does only contain fewer tokens than minPatternChunkLenght 
	 * or more tokens then maxPatternChunkLength it changes the field of the pattern 
	 * for evaluation to false.
	 * 
	 * Note that this method does not propagate the new information to the database.
	 * 
	 * @param patternMapping the mapping whose pattern should be checked
	 */
	@Override public void evaluatePattern(PatternMapping patternMapping) {
		
		for ( Pattern p : patternMapping.getPatterns() ) {
			
			// skip this evaluation, because it was characterized as not suitable in a previous evaluation
			if ( p.isUseForPatternEvaluation() ) {
				
				String[] naturalLanguageRepresentation = p.getNaturalLanguageRepresentation().substring(0, p.getNaturalLanguageRepresentation().length() - 3).substring(3).trim().split(" ");
				
				// the number of chunks (words get seperated at " ") needs to smaller then the configured max value
				p.setUseForPatternEvaluation(
						
						naturalLanguageRepresentation.length 
						<= 
						this.maxPatternChunkLength 
						&&
						naturalLanguageRepresentation.length
						>=
						this.minPatternChunkLenght 
				);
				
				if ( !p.isUseForPatternEvaluation() ) {
					
					System.out.println("Pattern " + p.getNaturalLanguageRepresentation() + " is too long or too short");
					this.logger.debug("Pattern " +  p.getNaturalLanguageRepresentation() + " is too long or too short");
				}
			}
		}
	}
}
