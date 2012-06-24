package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor;

import java.util.List;

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
    
    /**
     * 
     * @return
     */
    public boolean isActivated();
    
    /**
     * 
     * @param activated
     */
    public void setActivated(boolean activated);
}
