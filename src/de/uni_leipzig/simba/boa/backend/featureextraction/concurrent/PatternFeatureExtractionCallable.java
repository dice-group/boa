package de.uni_leipzig.simba.boa.backend.featureextraction.concurrent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uni_leipzig.simba.boa.backend.concurrent.BoaCallable;
import de.uni_leipzig.simba.boa.backend.concurrent.PatternMappingGeneralizedPatternPair;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.FeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.impl.ReverbFeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.impl.TotalOccurrenceFeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.impl.TypicityFeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.util.TimeUtil;

/**
 * 
 * @author Daniel Gerber
 */
public class PatternFeatureExtractionCallable extends BoaCallable<PatternMappingGeneralizedPatternPair> {

    private final NLPediaLogger logger = new NLPediaLogger(PatternFeatureExtractionCallable.class);
    
    int numberOfActivatedFeatureExtractors = 0;
    private List<PatternMappingGeneralizedPatternPair> patternMappingPatterns = null;
	private Map<String,FeatureExtractor> featureExtractorsMapping = FeatureFactory.getInstance().getFeatureExtractorMap();
	private Set<PatternMapping> patternMappings;
	
	public PatternFeatureExtractionCallable(Set<PatternMapping> patternMappings, List<PatternMappingGeneralizedPatternPair> featureExtractionPairsSubList) {
	    
		this.patternMappingPatterns = featureExtractionPairsSubList;
		this.patternMappings = patternMappings;
		this.logger.info("Pattern to measure: " + this.patternMappingPatterns.size() + " with " + featureExtractorsMapping.size() + " featureExtractorsMapping!");
		
		// just for logging purposes
        for ( FeatureExtractor fe : featureExtractorsMapping.values() ) if (fe.isActivated()) numberOfActivatedFeatureExtractors++;
		
		// reset the feature vector for each pair
//		for (PatternMappingPatternPair pair : this.patternMappingPatterns) {
//		    
//		    pair.getPattern().setFeatures(new HashMap<Feature,Double>());
//		}
	}
	
	@Override
	public Collection<PatternMappingGeneralizedPatternPair> call() {
		
	    // if we shuffle the future extractors first, we can avoid that all the extractors run heavy task,
	    // for example a Lucene search, in parallel
	    List<FeatureExtractor> featureExtractors = new ArrayList<FeatureExtractor>(featureExtractorsMapping.values());
	    Collections.shuffle(featureExtractors);
	    
		// go through all featureExtractorsMapping
		for ( FeatureExtractor featureExtractor : featureExtractors ) {
		    
		    if ( featureExtractor.isActivated() && featureExtractor.getLanguages().contains(NLPediaSettings.BOA_LANGUAGE) ) {
		    	
		    	featureExtractor.setPatternMappings(this.patternMappings);
		        
		        this.logger.info(featureExtractor.getClass().getSimpleName() + " started from " + this.getName() +"!");
	            long start = System.currentTimeMillis();
	            
	            // do feature score with respect to each pattern mapping
	            for (PatternMappingGeneralizedPatternPair pair : patternMappingPatterns) {
	                
	                this.logger.debug(featureExtractor.getClass().getSimpleName() + "/" + this.name + ": " + pair.getMapping().getProperty().getUri() + " / " + pair.getGeneralizedPattern().getNaturalLanguageRepresentation());
	                try {
	                	
	                	featureExtractor.score(pair);
	                }
	                catch ( Exception e) {
	                	
	                	System.out.println("Error for mapping in " + featureExtractor.getClass().getSimpleName() + ":\t"  + pair.getMapping().getProperty().getUri() + " & " + pair.getGeneralizedPattern().getNaturalLanguageRepresentation() + "\n" + e.getMessage() + " "+ Arrays.toString(e.getStackTrace()));
	                	logger.error("Error for mapping in " + featureExtractor.getClass().getSimpleName() + ":\t"  + pair.getMapping().getProperty().getUri() + " & " + pair.getGeneralizedPattern().getNaturalLanguageRepresentation(), e);
	                }
	                this.progress++;
	            }
	            // this features require a pattern searcher, which needs to be closed
			    // and we can only do it after the extractor has finished because otherwise we would close it after every pattern
			    if ( featureExtractor instanceof TotalOccurrenceFeatureExtractor ) ((TotalOccurrenceFeatureExtractor) featureExtractor).close();
			    if ( featureExtractor instanceof TypicityFeatureExtractor ) ((TypicityFeatureExtractor) featureExtractor).close();
			    if ( featureExtractor instanceof ReverbFeatureExtractor ) ((ReverbFeatureExtractor) featureExtractor).close();
	            
	            this.logger.info(featureExtractor.getClass().getSimpleName() + " from " + this.getName() + " finished in " + TimeUtil.convertMilliSeconds(System.currentTimeMillis() - start) + "!");
		    }
		    else {
		        
		    	if ( !featureExtractor.isActivated())
		    		this.logger.info(featureExtractor.getClass().getSimpleName() + " is deactivated and will not be started!");
		    	
		    	if ( !featureExtractor.getLanguages().contains(NLPediaSettings.BOA_LANGUAGE) )
		    		this.logger.info(featureExtractor.getClass().getSimpleName() + " is not supposed to run for language: '"+NLPediaSettings.BOA_LANGUAGE+"'");
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

        return this.patternMappingPatterns.size() * numberOfActivatedFeatureExtractors;
    }

    @Override
    public int getNumberOfResultsSoFar() {

        return -1;
    }
}
