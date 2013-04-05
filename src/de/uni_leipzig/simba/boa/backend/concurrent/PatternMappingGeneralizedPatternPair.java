package de.uni_leipzig.simba.boa.backend.concurrent;

import de.uni_leipzig.simba.boa.backend.entity.pattern.GeneralizedPattern;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;

/**
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 *
 */
public class PatternMappingGeneralizedPatternPair {

	private PatternMapping mapping;
	private GeneralizedPattern pattern;
	
	public PatternMappingGeneralizedPatternPair(PatternMapping mapping, GeneralizedPattern pattern) {
		
		this.setMapping(mapping);
		this.setGeneralizedPattern(pattern);
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
	public GeneralizedPattern getGeneralizedPattern() {

		return pattern;
	}

	/**
	 * @param pattern the pattern to set
	 */
	public void setGeneralizedPattern(GeneralizedPattern pattern) {

		this.pattern = pattern;
	}
}
