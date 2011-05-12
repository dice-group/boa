package de.uni_leipzig.simba.boa.backend.entity.pattern.evaluation;

import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

/**
 * 
 * @author Daniel Gerber
 */
public class PatternEvaluationThread extends Thread {

	private List<PatternMapping> patternMappings = null;
	
	private final NLPediaLogger logger = new NLPediaLogger(PatternEvaluationThread.class);

	private List<PatternMapping> results;
	
	public PatternEvaluationThread(List<PatternMapping> list, List<PatternMapping> results) {

		this.patternMappings = list;
		this.results = results;
	}
	
	@Override
	public void run() {
		
		Map<String,PatternEvaluator> patternEvaluators = PatternEvaluatorFactory.getInstance().getPatternEvaluatorMap();
		
		// go through all evaluators
		for ( PatternEvaluator patternEvaluator : patternEvaluators.values() ) {

			this.logger.info(patternEvaluator.getClass().getSimpleName() + " started from " + this.getName() +"!");
			
			// and check each pattern mapping
			for (PatternMapping patternMapping : patternMappings ) {
			
				this.logger.debug("Evaluating pattern: " + patternMapping);
				patternEvaluator.evaluatePattern(patternMapping);
			}
			this.logger.info(patternEvaluator.getClass().getSimpleName() + " from " + this.getName() + " finished!");
		}
		this.results.addAll(this.patternMappings);
	}
}
