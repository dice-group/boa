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
		
		int numberOfSearchThreads = new Integer(NLPediaSettings.getInstance().getSetting("numberOfEvaluationThreads")).intValue();
		
		// split the mappings into several lists
		List<List<PatternMapping>> patternMappingSubLists	= ListUtil.split(patternMappingList, (patternMappingList.size() / numberOfSearchThreads) + 10);
		Map<String,PatternEvaluator> patternEvaluators		= PatternEvaluatorFactory.getInstance().getPatternEvaluatorMap();
		
		this.logger.debug("Evaluation of " + patternMappingList.size() + " pattern mappings with " + patternEvaluators.size() + " evaluators started!");
		
		List<Thread> threadList = new ArrayList<Thread>();
		List<PatternMapping> results = new ArrayList<PatternMapping>();
		
		for (int i = 0 ; i < numberOfSearchThreads ; i++ ) {
			
				Thread t = new PatternEvaluationThread(patternMappingSubLists.get(i));
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
		
		for ( Thread t: threadList ) {
			
			results.addAll(((PatternEvaluationThread)t).getEvaluatedPatternMappings());
		}
		
		double maxWithLog = 0;
		double minWithLog = 1;
		double maxWithoutLog = 0;
		double minWithoutLog = 1;
		
		double maxWithLogWithLogLearnedFrom = 0;
		double minWithLogWithLogLearnedFrom = 1;
		double maxWithLogWithoutLogLearnedFrom = 0;
		double minWithLogWithoutLogLearnedFrom = 1; 
		
		double maxWithoutLogWithLogLearnedFrom = 0;
		double minWithoutLogWithLogLearnedFrom = 1;
		double maxWithoutLogWithoutLogLearnedFrom = 0;
		double minWithoutLogWithoutLogLearnedFrom = 1; 
		
		System.out.println("All evaluation threads are finished. Start to calculate normalized confidence!");
		
		
		for (PatternMapping pm : results ) {
			
			for (Pattern pattern : pm.getPatterns() ) {
				
				if ( pattern.isUseForPatternEvaluation() ) {
					
					maxWithLog 						= Math.max(maxWithLog, pattern.getWithLogConfidence());
					minWithLog 						= Math.min(minWithLog, pattern.getWithLogConfidence());
					maxWithoutLog 					= Math.max(maxWithoutLog, pattern.getWithoutLogConfidence());
					minWithoutLog 					= Math.min(minWithoutLog, pattern.getWithoutLogConfidence());
					
					maxWithLogWithLogLearnedFrom 	= Math.max(maxWithLogWithLogLearnedFrom, 	pattern.getWithLogWithLogLearndFromConfidence());
					minWithLogWithLogLearnedFrom	= Math.min(minWithLogWithLogLearnedFrom, 	pattern.getWithLogWithLogLearndFromConfidence());
					maxWithLogWithoutLogLearnedFrom	= Math.max(maxWithLogWithoutLogLearnedFrom, pattern.getWithLogWithoutLogLearndFromConfidence());
					minWithLogWithoutLogLearnedFrom	= Math.min(minWithLogWithoutLogLearnedFrom, pattern.getWithLogWithoutLogLearndFromConfidence());
					
					maxWithoutLogWithLogLearnedFrom 	= Math.max(maxWithoutLogWithLogLearnedFrom, 	pattern.getWithoutLogWithLogLearndFromConfidence());
					minWithoutLogWithLogLearnedFrom		= Math.min(minWithoutLogWithLogLearnedFrom, 	pattern.getWithoutLogWithLogLearndFromConfidence());
					maxWithoutLogWithoutLogLearnedFrom	= Math.max(maxWithoutLogWithoutLogLearnedFrom, 	pattern.getWithoutLogWithoutLogLearndFromConfidence());
					minWithoutLogWithoutLogLearnedFrom	= Math.min(minWithoutLogWithoutLogLearnedFrom,	pattern.getWithoutLogWithoutLogLearndFromConfidence());
				}
			}
		}
		
		double maxMinusMinWithLog 		= minWithLog == -1 		? (maxWithLog - 0) 		: maxWithLog;// - minWithLog;
		double maxMinusMinWithoutLog 	= minWithoutLog == -1 	? (maxWithoutLog - 0) 	: maxWithoutLog;// - minWithoutLog;
		
		double maxMinusMinWithLogWithLogLearned 	= minWithLogWithLogLearnedFrom == -1 	? (maxWithLogWithLogLearnedFrom - 0) 		: maxWithLogWithLogLearnedFrom;// - minWithLogWithLogLearnedFrom;
		double maxMinusMinWithLogWithoutLogLearned 	= minWithLogWithoutLogLearnedFrom == -1 ? (maxWithLogWithoutLogLearnedFrom - 0) 	: maxWithLogWithoutLogLearnedFrom;// - minWithLogWithoutLogLearnedFrom;
		
		double maxMinusMinWithoutLogWithLogLearned 		= minWithoutLogWithLogLearnedFrom == -1 	? (maxWithoutLogWithLogLearnedFrom - 0) 	: maxWithoutLogWithLogLearnedFrom;// - minWithoutLogWithLogLearnedFrom;
		double maxMinusMinWithoutLogWithoutLogLearned 	= minWithoutLogWithoutLogLearnedFrom == -1 	? (maxWithoutLogWithoutLogLearnedFrom - 0) 	: maxWithoutLogWithoutLogLearnedFrom;// - minWithoutLogWithoutLogLearnedFrom;
		
		System.out.println();
		System.out.println("maxMinusMinWithLog: "+maxMinusMinWithLog);
		System.out.println("maxMinusMinWithLogWithLogLearned: "+maxMinusMinWithLogWithLogLearned);
		System.out.println("maxMinusMinWithLogWithoutLogLearned: "+maxMinusMinWithLogWithoutLogLearned);
		System.out.println();
		System.out.println("maxMinusMinWithoutLog: "+ maxMinusMinWithoutLog);
		System.out.println("maxMinusMinWithoutLogWithLogLearned: "+maxMinusMinWithoutLogWithLogLearned);
		System.out.println("maxMinusMinWithoutLogWithoutLogLearned: "+maxMinusMinWithoutLogWithoutLogLearned);
		System.out.println();
		
		for ( PatternMapping mapping : results ) {
			
			System.out.println("Updating pattern mapping " + mapping.getUri());
			for (Pattern pattern : mapping.getPatterns() ) {
				
				if ( pattern.isUseForPatternEvaluation() ) {
					
					pattern.setWithoutLogConfidence(pattern.getWithoutLogConfidence() / (maxMinusMinWithoutLog));
					pattern.setWithLogConfidence(pattern.getWithLogConfidence() / (maxMinusMinWithLog));
					
					pattern.setWithLogWithLogLearndFromConfidence(pattern.getWithLogWithLogLearndFromConfidence() / maxMinusMinWithLogWithLogLearned);
					pattern.setWithLogWithoutLogLearndFromConfidence(pattern.getWithLogWithoutLogLearndFromConfidence() / maxMinusMinWithLogWithoutLogLearned);
					
					pattern.setWithoutLogWithLogLearndFromConfidence(pattern.getWithoutLogWithLogLearndFromConfidence() / maxMinusMinWithoutLogWithLogLearned);
					pattern.setWithoutLogWithoutLogLearndFromConfidence(pattern.getWithoutLogWithoutLogLearndFromConfidence() / maxMinusMinWithoutLogWithoutLogLearned);
					
//					System.out.println("" + pattern.getNaturalLanguageRepresentation());
//					
//					System.out.println("getWithLogConfidence" + pattern.getWithLogConfidence());
//					System.out.println("getWithLogWithLogLearndFromConfidence" + pattern.getWithLogWithLogLearndFromConfidence());
//					System.out.println("getWithLogWithoutLogLearndFromConfidence" + pattern.getWithLogWithoutLogLearndFromConfidence());
//					
//					System.out.println("getWithoutLogConfidence" + pattern.getWithoutLogConfidence());
//					System.out.println("getWithoutLogWithLogLearndFromConfidence" + pattern.getWithoutLogWithLogLearndFromConfidence());
//					System.out.println("getWithoutLogWithoutLogLearndFromConfidence" + pattern.getWithoutLogWithoutLogLearndFromConfidence());
				}
				else {
					
					pattern.setWithoutLogConfidence(0D);
					pattern.setWithLogConfidence(0D);
					pattern.setWithLogWithLogLearndFromConfidence(0D);
					pattern.setWithLogWithoutLogLearndFromConfidence(0D);
					pattern.setWithoutLogWithLogLearndFromConfidence(0D);
					pattern.setWithoutLogWithoutLogLearndFromConfidence(0D);
				}
			}
			this.patternMappingDao.updatePatternMapping(mapping);
		}
	}
}
