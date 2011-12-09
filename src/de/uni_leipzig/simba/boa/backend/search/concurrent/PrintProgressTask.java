package de.uni_leipzig.simba.boa.backend.search.concurrent;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.PatternScoreThread;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;


public class PrintProgressTask extends TimerTask {

	private List<Thread> threadList;
	private final NLPediaLogger logger = new NLPediaLogger(PrintProgressTask.class);
	
	public PrintProgressTask(List<Thread> threadList) {
		
		this.threadList = threadList;
	}
	
	@Override
	public void run() {

		int overallProgress = 0;
		int doneSearches = 0;
		
		for ( Thread t : this.threadList ) {
			
			if ( t instanceof PatternSearchThread ) {
				
				int progress = Integer.valueOf(  ((PatternSearchThread)t).getProgress().replaceAll("%", "") );
				overallProgress += progress;
				doneSearches += ((PatternSearchThread)t).getNumberOfDoneSearches();
				
				if ( progress != 100 ) {
				
					System.out.println(t.getName() + ": " + progress + "%. " + ((PatternSearchThread)t).getNumberOfDoneSearches() +"/"+ ((PatternSearchThread)t).getNumberOfSearches());
					this.logger.debug(t.getName() + ": " + progress + "%.");
				}
			}
			if ( t instanceof PatternScoreThread ) {

//				System.out.println(((PatternScoreThread)t).getProgress());
				int progress = Integer.valueOf( ((PatternScoreThread)t).getProgress().replaceAll("%", "") );
				
				overallProgress += progress;
				
				if ( progress != 100 ) {
					
					System.out.println(t.getName() + ": " + progress + "%.");
					this.logger.debug(t.getName() + ": " + progress + "%.");
				}
			}
			if ( t instanceof CreateKnowledgeThread ) {

//				System.out.println(((PatternScoreThread)t).getProgress());
				int progress = Integer.valueOf( ((CreateKnowledgeThread)t).getProgress().replaceAll("%", "") );
				
				overallProgress += progress;
				
				if ( progress != 100 ) {
					
					System.out.println(t.getName() + ": " + progress + "%. " + ((CreateKnowledgeThread)t).getNumberOfDoneSearchOperations() + "/" + ((CreateKnowledgeThread)t).getNumberOfAllSearchOperations());
					this.logger.debug(t.getName() + ": " + progress + "%. " + ((CreateKnowledgeThread)t).getNumberOfDoneSearchOperations() + "/" + ((CreateKnowledgeThread)t).getNumberOfAllSearchOperations());
				}
			}
		}
		System.out.println("Overall progress " + NumberFormat.getPercentInstance().format((double)overallProgress / (double)(this.threadList.size()*100)) + " at " + DateFormat.getTimeInstance().format(new Date()));
		this.logger.debug("Overall progress " + NumberFormat.getPercentInstance().format((double)overallProgress / (double)(this.threadList.size()*100)) + " at " + DateFormat.getTimeInstance().format(new Date()));
//		System.out.println("Overall done searches: " + doneSearches + " / " + ((PatternSearchThread)threadList.get(0)).getNumberOfSearches() * threadList.size());
		System.out.println("########################################################################################");
		this.logger.debug("########################################################################################");
	}
}
