/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.patternscoring.impl;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.machinelearning.MachineLearningTool;
import de.uni_leipzig.simba.boa.backend.machinelearning.MachineLearningToolFactory;
import de.uni_leipzig.simba.boa.backend.persistance.serialization.SerializationManager;
import de.uni_leipzig.simba.boa.backend.pipeline.module.patternscoring.AbstractPatternScoringModule;
import de.uni_leipzig.simba.boa.backend.util.TimeUtil;


/**
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public class DefaultPatternScoringModule extends AbstractPatternScoringModule {
    
    // dependent settings
    private final String PATTERN_MAPPING_FOLDER             = NLPediaSettings.BOA_DATA_DIRECTORY + "patternmappings/";
    
    private final NLPediaLogger logger                      = new NLPediaLogger(DefaultPatternScoringModule.class);
    private final MachineLearningTool machineLearningTool   = MachineLearningToolFactory.getInstance().createDefaultMachineLearningTool();
    
    // for the report
    private long scoringTime = 0;
    
    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#getName()
     */
    @Override
    public String getName() {

        return "Default Pattern Scoring Module";
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#run()
     */
    @Override
    public void run() {

        long start = System.currentTimeMillis();
        
        // go through each mapping / pattern combination
        for ( PatternMapping mapping : this.moduleInterchangeObject.getPatternMappings() ) {
            for ( Pattern pattern : mapping.getPatterns() ) {
                
                Double score = machineLearningTool.getScore(mapping, pattern);
                pattern.setScore(
                        score == Double.NaN || 
                        score == Double.NEGATIVE_INFINITY || 
                        score == Double.POSITIVE_INFINITY
                        ? 0D : score);
                
                this.logger.debug(pattern.getNaturalLanguageRepresentation() + ": " +score);
            }
            // update the pattern mapping "database"
            SerializationManager.getInstance().serializePatternMapping(mapping, PATTERN_MAPPING_FOLDER + mapping.getProperty().getLabel() + ".bin");
        }
        this.scoringTime = System.currentTimeMillis() - start;
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#getReport()
     */
    @Override
    public String getReport() {

        return "Pattern scoring finished in " + TimeUtil.convertMilliSeconds(this.scoringTime) + ".";
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#updateModuleInterchangeObject()
     */
    @Override
    public void updateModuleInterchangeObject() {

        // nothing to do here because we work on the interchange-object's pattern mappings already
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#isDataAlreadyAvailable()
     */
    @Override
    public boolean isDataAlreadyAvailable() {

        boolean isAvailable = true;
        
        if ( this.moduleInterchangeObject.getPatternMappings() != null ) {
            
            for (PatternMapping mapping : this.moduleInterchangeObject.getPatternMappings()) {
                for ( Pattern pattern : mapping.getPatterns() ) {
                    
                    isAvailable &= pattern.getScore() != 0D;
                }
            }
            return isAvailable;
        }
        throw new RuntimeException("Configuration error! The interchange object did not contain any pattern mappings!");
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#loadAlreadyAvailableData()
     */
    @Override
    public void loadAlreadyAvailableData() {

        // since this module depends on the feature extraction, this module has already loaded the pattern mappings
        if ( this.moduleInterchangeObject.getPatternMappings() == null ) 
            throw new RuntimeException("Configuration error! The interchange object did not contain any pattern mappings!");
    }
}
