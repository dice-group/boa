package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.impl;

import java.util.Date;

import de.uni_leipzig.simba.boa.backend.concurrent.PatternMappingGeneralizedPatternPair;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.AbstractFeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.util.TimeUtil;

/**
 * 
 * @author Daniel Gerber
 */
public class SupportFeatureExtractor extends AbstractFeatureExtractor {

	private NLPediaLogger logger = new NLPediaLogger(SupportFeatureExtractor.class);
	
	@Override
	public void score(PatternMappingGeneralizedPatternPair pair) {

		double totalMaxLearnedFrom = 0D, totalCountLearnedFrom = 0D;
		
		for ( Pattern pattern : pair.getGeneralizedPattern().getPatterns() ) {
			
			long start = new Date().getTime();
	
			double maxLearnedFrom = (double) (Math.log(((double) pattern.getMaxLearnedFrom() + 1)) / Math.log(2));
			double countLearnedFrom = (double) (Math.log((pattern.getLearnedFromPairs() + 1)) / Math.log(2));
			totalMaxLearnedFrom += pattern.getMaxLearnedFrom();
			totalCountLearnedFrom += pattern.getLearnedFromPairs();
			
			pattern.getFeatures().put(FeatureFactory.getInstance().getFeature("SUPPORT_NUMBER_OF_MAX_PAIRS_LEARNED_FROM"), maxLearnedFrom >= 0 ? maxLearnedFrom : 0);
			pattern.getFeatures().put(FeatureFactory.getInstance().getFeature("SUPPORT_NUMBER_OF_PAIRS_LEARNED_FROM"), countLearnedFrom  >= 0 ? countLearnedFrom : 0);

			this.logger.debug("Support feature for " + pair.getMapping().getProperty().getLabel() + "/\"" + pattern.getNaturalLanguageRepresentation() + "\"  finished in " + TimeUtil.convertMilliSeconds((new Date().getTime() - start)) + ".");
		}
		
		totalMaxLearnedFrom = (double) (Math.log((totalMaxLearnedFrom + 1)) / Math.log(2));
		totalCountLearnedFrom = (double) (Math.log((totalCountLearnedFrom + 1)) / Math.log(2));
		
		pair.getGeneralizedPattern().getFeatures().put(
				FeatureFactory.getInstance().getFeature("SUPPORT_NUMBER_OF_MAX_PAIRS_LEARNED_FROM"), totalMaxLearnedFrom >= 0 ? totalMaxLearnedFrom : 0);
		pair.getGeneralizedPattern().getFeatures().put(
				FeatureFactory.getInstance().getFeature("SUPPORT_NUMBER_OF_PAIRS_LEARNED_FROM"), totalCountLearnedFrom  >= 0 ? totalCountLearnedFrom : 0);
	}
}