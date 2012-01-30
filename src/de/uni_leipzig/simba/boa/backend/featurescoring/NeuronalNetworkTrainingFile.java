/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.featurescoring;

import java.util.ArrayList;
import java.util.List;

import de.danielgerber.format.OutputFormatter;
import de.uni_leipzig.simba.boa.backend.Constants;


/**
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public class NeuronalNetworkTrainingFile {

    private List<NeuronalNetworkTrainingFileEntry> annotatedEntries;
    private List<NeuronalNetworkTrainingFileEntry> notAnnotatedEntries;
    
    /**
     * 
     * @param annotatedAntries
     */
    public NeuronalNetworkTrainingFile(List<NeuronalNetworkTrainingFileEntry> entries) {
        
        this.annotatedEntries = new ArrayList<NeuronalNetworkTrainingFileEntry>();
        this.notAnnotatedEntries = new ArrayList<NeuronalNetworkTrainingFileEntry>();
        
        for (NeuronalNetworkTrainingFileEntry entry : entries) this.addEntry(entry);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        
        // one entry corresponds to one line
        for (NeuronalNetworkTrainingFileEntry entry : this.annotatedEntries) {
            
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
        for (NeuronalNetworkTrainingFileEntry entry : this.notAnnotatedEntries) {
            
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
    public NeuronalNetworkTrainingFileEntry getEntry(String uri, String naturalLanguageRepresentation) {

        // simply go through all annotatedAntries and look if the two properties match
        for ( NeuronalNetworkTrainingFileEntry entry : this.annotatedEntries ) 
            if ( entry.getPatternMappingUri().equals(uri) && entry.getNaturalLanguageRepresentation().equals(naturalLanguageRepresentation)) 
                return entry;
        
        for ( NeuronalNetworkTrainingFileEntry entry : this.notAnnotatedEntries ) 
            if ( entry.getPatternMappingUri().equals(uri) && entry.getNaturalLanguageRepresentation().equals(naturalLanguageRepresentation)) 
                return entry;

        return null;
    }

    /**
     * 
     * @param neuronalNetworkTrainingFileEntry
     */
    public void addEntry(NeuronalNetworkTrainingFileEntry neuronalNetworkTrainingFileEntry) {
        
        if ( neuronalNetworkTrainingFileEntry.getAnnotation() != null ) 
            this.annotatedEntries.add(neuronalNetworkTrainingFileEntry);
        else
            this.notAnnotatedEntries.add(neuronalNetworkTrainingFileEntry);
    }

    
    /**
     * @return the annotatedAntries
     */
    public List<NeuronalNetworkTrainingFileEntry> getAnnotatedAntries() {
    
        return this.annotatedEntries;
    }

    
    /**
     * @param annotatedAntries the annotatedAntries to set
     */
    public void setAnnotatedAntries(List<NeuronalNetworkTrainingFileEntry> annotatedAntries) {
    
        this.annotatedEntries = annotatedAntries;
    }

    
    /**
     * @return the notAnnotatedEntries
     */
    public List<NeuronalNetworkTrainingFileEntry> getNotAnnotatedEntries() {
    
        return this.notAnnotatedEntries;
    }

    
    /**
     * @param notAnnotatedEntries the notAnnotatedEntries to set
     */
    public void setNotAnnotatedEntries(List<NeuronalNetworkTrainingFileEntry> notAnnotatedEntries) {
    
        this.notAnnotatedEntries = notAnnotatedEntries;
    }
}
