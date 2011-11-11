package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl;

import java.util.Date;
import java.util.List;

import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.Feature;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

/**
 * 
 * @author Daniel Gerber
 */
public class SupportFeature implements Feature {

	private NLPediaLogger logger = new NLPediaLogger(SupportFeature.class);
	
	@Override
	public void score(List<PatternMapping> mappings) {

		// nothing to do here
	}
	
	@Override
	public void scoreMapping(PatternMapping mapping) {

		long start = new Date().getTime();
		
		for (Pattern pattern : mapping.getPatterns()) {
			
			double maxLearnedFrom = (double) (Math.log((pattern.getMaxLearnedFrom() + 1)) / Math.log(2));
			double countLearnedFrom = (double) (Math.log((pattern.getLearnedFromPairs() + 1)) / Math.log(2));
			
			pattern.getFeatures().put(de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature.SUPPORT_NUMBER_OF_MAX_PAIRS_LEARNED_FROM, maxLearnedFrom >= 0 ? maxLearnedFrom : 0);
			pattern.getFeatures().put(de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature.SUPPORT_NUMBER_OF_PAIRS_LEARNED_FROM, countLearnedFrom  >= 0 ? countLearnedFrom : 0);
		}
		this.logger.info("Support measuring for pattern_mapping: " + mapping.getProperty().getUri() + " finished in " + (new Date().getTime() - start) + "ms.");
	}
}