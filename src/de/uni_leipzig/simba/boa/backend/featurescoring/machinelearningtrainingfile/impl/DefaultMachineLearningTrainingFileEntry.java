/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.featurescoring.machinelearningtrainingfile.impl;

import java.util.List;

import de.uni_leipzig.simba.boa.backend.featurescoring.machinelearningtrainingfile.entry.AbstractMachineLearningTrainingFileEntry;


/**
 * @author gerb
 *
 */
public class DefaultMachineLearningTrainingFileEntry extends AbstractMachineLearningTrainingFileEntry {

    /**
     * Nothing to do here. Because most of standard impl is done in the abstract super class.
     * 
     * @param patternMappingUri
     * @param naturalLanguageRepresentation
     * @param featureVector
     * @param annotation
     */
    public DefaultMachineLearningTrainingFileEntry(String patternMappingUri, String naturalLanguageRepresentation, List<Double> featureVector, String pos, Boolean annotation) {

        super(patternMappingUri, naturalLanguageRepresentation, featureVector, pos, annotation);
    }
    
    // used for spring
    public DefaultMachineLearningTrainingFileEntry() {}
}
