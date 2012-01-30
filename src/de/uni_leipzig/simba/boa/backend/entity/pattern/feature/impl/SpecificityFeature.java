package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl;

import java.util.Date;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.dao.serialization.PatternMappingManager;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.Feature;
import de.uni_leipzig.simba.boa.backend.featureextraction.FeatureExtractionPair;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.util.TimeUtil;

/**
 * 
 * @author Daniel Gerber
 */
public class SpecificityFeature implements Feature {
	
	private NLPediaLogger logger = new NLPediaLogger(SpecificityFeature.class);
	private static final String PATTERN_MAPPINGS_FOLDER = NLPediaSettings.BOA_DATA_DIRECTORY + "patternmappings/";

	@Override
	public void score(FeatureExtractionPair pair) {

		long start = new Date().getTime();
		
		Double specificity = (double) PatternMappingManager.getInstance().getPatternMappings(PATTERN_MAPPINGS_FOLDER).size() / 
		        (double) PatternMappingManager.getInstance().findPatternMappingsWithSamePattern(PATTERN_MAPPINGS_FOLDER, pair.getPattern().getNaturalLanguageRepresentation()); 
			
		specificity = Math.log(specificity) / Math.log(2);
		specificity = specificity.isInfinite() || specificity.isNaN() ? 0D : specificity; 
		
		pair.getPattern().getFeatures().put(de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature.SPECIFICITY, specificity >= 0 ? specificity : 0);
		this.logger.debug("Specificity feature for " + pair.getMapping().getProperty().getLabel() + "/\"" + pair.getPattern().getNaturalLanguageRepresentation() + "\"  finished in " + TimeUtil.convertMilliSeconds((new Date().getTime() - start)) + ".");
	}
}
