package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.impl;

import java.util.Map;

import de.uni_leipzig.simba.boa.backend.concurrent.PatternMappingGeneralizedPatternPair;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.AbstractFeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl.Feature;
import de.uni_leipzig.simba.boa.backend.search.PatternSearcher;
import de.uni_leipzig.simba.boa.backend.search.impl.DefaultPatternSearcher;


public class TotalOccurrenceFeatureExtractor extends AbstractFeatureExtractor {

	private PatternSearcher searcher;
	
	/**
	 * needed by spring
	 */
	public TotalOccurrenceFeatureExtractor() {}
	
	/**
	 * 
	 * @param patternSearcher
	 */
	public TotalOccurrenceFeatureExtractor(PatternSearcher patternSearcher) {

	    this.searcher = patternSearcher;
    }

    @Override
	public void score(PatternMappingGeneralizedPatternPair pair) {
		
        if ( this.searcher == null ) this.searcher = new DefaultPatternSearcher();
        
        int totalOccurrences = 0;
        for ( Pattern pattern : pair.getGeneralizedPattern().getPatterns() ) {
        	
        	int occurrences = searcher.getTotalHits(pattern.getNaturalLanguageRepresentationWithoutVariables());
        	totalOccurrences += occurrences;
    		pattern.getFeatures().put(FeatureFactory.getInstance().getFeature("TOTAL_OCCURRENCE"), Math.log(Double.valueOf(occurrences) + 1));
        }
        
        Map<Feature,Double> features = pair.getGeneralizedPattern().getFeatures();
		features.put(FeatureFactory.getInstance().getFeature("TOTAL_OCCURRENCE"), Math.log(Double.valueOf(totalOccurrences) + 1));
	}
	
	public void close() {
		
		this.searcher.close();
	}
}
