package de.uni_leipzig.simba.boa.backend;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;

import de.uni_leipzig.simba.boa.backend.concurrent.PrintJvmMemoryTimerTask;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.dao.serialization.PatternMappingManager;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureHelper;
import de.uni_leipzig.simba.boa.backend.featurescoring.PatternScoreManager;
import de.uni_leipzig.simba.boa.backend.featurescoring.machinelearningtrainingfile.MachineLearningTrainingFile;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.pipeline.Pipeline;
import de.uni_leipzig.simba.boa.backend.search.concurrent.PatternSearchPrintProgressTask;


public class Boa {

    public static void main(String[] args) {

        System.out.println("Starting BOA Framework!");
        
        // Initialize logging, settings, factories etc., needs to be FIRST call!!
        NLPediaSetup setup = new NLPediaSetup(false);
        NLPediaLogger logger = new NLPediaLogger(NLPedia.class);
        
        
        
        PatternMappingManager pm = new PatternMappingManager();
        
        Set<PatternMapping> mappings = new HashSet<PatternMapping>(pm.getPatternMappings());
        FeatureHelper.createLocalMaxima(mappings);
        
        PatternScoreManager patternScoreManager = new PatternScoreManager();
        String MACHINE_LEARNING_TRAINING_FILE   = NLPediaSettings.BOA_DATA_DIRECTORY + Constants.NEURAL_NETWORK_PATH + "network_learn.txt";
        
        MachineLearningTrainingFile file = patternScoreManager.createNeuronalNetworkTrainingFile(mappings);
        patternScoreManager.writeNetworkTrainingFile(file, MACHINE_LEARNING_TRAINING_FILE);
        
        
        
        // this logs the jvm heap usage 
//        Timer timer = new Timer();
//        timer.schedule(new PrintJvmMemoryTimerTask(), 10000, 30000);
//        
//        Pipeline pipeline = new Pipeline();
//        pipeline.run();
//        
//        timer.cancel();
        
        System.out.println("Stopping BOA Framework!");
    }
}
