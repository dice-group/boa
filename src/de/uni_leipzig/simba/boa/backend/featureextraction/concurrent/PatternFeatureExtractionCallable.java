package de.uni_leipzig.simba.boa.backend.featureextraction.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.concurrent.BoaCallable;
import de.uni_leipzig.simba.boa.backend.concurrent.PatternMappingPatternPair;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.FeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl.Feature;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.util.TimeUtil;

/**
 * 
 * @author Daniel Gerber
 */
public class PatternFeatureExtractionCallable extends BoaCallable<PatternMappingPatternPair> {

    private final NLPediaLogger logger = new NLPediaLogger(PatternFeatureExtractionCallable.class);
    
    private List<PatternMappingPatternPair> patternMappingPatterns = null;
	private Map<String,FeatureExtractor> features = FeatureFactory.getInstance().getFeatureExtractorMap();
	
	public PatternFeatureExtractionCallable(List<PatternMappingPatternPair> featureExtractionPairsSubList) {
	    
		this.patternMappingPatterns = featureExtractionPairsSubList;
		this.logger.info("Pattern to measure: " + this.patternMappingPatterns.size() + " with " + features.size() + " features!");
		
		// reset the feature vector for each pair
//		for (PatternMappingPatternPair pair : this.patternMappingPatterns) {
//		    
//		    pair.getPattern().setFeatures(new HashMap<Feature,Double>());
//		}
	}
	
	@Override
	public Collection<PatternMappingPatternPair> call() {
		
	    // if we shuffle the future extractors first, we can avoid that all the extractors run heavy task,
	    // for example a Lucene search, in parallel
	    List<FeatureExtractor> featureExtractors = new ArrayList<FeatureExtractor>(features.values());
	    Collections.shuffle(featureExtractors);
	    
		// go through all features
		for ( FeatureExtractor featureExtractor : featureExtractors ) {

			this.logger.info(featureExtractor.getClass().getSimpleName() + " started from " + this.getName() +"!");
			long start = System.currentTimeMillis();
			
			// do feature score with respect to each pattern mapping
			for (PatternMappingPatternPair pair : patternMappingPatterns) {
				
				this.logger.debug(featureExtractor.getClass().getSimpleName() + "/" + this.name + ": " + pair.getMapping().getProperty().getUri() + " / " + pair.getPattern().getNaturalLanguageRepresentation());
				featureExtractor.score(pair);
				this.progress++;
			}
			this.logger.info(featureExtractor.getClass().getSimpleName() + " from " + this.getName() + " finished in " + TimeUtil.convertMilliSeconds(System.currentTimeMillis() - start) + "!");
		}
		
		return patternMappingPatterns;
	}
	
	@Override
	public double getProgress() {

		return (double) (this.progress) / (this.patternMappingPatterns.size() * features.values().size());
	}

    @Override
    public int getNumberTotal() {

        return this.patternMappingPatterns.size() * features.values().size();
    }

    @Override
    public int getNumberOfResultsSoFar() {

        return -1;
    }
}
