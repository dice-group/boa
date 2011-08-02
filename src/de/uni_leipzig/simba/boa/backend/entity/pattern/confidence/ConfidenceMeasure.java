package de.uni_leipzig.simba.boa.backend.entity.pattern.confidence;

import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;

/**
 * 
 * @author Daniel Gerber
 */
public interface ConfidenceMeasure {

	/**
	 * 
	 * @param mapping
	 */
	public void measureConfidence(PatternMapping mapping);
}
