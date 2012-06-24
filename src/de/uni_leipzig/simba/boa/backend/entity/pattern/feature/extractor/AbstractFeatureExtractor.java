package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor;

import java.util.ArrayList;
import java.util.List;

import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl.Feature;

/**
 * 
 * @author Daniel Gerber
 */
public abstract class AbstractFeatureExtractor implements FeatureExtractor {

    protected List<Feature> handeledFeatures = new ArrayList<Feature>();
    protected boolean activated;

    /**
     * @return the activated
     */
    public boolean isActivated() {
    
        return activated;
    }

    /**
     * @param activated the activated to set
     */
    public void setActivated(boolean activated) {
    
        this.activated = activated;
    }

    /**
     * @return the handeledFeatures
     */
    public List<Feature> getHandeledFeatures() {
    
        return handeledFeatures;
    }

    /**
     * @param handeledFeatures the handeledFeatures to set
     */
    public void setHandeledFeatures(List<Feature> handeledFeatures) {
    
        this.handeledFeatures = handeledFeatures;
    }
}
