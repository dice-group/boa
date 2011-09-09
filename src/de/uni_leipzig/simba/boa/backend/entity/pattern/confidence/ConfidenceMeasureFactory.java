package de.uni_leipzig.simba.boa.backend.entity.pattern.confidence;

import java.util.HashMap;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;


/**
 * @author Daniel Gerber
 */
public class ConfidenceMeasureFactory {

	private static final NLPediaLogger logger = new NLPediaLogger(ConfidenceMeasureFactory.class);

	/**
	 * singleton
	 */
	private static ConfidenceMeasureFactory INSTANCE = null;
	
	/**
	 * Ordered list for all confidence measures
	 */
	private static Map<String,ConfidenceMeasure> confidenceMeasureMap = null;
	
	/**
	 * don't allow construction of this factory 
	 */
	private ConfidenceMeasureFactory() {
		
		logger.debug(ConfidenceMeasureFactory.class.getName() + " initialized");
	}
	
	/**
	 * @return the ConfidenceMeasureFactory singleton
	 */
	public static ConfidenceMeasureFactory getInstance() {
		
		// check if singleton already initialized
		if ( ConfidenceMeasureFactory.INSTANCE == null ) {
			
			ConfidenceMeasureFactory.INSTANCE = new ConfidenceMeasureFactory();
			confidenceMeasureMap = new HashMap<String,ConfidenceMeasure>();
		}
		
		return ConfidenceMeasureFactory.INSTANCE;
	}
	
	/**
	 * Returns the pattern confidence measures specified by the class. If no 
	 * confidence measures was found null is returned.
	 * 
	 * @param clazz - the class for the confidence measure
	 * @return the requested pattern filter or null if not found
	 */
	public ConfidenceMeasure getConfidenceMeasure(Class clazz) {
		
		ConfidenceMeasureFactory.logger.debug("There is/are " + ConfidenceMeasureFactory.confidenceMeasureMap.size() + " confidence measure(s) available!");
		
		for ( ConfidenceMeasure confidenceMeasure : ConfidenceMeasureFactory.confidenceMeasureMap.values() ) {

			ConfidenceMeasureFactory.logger.debug("ConfidenceMeasure: " + confidenceMeasure.getClass().getName() + " is available!");
		}
		
		ConfidenceMeasure pe = ConfidenceMeasureFactory.confidenceMeasureMap.get(clazz.getName());
		
		if ( pe == null ) {
			
			ConfidenceMeasureFactory.logger.debug("Can't create requested confidence measure: " + clazz.getName());
		}
		return pe; 
	}

	/**
	 * @param confidenceMeasureMap the confidenceMeasureMap to set
	 */
	public void setConfidenceMeasureMap(Map<String,ConfidenceMeasure> confidenceMeasureMap) {

		ConfidenceMeasureFactory.confidenceMeasureMap = confidenceMeasureMap;
	}

	/**
	 * @return the confidenceMeasureMap
	 */
	public Map<String,ConfidenceMeasure> getConfidenceMeasureMap() {

		Map<String,ConfidenceMeasure> map = new HashMap<String,ConfidenceMeasure>();
		for ( Map.Entry<String,ConfidenceMeasure> entry : ConfidenceMeasureFactory.confidenceMeasureMap.entrySet() ) {
			
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
