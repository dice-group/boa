package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl;

import java.util.List;

import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.Feature;


public class LengthFeature implements Feature {

	@Override
	public void score(List<PatternMapping> mappings) {

		// don't do anything here
	}

	@Override
	public void scoreMapping(PatternMapping mapping) {

		for ( Pattern pattern : mapping.getPatterns() ) {
			
			String patternWithoutVariables = pattern.getNaturalLanguageRepresentationWithoutVariables();
			
			int tokenCount = patternWithoutVariables.split(" ").length;
			int characterCount = patternWithoutVariables.length();
			// the first entry is always empty so remove it, regex does not fit 100% 
			int upperCaseCharacterCount = patternWithoutVariables.split("(?=\\p{Lu})").length - 1; 
			
			pattern.getFeatures().put(
					de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature.CHARACTER_COUNT, Double.valueOf(characterCount));
			pattern.getFeatures().put(
					de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature.TOKEN_COUNT, Double.valueOf(tokenCount));
			pattern.getFeatures().put(
					de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature.UPPERCASE_LETTER_COUNT, Double.valueOf(upperCaseCharacterCount));
		}
	}
}
