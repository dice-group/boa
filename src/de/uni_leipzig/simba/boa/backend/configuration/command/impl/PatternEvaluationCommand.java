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
	
	private PatternMappingDao patternMappingDao = (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
	public List<PatternMapping> patternMappingList = this.patternMappingDao.findAllPatternMappings();
	
	public static int NUMBER_OF_PATTERN_MAPPINGS;

	public PatternEvaluationCommand() {
		
		NUMBER_OF_PATTERN_MAPPINGS = patternMappingList.size();
	}
	
	@Override
	public void execute() {
		
		int numberOfSearchThreads = new Integer(NLPediaSettings.getInstance().getSetting("numberOfEvaluationThreads")).intValue();
		
		// split the mappings into several lists
		List<List<PatternMapping>> patternMappingSubLists	= ListUtil.split(patternMappingList, (patternMappingList.size() / numberOfSearchThreads));
		
		List<Thread> threadList = new ArrayList<Thread>();
		List<PatternMapping> results = new ArrayList<PatternMapping>();
		
		if ( numberOfSearchThreads != 1 ) {
			
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
		}
		else {
			
			Map<String,PatternEvaluator> patternEvaluators = PatternEvaluatorFactory.getInstance().getPatternEvaluatorMap();
			
			// go through all evaluators
			for ( PatternEvaluator patternEvaluator : patternEvaluators.values() ) {

				this.logger.info(patternEvaluator.getClass().getSimpleName() + " started!");
				
				// and check each pattern mapping
				for ( PatternMapping patternMapping : this.patternMappingList ) {
				
					this.logger.debug("Evaluating pattern: " + patternMapping);
					patternEvaluator.evaluatePattern(patternMapping);
					this.patternMappingDao.updatePatternMapping(patternMapping);
				}
				this.logger.info(patternEvaluator.getClass().getSimpleName() + " finished!");
			}
			results.addAll(patternMappingList);
		}

		System.out.println("All evaluation threads are finished. Start to calculate normalized confidence!");
		
		for (PatternMapping pm : results ) {
			
			// local maxima
			double maxConfidenceForPatternMapping = 0;
			double maxDoubleSupportConfidence = 0;
			
			for (Pattern pattern : pm.getPatterns() ) {
				
				if ( pattern.isUseForPatternEvaluation() ) {
					
					maxDoubleSupportConfidence		= Math.max(maxDoubleSupportConfidence, pattern.getConfidence());
				}
			}
			
			System.out.println("Mapping: " + pm.getUri());
			System.out.println("\tmaxConfidence: "+maxDoubleSupportConfidence);
			
			// set local maximums
			for ( Pattern pattern : pm.getPatterns() ) {
				
				if ( pattern.isUseForPatternEvaluation() ) {
				
					pattern.setConfidence(pattern.getConfidence() / maxConfidenceForPatternMapping);
				}
			}
		}
		
		for ( PatternMapping mapping : results ) {
			
			System.out.println("Updating pattern mapping " + mapping.getUri());
			this.patternMappingDao.updatePatternMapping(mapping);
		}
	}
}
