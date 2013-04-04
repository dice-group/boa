/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.concurrent.PatternMappingGeneralizedPatternPair;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.context.Context;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.AbstractFeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl.Feature;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;

/**
 * @author gerb
 *
 */
public class PartOfSpeechFeatureExtractor extends AbstractFeatureExtractor {

	@Override
	public void score(PatternMappingGeneralizedPatternPair pair) {
		
		if ( NLPediaSettings.BOA_LANGUAGE.equals("en") ) this.processEnglishPair(pair);
		else if ( NLPediaSettings.BOA_LANGUAGE.equals("de") ) this.processGermanPair(pair);
	}

	private void processGermanPair(PatternMappingGeneralizedPatternPair pair) {
		
		SummaryStatistics pposatStat = new SummaryStatistics();
		SummaryStatistics ppossStat = new SummaryStatistics();
		SummaryStatistics nnCountStat = new SummaryStatistics();
		SummaryStatistics neCountStat = new SummaryStatistics();
		SummaryStatistics verbCountStat = new SummaryStatistics();
		
		for ( Pattern pattern : pair.getGeneralizedPattern().getPatterns() ) {
			
			String pos = pattern.getPosTaggedString();
			
			// meins, deiner
			setValue(pattern, "PPOSAT_COUNT", (double) StringUtils.countMatches(pos, "PPOSAT"), pposatStat);
			// mein [Buch], deine [Mutter]
			setValue(pattern, "PPOSS_COUNT", (double) StringUtils.countMatches(pos, "PPOSS"), ppossStat);
			// Tisch, Herr, [das] Reisen
			setValue(pattern, "NN_COUNT", (double) StringUtils.countMatches(pos, "NN"), nnCountStat);
			// Hans, Hamburg, HSV
			setValue(pattern, "NE_COUNT", (double) StringUtils.countMatches(pos, "NE"), neCountStat);
			// number of verbs
			setValue(pattern, "VERB_COUNT", 
					Double.valueOf(StringUtils.countMatches(pos.replace("PWAV", "").replace("PAV", "").replace("ADV", ""), "V")), verbCountStat);
		}
		
		Map<Feature,Double> features = pair.getGeneralizedPattern().getFeatures();
		features.put(FeatureFactory.getInstance().getFeature("PPOSAT_COUNT"), pposatStat.getMean());
		features.put(FeatureFactory.getInstance().getFeature("PPOSS_COUNT"), ppossStat.getMean());
		features.put(FeatureFactory.getInstance().getFeature("NN_COUNT"), nnCountStat.getMean());
		features.put(FeatureFactory.getInstance().getFeature("NE_COUNT"), neCountStat.getMean());
		features.put(FeatureFactory.getInstance().getFeature("VERB_COUNT"), verbCountStat.getMean());
	}

	private void processEnglishPair(PatternMappingGeneralizedPatternPair pair) {
		
		SummaryStatistics posStat = new SummaryStatistics();
		SummaryStatistics regex1Stat = new SummaryStatistics();
		SummaryStatistics regex2Stat = new SummaryStatistics();
		SummaryStatistics regex3Stat = new SummaryStatistics();
		SummaryStatistics singleNounStat = new SummaryStatistics();
		SummaryStatistics nnpStat = new SummaryStatistics();
		SummaryStatistics verbCountStat = new SummaryStatistics();
		SummaryStatistics goodDomainStat = new SummaryStatistics();
		SummaryStatistics goodRangeStat = new SummaryStatistics();
		
		for ( Pattern pattern : pair.getGeneralizedPattern().getPatterns() ) {
			
			PatternMapping m = pair.getMapping();
			String nlr = pattern.getNaturalLanguageRepresentation();
			String pos = pattern.getPosTaggedString();
			
			// p contains 's (ORGANIZATION 's PERSON)
			setValue(pattern, "POSSESSIVE", nlr.contains("'s") ? 1D : 0D, posStat);
			// p equals ", NN of( the)?" (PERSON , president of (the) ORGANIZATION)
			setValue(pattern, "POS_REGEX_1", pos.matches(",( DT)* NN IN( DT)*") ? 1D : 0D, regex1Stat);
			// p equals ", JJ NN of( the)?" (PERSON , former president of (the) ORGANIZATION)
			setValue(pattern, "POS_REGEX_2", pos.matches(", JJ NN IN( DT)*") ? 1D : 0D, regex2Stat);
			// p equals ", DT( JJ)* NN of( the)?" (PERSON , a (former) president of (the) ORGANIZATION)
			setValue(pattern, "POS_REGEX_3", pos.matches(", DT( JJ)* NN IN( DT)*") ? 1D : 0D, regex3Stat);
			// only word contained in p is NP (ORGANIZATION reporter PERSON)
			setValue(pattern, "SINGLE_NOUN", pos.equals("NN") ? 1D : 0D, singleNounStat);
			// number of nouns
			setValue(pattern, "NNP_COUNT", (double) StringUtils.countMatches(pos, "NNP"), nnpStat);
			// number of verbs
			setValue(pattern, "VERB_COUNT", Double.valueOf(StringUtils.countMatches(pos, "V")), verbCountStat);
			
			Double goodDomain = 0D, goodRange = 0D;
			checkDomainAndRange(m, pattern, goodDomain, goodRange);
			
			// good domain value
			setValue(pattern, "GOOD_PLACE_DOMAIN", goodDomain, goodDomainStat);
			// good range value
			setValue(pattern, "GOOD_PLACE_RANGE", goodRange, goodRangeStat);
		}
		
		Map<Feature,Double> features = pair.getGeneralizedPattern().getFeatures();
		features.put(FeatureFactory.getInstance().getFeature("POSSESSIVE"), posStat.getMean());
		features.put(FeatureFactory.getInstance().getFeature("POS_REGEX_1"), regex1Stat.getMean());
		features.put(FeatureFactory.getInstance().getFeature("POS_REGEX_2"), regex2Stat.getMean());
		features.put(FeatureFactory.getInstance().getFeature("POS_REGEX_3"), regex3Stat.getMean());
		features.put(FeatureFactory.getInstance().getFeature("SINGLE_NOUN"), singleNounStat.getMean());
		features.put(FeatureFactory.getInstance().getFeature("NNP_COUNT"), nnpStat.getMean());
		features.put(FeatureFactory.getInstance().getFeature("VERB_COUNT"), verbCountStat.getMean());
		features.put(FeatureFactory.getInstance().getFeature("GOOD_PLACE_DOMAIN"), goodDomainStat.getMean());
		features.put(FeatureFactory.getInstance().getFeature("GOOD_PLACE_RANGE"), goodRangeStat.getMean());
	}
	
	/**
	 * 
	 * @param m
	 * @param pattern
	 * @param goodDomain
	 * @param goodRange
	 */
	private void checkDomainAndRange(PatternMapping m, Pattern pattern, Double goodDomain, Double goodRange){
		
		String nlrWithVariables = pattern.getNaturalLanguageRepresentationWithoutVariables().trim();
		
		// look at the last word if the domain or range is a place
		boolean isGoodLocation = nlrWithVariables.endsWith(" in") || nlrWithVariables.endsWith(" to") || nlrWithVariables.endsWith(" from") || nlrWithVariables.endsWith(" at");
		
		try {

			if ( m.getProperty() != null ) {
				
				if ( m.getProperty().getRdfsDomain() != null ) {
					
					boolean isGoodLocationDomain = isGoodLocation && Context.namedEntityRecognitionMappings.get(m.getProperty().getRdfsDomain()).equals(Constants.NAMED_ENTITY_TAG_PLACE);
					goodDomain = isGoodLocationDomain ? 1D : 0D;
//					pattern.getFeatures().put(FeatureFactory.getInstance().getFeature("GOOD_PLACE_DOMAIN"), );
				}
				
				if ( m.getProperty().getRdfsRange() != null ) {
					
					boolean isGoodLocationRange	 =  isGoodLocation && Context.namedEntityRecognitionMappings.get(m.getProperty().getRdfsRange()).equals(Constants.NAMED_ENTITY_TAG_PLACE);
					goodRange = isGoodLocationRange ? 1D : 0D;
//					pattern.getFeatures().put(FeatureFactory.getInstance().getFeature("GOOD_PLACE_RANGE"),  isGoodLocationRange  ? 1D : 0D);
				}
			}
		}
		catch ( java.lang.NullPointerException npe) {
			
			System.out.println("Property-URI    : " + m.getProperty().getUri());
			System.out.println("Property-Domain : " + m.getProperty().getRdfsDomain());
			System.out.println("Property-Range  : " + m.getProperty().getRdfsRange());
			System.out.println("Domain-Mapping  : " + Context.namedEntityRecognitionMappings.get(m.getProperty().getRdfsDomain()));
			System.out.println("Range-Mapping   : " + Context.namedEntityRecognitionMappings.get(m.getProperty().getRdfsRange()));
		}
	}
}
