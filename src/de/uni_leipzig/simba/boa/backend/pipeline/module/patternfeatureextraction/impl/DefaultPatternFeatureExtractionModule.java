/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.patternfeatureextraction.impl;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import de.uni_leipzig.simba.boa.backend.concurrent.PatternFeatureExtractionThreadManager;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.featurescoring.NeuronalNetworkTrainingFile;
import de.uni_leipzig.simba.boa.backend.featurescoring.PatternScoreManager;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.machinelearning.MachineLearningTool;
import de.uni_leipzig.simba.boa.backend.machinelearning.MachineLearningToolFactory;
import de.uni_leipzig.simba.boa.backend.persistance.serialization.SerializationManager;
import de.uni_leipzig.simba.boa.backend.pipeline.module.patternfeatureextraction.AbstractPatternFeatureExtractionModule;
import de.uni_leipzig.simba.boa.backend.util.TimeUtil;


/**
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public class DefaultPatternFeatureExtractionModule extends AbstractPatternFeatureExtractionModule {

	private final NLPediaLogger logger = new NLPediaLogger(DefaultPatternFeatureExtractionModule.class);

	private final String PATTERN_MAPPING_FOLDER						= NLPediaSettings.BOA_DATA_DIRECTORY + "patternmappings/";
	private final int TOTAL_NUMBER_OF_FEATURE_EXTRACTION_THREADS	= NLPediaSettings.getInstance().getIntegerSetting("numberOfFeatureExtractionsThreads");
	private final String MACHINE_LEARNING_TRAINING_FILE             = NLPediaSettings.BOA_BASE_DIRECTORY + NLPediaSettings.getInstance().getSetting("neural.network.network.directory") + "network_learn.txt";
	
	private static final int N_FOLD_CROSS_VALIDATION                = NLPediaSettings.getInstance().getIntegerSetting("neuronal.network.n.fold.cross.validation");
    private static final String NETWORK_DIRECTORY                   = NLPediaSettings.BOA_BASE_DIRECTORY + NLPediaSettings.getInstance().getSetting("neural.network.network.directory");
    private static final String LEARN_FILE                          = NETWORK_DIRECTORY + "network_learn.txt";
    private static final String EVAL_OUTPUT_FILE                    = NETWORK_DIRECTORY + N_FOLD_CROSS_VALIDATION + "FCV_network_evaluation.txt";
    private static final String NETWORK_FILE                        = NETWORK_DIRECTORY + N_FOLD_CROSS_VALIDATION + "FCV_network";
	
	// for the report
	private long patternFeatureExtractionTime;
	private long patternSaveTime;
	private long machineLearningTrainingTime;
	private long machineLearningReTrainingTime;
	
	@Override
	public String getName() {

		return "Default Feature Extraction Module";
	}

	@Override
	public void run() {

		// starts the threads which extract the features
		this.logger.info("Starting feature extraction!");
		long startFeatureExtraction = System.currentTimeMillis();
		PatternFeatureExtractionThreadManager.startFeatureExtractionCallables(this.moduleInterchangeObject.getPatternMappings(), TOTAL_NUMBER_OF_FEATURE_EXTRACTION_THREADS);
		this.patternFeatureExtractionTime = (System.currentTimeMillis() - startFeatureExtraction);
		this.logger.info("Extaction of pattern features finished in " + TimeUtil.convertMilliSeconds(patternFeatureExtractionTime) + "!");
		
		// serialize the new pattern mappings 
        this.logger.info("Starting to save features!");
        long patternSaveTime = System.currentTimeMillis();
        SerializationManager.getInstance().serializePatternMappings(this.moduleInterchangeObject.getPatternMappings(), PATTERN_MAPPING_FOLDER);
        this.patternSaveTime = (System.currentTimeMillis() - patternSaveTime);
        this.logger.info("Extraction of pattern features finished in " + TimeUtil.convertMilliSeconds(patternFeatureExtractionTime) + "!");
		
        // generate update the machine learning training file
        this.logger.info("Starting to generate/update machine learning training file!");
        long networkUpdateTime = System.currentTimeMillis();
        this.createOrUpdateMachineLearningTrainingFile();
        machineLearningTrainingTime = System.currentTimeMillis() - networkUpdateTime;
        this.logger.info("The update of the machine learning training file took " + TimeUtil.convertMilliSeconds(machineLearningTrainingTime) + "!");
        
        // retrain the machine learning tool
        this.logger.info("Starting to retrain the neural network based on the extracted features!");
        long networkRetrainingTime = System.currentTimeMillis();
        MachineLearningTool machineLearningTool = MachineLearningToolFactory.getInstance().createDefaultMachineLearningTool();
        machineLearningTool.train(new File(LEARN_FILE), new File(EVAL_OUTPUT_FILE), new File(NETWORK_FILE), N_FOLD_CROSS_VALIDATION);
        machineLearningReTrainingTime = System.currentTimeMillis() - networkRetrainingTime;
        this.logger.info("Retraining of the neural network finished after " + TimeUtil.convertMilliSeconds(machineLearningReTrainingTime) + "!");
        
        // only for test pruposes TODO remove
		for ( PatternMapping mapping : this.moduleInterchangeObject.getPatternMappings()) {
		    for (Pattern pattern :mapping.getPatterns()) {
		        System.out.println(pattern);
		    }
		}
	}

	/**
	 * 
	 */
	private void createOrUpdateMachineLearningTrainingFile() {

	    PatternScoreManager patternScoreManager = new PatternScoreManager();
	    NeuronalNetworkTrainingFile file = null;
	    
	    if ( new File(MACHINE_LEARNING_TRAINING_FILE).exists() ) {
	            
	        file = patternScoreManager.readNetworkTrainingFile(MACHINE_LEARNING_TRAINING_FILE, "UTF-8");
	        file = patternScoreManager.updateNetworkTrainingFile(this.moduleInterchangeObject.getPatternMappings(), file);
	    }
	    else {
	        
	        file = patternScoreManager.createNeuronalNetworkTrainingFile(this.moduleInterchangeObject.getPatternMappings());
	    }
	    patternScoreManager.writeNetworkTrainingFile(file, MACHINE_LEARNING_TRAINING_FILE);
    }

    @Override
	public String getReport() {

		// TODO Auto-generated method stub
		return "Pattern Feature Extraction finished in " + TimeUtil.convertMilliSeconds(patternFeatureExtractionTime) + ". Updated pattern mappings successfully.";
	}

	@Override
	public void updateModuleInterchangeObject() {

		// nothing to do here, since we work directly on the interchangeobject's mappings
	}

	@Override
	public boolean isDataAlreadyAvailable() {
		
		// get from disk or from cache
		Set<PatternMapping> mappings = new HashSet<PatternMapping>(); 
		mappings = this.moduleInterchangeObject.getPatternMappings() == null ?
				SerializationManager.getInstance().deserializePatternMappings(PATTERN_MAPPING_FOLDER) : 
				this.moduleInterchangeObject.getPatternMappings();
		
		// look if all patterns have extracted features
		boolean patternsScored = true;
		for ( PatternMapping mapping : mappings ) {
			
			// we can stop after we found one pattern which is not scored
			if ( !patternsScored ) break;
			for (Pattern pattern : mapping.getPatterns()) {
				// check if a patterns has more than 0 feature values
				patternsScored &= pattern.getFeatures().size() > 0;
				if ( !patternsScored ) break;
			}
		}
				
		return patternsScored;
	}

	@Override
	public void loadAlreadyAvailableData() {

		// add the patterns to the interchange module only if they are not already their
		if ( this.moduleInterchangeObject.getPatternMappings() == null )
			this.moduleInterchangeObject.getPatternMappings().addAll(
					SerializationManager.getInstance().deserializePatternMappings(PATTERN_MAPPING_FOLDER));
	}
}
