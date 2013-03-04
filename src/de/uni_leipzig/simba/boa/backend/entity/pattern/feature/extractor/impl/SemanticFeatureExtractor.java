/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.impl;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.concurrent.PatternMappingPatternPair;
import de.uni_leipzig.simba.boa.backend.entity.context.Context;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.AbstractFeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;

/**
 * @author gerb
 *
 */
public class SemanticFeatureExtractor extends AbstractFeatureExtractor {

	@Override
	public void score(PatternMappingPatternPair pair) {
		
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
		
		String nlrWithVariables = pair.getPattern().getNaturalLanguageRepresentationWithoutVariables().trim();
		
		pair.getPattern().getFeatures().put(FeatureFactory.getInstance().getFeature("GOOD_PLACE_DOMAIN"), 0D);
		pair.getPattern().getFeatures().put(FeatureFactory.getInstance().getFeature("GOOD_PLACE_RANGE"),  0D);
		
		// look at the last word if the domain or range is a place
		boolean isGoodLocation = nlrWithVariables.endsWith(" in") || nlrWithVariables.endsWith(" to") || nlrWithVariables.endsWith(" from") || nlrWithVariables.endsWith(" at");
		
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
}
