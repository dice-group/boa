/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.machinelearning;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.featurescoring.PatternScoreManager;
import de.uni_leipzig.simba.boa.backend.featurescoring.machinelearningtrainingfile.MachineLearningTrainingFile;
import de.uni_leipzig.simba.boa.backend.featurescoring.machinelearningtrainingfile.entry.MachineLearningTrainingFileEntry;


/**
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 *
 */
public class MeanSquaredErrorFeatureSelection {

    private static final NLPediaSetup setup                         = new NLPediaSetup(true);
    private static final PatternScoreManager patternScoreManager    = new PatternScoreManager();
    private static final String MACHINE_LEARNING_TRAINING_FILE      = NLPediaSettings.BOA_DATA_DIRECTORY + Constants.NEURAL_NETWORK_PATH + "network_learn.txt";
    
    /**
     * @param args
     */
    public static void main(String[] args) {

        MachineLearningTrainingFile file = patternScoreManager.readNetworkTrainingFile(MACHINE_LEARNING_TRAINING_FILE, "UTF-8");
        
        Map<String,Double> results = new HashMap<String,Double>();
        List<MachineLearningTrainingFileEntry> annotatedEntries = file.getAnnotatedEntries();
        
        // we want to go through one column at the time
        for ( int i = 0; i < file.getFeatureNames().size() ; i++ ) {
            
            List<Double> positives = new ArrayList<Double>();
            List<Double> negatives = new ArrayList<Double>();
            
            for (MachineLearningTrainingFileEntry entry : annotatedEntries) {
                
                if ( entry.getAnnotation() == true ) positives.add(entry.getFeatures().get(i));
                else negatives.add(entry.getFeatures().get(i));
            }
            
            double sum = 0;
            for ( Double negative : negatives ) {
                
                Double minimum = Double.POSITIVE_INFINITY;
                for ( Double positive : positives ) {
                    
                    if ( Math.pow(negative - positive , 2) < minimum ) minimum = Math.pow(negative - positive , 2);
                    sum += minimum;
                }
            }
            results.put(file.getFeatureNames().get(i), (double) sum / negatives.size());
        }
        List<String> res = new ArrayList<String>();
        for (Map.Entry<String, Double> entry : results.entrySet() ) {
            
            res.add(new DecimalFormat("0.0000").format(entry.getValue()) + " " + entry.getKey());
        }
        Collections.sort(res);
        for (String line : res) System.out.println(line);
    }
}
