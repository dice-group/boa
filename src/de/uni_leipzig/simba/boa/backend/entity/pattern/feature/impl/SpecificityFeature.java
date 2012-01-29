package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl;

import java.util.Date;
import java.util.List;

import de.uni_leipzig.simba.boa.backend.configuration.command.impl.PatternScoreFeatureCommand;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.Feature;
import de.uni_leipzig.simba.boa.backend.featureextraction.FeatureExtractionPair;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

/**
 * 
 * @author Daniel Gerber
 */
public class SpecificityFeature implements Feature {
	
	private PatternMappingDao patternMappingDao = (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
	private NLPediaLogger logger = new NLPediaLogger(SpecificityFeature.class);

	@Override
	public void score(FeatureExtractionPair pair) {

		long start = new Date().getTime();
		
		for (Pattern pattern : pair.getMapping().getPatterns()) {
			
			double specificity = PatternScoreFeatureCommand.NUMBER_OF_PATTERN_MAPPINGS / 
					patternMappingDao.findPatternMappingsWithSamePattern(pattern.getNaturalLanguageRepresentation()); 
				
			specificity = Math.log(specificity) / Math.log(2);
			
			pattern.getFeatures().put(de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature.SPECIFICITY, specificity >= 0 ? specificity : 0);
		}
		logger.info("Specificity measuring for pattern_mapping: " + pair.getMapping().getProperty().getUri() + " finished in " + (new Date().getTime() - start) + "ms.");
	}
}
