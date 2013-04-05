package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
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
    	
        return this.handeledFeatures;
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
    
    /**
	 * 
	 * @param pattern
	 * @param featureName
	 * @param value
	 * @param statistic
	 */
	protected void setValue(Pattern pattern, String featureName, Double value, SummaryStatistics statistic) {
		
		pattern.getFeatures().put(FeatureFactory.getInstance().getFeature(featureName), value);
		statistic.addValue(Double.valueOf(value));
	}
	
	protected void setValue(Pattern pattern, String featureName, double value) {
		
		pattern.getFeatures().put(FeatureFactory.getInstance().getFeature(featureName), value);
	}
}
