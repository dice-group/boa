package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.impl;

import java.util.Date;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.dao.serialization.PatternMappingManager;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.AbstractFeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.FeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.featureextraction.FeatureExtractionPair;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.util.TimeUtil;

/**
 * 
 * @author Daniel Gerber
 */
public class SpecificityFeatureExtractor extends AbstractFeatureExtractor {
	
	private NLPediaLogger logger = new NLPediaLogger(SpecificityFeatureExtractor.class);
	private static final String PATTERN_MAPPINGS_FOLDER = NLPediaSettings.BOA_DATA_DIRECTORY + "patternmappings/";
	
	private final PatternMappingManager manager = new PatternMappingManager();

	@Override
	public void score(FeatureExtractionPair pair) {

		long start = new Date().getTime();
		
		Double specificity = (double) manager.getPatternMappings(PATTERN_MAPPINGS_FOLDER).size() / 
		        (double) manager.findPatternMappingsWithSamePattern(PATTERN_MAPPINGS_FOLDER, pair.getPattern().getNaturalLanguageRepresentation()); 
			
		specificity = Math.log(specificity) / Math.log(2);
		specificity = specificity.isInfinite() || specificity.isNaN() ? 0D : specificity; 
		
		pair.getPattern().getFeatures().put(FeatureFactory.getInstance().getFeature("SPECIFICITY"), specificity >= 0 ? specificity : 0);
		this.logger.debug("Specificity feature for " + pair.getMapping().getProperty().getLabel() + "/\"" + pair.getPattern().getNaturalLanguageRepresentation() + "\"  finished in " + TimeUtil.convertMilliSeconds((new Date().getTime() - start)) + ".");
	}
}
