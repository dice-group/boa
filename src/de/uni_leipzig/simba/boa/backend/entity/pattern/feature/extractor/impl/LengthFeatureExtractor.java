package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import de.uni_leipzig.simba.boa.backend.concurrent.PatternMappingGeneralizedPatternPair;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.AbstractFeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl.Feature;


public class LengthFeatureExtractor extends AbstractFeatureExtractor {

	@Override
	public void score(PatternMappingGeneralizedPatternPair pair) {
		
		SummaryStatistics characterCountStat = new SummaryStatistics();
		SummaryStatistics tokenCountStat = new SummaryStatistics();
		SummaryStatistics upperCaseLetterCountStat = new SummaryStatistics();
		SummaryStatistics averageTokenLengthStat = new SummaryStatistics();
		SummaryStatistics digitCountStat = new SummaryStatistics();
		SummaryStatistics commaStat = new SummaryStatistics();
		SummaryStatistics nonAlphaSpaceCountStat = new SummaryStatistics();
		
		for ( Pattern pattern : pair.getGeneralizedPattern().getPatterns() ) {
			
			String patternWithoutVariables = pattern.getNaturalLanguageRepresentationWithoutVariables();
			String[] patternParts = patternWithoutVariables.split(" ");
			
			int tokenCount = patternParts.length;
			int characterCount = patternWithoutVariables.length();
			// the first entry is always empty so remove it, regex does not fit 100% 
			int upperCaseCharacterCount = patternWithoutVariables.split("(?=\\p{Lu})").length - 1; 
			
			// all characters minus the number of whitespaces divided by number of tokens
			// number of whitespaces is one smaller then token count
			double averageTokenLength = (double)(characterCount - (tokenCount - 1)) / (double) tokenCount;
			
			// count the number of all digits in a pattern
			int digitCount = 0;
			int nonAlphaSpaceCharacterCount = 0;
			for (int i = 0; i < patternWithoutVariables.length(); i++) {
				
				if (Character.isDigit(patternWithoutVariables.charAt(i))) digitCount++;
				if (!StringUtils.isAlphaSpace(patternWithoutVariables.charAt(i)+"")) nonAlphaSpaceCharacterCount++;
			}
			
			setValue(pattern, "CHARACTER_COUNT", Double.valueOf(characterCount), characterCountStat);
			setValue(pattern, "TOKEN_COUNT", Double.valueOf(tokenCount), tokenCountStat);
			setValue(pattern, "UPPERCASE_LETTER_COUNT", Double.valueOf(upperCaseCharacterCount), upperCaseLetterCountStat);
			setValue(pattern, "AVERAGE_TOKEN_LENGHT", averageTokenLength, averageTokenLengthStat);
			setValue(pattern, "DIGIT_COUNT", Double.valueOf(digitCount), digitCountStat);
			setValue(pattern, "COMMA_COUNT", Double.valueOf(StringUtils.countMatches(patternWithoutVariables, ",")), commaStat);
			setValue(pattern, "NON_ALPHA_SPACE_COUNT", Double.valueOf(nonAlphaSpaceCharacterCount), nonAlphaSpaceCountStat);
		}
		
		Map<Feature,Double> features = pair.getGeneralizedPattern().getFeatures();
		features.put(FeatureFactory.getInstance().getFeature("CHARACTER_COUNT"), characterCountStat.getMean());
		features.put(FeatureFactory.getInstance().getFeature("TOKEN_COUNT"), tokenCountStat.getMean());
		features.put(FeatureFactory.getInstance().getFeature("UPPERCASE_LETTER_COUNT"), upperCaseLetterCountStat.getMean());
		features.put(FeatureFactory.getInstance().getFeature("AVERAGE_TOKEN_LENGHT"), averageTokenLengthStat.getMean());
		features.put(FeatureFactory.getInstance().getFeature("DIGIT_COUNT"), digitCountStat.getMean());
		features.put(FeatureFactory.getInstance().getFeature("COMMA_COUNT"), commaStat.getMean());
		features.put(FeatureFactory.getInstance().getFeature("NON_ALPHA_SPACE_COUNT"), nonAlphaSpaceCountStat.getMean());
	}
}
