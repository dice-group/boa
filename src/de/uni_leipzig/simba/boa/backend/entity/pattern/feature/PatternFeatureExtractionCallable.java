package de.uni_leipzig.simba.boa.backend.entity.pattern.feature;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.concurrent.BoaCallable;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.featureextraction.FeatureExtractionPair;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

/**
 * 
 * @author Daniel Gerber
 */
public class PatternFeatureExtractionCallable extends BoaCallable<FeatureExtractionPair> {

	private List<FeatureExtractionPair> patternMappingPatterns = null;
	private Map<String,Feature> features = FeatureFactory.getInstance().getFeatureMap();
	
	private final NLPediaLogger logger = new NLPediaLogger(PatternFeatureExtractionCallable.class);

	public PatternFeatureExtractionCallable(List<FeatureExtractionPair> featureExtractionPairsSubList) {
	    
		this.patternMappingPatterns = featureExtractionPairsSubList;
		this.logger.info("Pattern to measure: " + this.patternMappingPatterns.size() + " with " + features.size() + " features!");
	}
	
	@Override
	public Collection<FeatureExtractionPair> call() {
		
		// go through all features
		for ( Feature feature : features.values() ) {

			this.logger.info(feature.getClass().getSimpleName() + " started from " + this.getName() +"!");
			long start = new Date().getTime();
			
			// do feature score with respect to each pattern mapping
			for (FeatureExtractionPair pair : patternMappingPatterns) {
				
				this.logger.debug(feature.getClass().getSimpleName() + "-calculation for: " + pair.getMapping().getProperty().getUri() + " / " + pair.getPattern().getNaturalLanguageRepresentation());
				feature.score(pair);
				this.progress++;
			}
			this.logger.info(feature.getClass().getSimpleName() + " from " + this.getName() + " finished in " + (new Date().getTime() - start) + "ms!");
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
}
