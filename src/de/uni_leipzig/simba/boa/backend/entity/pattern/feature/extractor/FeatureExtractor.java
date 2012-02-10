package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor;

import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.concurrent.PatternMappingPatternPair;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl.Feature;

/**
 * 
 * @author Daniel Gerber
 */
public interface FeatureExtractor {

	/**
	 * 
	 * @param pair
	 */
	public void score(PatternMappingPatternPair pair);

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
