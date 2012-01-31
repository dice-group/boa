package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.impl;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.AbstractFeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.FeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.featureextraction.FeatureExtractionPair;
import de.uni_leipzig.simba.boa.backend.search.PatternSearcher;
import de.uni_leipzig.simba.boa.backend.search.impl.DefaultPatternSearcher;


public class TotalOccurrenceFeatureExtractor extends AbstractFeatureExtractor {

    private final int MAXIMUM_NUMBER_OF_TOTAL_OCCURRENCES = NLPediaSettings.getInstance().getIntegerSetting("maxmimumNumberOfTotalOccurrences");
	private PatternSearcher searcher;
	
	public TotalOccurrenceFeatureExtractor() {

        this.searcher = new DefaultPatternSearcher();
    }
	
	public TotalOccurrenceFeatureExtractor(PatternSearcher patternSearcher) {

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
		
		pattern.getFeatures().put(FeatureFactory.getInstance().getFeature("TOTAL_OCCURRENCE"), Double.valueOf(totalOccurrences));
	}
}
