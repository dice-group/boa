/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.featurescoring.machinelearningtrainingfile.entry;

import java.util.List;


/**
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 *
 */
public interface MachineLearningTrainingFileEntry {

    /**
     * @return the features
     */
    public abstract List<Double> getFeatures();

    /**
     * @param features the features to set
     */
    public abstract void setFeatures(List<Double> features);
    
    /**
     * @return the annotation
     */
    public abstract Boolean getAnnotation();

    /**
     * @param annotation the annotation to set
     */
    public abstract void setAnnotation(Boolean annotation);
    
    /**
     * @return the patternMappingUri
     */
    public abstract String getPatternMappingUri();    
    /**
     * @param patternMappingUri the patternMappingUri to set
     */
    public abstract void setPatternMappingUri(String patternMappingUri);
    
    /**
     * @return the naturalLanguageRepresentation
     */
    public abstract String getNaturalLanguageRepresentation();
    
    /**
     * @param naturalLanguageRepresentation the naturalLanguageRepresentation to set
     */
    public abstract void setNaturalLanguageRepresentation(String naturalLanguageRepresentation);
    
    /**
     * @return the naturalLanguageRepresentation
     */
    public abstract String getNaturalLanguageRepresentationPartOfSpeech();
    
    /**
     * @param naturalLanguageRepresentation the naturalLanguageRepresentation to set
     */
    public abstract void setNaturalLanguageRepresentationPartOfSpeech(String pos);
}
