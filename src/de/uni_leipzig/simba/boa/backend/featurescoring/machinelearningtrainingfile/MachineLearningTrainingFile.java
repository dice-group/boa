/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.featurescoring.machinelearningtrainingfile;

import java.util.List;

import de.uni_leipzig.simba.boa.backend.featurescoring.machinelearningtrainingfile.entry.MachineLearningTrainingFileEntry;


/**
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public interface MachineLearningTrainingFile {

    /**
     * 
     * @return
     */
    public abstract String toString();
    
    /**
     * returns this file in arff (weka) format
     * 
     * @return
     */
    public abstract String toArff();
    
    /**
     * 
     * @param uri
     * @param naturalLanguageRepresentation
     * @return
     */
    public abstract MachineLearningTrainingFileEntry getEntry(String uri, String naturalLanguageRepresentation);
    
    /**
     * 
     * @param neuronalNetworkTrainingFileEntry
     */
    public abstract void addEntry(MachineLearningTrainingFileEntry neuronalNetworkTrainingFileEntry);

    
    /**
     * @return the annotatedAntries
     */
    public abstract List<MachineLearningTrainingFileEntry> getAnnotatedEntries();

    
    /**
     * @param annotatedAntries the annotatedAntries to set
     */
    public abstract void setAnnotatedAntries(List<MachineLearningTrainingFileEntry> annotatedAntries);

    
    /**
     * @return the notAnnotatedEntries
     */
    public abstract List<MachineLearningTrainingFileEntry> getNotAnnotatedEntries();

    
    /**
     * @param notAnnotatedEntries the notAnnotatedEntries to set
     */
    public abstract void setNotAnnotatedEntries(List<MachineLearningTrainingFileEntry> notAnnotatedEntries);
    
    /**
     * 
     * @param featureNames
     */
    public abstract void setFeatureNames(List<String> featureNames);
    
    /**
     * 
     * @return
     */
    public abstract List<String> getFeatureNames();
}
