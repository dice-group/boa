/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.featurescoring;

import java.util.List;

import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureHelper;


/**
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public class NeuronalNetworkTrainingFileEntry {

    private List<Double> features;
    private Boolean annotation;
    private String patternMappingUri;
    private String naturalLanguageRepresentation;
    
    public NeuronalNetworkTrainingFileEntry(String patternMappingUri, String naturalLanguageRepresentation, List<Double> featureVector, Boolean annotation) {

        this.patternMappingUri              = patternMappingUri;
        this.naturalLanguageRepresentation  = naturalLanguageRepresentation;
        this.annotation                     = annotation;
        this.features                       = featureVector;
    }
    
    /**
     * @return the features
     */
    public List<Double> getFeatures() {
    
        return features;
    }

    /**
     * @param features the features to set
     */
    public void setFeatures(List<Double> features) {
    
        this.features = features;
    }
    
    /**
     * @return the annotation
     */
    public Boolean getAnnotation() {
    
        return annotation;
    }

    /**
     * @param annotation the annotation to set
     */
    public void setAnnotation(Boolean annotation) {
    
        this.annotation = annotation;
    }

    
    /**
     * @return the patternMappingUri
     */
    public String getPatternMappingUri() {
    
        return patternMappingUri;
    }

    
    /**
     * @param patternMappingUri the patternMappingUri to set
     */
    public void setPatternMappingUri(String patternMappingUri) {
    
        this.patternMappingUri = patternMappingUri;
    }

    
    /**
     * @return the naturalLanguageRepresentation
     */
    public String getNaturalLanguageRepresentation() {
    
        return naturalLanguageRepresentation;
    }

    
    /**
     * @param naturalLanguageRepresentation the naturalLanguageRepresentation to set
     */
    public void setNaturalLanguageRepresentation(String naturalLanguageRepresentation) {
    
        this.naturalLanguageRepresentation = naturalLanguageRepresentation;
    }
}
