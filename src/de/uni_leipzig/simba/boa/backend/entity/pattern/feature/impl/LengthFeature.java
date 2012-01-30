package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl;

import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.Feature;
import de.uni_leipzig.simba.boa.backend.featureextraction.FeatureExtractionPair;


public class LengthFeature implements Feature {

	@Override
	public void score(FeatureExtractionPair pair) {

		String patternWithoutVariables = pair.getPattern().getNaturalLanguageRepresentationWithoutVariables();
		String[] patternParts = patternWithoutVariables.split(" ");
		
		int tokenCount = patternParts.length;
		int characterCount = patternWithoutVariables.length();
		// the first entry is always empty so remove it, regex does not fit 100% 
		int upperCaseCharacterCount = patternWithoutVariables.split("(?=\\p{Lu})").length - 1; 
		
		// all characters minus the number of whitespaces divided by number of tokens
		// number of whitespaces is one smaller then token count
		double averageTokenLength = (double)(characterCount - (tokenCount - 1)) / (double) tokenCount;
		
		pair.getPattern().getFeatures().put(
				de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature.CHARACTER_COUNT, Double.valueOf(characterCount));
		pair.getPattern().getFeatures().put(
				de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature.TOKEN_COUNT, Double.valueOf(tokenCount));
		pair.getPattern().getFeatures().put(
				de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature.UPPERCASE_LETTER_COUNT, Double.valueOf(upperCaseCharacterCount));
		pair.getPattern().getFeatures().put(
				de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature.AVERAGE_TOKEN_LENGHT, averageTokenLength);
	}
}
