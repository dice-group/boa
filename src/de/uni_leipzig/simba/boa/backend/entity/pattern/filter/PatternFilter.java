package de.uni_leipzig.simba.boa.backend.entity.pattern.filter;

import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;

/**
 * Interface for all evaluators.
 * 
 * @author Daniel Gerber
 */
public interface PatternFilter {

	/**
	 * Found pattern NAMED_ENTITY_TAG_MAPPINGS need to be filtered, e.g. pattern NAMED_ENTITY_TAG_MAPPINGS 
	 * with patterns which do not contain a certain amount of cleanWords or 
	 * only contain stop-words need to be filtered. Furthermore checked 
	 * patterns need to be evaluated, if they deliver suitable results. 
	 * This method should be used to check patterns for a given characteristic 
	 * like the examples above. 
	 * 
	 * @param pattern mapping - The pattern mapping to be filter
	 */
	public void filterPattern(PatternMapping patternMapping);
}
