/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.machinelearning;

import java.io.File;

import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;


/**
 * @author gerb
 *
 */
public interface MachineLearningTool {

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
    public void train(File trainFile, File evalFile, File networkFile, int nFoldCrossValidation);
}
