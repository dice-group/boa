package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.evaluation.PatternEvaluator;
import de.uni_leipzig.simba.boa.backend.entity.pattern.evaluation.PatternEvaluatorFactory;
import de.uni_leipzig.simba.boa.backend.entity.pattern.evaluation.impl.DomainAndRangeEvaluator;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

/**
 * 
 * @author Daniel Gerber
 */
public class PatternEvaluationCommand implements Command {

	private NLPediaLogger logger = new NLPediaLogger(PatternEvaluationCommand.class);
	
	private PatternMappingDao patternMappingDao = null;
	
	public static List<PatternMapping> patternMappingList = null;

	public PatternEvaluationCommand() {
		
		this.patternMappingDao	= (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
		
		patternMappingList		= this.patternMappingDao.findAllPatternMappings();
	}
	
	@Override
	public void execute() {

		Map<String,PatternEvaluator> patternEvaluators = PatternEvaluatorFactory.getInstance().getPatternEvaluatorMap();
		
		this.logger.debug("Evaluation of " + patternMappingList.size() + " pattern mappings with " + patternEvaluators.size() + " evaluators started!");
		
		// go through all evaluators
		for ( PatternEvaluator patternEvaluator : patternEvaluators.values() ) {

			this.logger.debug(patternEvaluator.getClass().getSimpleName() + " started!");
			
			// and check each pattern mapping
			for (PatternMapping patternMapping : patternMappingList ) {
			
				this.logger.debug("Evaluating pattern: " + patternMapping);
				patternEvaluator.evaluatePattern(patternMapping);
//				this.patternMappingDao.updatePatternMapping(patternMapping);
			}
			this.logger.debug(patternEvaluator.getClass().getSimpleName() + " finished!");
		}
		
		double max = ((DomainAndRangeEvaluator) patternEvaluators.get(DomainAndRangeEvaluator.class.getName())).getMax();
		double min = ((DomainAndRangeEvaluator) patternEvaluators.get(DomainAndRangeEvaluator.class.getName())).getMin();
		
		System.out.println(max);
		System.out.println(min);
		
		for ( PatternMapping mapping : patternMappingList ) {
			
			for (Pattern pattern : mapping.getPatterns() ) {
				
				if ( pattern.isUseForPatternEvaluation() ) {
					
					pattern.setConfidence(pattern.getConfidence() / (max - min));
				}
			}
			this.patternMappingDao.updatePatternMapping(mapping);
		}
	}
}
