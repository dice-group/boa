package de.uni_leipzig.simba.boa.backend.entity.pattern.feature;

import de.uni_leipzig.simba.boa.backend.featureextraction.FeatureExtractionPair;

/**
 * 
 * @author Daniel Gerber
 */
public interface Feature {

	/**
	 * 
	 * @param pair
	 */
	public void score(FeatureExtractionPair pair);
}
