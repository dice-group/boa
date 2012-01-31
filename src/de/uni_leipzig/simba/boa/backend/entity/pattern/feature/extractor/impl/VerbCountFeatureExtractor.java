package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.impl;

import org.apache.commons.lang3.StringUtils;

import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.AbstractFeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.FeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.featureextraction.FeatureExtractionPair;


public class VerbCountFeatureExtractor extends AbstractFeatureExtractor {

	@Override
	public void score(FeatureExtractionPair pair) {

		pair.getPattern().getFeatures().put(
		        FeatureFactory.getInstance().getFeature("VERB_COUNT"), 
		        Double.valueOf(StringUtils.countMatches(pair.getPattern().getPosTaggedString(), "V")));
	}
}
