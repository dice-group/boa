package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.impl;

import java.util.Date;

import de.uni_leipzig.simba.boa.backend.concurrent.PatternMappingPatternPair;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.dao.serialization.PatternMappingManager;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.AbstractFeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.FeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.util.TimeUtil;

/**
 * 
 * @author Daniel Gerber
 */
public class SpecificityFeatureExtractor extends AbstractFeatureExtractor {
	
	private NLPediaLogger logger = new NLPediaLogger(SpecificityFeatureExtractor.class);
	
	private PatternMappingManager manager;

	@Override
	public void score(PatternMappingPatternPair pair) {

	    if ( manager == null ) manager = new PatternMappingManager();
	    
		long start = new Date().getTime();
		
		Double specificity = (double) manager.getPatternMappings().size() / 
		        (double) manager.findPatternMappingsWithSamePattern(pair.getPattern().getNaturalLanguageRepresentation()); 
			
		specificity = Math.log(specificity) / Math.log(2);
		specificity = specificity.isInfinite() || specificity.isNaN() ? 0D : specificity; 
		
		pair.getPattern().getFeatures().put(FeatureFactory.getInstance().getFeature("SPECIFICITY"), specificity >= 0 ? specificity : 0);
		this.logger.debug("Specificity feature for " + pair.getMapping().getProperty().getLabel() + "/\"" + pair.getPattern().getNaturalLanguageRepresentation() + "\"  finished in " + TimeUtil.convertMilliSeconds((new Date().getTime() - start)) + ".");
	}
}
