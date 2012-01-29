package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.Feature;
import de.uni_leipzig.simba.boa.backend.featureextraction.FeatureExtractionPair;


public class VerbCountFeature implements Feature {

	@Override
	public void score(FeatureExtractionPair pair) {

		for (Pattern pattern : pair.getMapping().getPatterns()){
			
			int numberOfVerbs = StringUtils.countMatches(pattern.getPosTaggedString(), "V");
			
			pattern.getFeatures().put(
					de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature.VERB_COUNT, Double.valueOf(numberOfVerbs));
		}
	}
}
