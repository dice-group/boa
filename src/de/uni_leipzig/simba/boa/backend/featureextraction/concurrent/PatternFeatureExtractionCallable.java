package de.uni_leipzig.simba.boa.backend.featureextraction.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.concurrent.BoaCallable;
import de.uni_leipzig.simba.boa.backend.concurrent.PatternMappingPatternPair;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.FeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.util.TimeUtil;

/**
 * 
 * @author Daniel Gerber
 */
public class PatternFeatureExtractionCallable extends BoaCallable<PatternMappingPatternPair> {

    private final NLPediaLogger logger = new NLPediaLogger(PatternFeatureExtractionCallable.class);
    
    int numberOfActivatedFeatureExtractors = 0;
    private List<PatternMappingPatternPair> patternMappingPatterns = null;
	private Map<String,FeatureExtractor> featureExtractorsMapping = FeatureFactory.getInstance().getFeatureExtractorMap();
	
	public PatternFeatureExtractionCallable(List<PatternMappingPatternPair> featureExtractionPairsSubList) {
	    
		this.patternMappingPatterns = featureExtractionPairsSubList;
		this.logger.info("Pattern to measure: " + this.patternMappingPatterns.size() + " with " + featureExtractorsMapping.size() + " featureExtractorsMapping!");
		
        for ( FeatureExtractor fe : featureExtractorsMapping.values() ) if (fe.isActivated()) numberOfActivatedFeatureExtractors++;
		
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
	    List<FeatureExtractor> featureExtractors = new ArrayList<FeatureExtractor>(featureExtractorsMapping.values());
	    Collections.shuffle(featureExtractors);
	    
		// go through all featureExtractorsMapping
		for ( FeatureExtractor featureExtractor : featureExtractors ) {
		    
		    if ( featureExtractor.isActivated() ) {
		        
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
		    else {
		        
		        this.logger.info(featureExtractor.getClass().getSimpleName() + " is deactivated and will not be started!");
		    }
		}
		
		return patternMappingPatterns;
	}
	
	@Override
	public double getProgress() {

		return (double) (this.progress) / (this.patternMappingPatterns.size() * numberOfActivatedFeatureExtractors);
	}

    @Override
    public int getNumberTotal() {

        return this.patternMappingPatterns.size() * featureExtractorsMapping.values().size();
    }

    @Override
    public int getNumberOfResultsSoFar() {

        return -1;
    }
}
