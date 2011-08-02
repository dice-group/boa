package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.filter.PatternFilterFactory;
import de.uni_leipzig.simba.boa.backend.entity.pattern.filter.PatternFilter;
import de.uni_leipzig.simba.boa.backend.entity.pattern.filter.PatternFilteringThread;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.util.ListUtil;

/**
 * 
 * @author Daniel Gerber
 */
public class PatternFilteringCommand implements Command {

	private final NLPediaLogger logger = new NLPediaLogger(PatternFilteringCommand.class);
	
	private PatternMappingDao patternMappingDao = (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
	public List<PatternMapping> patternMappingList = this.patternMappingDao.findAllPatternMappings();
	
	public static int NUMBER_OF_PATTERN_MAPPINGS;

	public PatternFilteringCommand() {
		
		NUMBER_OF_PATTERN_MAPPINGS = patternMappingList.size();
	}
	
	@Override
	public void execute() {
		
		int numberOfFilteringThreads = new Integer(NLPediaSettings.getInstance().getSetting("numberOfEvaluationThreads")).intValue();
		
		// split the mappings into several lists
		List<List<PatternMapping>> patternMappingSubLists	= ListUtil.split(patternMappingList, (patternMappingList.size() / numberOfFilteringThreads));
		
		List<Thread> threadList = new ArrayList<Thread>();
		List<PatternMapping> results = new ArrayList<PatternMapping>();
		
		if ( numberOfFilteringThreads != 1 ) {
			
			for (int i = 0 ; i < numberOfFilteringThreads ; i++ ) {
				
				Thread t = new PatternFilteringThread(patternMappingSubLists.get(i));
				t.setName("PatternFilteringThread-" + (i + 1));
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
				
				results.addAll(((PatternFilteringThread)t).getEvaluatedPatternMappings());
			}
		}
		else {
			
			Map<String,PatternFilter> patternEvaluators = PatternFilterFactory.getInstance().getPatternFilterMap();
			
			// go through all evaluators
			for ( PatternFilter patternEvaluator : patternEvaluators.values() ) {

				this.logger.info(patternEvaluator.getClass().getSimpleName() + " started!");
				
				// and check each pattern mapping
				for ( PatternMapping patternMapping : this.patternMappingList ) {
				
					this.logger.debug("Evaluating pattern: " + patternMapping);
					patternEvaluator.filterPattern(patternMapping);
					this.patternMappingDao.updatePatternMapping(patternMapping);
				}
				this.logger.info(patternEvaluator.getClass().getSimpleName() + " finished!");
			}
			results.addAll(patternMappingList);
		}

		System.out.println("All evaluation threads are finished. Start to calculate normalized confidence!");
		
		double maxConfidenceForAllPatternMappings = 0;
		
		for (PatternMapping pm : results ) {
			
			// local maxima
			double maxConfidenceForPatternMapping = 0;
			
			for (Pattern pattern : pm.getPatterns() ) {
				
				if ( pattern.isUseForPatternEvaluation() ) {
					
					maxConfidenceForPatternMapping		= Math.max(maxConfidenceForPatternMapping, pattern.retrieveTempConfidence());
					maxConfidenceForAllPatternMappings	= Math.max(maxConfidenceForAllPatternMappings, pattern.retrieveTempConfidence());
				}
			}
			
			System.out.println("Mapping: " + pm.getProperty().getUri());
			System.out.println("\tmaxConfidence: "+maxConfidenceForPatternMapping);
			
			// set local maximums
			for ( Pattern pattern : pm.getPatterns() ) {
				
				if ( pattern.isUseForPatternEvaluation() ) {
				
					pattern.setConfidence(pattern.retrieveTempConfidence() / maxConfidenceForPatternMapping);
				}
			}
		}
		
		for ( PatternMapping mapping : results ) {
			
			for ( Pattern p : mapping.getPatterns() ) {
				
				if ( p.isUseForPatternEvaluation() ) {
					
					p.setGlobalConfidence(p.retrieveTempConfidence() / maxConfidenceForAllPatternMappings);
				}
			}
			
			System.out.println("Updating pattern mapping " + mapping.getProperty().getUri());
			this.patternMappingDao.updatePatternMapping(mapping);
		}
	}
}
