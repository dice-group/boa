package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.impl;

import org.apache.lucene.search.TotalHitCountCollector;

import de.uni_leipzig.simba.boa.backend.concurrent.PatternMappingPatternPair;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.AbstractFeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.search.PatternSearcher;
import de.uni_leipzig.simba.boa.backend.search.impl.DefaultPatternSearcher;


public class TotalOccurrenceFeatureExtractor extends AbstractFeatureExtractor {

    private final int MAXIMUM_NUMBER_OF_TOTAL_OCCURRENCES = NLPediaSettings.getIntegerSetting("maxmimumNumberOfTotalOccurrences");
	private PatternSearcher searcher;
	
	public TotalOccurrenceFeatureExtractor() {}
	
	public TotalOccurrenceFeatureExtractor(PatternSearcher patternSearcher) {

	    this.searcher = patternSearcher;
    }

    @Override
	public void score(PatternMappingPatternPair pair) {
		
        if ( this.searcher == null ) this.searcher = new DefaultPatternSearcher();
        
		// this is for junit testing
		this.scoreMapping(pair.getPattern());
	}
	
	public void scoreMapping(Pattern pattern) {

		int totalOccurrences = searcher.getTotalHits(pattern.getNaturalLanguageRepresentationWithoutVariables());
		
		pattern.getFeatures().put(FeatureFactory.getInstance().getFeature("TOTAL_OCCURRENCE"), Double.valueOf(totalOccurrences));
	}
}
