package de.uni_leipzig.simba.boa.backend.entity.pattern.filter;

import java.util.HashMap;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;


/**
 * @author Daniel Gerber
 */
public class PatternFilterFactory {

	private static final NLPediaLogger logger = new NLPediaLogger(PatternFilterFactory.class);

	/**
	 * singleton
	 */
	private static PatternFilterFactory INSTANCE = null;
	
	/**
	 * Ordered list for all pattern filters
	 */
	private static Map<String,PatternFilter> patternFilterMap = null;
	
	/**
	 * don't allow construction of this factory 
	 */
	private PatternFilterFactory() {
		
		logger.debug(PatternFilterFactory.class.getName() + " initialized");
	}
	
	/**
	 * @return the PatternFilterFactory singleton
	 */
	public static PatternFilterFactory getInstance() {
		
		// check if singleton already initialized
		if ( PatternFilterFactory.INSTANCE == null ) {
			
			PatternFilterFactory.INSTANCE = new PatternFilterFactory();
			patternFilterMap = new HashMap<String,PatternFilter>();
		}
		
		return PatternFilterFactory.INSTANCE;
	}
	
	/**
	 * Returns the pattern filter specified by the class. If no 
	 * filter was found null is returned.
	 * 
	 * @param clazz - the class for the filter
	 * @return the requested pattern filter or null if not found
	 */
	public PatternFilter getPatternFilter(Class<PatternFilter> clazz) {
		
		PatternFilterFactory.logger.debug("There is/are " + PatternFilterFactory.patternFilterMap.size() + " filter(s) available!");
		
		for ( PatternFilter filter : PatternFilterFactory.patternFilterMap.values() ) {

			PatternFilterFactory.logger.debug("PatternFilter: " + filter.getClass().getName() + " is available!");
		}
		
		PatternFilter pe = PatternFilterFactory.patternFilterMap.get(clazz.getName());
		
		if ( pe == null ) {
			
			PatternFilterFactory.logger.debug("Can't create requested pattern filter: " + clazz.getName());
		}
		return pe; 
	}

	/**
	 * @param patternFilterMap the patternFilterMap to set
	 */
	public void setPatternFilterMap(Map<String,PatternFilter> patternFilterMap) {

		PatternFilterFactory.patternFilterMap = patternFilterMap;
	}

	/**
	 * @return the patternFilterMap
	 */
	public Map<String,PatternFilter> getPatternFilterMap() {

		return patternFilterMap;
	}
}
