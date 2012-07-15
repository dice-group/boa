package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.impl;

import de.uni_leipzig.simba.boa.backend.concurrent.PatternMappingPatternPair;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.AbstractFeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;


public class LengthFeatureExtractor extends AbstractFeatureExtractor {

	@Override
	public void score(PatternMappingPatternPair pair) {
		String patternWithoutVariables = pair.getPattern().getNaturalLanguageRepresentationWithoutVariables();
		String[] patternParts = patternWithoutVariables.split(" ");
		
		int tokenCount = patternParts.length;
		int characterCount = patternWithoutVariables.length();
		// the first entry is always empty so remove it, regex does not fit 100% 
		int upperCaseCharacterCount = patternWithoutVariables.split("(?=\\p{Lu})").length - 1; 
		
		// all characters minus the number of whitespaces divided by number of tokens
		// number of whitespaces is one smaller then token count
		double averageTokenLength = (double)(characterCount - (tokenCount - 1)) / (double) tokenCount;
		
		pair.getPattern().getFeatures().put(FeatureFactory.getInstance().getFeature("CHARACTER_COUNT"), Double.valueOf(characterCount));
		pair.getPattern().getFeatures().put(FeatureFactory.getInstance().getFeature("TOKEN_COUNT"), Double.valueOf(tokenCount));
		pair.getPattern().getFeatures().put(FeatureFactory.getInstance().getFeature("UPPERCASE_LETTER_COUNT"), Double.valueOf(upperCaseCharacterCount));
		pair.getPattern().getFeatures().put(FeatureFactory.getInstance().getFeature("AVERAGE_TOKEN_LENGHT"), averageTokenLength);
	}
}
