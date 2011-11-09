package de.uni_leipzig.simba.boa.backend.entity.pattern.feature;

import java.util.List;

import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;

/**
 * 
 * @author Daniel Gerber
 */
public interface Feature {

	/**
	 * 
	 * @param mapping
	 */
	public void score(List<PatternMapping> mappings);
	
	/**
	 * 
	 * @param mapping
	 */
	public void scoreMapping(PatternMapping mapping);
}
