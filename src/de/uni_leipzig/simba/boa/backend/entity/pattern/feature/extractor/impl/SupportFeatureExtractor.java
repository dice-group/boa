package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.impl;

import java.util.Date;

import de.uni_leipzig.simba.boa.backend.concurrent.PatternMappingPatternPair;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.AbstractFeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.FeatureExtractor;
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
	public void score(PatternMappingPatternPair pair) {

		long start = new Date().getTime();
		
		double maxLearnedFrom = (double) (Math.log(((double) pair.getPattern().getMaxLearnedFrom() + 1)) / Math.log(2));
		double countLearnedFrom = (double) (Math.log((pair.getPattern().getLearnedFromPairs() + 1)) / Math.log(2));
		
		pair.getPattern().getFeatures().put(FeatureFactory.getInstance().getFeature("SUPPORT_NUMBER_OF_MAX_PAIRS_LEARNED_FROM"), maxLearnedFrom >= 0 ? maxLearnedFrom : 0);
		pair.getPattern().getFeatures().put(FeatureFactory.getInstance().getFeature("SUPPORT_NUMBER_OF_PAIRS_LEARNED_FROM"), countLearnedFrom  >= 0 ? countLearnedFrom : 0);

		this.logger.debug("Typicity feature for " + pair.getMapping().getProperty().getLabel() + "/\"" + pair.getPattern().getNaturalLanguageRepresentation() + "\"  finished in " + TimeUtil.convertMilliSeconds((new Date().getTime() - start)) + ".");
	}
}