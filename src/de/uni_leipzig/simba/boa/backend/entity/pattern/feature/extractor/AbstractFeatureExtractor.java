package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl.Feature;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;

/**
 * 
 * @author Daniel Gerber
 */
public abstract class AbstractFeatureExtractor implements FeatureExtractor {

    protected List<Feature> handeledFeatures = new ArrayList<Feature>();
    protected Set<PatternMapping> mappings = new HashSet<PatternMapping>();
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

	/**
	 * @return the mappings
	 */
    @Override
	public Set<PatternMapping> getPatternMappings() {
		return mappings;
	}

	/**
	 * @param mappings the mappings to set
	 */
    @Override
	public void setPatternMappings(Set<PatternMapping> mappings) {
		this.mappings = mappings;
	}
}
