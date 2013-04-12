/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.patternfeatureextraction.impl;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.concurrent.PatternFeatureExtractionThreadManager;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.FeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureHelper;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl.Feature;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.serialization.PatternMappingManager;
import de.uni_leipzig.simba.boa.backend.featurescoring.PatternScoreManager;
import de.uni_leipzig.simba.boa.backend.featurescoring.machinelearningtrainingfile.MachineLearningTrainingFile;
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

	private final String PATTERN_MAPPING_FOLDER						= NLPediaSettings.BOA_DATA_DIRECTORY + Constants.PATTERN_MAPPINGS_PATH;
	private final int TOTAL_NUMBER_OF_FEATURE_EXTRACTION_THREADS	= NLPediaSettings.getIntegerSetting("numberOfFeatureExtractionsThreads");
	public final static String WEKA_MACHINE_LEARNING_TRAINING_FILE  = NLPediaSettings.BOA_DATA_DIRECTORY + Constants.MACHINE_LEARNING_TRAINING_PATH + "boa_weka.arff";
	public final static String MACHINE_LEARNING_TRAINING_FILE       = NLPediaSettings.BOA_DATA_DIRECTORY + Constants.MACHINE_LEARNING_TRAINING_PATH + "boa_ml.txt";
	
    private final PatternScoreManager patternScoreManager = new PatternScoreManager();
    private MachineLearningTrainingFile trainFile;
    private MachineLearningTrainingFile testFile;
	
	// for the report
	private long patternFeatureExtractionTime;
	private long patternSaveTime;
	private long machineLearningTrainingTime;
	private long machineLearningReTrainingTime;
	
	@Override
	public String getName() {

		return "Default FeatureExtractor Extraction Module";
	}

	@Override
	public void run() {

	    if ( NLPediaSettings.getBooleanSetting("extractFeatures") ) {
	        
	        // starts the threads which extract the features
	        this.logger.info("Starting feature extraction!");
	        long startFeatureExtraction = System.currentTimeMillis();
	        PatternFeatureExtractionThreadManager.startFeatureExtractionCallables(this.moduleInterchangeObject.getPatternMappings(), TOTAL_NUMBER_OF_FEATURE_EXTRACTION_THREADS);
	        this.patternFeatureExtractionTime = (System.currentTimeMillis() - startFeatureExtraction);
	        this.logger.info("Extaction of pattern features finished in " + TimeUtil.convertMilliSeconds(this.patternFeatureExtractionTime) + "!");
	        
	        // serialize the new pattern mappings 
	        this.logger.info("Starting to save features!");
	        long patternSaveTime = System.currentTimeMillis();
	        SerializationManager.getInstance().serializePatternMappings(this.moduleInterchangeObject.getPatternMappings(), PATTERN_MAPPING_FOLDER);
	        this.patternSaveTime = (System.currentTimeMillis() - patternSaveTime);
	        this.logger.info("Extraction of pattern features finished in " + TimeUtil.convertMilliSeconds(this.patternSaveTime) + "!");
	    }
	    
        if ( this.createOrUpdateMachineLearningTrainingFile() ) {
        	
        	// generate update the machine learning training file
            this.logger.info("Starting to generate/update machine learning training file!");
            long networkUpdateTime = System.currentTimeMillis();

        	machineLearningTrainingTime = System.currentTimeMillis() - networkUpdateTime;
            this.logger.info("The update of the machine learning training file took " + TimeUtil.convertMilliSeconds(this.machineLearningTrainingTime) + "!");
            
            // retrain the machine learning tool
            this.logger.info("Starting to retrain the neural network based on the extracted features!");
            long networkRetrainingTime = System.currentTimeMillis();
            // only train the file if we have annotated patterns in the network learn file
            this.trainFile = patternScoreManager.readNetworkTrainingFile(MACHINE_LEARNING_TRAINING_FILE, "UTF-8");
            this.testFile = null;//patternScoreManager.readNetworkTrainingFile(MACHINE_LEARNING_TEST_FILE, "UTF-8");
            
            if ( trainFile.getAnnotatedEntries().size() > 0 && new File(WEKA_MACHINE_LEARNING_TRAINING_FILE).exists() ) {
                
                MachineLearningTool mlTool = MachineLearningToolFactory.getInstance().createDefaultMachineLearningTool(trainFile, testFile);
                this.moduleInterchangeObject.setMachineLearningTool(mlTool);
            }
            machineLearningReTrainingTime = System.currentTimeMillis() - networkRetrainingTime;
            this.logger.info("Retraining of the neural network finished after " + TimeUtil.convertMilliSeconds(this.machineLearningReTrainingTime) + "!");
        }
	}

	/**
	 * returns true, if a file was updated
	 */
	private boolean createOrUpdateMachineLearningTrainingFile() {

	    // create the maximas beforehand, so we can use them as a cache
	    FeatureHelper.init(this.moduleInterchangeObject.getPatternMappings());
	    MachineLearningTrainingFile file = null;
	    
	    if ( new File(MACHINE_LEARNING_TRAINING_FILE).exists() ) {
	        
	        this.logger.info("Updating ml train file!");
	        file = patternScoreManager.readNetworkTrainingFile(MACHINE_LEARNING_TRAINING_FILE, "UTF-8");
	        this.logger.info("Finished reading old ml train file!");
	        file = patternScoreManager.updateNetworkTrainingFile(this.moduleInterchangeObject.getPatternMappings(), file);
	        this.logger.info("Finished updating ml train file!");
	    }
	    else {
	        
	        this.logger.info("Starting to create new network train file!");
	        file = patternScoreManager.createNeuronalNetworkTrainingFile(this.moduleInterchangeObject.getPatternMappings());
	        this.logger.info("Finished creation of new network train file");
	    }
	    patternScoreManager.writeNetworkTrainingFile(file, MACHINE_LEARNING_TRAINING_FILE);
	    
	    return new File(MACHINE_LEARNING_TRAINING_FILE).exists() ? true : false;
    }

    @Override
	public String getReport() {

		return "Pattern FeatureExtractor Extraction finished in " + TimeUtil.convertMilliSeconds(patternFeatureExtractionTime) + ". Updated pattern mappings successfully.";
	}

	@Override
	public void updateModuleInterchangeObject() {

	}

	@Override
	public boolean isDataAlreadyAvailable() {
		
		if ( NLPediaSettings.getBooleanSetting("extractFeatures") ) return false; 
		
		// get from disk or from cache
		Set<PatternMapping> mappings = new HashSet<PatternMapping>(); 
		mappings = this.moduleInterchangeObject.getPatternMappings() == null ?
				PatternMappingManager.getInstance().getPatternMappings() : 
				this.moduleInterchangeObject.getPatternMappings();
		
		// look if all patterns have extracted features
        boolean patternsScored = true;
		for ( FeatureExtractor featureExtractor : FeatureFactory.getInstance().getFeatureExtractorMap().values() ) {
		    if ( featureExtractor.isActivated() ) {
		        
		        for ( PatternMapping mapping : mappings ) {
		            
		            // we can stop after we found one pattern which is not scored
		            if ( !patternsScored ) break;
		            for (Pattern pattern : mapping.getPatterns()) {
		                
		                // check if each pattern has more values for every feature
		                for ( Feature feature : featureExtractor.getHandeledFeatures() ) {
		                	
		                	if ( feature.getSupportedLanguages().contains(NLPediaSettings.getSystemLanguage()) ) {
			                    
			                    // exclude everything which is not activated
			                    if ( feature.isUseForPatternLearning() ) {
			                    	
			                    	// we can stop if we found one pattern without a key/value pair
				                    patternsScored &= pattern.getFeatures().containsKey(feature);
				                    if ( !patternsScored ) break;
			                    }
		                	}
		                }
		            }
		        }
		    }
		}
		return patternsScored;
	}

	@Override
	public void loadAlreadyAvailableData() {

		// add the patterns to the interchange module only if they are not already their
		if ( this.moduleInterchangeObject.getPatternMappings() == null )
			this.moduleInterchangeObject.getPatternMappings().addAll(PatternMappingManager.getInstance().getPatternMappings());
		
		// load the machine learning in case we did not run the run method of the feature extraction
        // this happens when we annotated data and restart the system
        if ( new File(MACHINE_LEARNING_TRAINING_FILE).exists() ) {

            MachineLearningTrainingFile trainFile = patternScoreManager.readNetworkTrainingFile(MACHINE_LEARNING_TRAINING_FILE, "UTF-8");
            
            if ( this.moduleInterchangeObject.getMachineLearningTool() == null ) 
                this.moduleInterchangeObject.setMachineLearningTool(MachineLearningToolFactory.getInstance().createDefaultMachineLearningTool(trainFile, testFile));

            else logger.warn("Machine learning object already exist. This should not be possible in the first iteration!");
        }
        else {
            
            this.logger.error("Machine learning traing file does not exist: " + MACHINE_LEARNING_TRAINING_FILE + ".");
            
            // create a file if possible
            if ( this.moduleInterchangeObject.getPatternMappings() != null ) {
                
                FeatureHelper.init(this.moduleInterchangeObject.getPatternMappings());
                MachineLearningTrainingFile file = patternScoreManager.createNeuronalNetworkTrainingFile(this.moduleInterchangeObject.getPatternMappings());
                patternScoreManager.writeNetworkTrainingFile(file, MACHINE_LEARNING_TRAINING_FILE);
            }
        }
	}
}
