package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.Feature;
import de.uni_leipzig.simba.boa.backend.featureextraction.FeatureExtractionPair;
import de.uni_leipzig.simba.boa.backend.search.PatternSearcher;
import de.uni_leipzig.simba.boa.backend.search.impl.DefaultPatternSearcher;


public class TotalOccurrenceFeature implements Feature {

    private final int MAXIMUM_NUMBER_OF_TOTAL_OCCURRENCES = NLPediaSettings.getInstance().getIntegerSetting("maxmimumNumberOfTotalOccurrences");
	private PatternSearcher searcher;
	
	public TotalOccurrenceFeature() {

        this.searcher = new DefaultPatternSearcher();
    }
	
	public TotalOccurrenceFeature(PatternSearcher patternSearcher) {

	    this.searcher = patternSearcher;
    }

    @Override
	public void score(FeatureExtractionPair pair) {
		
		// this is for junit testing
		this.scoreMapping(pair.getPattern());
	}
	
	public void scoreMapping(Pattern pattern) {

		int totalOccurrences = searcher.getExactMatchSentences(
		        pattern.getNaturalLanguageRepresentationWithoutVariables(), MAXIMUM_NUMBER_OF_TOTAL_OCCURRENCES).size();
		
		pattern.getFeatures().put(
			de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature.TOTAL_OCCURRENCE, 
			Double.valueOf(totalOccurrences));
	}
}
