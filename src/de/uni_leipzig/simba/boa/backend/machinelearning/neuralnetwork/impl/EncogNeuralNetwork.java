package de.uni_leipzig.simba.boa.backend.machinelearning.neuralnetwork.impl;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.ContainsFlat;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.obj.SerializeObject;

import de.danielgerber.file.BufferedFileWriter;
import de.danielgerber.file.BufferedFileWriter.WRITER_WRITE_MODE;
import de.danielgerber.file.FileUtil;
import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.FeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl.Feature;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;
import de.uni_leipzig.simba.boa.backend.featurescoring.PatternScoreManager;
import de.uni_leipzig.simba.boa.backend.featurescoring.machinelearningtrainingfile.MachineLearningTrainingFile;
import de.uni_leipzig.simba.boa.backend.featurescoring.machinelearningtrainingfile.entry.MachineLearningTrainingFileEntry;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.machinelearning.neuralnetwork.NeuralNetwork;


public class EncogNeuralNetwork implements NeuralNetwork {

    private static NLPediaSetup setup = new NLPediaSetup(true);
    
    private final NLPediaLogger logger               = new NLPediaLogger(EncogNeuralNetwork.class);
    
    private BasicNetwork network;
    private double maxError                          = NLPediaSettings.getDoubleSetting("neuronal.network.max.error");
    private double errorDecrement                    = NLPediaSettings.getDoubleSetting("neuronal.network.error.decrement");
    private double minError                          = NLPediaSettings.getDoubleSetting("neuronal.network.min.error");
    private double maxHiddenToInputRatio             = NLPediaSettings.getIntegerSetting("neuronal.network.hidden.layer.ratio");
    private int maxEpochs                            = NLPediaSettings.getIntegerSetting("neuronal.network.maxEpochs");
    
    private static int N_FOLD_CROSS_VALIDATION       = NLPediaSettings.getIntegerSetting("neuronal.network.n.fold.cross.validation");
    private static final String NETWORK_DIRECTORY    = NLPediaSettings.BOA_DATA_DIRECTORY + Constants.MACHINE_LEARNING_PATH;
    private static String EVAL_OUTPUT_FILE           = NETWORK_DIRECTORY + N_FOLD_CROSS_VALIDATION + "FCV_network_evaluation.txt";
    private static String NETWORK_FILE               = NETWORK_DIRECTORY + N_FOLD_CROSS_VALIDATION + "FCV_network";
    
    private MachineLearningTrainingFile trainingFile = null;
    private MachineLearningTrainingFile testFile = null;
    
    /**
     * Default constructor
     * 
     */
    public EncogNeuralNetwork(MachineLearningTrainingFile trainingFile, MachineLearningTrainingFile testFile) {
        
        this.trainingFile = trainingFile;
        this.testFile = testFile;
        this.loadModel();
    }
    
    public static void main(String[] args) {
    
        final String MACHINE_LEARNING_TRAINING_FILE = NLPediaSettings.BOA_DATA_DIRECTORY + Constants.MACHINE_LEARNING_PATH + "boa_ml.txt";
        
        PatternScoreManager pcm = new PatternScoreManager();
        MachineLearningTrainingFile trainFile = pcm.readNetworkTrainingFile(MACHINE_LEARNING_TRAINING_FILE, "UTF-8");
        MachineLearningTrainingFile testFile = pcm.readNetworkTrainingFile(MACHINE_LEARNING_TRAINING_FILE, "UTF-8");
        
        for ( Integer i : Arrays.asList(/*2,3,4,5,6,7,8,9,*/10)) {
            
            System.out.println(i + "-fold Cross-Validation");
            N_FOLD_CROSS_VALIDATION    = i;
            EVAL_OUTPUT_FILE           = NETWORK_DIRECTORY + N_FOLD_CROSS_VALIDATION + "FCV_network_evaluation.txt";
            NETWORK_FILE               = NETWORK_DIRECTORY + N_FOLD_CROSS_VALIDATION + "FCV_network";
            EncogNeuralNetwork network = new EncogNeuralNetwork(trainFile, testFile);
        }
        System.exit(0);
    }
    
    @Override
    public void loadModel() {
        
        if ( this.network == null ) {
            
            // load the network from disk in case it exists
            if ( new File(NETWORK_FILE).exists() ) {
                
                network = getNetwork(NETWORK_FILE);
                this.logger.info("Reading trained network file!");
            }
            else {
                
                // there is no network file available but annotated data, so we can train the network
                if ( this.trainingFile != null && this.trainingFile.getAnnotatedEntries().size() > 0 ) {
                    
                    train(this.trainingFile, this.testFile, new File(EVAL_OUTPUT_FILE), new File(NETWORK_FILE), N_FOLD_CROSS_VALIDATION);
                    network = getNetwork(NETWORK_FILE);
                }
                else this.logger.error("Could not read network file from location : " + NETWORK_FILE);
            }
        }
    }

    /**
     * Runs n-fold cross-evaluation for an input file. Serializes best network
     * to network file. Writes eval results to outputFile. Repeats each
     * experiments once.
     * 
     * @param inputFile
     *            Tab-separated input file; last column is 1 = good or 0 false
     * @param outputFile
     *            Output file for evaluation
     * @param networkFile
     * @param n
     *            n for n-fold
     */
    public void train(MachineLearningTrainingFile trainFile, MachineLearningTrainingFile testFile, File outputFile, File networkFile, int n) {
       
        MLDataSet[] allData = getData(trainFile, n);
        MLDataSet[] testData = getData(trainFile, 1);

        int repetitions = 1;
        double bestAccuracy, accuracy;
        StringBuffer output = new StringBuffer();
        StringBuffer summary = new StringBuffer();
        int inputSize = allData[0].getInputSize();

        // create header for eval table
        output.append("Data Set\tHidden Size\tError Threshold");
        for (int i = 0; i < repetitions; i++) {
            output.append("\tAccuracy" + i);
        }
        output.append("\n");

        // create header for summarized results
        summary.append("Error Threshold");
        for (int hidden = inputSize; hidden < maxHiddenToInputRatio * inputSize + 1; hidden++) {
            summary.append("\tHidden Size = " + hidden);
        }
        summary.append("\n");
        // remember best confidence for serialization
        bestAccuracy = 0;
        double avgAcc;

        DecimalFormat format = new DecimalFormat("#.##");

        for (double error = maxError; error >= minError; error = error - errorDecrement) {
            summary.append(error + "\t");
            for (int hidden = inputSize; hidden < maxHiddenToInputRatio * inputSize + 1; hidden++) {

                avgAcc = 0;
                for (int i = 0; i < n; i++) {
                    MLDataSet trainingData = merge(allData, i, n);
                    MLDataSet devTestData = allData[i];
                    output.append(i + "\t" + hidden + "\t" + format.format(error));

                    for (int rep = 0; rep < repetitions; rep++) {
                        accuracy = trainAndEvaluate(trainingData, devTestData, inputSize, hidden, 1, error);
                        if (accuracy > bestAccuracy) {
                            try {
                                SerializeObject.save(networkFile, network);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            bestAccuracy = accuracy;
                        }
                        output.append("\t" + format.format(accuracy * 100) + "%");
                        avgAcc = avgAcc + accuracy;
                    }
                    output.append("\n");
                }
                avgAcc = avgAcc / (double) (n * repetitions);
                summary.append(avgAcc + "\t");
            }
            summary.append("\n");
        }
        
        // write the eval file
        BufferedFileWriter writer = FileUtil.openWriter(outputFile.getAbsolutePath(), "UTF-8", WRITER_WRITE_MODE.OVERRIDE);
        writer.write(summary.toString().replace(".", ",") + "\n");
        writer.write("Best accuracy for " + N_FOLD_CROSS_VALIDATION + "-fold CV: " + bestAccuracy);
        writer.write(output.toString().replace(".", ","));
        writer.close();
    }

    /**
     * Merge all data included in alldata with the exception of nth one to
     * training data The nth data set is used for testing
     * 
     * @param allData
     *            All data available
     * @param exception
     *            Data set to use for testing
     * @param n
     *            Number of slices to creates (n-fold cross evaluation)
     * @return Training data
     */
    public MLDataSet merge(MLDataSet[] allData, int exception, int n) {

        MLDataSet result = new BasicMLDataSet();
        for (int i = 0; i < n; i++) {
            if (i != exception) {
                for (MLDataPair pair : allData[i]) {
                    result.add(pair);
                }
            }
        }
        return result;
    }

    /**
     * Fetches data from a file and split this data into n slices for a n-fold
     * cross evaluation
     * 
     * @param file
     *            File containing the data
     * @param n
     *            Number of slices
     * @return n data sets
     */
    public MLDataSet[] getData(MachineLearningTrainingFile trainFile, int n) {

        // split input data into n data sets
        MLDataSet data[] = new BasicMLDataSet[n];
        for (int i = 0; i < n; i++) {
            data[i] = new BasicMLDataSet();
        }
        
        List<Integer> disabledColumns = new ArrayList<Integer>();
        int j = 0;
        for ( FeatureExtractor featureExtractor : FeatureFactory.getInstance().getFeatureExtractorMap().values() )
            for ( Feature feature: featureExtractor.getHandeledFeatures() ) {
                
                if (trainFile.getFeatureNames().contains(feature.getName())) {
                    
                    if ( feature.getSupportedLanguages().contains(NLPediaSettings.getSystemLanguage()) ) {
                        
                        if ( !feature.isUseForPatternLearning() ) {
                            
                            logger.warn("Feature " + j + ": " + feature.getName() + " not activated!");
                            if (trainFile.getFeatureNames().contains(feature.getName())) disabledColumns.add(j);
                        }
                        else logger.info("Feature " + j + ": " + feature.getName() + " activated!");
                    }
                    else {

                        logger.warn("Feature " + feature.getName() + " not for language " + NLPediaSettings.getSystemLanguage() + " not activated!");
                    }
                    j++;
                }
                else {
                    
                    logger.error("Feature: " + feature.getName() + " not in train file!");
                }
            }
        logger.info("Not using columns: " + disabledColumns);
        
        int counter = 0;
        for ( MachineLearningTrainingFileEntry line : trainFile.getAnnotatedEntries() ) {
            
//            System.out.println("line.getFeatures().size(): " + line.getFeatures().size());
//            System.out.println("disabledColumns.size(): " + disabledColumns.size());
//            System.out.println("line.getFeatures().size() - disabledColumns.size(): " + (line.getFeatures().size() - disabledColumns.size() - 1));
//            System.out.println(disabledColumns);
            
            // create a new entry data-set and add all the features to it
            MLData entry = new BasicMLData(line.getFeatures().size() - disabledColumns.size());
            
//            System.out.println("entry.size(): " + entry.size());
            for (Integer i = 0, m = 0; i < line.getFeatures().size(); i++) {
                
//                System.out.println(i + " " + !disabledColumns.contains(i));
                if ( !disabledColumns.contains(i) ) {

//                    System.out.println("USE: "+i+"  " + line.getFeatures().get(i));
                    entry.add(m++, line.getFeatures().get(i));
                }
//                else System.out.println("DONT USE");
            }
            
            // maps each entry data-set to the manually annotated value
            BasicMLData ideal = new BasicMLData(1);
            ideal.add(0, line.getAnnotation() ? 1D : 0D);
            data[counter++ % n].add(entry, ideal);
        }
        
        return data;
    }

    /**
     * Generates a network with one hidden layer
     * 
     * @param inputSize
     *            Input size
     * @param hiddenSize
     *            Size of hidden layer
     * @param outputSize
     *            Output size
     * @return Neural network
     */
    public BasicNetwork generateBasicNetwork(int inputSize, int hiddenSize, int outputSize) {

        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, true, inputSize));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, hiddenSize));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), false, outputSize));
        network.getStructure().finalizeStructure();
        network.reset();
        return network;
    }

    /**
     * Returns the number of misclassified patterns
     * 
     * @param trainingData
     *            Training data
     * @param testData
     *            Test data
     * @param inputSize
     *            Size of input layer
     * @param hiddenSize
     *            Size of hidden layer
     * @param outputSize
     *            Size of output layer
     * @param error
     *            Error to train to
     * @return Percentage of correctly classified patterns from test data
     */
    public double trainAndEvaluate(MLDataSet trainingData, MLDataSet testData, int inputSize, int hiddenSize, int outputSize, double error) {

        network = generateBasicNetwork(inputSize, hiddenSize, outputSize);

        Train train = new ResilientPropagation((ContainsFlat) network, trainingData);
        trainToError(train, error, maxEpochs);

        // evaluate
        double count = (double) testData.getRecordCount();
        for (MLDataPair pair : testData) {
            
            MLData result = network.compute(pair.getInput());
            double value = 0;
            
            if (result.getData(0) > 0.5) value = 1;
            if (pair.getIdeal().getData(0) != value) count--;
        }
        return count / (double) testData.getRecordCount();
    }

    /**
     * Runs the training of a neural networks. Training runs until the error
     * threshold or the maximal number of epochs are reacher
     * 
     * @param train
     *            Training data
     * @param network
     *            Network to train
     * @param error
     *            Error threshold
     * @param maxEpochs
     *            Maximal number of epochs
     */
    public void trainToError(final Train train, final double error, final int maxEpochs) {

        int epoch = 1;
        this.logger.debug("Traning for neural network begins with error rate " + error);
        do {
            train.iteration();
            epoch++;
        } 
        while ((train.getError() > error) && !train.isTrainingDone() && (epoch <= maxEpochs));
        train.finishTraining();
    }

    /**
     * Transform a string into a data entry
     * 
     * @param s
     *            String input
     * @return Data entry
     */
    public MLData getSingleEntry(List<Double> features) {

        MLData data = new BasicMLData(features.size());
        for (int i = 0; i < features.size(); i++) data.add(i, features.get(i));

        return data;
    }

    /**
     * Fetches a network from a file
     * 
     * @param netFile
     *            Input file
     * @return Network
     */
    public BasicNetwork getNetwork(String netFile) {

        try {

            return (BasicNetwork) SerializeObject.load(new File(netFile));
        }
        catch (Exception e) {
            
            e.printStackTrace();
            String error = "Could not load network from file: " + netFile;
            this.logger.error(error, e);
            throw new RuntimeException(error, e);
        }
    }

    /**
     * Computes the score of an entry by using the network in the confidence
     * learner
     * 
     * @param entry
     *            Entry
     * @return Confidence
     */
    public double getScore(PatternMapping mapping, Pattern pattern) {

        MLData data = getSingleEntry(pattern.buildNormalizedFeatureVector(mapping));
        return network.compute(data).getData(0);
    }

    @Override
    public MachineLearningTrainingFile getMachineLearningTrainingFile() {

        return this.trainingFile;
    }
    
    @Override
    public void setMachineLearningTrainingFile(MachineLearningTrainingFile machineLearningTrainingFile) {

        this.trainingFile = machineLearningTrainingFile;
    }
}
