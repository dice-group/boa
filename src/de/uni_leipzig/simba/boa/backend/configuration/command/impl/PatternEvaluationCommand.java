package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.evaluation.PatternEvaluationThread;
import de.uni_leipzig.simba.boa.backend.entity.pattern.evaluation.PatternEvaluator;
import de.uni_leipzig.simba.boa.backend.entity.pattern.evaluation.PatternEvaluatorFactory;
import de.uni_leipzig.simba.boa.backend.entity.pattern.evaluation.impl.DomainAndRangeEvaluator;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.nlp.NamedEntityRecognizer;
import de.uni_leipzig.simba.boa.backend.search.concurrent.PatternSearchThread;
import de.uni_leipzig.simba.boa.backend.search.concurrent.PrintProgressTask;
import de.uni_leipzig.simba.boa.backend.util.ListUtil;

/**
 * 
 * @author Daniel Gerber
 */
public class PatternEvaluationCommand implements Command {

	private final NLPediaLogger logger = new NLPediaLogger(PatternEvaluationCommand.class);
	
	private PatternMappingDao patternMappingDao = null;
	
	public static List<PatternMapping> patternMappingList = null;

	public PatternEvaluationCommand() {
		
		this.patternMappingDao	= (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
		
		patternMappingList		= this.patternMappingDao.findAllPatternMappings();
	}
	
	@Override
	public void execute() {
		
		int numberOfSearchThreads = new Integer(NLPediaSettings.getInstance().getSetting("numberOfSearchThreads")).intValue();
		
		// split the mappings into several lists
		List<List<PatternMapping>> patternMappingSubLists	= ListUtil.split(patternMappingList, patternMappingList.size() / numberOfSearchThreads);
		Map<String,PatternEvaluator> patternEvaluators		= PatternEvaluatorFactory.getInstance().getPatternEvaluatorMap();
		
		this.logger.debug("Evaluation of " + patternMappingList.size() + " pattern mappings with " + patternEvaluators.size() + " evaluators started!");
		
		List<Thread> threadList = new ArrayList<Thread>();
		List<PatternMapping> evaluatedMappings = new ArrayList<PatternMapping>();
		
		for (int i = 0 ; i < numberOfSearchThreads ; i++ ) {
			
				Thread t = new PatternEvaluationThread(patternMappingSubLists.get(i), evaluatedMappings);
				t.setName("PatternEvaluationThread-" + (i + 1));
				threadList.add(i, t);
				t.start();
				System.out.println(t.getName() + " started!");
				this.logger.info(t.getName() + " started!");
		}
		// wait for all to finish
		for ( Thread t : threadList ) {
			
			try {
				t.join();	
			}
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		double maxWithLog = 0;
		double minWithLog = 1;
		double maxWithoutLog = 0;
		double minWithoutLog = 1;
		
		for (PatternMapping pm : evaluatedMappings ) {
			
			for (Pattern pattern : pm.getPatterns() ) {
				
				maxWithLog = Math.max(maxWithLog, pattern.getWithLogConfidence());
				minWithLog = Math.min(minWithLog, pattern.getWithLogConfidence());
				maxWithoutLog = Math.max(maxWithoutLog, pattern.getWithoutLogConfidence());
				minWithoutLog = Math.min(minWithoutLog, pattern.getWithoutLogConfidence());
			}
		}
		
		double maxMinusMinWithLog 		= minWithLog == -1 		? (maxWithLog - 0) 		: maxWithLog - minWithLog;
		double maxMinusMinWithoutLog 	= minWithoutLog == -1 	? (maxWithoutLog - 0) 	: minWithoutLog - minWithoutLog;
		
		for ( PatternMapping mapping : patternMappingList ) {
			
			for (Pattern pattern : mapping.getPatterns() ) {
				
				if ( pattern.isUseForPatternEvaluation() ) {
					
					pattern.setWithoutLogConfidence(pattern.getWithoutLogConfidence() / (maxMinusMinWithoutLog));
					pattern.setWithLogConfidence(pattern.getWithLogConfidence() / (maxMinusMinWithLog));
				}
			}
			System.out.println("Updating pattern mapping " + mapping.getUri());
			this.patternMappingDao.updatePatternMapping(mapping);
		}
	}
	
	@Deprecated
	public void execute2() {

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
		
		double maxWithLog = ((DomainAndRangeEvaluator) patternEvaluators.get(DomainAndRangeEvaluator.class.getName())).getMaxWithLog();
		double minWithLog = ((DomainAndRangeEvaluator) patternEvaluators.get(DomainAndRangeEvaluator.class.getName())).getMinWithLog();
		double maxWithoutLog = ((DomainAndRangeEvaluator) patternEvaluators.get(DomainAndRangeEvaluator.class.getName())).getMaxWithoutLog();
		double minWithoutLog = ((DomainAndRangeEvaluator) patternEvaluators.get(DomainAndRangeEvaluator.class.getName())).getMinWithoutLog();
		
		double maxMinusMinWithLog = maxWithLog - minWithLog;
		double maxMinusMinWithoutLog = maxWithoutLog - minWithoutLog;
		
		for ( PatternMapping mapping : patternMappingList ) {
			
			for (Pattern pattern : mapping.getPatterns() ) {
				
				if ( pattern.isUseForPatternEvaluation() ) {
					
					pattern.setWithoutLogConfidence(pattern.getWithoutLogConfidence() / (maxMinusMinWithoutLog));
					pattern.setWithLogConfidence(pattern.getWithLogConfidence() / (maxMinusMinWithLog));
				}
			}
			this.patternMappingDao.updatePatternMapping(mapping);
		}
	}
}
