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
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.util.ListUtil;

/**
 * 
 * @author Daniel Gerber
 */
public class PatternScoreFeatureCommand implements Command {

	private final NLPediaLogger logger					= new NLPediaLogger(PatternScoreFeatureCommand.class);
	private final PatternMappingDao patternMappingDao	= (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
	public static List<PatternMapping> patternMappingList		= null;
	public static double NUMBER_OF_PATTERN_MAPPINGS;

	public PatternScoreFeatureCommand(Map<Integer,PatternMapping> patternMappingList) {
		
		if ( patternMappingList != null ){
		
			PatternScoreFeatureCommand.patternMappingList = new ArrayList<PatternMapping>(patternMappingList.values());
		}
		else {
			
			PatternScoreFeatureCommand.patternMappingList = this.patternMappingDao.findAllPatternMappings();
		}
		PatternScoreFeatureCommand.NUMBER_OF_PATTERN_MAPPINGS = (double) PatternScoreFeatureCommand.patternMappingList.size();
	}
	
	public static void main(String[] args) {

		System.out.println( 268 / 20);
		System.out.println( Math.ceil(268 / 20));
		System.out.println( (268 / 20) + 1);
	}
	
	@Override
	public void execute() {
		
		int numberOfConfidenceMeasureThreads = new Integer(NLPediaSettings.getInstance().getSetting("numberOfConfidenceMeasureThreads")).intValue();
		
		// split the NAMED_ENTITY_TAG_MAPPINGS into several lists
		List<List<PatternMapping>> patternMappingSubLists	= ListUtil.split(patternMappingList, (patternMappingList.size() / numberOfConfidenceMeasureThreads) + 1 );
		
		int count= 0;
		for (List<PatternMapping> list : patternMappingSubLists) count += list.size();
		System.out.println(String.format("Found %s pattern NAMED_ENTITY_TAG_MAPPINGS in database, %s NAMED_ENTITY_TAG_MAPPINGS got distributed to confidence measure threads", patternMappingList.size(), count));
		
		List<Thread> threadList = new ArrayList<Thread>();
		
//		// start all threads
//		for (int i = 0 ; i < numberOfConfidenceMeasureThreads ; i++ ) {
//			
//			Thread t = new PatternFeatureExtractionCallable(patternMappingSubLists.get(i));
//			t.setName("PatternFeatureExtractionCallable-" + (i + 1) + "-" + patternMappingSubLists.get(i).size());
//			threadList.add(i, t);
//			t.start();
//			System.out.println(t.getName() + " started!");
//			this.logger.info(t.getName() + " started!");
//		}
//		
		// print the progress
		Timer timer = new Timer();
//		timer.schedule(new PatternSearchPrintProgressTask(threadList), 0, 30000);
		
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
	
//		System.out.println("All confidence measurement threads are finished.\n Starting to update pattern NAMED_ENTITY_TAG_MAPPINGS..");
//		for ( Thread t: threadList ) {
//			
//			for ( PatternMapping mapping : ((PatternFeatureExtractionCallable)t).getScoredPatternMappings() ) {
//				
//				this.patternMappingDao.updatePatternMapping(mapping);
//			}
//			// set this so that the next command does not need to query them from the database again
//			this.patternMappingList.addAll(((PatternFeatureExtractionCallable)t).getScoredPatternMappings());
//		}
	}
	
	private List<List<PatternMapping>> createPatternMappingSubLists(List<PatternMapping> patternMappingList, int numberOfConfidenceMeasureThreads) {
		
		// create the correct amount of sublists
		List<List<PatternMapping>> list = new ArrayList<List<PatternMapping>>();
		for (int i = 0; i < numberOfConfidenceMeasureThreads; i++) list.add(new ArrayList<PatternMapping>());
		
		System.out.println("Es gibt " + list.size() + " Threads.");
		
		// the maximum amount of patterns a sub list should contain
		int numberOfPatternsProList = 0;
		for ( PatternMapping mapping : patternMappingList ) numberOfPatternsProList += mapping.getPatterns().size();
		System.out.println("pattern insgesamt " + numberOfPatternsProList);		
		
		numberOfPatternsProList = (numberOfPatternsProList / numberOfConfidenceMeasureThreads) + 1;
		
		System.out.println("numberOfPatternsProList ist " + numberOfPatternsProList + " groß.");
				
		int i = 0;
		for (PatternMapping mapping : patternMappingList ) {
			
			if ( getNumberOfPatternsInPatternMappingList(list.get(i)) < numberOfPatternsProList ) {
				
				list.get(i).add(mapping);
				System.out.println("mapping " + i + " has " + mapping.getPatterns().size() +" patterns");
			}
			else {
				
				list.get(i+1).add(mapping); // add it to the next list because the current one is full
				i++; // switch to next list
			}
		}
		return list;
	}
	
	private int getNumberOfPatternsInPatternMappingList(List<PatternMapping> mappings) {
		
		int i = 0;
		for (PatternMapping mapping : mappings ) i += mapping.getPatterns().size();
		return i;
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
