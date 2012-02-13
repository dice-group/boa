package de.uni_leipzig.simba.boa.backend.concurrent;

import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;

/**
 * 
 */

/**
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 *
 */
public class PatternMappingPatternPair {

	private PatternMapping mapping;
	private Pattern pattern;
	
	public PatternMappingPatternPair(PatternMapping mapping, Pattern pattern) {
		
		this.setMapping(mapping);
		this.setPattern(pattern);
	}

	/**
	 * @return the mapping
	 */
	public PatternMapping getMapping() {

		return mapping;
	}

	/**
	 * @param mapping the mapping to set
	 */
	public void setMapping(PatternMapping mapping) {

		this.mapping = mapping;
	}

	/**
	 * @return the pattern
	 */
	public Pattern getPattern() {

		return pattern;
	}

	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(Pattern pattern) {

		this.pattern = pattern;
	}
}
