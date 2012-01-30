package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper;

import java.util.HashMap;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.Feature;
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
	private static Map<String,Feature> featureMap = null;
	
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
			featureMap = new HashMap<String,Feature>();
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
	public Feature getConfidenceMeasure(Class<Feature> clazz) {
		
		FeatureFactory.logger.debug("There is/are " + FeatureFactory.featureMap.size() + " confidence measure(s) available!");
		
		for ( Feature confidenceMeasure : FeatureFactory.featureMap.values() ) {

			FeatureFactory.logger.debug("Feature: " + confidenceMeasure.getClass().getName() + " is available!");
		}
		
		Feature pe = FeatureFactory.featureMap.get(clazz.getName());
		
		if ( pe == null ) {
			
			FeatureFactory.logger.debug("Can't create requested confidence measure: " + clazz.getName());
		}
		return pe; 
	}

	/**
	 * @param featureMap the featureMap to set
	 */
	public void setFeatureMap(Map<String,Feature> featureMap) {

		FeatureFactory.featureMap = featureMap;
	}

	/**
	 * @return the featureMap
	 */
	public Map<String,Feature> getFeatureMap() {

		Map<String,Feature> map = new HashMap<String,Feature>();
		for ( Map.Entry<String,Feature> entry : FeatureFactory.featureMap.entrySet() ) {
			
			try {
				
				map.put(entry.getKey(), entry.getValue().getClass().newInstance());
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
}
