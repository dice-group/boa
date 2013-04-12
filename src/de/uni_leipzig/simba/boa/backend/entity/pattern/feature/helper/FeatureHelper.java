package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.comparator.PatternComparatorGenerator;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.FeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl.Feature;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

public class FeatureHelper {
    
    private static NLPediaLogger logger = new NLPediaLogger(FeatureHelper.class);
	
	private static Map<Feature,Map<PatternMapping,Double>> localFeatureMaxima;
	private static Map<Feature,Map<PatternMapping,Double>> localFeatureMinima;

	/**
	 * Calculates the local maximum for a mapping and a feature.
	 * 
	 * @param mapping
	 * @param feature
	 * @return
	 */
	private static Double calculateLocalMaximum(PatternMapping mapping, Feature feature){
		
		return Collections.max(mapping.getGeneralizedPatterns(), PatternComparatorGenerator.getPatternFeatureComparator(feature)).getFeatures().get(feature);
	}
	
	/**
	 * Calculates the local maximum for a mapping and a feature.
	 * 
	 * @param mapping
	 * @param feature
	 * @return
	 */
	private static Double calculateLocalMinima(PatternMapping mapping, Feature feature){
		
		return Collections.min(mapping.getGeneralizedPatterns(), PatternComparatorGenerator.getPatternFeatureComparator(feature)).getFeatures().get(feature);
	}
	
	/**
	 * Calculates the maximum of all patterns for all pattern mappings
	 * for a given feature.
	 * 
	 * @param feature - the feature to be examined
	 * @return the maximum value for this feature from all patterns 
	 */
	public static Double getGlobalMaximum(Feature feature){
		
		Double maximum = 0D;
		for (Map.Entry<PatternMapping,Double> entry : FeatureHelper.localFeatureMaxima.get(feature).entrySet()){
		    
		    maximum = Math.max(maximum, entry.getValue());
		}
		return maximum;
	}
	
	public static Double getGlobalMinimum(Feature feature){
		
		Double minimum = 0D;
		for (Map.Entry<PatternMapping,Double> entry : FeatureHelper.localFeatureMinima.get(feature).entrySet()){
		    
		    minimum = Math.min(minimum, entry.getValue());
		}
		return minimum;
	}
	
	/**
     * Returns the specific feature score for the pattern with the 
     * maximum value of this feature.
     * 
     * @param mapping - the mapping which is examined
     * @param feature - the feature of interest
     * @return the maximum feature value of the patterns
     */
	public static Double getLocalMaximum(PatternMapping mapping, Feature feature) {
	    
	    return FeatureHelper.localFeatureMaxima.get(feature).get(mapping);
	}
	
	public static Double getLocalMinimum(PatternMapping mapping, Feature feature) {
	    
	    return FeatureHelper.localFeatureMinima.get(feature).get(mapping);
	}
	
	/**
	 * 
	 * @param mappings
	 */
	public static void createLocalMaximaAndMinima(Set<PatternMapping> mappings) {
	    
	    FeatureHelper.localFeatureMaxima = new HashMap<Feature, Map<PatternMapping,Double>>();
	    FeatureHelper.localFeatureMinima = new HashMap<Feature, Map<PatternMapping,Double>>();
	    
	    logger.info("Starting to generate feature cache!");
	    
	    for ( FeatureExtractor featureExtractor : FeatureFactory.getInstance().getFeatureExtractorMap().values() ) {
	        
	        if ( featureExtractor.isActivated() ) {
	            
	            for ( Feature feature : featureExtractor.getHandeledFeatures() ) {
	            	
	            	if ( feature.getSupportedLanguages().contains(NLPediaSettings.getSystemLanguage()) ) {
	                
		                Map<PatternMapping,Double> maximas = new HashMap<PatternMapping,Double>();
		                Map<PatternMapping,Double> minimas = new HashMap<PatternMapping,Double>();
		                
		                // calculate the local maximum of the current feature for all pattern mappings 
		                for ( PatternMapping mapping : mappings ) {
		                    
		                    // empty patterns wont have a maximum
		                    if ( mapping.getPatterns().size() > 0 ) {
		                        
		                        logger.debug("Starting to generate feature cache for feature: " + feature.getName() + " and mapping : " + mapping.getProperty().getUri() + "  !");
		                        maximas.put(mapping, calculateLocalMaximum(mapping, feature));
		                        minimas.put(mapping, calculateLocalMinima(mapping, feature));
		                        logger.debug("Finished to generate feature cache for feature: " + feature.getName() + " and mapping : " + mapping.getProperty().getUri() + "  !");
		                    }
		                }
		                FeatureHelper.localFeatureMinima.put(feature, minimas);
		                FeatureHelper.localFeatureMaxima.put(feature, maximas);
	            	}
	            }
	        }
	    }
	    
	    logger.info("Finished to generate feature cache!");
	}

	public static void init(Set<PatternMapping> patternMappings) {
		
		createLocalMaximaAndMinima(patternMappings);
	}
}
