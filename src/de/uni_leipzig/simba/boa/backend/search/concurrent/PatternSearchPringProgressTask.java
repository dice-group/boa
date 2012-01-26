package de.uni_leipzig.simba.boa.backend.search.concurrent;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.search.result.SearchResult;


public class PatternSearchPringProgressTask extends TimerTask {

	private DecimalFormat format = new DecimalFormat("##");
	private List<Callable<Collection<SearchResult>>> callableList;
	private final NLPediaLogger logger = new NLPediaLogger(PatternSearchPringProgressTask.class);
	
	public PatternSearchPringProgressTask(List<Callable<Collection<SearchResult>>> callableList) {
		
		this.callableList = callableList;
	}
	
	@Override
	public void run() {

		for (Callable<Collection<SearchResult>> patternSearchCallable : this.callableList) {

			PatternSearchCallable patternSearchThread = (PatternSearchCallable) patternSearchCallable;

			int progress	= Integer.valueOf(format.format(patternSearchThread.getProgress() * 100));

			if (progress != 100) {

				this.logger.info(patternSearchThread.getName() + ": " + progress + "%. " +
						"(" + patternSearchThread.getNumberOfDoneSearches() + "/" + patternSearchThread.getNumberOfSearches() + ")");
			}
		}
		this.logger.info("########################################################################################");
	}
}



//if ( t instanceof PatternScoreThread ) {
//
////			System.out.println(((PatternScoreThread)t).getProgress());
//			int progress = Integer.valueOf( ((PatternScoreThread)t).getProgress().replaceAll("%", "") );
//			
//			overallProgress += progress;
//			
//			if ( progress != 100 ) {
//				
//				System.out.println(t.getName() + ": " + progress + "%.");
//				this.logger.debug(t.getName() + ": " + progress + "%.");
//			}
//		}
//		if ( t instanceof CreateKnowledgeThread ) {
//
////			System.out.println(((PatternScoreThread)t).getProgress());
//			int progress = Integer.valueOf( ((CreateKnowledgeThread)t).getProgress().replaceAll("%", "") );
//			
//			overallProgress += progress;
//			
//			if ( progress != 100 ) {
//				
//				System.out.println(t.getName() + ": " + progress + "%. " + ((CreateKnowledgeThread)t).getNumberOfDoneSearchOperations() + "/" + ((CreateKnowledgeThread)t).getNumberOfAllSearchOperations());
//				this.logger.debug(t.getName() + ": " + progress + "%. " + ((CreateKnowledgeThread)t).getNumberOfDoneSearchOperations() + "/" + ((CreateKnowledgeThread)t).getNumberOfAllSearchOperations());
//			}
//		}
