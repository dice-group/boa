package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor;

import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl.Feature;
import de.uni_leipzig.simba.boa.backend.featureextraction.FeatureExtractionPair;

/**
 * 
 * @author Daniel Gerber
 */
public interface FeatureExtractor {

	/**
	 * 
	 * @param pair
	 */
	public void score(FeatureExtractionPair pair);

	/**
	 * 
	 * @return
	 */
    public List<Feature> getHandeledFeatures();
    
    /**
     * 
     * @param handeledFeatures
     */
    public void setHandeledFeatures(List<Feature> handeledFeatures);
}
