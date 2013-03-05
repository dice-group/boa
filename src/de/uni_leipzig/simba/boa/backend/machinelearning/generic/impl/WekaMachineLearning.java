/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.machinelearning.generic.impl;

import java.io.File;
import java.io.IOException;

import cern.colt.Arrays;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import de.danielgerber.format.OutputFormatter;
import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;
import de.uni_leipzig.simba.boa.backend.featurescoring.machinelearningtrainingfile.MachineLearningTrainingFile;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.machinelearning.generic.GenericTool;

/**
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 *
 */
public class WekaMachineLearning implements GenericTool {

    private NLPediaLogger logger    = new NLPediaLogger(WekaMachineLearning.class);
    private Classifier classifier   = null;
    private Instances instances     = null;
    
    private MachineLearningTrainingFile trainingFile = null;
    private MachineLearningTrainingFile testFile = null;
    
    /**
     * 
     */
    public WekaMachineLearning() {
        
        init();
    }
    
    public WekaMachineLearning(MachineLearningTrainingFile trainingFile, MachineLearningTrainingFile testFile) {

        this.trainingFile = trainingFile;
        this.testFile = testFile;
        init();
    }
    
    private void init() {
        
        try {
            
            ArffLoader loader = new ArffLoader();
            loader.setFile(new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.MACHINE_LEARNING_TRAINING_PATH + "boa_weka.arff"));
            this.instances = loader.getStructure();
            this.instances.deleteAttributeAt(this.instances.numAttributes() -1); // we need to delete those two
            this.instances.deleteAttributeAt(this.instances.numAttributes() -1); // because we dont need them and weka is confused
            this.instances.setClassIndex(this.instances.numAttributes() - 3);
            this.loadModel();
        }
        catch (Exception e) {

            logger.error("Could not load stucture definition from weka learning file!", e);
            throw new RuntimeException("Could not load stucture definition from weka learning file!", e);
        }
    }
    
    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.machinelearning.MachineLearningTool#getScore(de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping, de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern)
     */
    @Override
    public double getScore(PatternMapping mapping, Pattern pattern) {

        Instance instance = new Instance(this.instances.numAttributes());
        instance.setDataset(this.instances);
        
        int index = 0;
        for (Double featureValue : pattern.buildNormalizedFeatureVector(mapping) ) 
            instance.setValue(this.instances.attribute(index++), featureValue);
                
        try {
        	
            return this.classifier.classifyInstance(instance);
        }
        catch (Exception e) {
            
            logger.error("Could not classify instance: " + instance, e);
            throw new RuntimeException("Could not classify instance: " + instance, e);
        }
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.machinelearning.MachineLearningTool#train(de.uni_leipzig.simba.boa.backend.featurescoring.machinelearningtrainingfile.MachineLearningTrainingFile, de.uni_leipzig.simba.boa.backend.featurescoring.machinelearningtrainingfile.MachineLearningTrainingFile, java.io.File, java.io.File, int)
     */
    @Override
    public void train(MachineLearningTrainingFile trainFile, MachineLearningTrainingFile testFile, File evalFile, File networkFile, int nFoldCrossValidation) {

        // This feature is not going to be implemented at the time
        // you should use the GUI of weka to create a model and then place
        // it in BOA_DATA_DIRECTORY/machinelearning/classifier
        throw new RuntimeException("Method not supported for Weka Machine Learning! Use Weka GUI to create Model!");
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.machinelearning.MachineLearningTool#loadModel()
     */
    @Override
    public void loadModel() {

        try {
            
            if ( this.classifier == null )
                this.classifier = (Classifier) weka.core.SerializationHelper.read(
                        NLPediaSettings.BOA_DATA_DIRECTORY + Constants.MACHINE_LEARNING_CLASSIFIER_PATH + NLPediaSettings.getSetting("classifier.file.name"));
        }
        catch (Exception e) {
            
            String error = String.format("Could not load classifier from %s!", NLPediaSettings.getSetting("classifier.file.name"));
            logger.error(error, e);
            throw new RuntimeException(error, e);
        }
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.machinelearning.MachineLearningTool#setMachineLearningTrainingFile(de.uni_leipzig.simba.boa.backend.featurescoring.machinelearningtrainingfile.MachineLearningTrainingFile)
     */
    @Override
    public void setMachineLearningTrainingFile(MachineLearningTrainingFile machineLearningTrainingFile) {

        this.trainingFile = machineLearningTrainingFile;
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.machinelearning.MachineLearningTool#getMachineLearningTrainingFile()
     */
    @Override
    public MachineLearningTrainingFile getMachineLearningTrainingFile() {
        
        return this.trainingFile;
    }
}
