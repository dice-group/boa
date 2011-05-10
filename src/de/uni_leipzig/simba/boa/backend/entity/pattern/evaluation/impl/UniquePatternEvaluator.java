package de.uni_leipzig.simba.boa.backend.entity.pattern.evaluation.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.configuration.Initializeable;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.PatternEvaluationCommand;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.evaluation.PatternEvaluator;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;


public class UniquePatternEvaluator extends Initializeable implements PatternEvaluator {

	private final NLPediaLogger logger					= new NLPediaLogger(StopWordPatternEvaluator.class);
	private List<PatternMapping> mappings				= null;
	private Map<String, Integer> patterns						= null;
	
	/**
	 * 
	 */
	public UniquePatternEvaluator() {}
	
	@Override
	public void initialize() {

		// we need to read all pattern mappings into RAM to avoid querying for every pattern
		this.mappings = PatternEvaluationCommand.patternMappingList;
		this.patterns = new HashMap<String, Integer>();
		
		int i = 0;
		
		for ( PatternMapping patternMapping : this.mappings ) {
			
			System.out.println("Iteration: " + i++);
			
			for ( Pattern p : patternMapping.getPatterns() ) {
				
				if ( this.patterns.containsKey(p.getNaturalLanguageRepresentation())) {
					
					this.patterns.put(p.getNaturalLanguageRepresentation(), this.patterns.get(p.getNaturalLanguageRepresentation()) + 1);
				}
				else {
					
					this.patterns.put(p.getNaturalLanguageRepresentation(), 1);
				}
			}
		}
	}

	@Override
	public void evaluatePattern(PatternMapping patternMapping) {

		for ( Pattern p : patternMapping.getPatterns() ) {
			
			if ( p.isUseForPatternEvaluation() ) {
				
				if ( !this.isUniquePatternByNaturalLanguageRepresentation(p.getNaturalLanguageRepresentation()) ) {
					
					p.setUseForPatternEvaluation(false);
					
					System.out.println("Pattern " + p.getNaturalLanguageRepresentation() + " is not unique.");
					this.logger.debug("Pattern " + p.getNaturalLanguageRepresentation() + " is not unique.");
				}
			}
		}
	}
	
	private boolean isUniquePatternByNaturalLanguageRepresentation(String naturalLanguageRepresentation) {

		if ( this.patterns.get(naturalLanguageRepresentation) > 1 ) return false;
		return true;
	}
}
