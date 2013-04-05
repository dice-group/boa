package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.FeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl.Feature;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;


/**
 * @author Daniel Gerber
 */
public class FeatureFactory {

	private static final NLPediaLogger logger = new NLPediaLogger(FeatureFactory.class);

	/**
	 * singleton
	 */
	private static FeatureFactory INSTANCE = null;
	
	/**
	 * Ordered list for all confidence measures
	 */
	private Map<String,FeatureExtractor> featureExtractorMap = new HashMap<String,FeatureExtractor>();
	private static Set<Feature> handeledfeatures;
	
	/**
	 * don't allow construction of this factory 
	 */
	private FeatureFactory() {
		
		logger.debug(FeatureFactory.class.getName() + " initialized");
	}
	
	/**
	 * @return the FeatureFactory singleton
	 */
	public static FeatureFactory getInstance() {
		
		// check if singleton already initialized
		if ( FeatureFactory.INSTANCE == null ) {
			
			FeatureFactory.INSTANCE = new FeatureFactory();
		}
		
		return FeatureFactory.INSTANCE;
	}
	
	/**
	 * Returns the pattern confidence measures specified by the class. If no 
	 * confidence measures was found null is returned.
	 * 
	 * @param clazz - the class for the confidence measure
	 * @return the requested pattern filter or null if not found
	 */
	public FeatureExtractor getConfidenceMeasure(Class<FeatureExtractor> clazz) {
		
		FeatureFactory.logger.debug("There is/are " + featureExtractorMap.size() + " confidence measure(s) available!");
		
		for ( FeatureExtractor confidenceMeasure : featureExtractorMap.values() ) {

			FeatureFactory.logger.debug("FeatureExtractor: " + confidenceMeasure.getClass().getName() + " is available!");
		}
		
		FeatureExtractor pe = featureExtractorMap.get(clazz.getName());
		
		if ( pe == null ) {
			
			FeatureFactory.logger.debug("Can't create requested confidence measure: " + clazz.getName());
		}
		return pe; 
	}

	/**
	 * @param featureExtractorMap the featureExtractorMap to set
	 */
	public void setFeatureExtractorMap(Map<String,FeatureExtractor> featureMap) {

		featureExtractorMap = featureMap;
	}

	/**
	 * @return the featureExtractorMap
	 */
	public Map<String,FeatureExtractor> getFeatureExtractorMap() {

		Map<String,FeatureExtractor> map = new TreeMap<String,FeatureExtractor>();
		for ( Map.Entry<String,FeatureExtractor> entry : featureExtractorMap.entrySet() ) {
			
			try {
				
			    FeatureExtractor fe = entry.getValue().getClass().newInstance();
			    fe.setHandeledFeatures(entry.getValue().getHandeledFeatures());
			    fe.setActivated(entry.getValue().isActivated());
			    
				map.put(entry.getKey(), fe);
			}
			catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return map;
	}

    /**
     * 
     * @param key
     * @return
     */
    public Feature getFeature(String key) {

    	for (FeatureExtractor extractor : featureExtractorMap.values()) 
            for ( Feature feature : extractor.getHandeledFeatures() ) 
            	if ( feature.toString().equals(key) ) return feature;
    	
    	throw new RuntimeException("Feature \""+key+"\" not found!");
    }

    /**
     * @return all available features
     */
    public Set<Feature> getHandeldFeatures() {

        if ( handeledfeatures == null ) {
            
            handeledfeatures = new HashSet<Feature>();
            for (FeatureExtractor extractor : featureExtractorMap.values()) {
                
                handeledfeatures.addAll(extractor.getHandeledFeatures());
            }
        }
        return handeledfeatures;
    }
}
