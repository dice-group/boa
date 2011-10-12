package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.confidence.PatternConfidenceMeasureThread;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.search.concurrent.PrintProgressTask;
import de.uni_leipzig.simba.boa.backend.util.ListUtil;

/**
 * 
 * @author Daniel Gerber
 */
public class PatternConfidenceMeasureCommand implements Command {

	private final NLPediaLogger logger					= new NLPediaLogger(PatternConfidenceMeasureCommand.class);
	private final PatternMappingDao patternMappingDao	= (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
	private List<PatternMapping> patternMappingList		= null;
	public static double NUMBER_OF_PATTERN_MAPPINGS;

	public PatternConfidenceMeasureCommand(Map<Integer,PatternMapping> patternMappingList) {
		
		if ( patternMappingList != null ){
		
			this.patternMappingList = new ArrayList<PatternMapping>(patternMappingList.values());
		}
		else {
			
			this.patternMappingList = this.patternMappingDao.findAllPatternMappings();
		}
		PatternConfidenceMeasureCommand.NUMBER_OF_PATTERN_MAPPINGS = (double) this.patternMappingList.size();
	}
	
	@Override
	public void execute() {
		
		int numberOfConfidenceMeasureThreads = new Integer(NLPediaSettings.getInstance().getSetting("numberOfConfidenceMeasureThreads")).intValue();
		
		// split the mappings into several lists
		List<List<PatternMapping>> patternMappingSubLists	= ListUtil.split(patternMappingList, (patternMappingList.size() / numberOfConfidenceMeasureThreads));
		
		List<Thread> threadList = new ArrayList<Thread>();
		List<PatternMapping> results = new ArrayList<PatternMapping>();
		
		// start all threads
		for (int i = 0 ; i < numberOfConfidenceMeasureThreads ; i++ ) {
			
			Thread t = new PatternConfidenceMeasureThread(patternMappingSubLists.get(i));
			t.setName("PatternConfidenceMeasureThread-" + (i + 1));
			threadList.add(i, t);
			t.start();
			System.out.println(t.getName() + " started!");
			this.logger.info(t.getName() + " started!");
		}
		
		// print the progress
		Timer timer = new Timer();
		timer.schedule(new PrintProgressTask(threadList), 0, 30000);
		
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
		timer.cancel();
		
		for ( Thread t: threadList ) {
			
			results.addAll(((PatternConfidenceMeasureThread)t).getConfidenceMeasuredPatternMappings());
		}

		System.out.println("All confidence measurement threads are finished.");
		
		// set this so that the next command does not need to query them from the database again
		this.patternMappingList = results;
	}
	
	public static void main(String[] args) {

	}
	
	/**
	 * @return the patternMappingList
	 */
	public List<PatternMapping> getPatternMappingList() {
	
		return patternMappingList;
	}

	
	/**
	 * @param patternMappingList the patternMappingList to set
	 */
	public void setPatternMappingList(List<PatternMapping> patternMappingList) {
	
		this.patternMappingList = patternMappingList;
	}
}
