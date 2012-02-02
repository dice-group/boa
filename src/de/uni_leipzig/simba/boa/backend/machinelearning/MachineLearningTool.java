/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.machinelearning;

import java.io.File;

import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.featurescoring.machinelearningtrainingfile.MachineLearningTrainingFile;
import de.uni_leipzig.simba.boa.backend.tool.BoaTool;


/**
 * @author gerb
 *
 */
public interface MachineLearningTool extends BoaTool {

    /**
     * 
     * @param mapping
     * @param pattern
     * @return
     */
    public double getScore(PatternMapping mapping, Pattern pattern);
    
    /**
     * 
     * @param learnFile
     * @param evalFile
     * @param networkFile
     * @param nFoldCrossValidation
     * @return
     */
    public void train(MachineLearningTrainingFile trainFile, File evalFile, File networkFile, int nFoldCrossValidation);

    /**
     * Loads the serialized model from the default position. If no such model exist
     * this method should check if annotated test data is available and if so
     * it should start the machine learning tools learning process.
     */
    public void loadModel();

    /**
     * 
     * @param machineLearningTrainingFile
     */
    public void setMachineLearningTrainingFile(MachineLearningTrainingFile machineLearningTrainingFile);
    
    /**
     * 
     * @return
     */
    public MachineLearningTrainingFile getMachineLearningTrainingFile();
}
