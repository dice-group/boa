/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.featurescoring.machinelearningtrainingfile.impl;

import java.util.List;

import de.uni_leipzig.simba.boa.backend.featurescoring.machinelearningtrainingfile.AbstractMachineLearningTrainingFile;
import de.uni_leipzig.simba.boa.backend.featurescoring.machinelearningtrainingfile.entry.MachineLearningTrainingFileEntry;


/**
 * @author gerb
 *
 */
public class DefaultMachineLearningTrainingFile extends AbstractMachineLearningTrainingFile {

    /**
     * Nothing to do here. Because most of standard impl is done in the abstract super class.
     * 
     * @param entries a set of entries either annotated or not annotated
     */
    public DefaultMachineLearningTrainingFile(List<String> featureNames, List<MachineLearningTrainingFileEntry> entries) {
        super(featureNames, entries);
    }
    
    // used for spring
    public DefaultMachineLearningTrainingFile() {}
}
