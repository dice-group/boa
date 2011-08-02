package de.uni_leipzig.simba.boa.backend.entity.pattern.filter;

import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

/**
 * 
 * @author Daniel Gerber
 */
public class PatternFilteringThread extends Thread {

	private List<PatternMapping> patternMappings = null;
	
	private final NLPediaLogger logger = new NLPediaLogger(PatternFilteringThread.class);

	public PatternFilteringThread(List<PatternMapping> list) {

		this.patternMappings = list;
	}
	
	@Override
	public void run() {
		
		Map<String,PatternFilter> patternEvaluators = PatternFilterFactory.getInstance().getPatternFilterMap();
		
		// go through all evaluators
		for ( PatternFilter patternEvaluator : patternEvaluators.values() ) {

			this.logger.info(patternEvaluator.getClass().getSimpleName() + " started from " + this.getName() +"!");
			
			// and check each pattern mapping
			for (PatternMapping patternMapping : patternMappings ) {
			
				this.logger.debug("Evaluating pattern: " + patternMapping);
				patternEvaluator.filterPattern(patternMapping);
			}
			this.logger.info(patternEvaluator.getClass().getSimpleName() + " from " + this.getName() + " finished!");
		}
	}
	
	public List<PatternMapping> getEvaluatedPatternMappings(){
		
		return this.patternMappings;
	}
}
