package de.uni_leipzig.simba.boa.backend.featureextraction;

import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;

/**
 * 
 */

/**
 * @author gerb
 *
 */
public class FeatureExtractionPair {

	private PatternMapping mapping;
	private Pattern pattern;
	
	public FeatureExtractionPair(PatternMapping mapping, Pattern pattern) {
		
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
