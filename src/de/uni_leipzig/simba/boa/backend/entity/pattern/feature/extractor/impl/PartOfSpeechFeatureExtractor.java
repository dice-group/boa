/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.impl;

import org.apache.commons.lang3.StringUtils;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.concurrent.PatternMappingPatternPair;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.context.Context;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.AbstractFeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;

/**
 * @author gerb
 *
 */
public class PartOfSpeechFeatureExtractor extends AbstractFeatureExtractor {

	@Override
	public void score(PatternMappingPatternPair pair) {
		
		if ( NLPediaSettings.BOA_LANGUAGE.equals("en") ) this.processEnglishPair(pair);
		else if ( NLPediaSettings.BOA_LANGUAGE.equals("de") ) this.processGermanPair(pair);
	}

	private void processGermanPair(PatternMappingPatternPair pair) {
		
		PatternMapping m = pair.getMapping();
		String nlr = pair.getPattern().getNaturalLanguageRepresentation();
		String pos = pair.getPattern().getPosTaggedString();
		
		// meins, deiner
		pair.getPattern().getFeatures().put(FeatureFactory.getInstance().getFeature("PPOSAT_COUNT"), (double) StringUtils.countMatches(pos, "PPOSAT"));
		// mein [Buch], deine [Mutter]
		pair.getPattern().getFeatures().put(FeatureFactory.getInstance().getFeature("PPOSS_COUNT"), (double) StringUtils.countMatches(pos, "PPOSS"));
		// Tisch, Herr, [das] Reisen
		pair.getPattern().getFeatures().put(FeatureFactory.getInstance().getFeature("NN_COUNT"), (double) StringUtils.countMatches(pos, "NN"));
		// Hans, Hamburg, HSV
		pair.getPattern().getFeatures().put(FeatureFactory.getInstance().getFeature("NE_COUNT"), (double) StringUtils.countMatches(pos, "NE"));
		
		// number of verbs
		pair.getPattern().getFeatures().put(FeatureFactory.getInstance().getFeature("VERB_COUNT"), 
		        Double.valueOf(StringUtils.countMatches(pos.replace("PWAV", "").replace("PAV", "").replace("ADV", ""), "V")));
	}

	private void processEnglishPair(PatternMappingPatternPair pair) {
		
		PatternMapping m = pair.getMapping();
		String nlr = pair.getPattern().getNaturalLanguageRepresentation();
		String pos = pair.getPattern().getPosTaggedString();
		
		// p contains 's (ORGANIZATION 's PERSON)
		pair.getPattern().getFeatures().put(FeatureFactory.getInstance().getFeature("POSSESSIVE"), 
				nlr.contains("'s") ? 1D : 0D);
		
		// p equals ", NN of( the)?" (PERSON , president of (the) ORGANIZATION)
		pair.getPattern().getFeatures().put(FeatureFactory.getInstance().getFeature("POS_REGEX_1"), 
				pos.matches(",( DT)* NN IN( DT)*") ? 1D : 0D);
		
		// p equals ", JJ NN of( the)?" (PERSON , former president of (the) ORGANIZATION)
		pair.getPattern().getFeatures().put(FeatureFactory.getInstance().getFeature("POS_REGEX_2"), 
				pos.matches(", JJ NN IN( DT)*") ? 1D : 0D);
		
		// p equals ", DT( JJ)* NN of( the)?" (PERSON , a (former) president of (the) ORGANIZATION)
		pair.getPattern().getFeatures().put(FeatureFactory.getInstance().getFeature("POS_REGEX_3"), 
				pos.matches(", DT( JJ)* NN IN( DT)*") ? 1D : 0D);
		
		// only word contained in p is NP (ORGANIZATION reporter PERSON)
		pair.getPattern().getFeatures().put(FeatureFactory.getInstance().getFeature("SINGLE_NOUN"), 
				pos.equals("NN") ? 1D : 0D);
		
		// number of nouns
		pair.getPattern().getFeatures().put(FeatureFactory.getInstance().getFeature("NNP_COUNT"), 
				(double) StringUtils.countMatches(pos, "NNP"));
		
		// number of verbs
		pair.getPattern().getFeatures().put(
		        FeatureFactory.getInstance().getFeature("VERB_COUNT"), 
		        Double.valueOf(StringUtils.countMatches(pos, "V")));
		
		String nlrWithVariables = pair.getPattern().getNaturalLanguageRepresentationWithoutVariables().trim();
		
		pair.getPattern().getFeatures().put(FeatureFactory.getInstance().getFeature("GOOD_PLACE_DOMAIN"), 0D);
		pair.getPattern().getFeatures().put(FeatureFactory.getInstance().getFeature("GOOD_PLACE_RANGE"),  0D);
		
		// look at the last word if the domain or range is a place
		boolean isGoodLocation = nlrWithVariables.endsWith(" in") || nlrWithVariables.endsWith(" to") || nlrWithVariables.endsWith(" from") || nlrWithVariables.endsWith(" at");
		
		try {

			if ( m.getProperty() != null ) {
				
				if ( m.getProperty().getRdfsDomain() != null ) {
					
					boolean isGoodLocationDomain =  isGoodLocation && Context.namedEntityRecognitionMappings.get(m.getProperty().getRdfsDomain()).equals(Constants.NAMED_ENTITY_TAG_PLACE);
					pair.getPattern().getFeatures().put(FeatureFactory.getInstance().getFeature("GOOD_PLACE_DOMAIN"), isGoodLocationDomain ? 1D : 0D);
				}
				
				if ( m.getProperty().getRdfsRange() != null ) {
					
					boolean isGoodLocationRange	 =  isGoodLocation && Context.namedEntityRecognitionMappings.get(m.getProperty().getRdfsRange()).equals(Constants.NAMED_ENTITY_TAG_PLACE);
					pair.getPattern().getFeatures().put(FeatureFactory.getInstance().getFeature("GOOD_PLACE_RANGE"),  isGoodLocationRange  ? 1D : 0D);
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
