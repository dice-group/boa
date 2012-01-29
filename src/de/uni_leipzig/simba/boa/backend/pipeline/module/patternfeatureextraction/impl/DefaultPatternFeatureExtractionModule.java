/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.patternfeatureextraction.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.uni_leipzig.simba.boa.backend.concurrent.PatternFeatureExtractionThreadManager;
import de.uni_leipzig.simba.boa.backend.concurrent.PatternSearchThreadManager;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.persistance.serialization.SerializationManager;
import de.uni_leipzig.simba.boa.backend.pipeline.module.patternfeatureextraction.AbstractPatternFeatureExtractionModule;
import de.uni_leipzig.simba.boa.backend.search.result.SearchResult;
import de.uni_leipzig.simba.boa.backend.util.SerializationUtil;


/**
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public class DefaultPatternFeatureExtractionModule extends AbstractPatternFeatureExtractionModule {

	private final NLPediaLogger logger = new NLPediaLogger(DefaultPatternFeatureExtractionModule.class);

	private final String PATTERN_MAPPING_FOLDER						= NLPediaSettings.BOA_DATA_DIRECTORY + "patternmappings/";
	private final int TOTAL_NUMBER_OF_FEATURE_EXTRACTION_THREADS	= NLPediaSettings.getInstance().getIntegerSetting("numberOfFeatureExtractionsThreads");
	
	// for the report
	private long patternFeatureExtractionTime;
	private long patternSaveTime;
	
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
		this.logger.info("Extaction of pattern features finished in " + patternFeatureExtractionTime + "ms!");
		
		// serialize the new pattern mappings 
        this.logger.info("Starting to save features!");
        patternSaveTime = System.currentTimeMillis();
        SerializationManager.getInstance().serializePatternMappings(this.moduleInterchangeObject.getPatternMappings(), PATTERN_MAPPING_FOLDER);
        this.patternFeatureExtractionTime = (System.currentTimeMillis() - patternSaveTime);
        this.logger.info("Extaction of pattern features finished in " + patternFeatureExtractionTime + "ms!");
		
		for ( PatternMapping mapping : this.moduleInterchangeObject.getPatternMappings()) {
		    for (Pattern pattern :mapping.getPatterns()) {
		        System.out.println(pattern);
		    }
		}
	}

	@Override
	public String getReport() {

		// TODO Auto-generated method stub
		return "Pattern Feature Extraction finished in " + patternFeatureExtractionTime + "ms.";
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
