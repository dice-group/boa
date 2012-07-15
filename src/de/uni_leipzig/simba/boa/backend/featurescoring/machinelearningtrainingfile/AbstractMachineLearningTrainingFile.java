/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.featurescoring.machinelearningtrainingfile;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.danielgerber.format.OutputFormatter;
import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.featurescoring.machinelearningtrainingfile.entry.MachineLearningTrainingFileEntry;


/**
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public abstract class AbstractMachineLearningTrainingFile implements MachineLearningTrainingFile {

    protected List<MachineLearningTrainingFileEntry> annotatedEntries;
    protected List<MachineLearningTrainingFileEntry> notAnnotatedEntries;
    protected List<String> featureNames;
    
    // used for spring
    public AbstractMachineLearningTrainingFile() {
        
//        this.annotatedEntries = new ArrayList<MachineLearningTrainingFileEntry>();
//        this.notAnnotatedEntries = new ArrayList<MachineLearningTrainingFileEntry>();
    }
    
    /**
     * 
     * @param annotatedAntries
     */
    public AbstractMachineLearningTrainingFile(List<String> featureNames, List<MachineLearningTrainingFileEntry> entries) {
        
        this.featureNames           = featureNames;
        this.annotatedEntries       = new ArrayList<MachineLearningTrainingFileEntry>();
        this.notAnnotatedEntries    = new ArrayList<MachineLearningTrainingFileEntry>();
        
        for (MachineLearningTrainingFileEntry entry : entries) this.addEntry(entry);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        
        builder.append(StringUtils.join(this.featureNames, "\t") + Constants.NEW_LINE_SEPARATOR);
        
        // one entry corresponds to one line
        for (MachineLearningTrainingFileEntry entry : this.annotatedEntries) {
            // first the features tab separated
            for (Double featureValue : entry.getFeatures()) builder.append(OutputFormatter.format((featureValue), "0.00000")).append(Constants.FEATURE_FILE_COLUMN_SEPARATOR);
            // the part which need to be edited by a human, values to enter are either 1 or 0
            // this is also used to indicate the a pattern should be used for learning the machine learning tool (if not MANUAL then use it for learning)
            builder.append(entry.getAnnotation() == null ? "MANUAL" : entry.getAnnotation() ? 1 : 0).append(Constants.FEATURE_FILE_COLUMN_SEPARATOR);
            // add the uri of the pattern mapping
            builder.append(entry.getPatternMappingUri()).append(Constants.FEATURE_FILE_COLUMN_SEPARATOR);
            // add the pattern's natural language representations
            builder.append(entry.getNaturalLanguageRepresentation()).append(Constants.FEATURE_FILE_COLUMN_SEPARATOR);
            // close an entry with the new line
            builder.append(Constants.NEW_LINE_SEPARATOR);
        }
        // one entry corresponds to one line
        for (MachineLearningTrainingFileEntry entry : this.notAnnotatedEntries) {
            
            // first the features tab separated
            for (Double featureValue : entry.getFeatures()) builder.append(OutputFormatter.format((featureValue), "0.00000")).append(Constants.FEATURE_FILE_COLUMN_SEPARATOR);
            // the part which need to be edited by a human, values to enter are either 1 or 0
            // this is also used to indicate the a pattern should be used for learning the machine learning tool (if not MANUAL then use it for learning)
            builder.append(entry.getAnnotation() == null ? "MANUAL" : entry.getAnnotation() ? 1 : 0).append(Constants.FEATURE_FILE_COLUMN_SEPARATOR);
            // add the uri of the pattern mapping
            builder.append(entry.getPatternMappingUri()).append(Constants.FEATURE_FILE_COLUMN_SEPARATOR);
            // add the pattern's natural language representations
            builder.append(entry.getNaturalLanguageRepresentation()).append(Constants.FEATURE_FILE_COLUMN_SEPARATOR);
            
            if ( this.notAnnotatedEntries.indexOf(entry) != this.notAnnotatedEntries.size() - 1 ) {
                
                // close an entry with the new line
                builder.append(Constants.NEW_LINE_SEPARATOR);
            }
        }
        
        return builder.toString();
    }

    /**
     * 
     * @param uri
     * @param naturalLanguageRepresentation
     * @return
     */
    public MachineLearningTrainingFileEntry getEntry(String uri, String naturalLanguageRepresentation) {

        // simply go through all annotatedAntries and look if the two properties match
        for ( MachineLearningTrainingFileEntry entry : this.annotatedEntries ) 
            if ( entry.getPatternMappingUri().equals(uri) && entry.getNaturalLanguageRepresentation().equals(naturalLanguageRepresentation)) 
                return entry;
        
        for ( MachineLearningTrainingFileEntry entry : this.notAnnotatedEntries ) 
            if ( entry.getPatternMappingUri().equals(uri) && entry.getNaturalLanguageRepresentation().equals(naturalLanguageRepresentation)) 
                return entry;

        return null;
    }

    /**
     * 
     * @param neuronalNetworkTrainingFileEntry
     */
    public void addEntry(MachineLearningTrainingFileEntry neuronalNetworkTrainingFileEntry) {
        
        if ( neuronalNetworkTrainingFileEntry.getAnnotation() != null ) 
            this.annotatedEntries.add(neuronalNetworkTrainingFileEntry);
        else
            this.notAnnotatedEntries.add(neuronalNetworkTrainingFileEntry);
    }

    
    /**
     * @return the annotatedAntries
     */
    public List<MachineLearningTrainingFileEntry> getAnnotatedEntries() {
    
        return this.annotatedEntries;
    }

    
    /**
     * @param annotatedAntries the annotatedAntries to set
     */
    public void setAnnotatedAntries(List<MachineLearningTrainingFileEntry> annotatedAntries) {
    
        this.annotatedEntries = annotatedAntries;
    }

    
    /**
     * @return the notAnnotatedEntries
     */
    public List<MachineLearningTrainingFileEntry> getNotAnnotatedEntries() {
    
        return this.notAnnotatedEntries;
    }

    
    /**
     * @param notAnnotatedEntries the notAnnotatedEntries to set
     */
    public void setNotAnnotatedEntries(List<MachineLearningTrainingFileEntry> notAnnotatedEntries) {
    
        this.notAnnotatedEntries = notAnnotatedEntries;
    }
    
    @Override
    public void setFeatureNames(List<String> featureNames) {

        this.featureNames = featureNames;
    }

    @Override
    public List<String> getFeatureNames() {

        return this.featureNames;
    }
}
