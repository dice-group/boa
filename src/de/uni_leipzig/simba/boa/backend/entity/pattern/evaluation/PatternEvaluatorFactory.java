package de.uni_leipzig.simba.boa.backend.entity.pattern.evaluation;

import java.util.HashMap;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.configuration.Initializeable;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;


/**
 * @author Daniel Gerber
 */
public class PatternEvaluatorFactory {

	private static final NLPediaLogger logger = new NLPediaLogger(PatternEvaluatorFactory.class);

	/**
	 * singleton
	 */
	private static PatternEvaluatorFactory INSTANCE = null;
	
	/**
	 * Ordered list for all pattern evalutors
	 */
	private static Map<String,PatternEvaluator> patternEvaluatorMap = null;
	
	/**
	 * don't allow construction of this factory 
	 */
	private PatternEvaluatorFactory() {
		
		logger.debug(PatternEvaluatorFactory.class.getName() + " initialized");
	}
	
	/**
	 * @return the PatternEvaluatorFactory singleton
	 */
	public static PatternEvaluatorFactory getInstance() {
		
		// check if singleton already initialized
		if ( PatternEvaluatorFactory.INSTANCE == null ) {
			
			PatternEvaluatorFactory.INSTANCE = new PatternEvaluatorFactory();
			patternEvaluatorMap = new HashMap<String,PatternEvaluator>();
		}
		
		return PatternEvaluatorFactory.INSTANCE;
	}
	
	/**
	 * Returns the pattern evaluator specified by the class. If no 
	 * evaluator was found null is returned.
	 * 
	 * @param clazz - the class for the evaluator
	 * @return the requested pattern evaluator or null if not found
	 */
	public PatternEvaluator getPatternEvaluator(Class clazz) {
		
		PatternEvaluatorFactory.logger.debug("There is/are " + PatternEvaluatorFactory.patternEvaluatorMap.size() + " evaluator(s) available!");
		
		for ( PatternEvaluator evaluator : PatternEvaluatorFactory.patternEvaluatorMap.values() ) {

			PatternEvaluatorFactory.logger.debug("PatternEvaluator: " + evaluator.getClass().getName() + " is available!");
		}
		
		PatternEvaluator pe = PatternEvaluatorFactory.patternEvaluatorMap.get(clazz.getName());
		
		if ( pe == null ) {
			
			PatternEvaluatorFactory.logger.debug("Can't create requested pattern evaluator: " + clazz.getName());
		}
		return pe; 
	}

	/**
	 * @param patternEvaluatorMap the patternEvaluatorMap to set
	 */
	public void setPatternEvaluatorMap(Map<String,PatternEvaluator> patternEvaluatorMap) {

		PatternEvaluatorFactory.patternEvaluatorMap = patternEvaluatorMap;
	}

	/**
	 * @return the patternEvaluatorMap
	 */
	public Map<String,PatternEvaluator> getPatternEvaluatorMap() {

		// make sure to return only initialized evaluators
		for (PatternEvaluator pe : PatternEvaluatorFactory.patternEvaluatorMap.values() ) {
			
			if ( !((Initializeable) pe).isInitialized() ) {
				
				PatternEvaluatorFactory.logger.debug("Begin initialization of " + pe.getClass().getName() + "!");
				((Initializeable) pe).initialize();
				PatternEvaluatorFactory.logger.debug("Initialization of " + pe.getClass().getName() + " finished!");
			}
		}
		
		return patternEvaluatorMap;
	}
}
