package de.uni_leipzig.simba.boa.backend.entity.pattern.evaluation;

import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;

/**
 * Interface for all evaluators.
 * 
 * @author Daniel Gerber
 */
public interface PatternEvaluator {

	/**
	 * Found pattern mappings need to be evaluated, e.g. pattern mappings 
	 * with patterns which do not contain a certain amount of words or 
	 * only contain stop-words need to be filtered. Furthermore checked 
	 * patterns need to be evaluated, if they deliver suitable results. 
	 * This method should be used to check patterns for a given characteristic 
	 * like the examples above. 
	 * 
	 * @param pattern mapping - The pattern mapping to be evaluated
	 */
	public void evaluatePattern(PatternMapping patternMapping);
}
