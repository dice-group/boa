package de.uni_leipzig.simba.boa.backend.search.concurrent;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import de.uni_leipzig.simba.boa.backend.entity.pattern.confidence.PatternConfidenceMeasureThread;
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
		
		for ( Thread t : this.threadList ) {
			
			if ( t instanceof PatternSearchThread ) {
				
				int progress = Integer.valueOf(  ((PatternSearchThread)t).getProgress().replaceAll("%", "") );
				overallProgress += progress;
				
				if ( progress != 100 ) {
					
					System.out.println(t.getName() + ": " + progress + "%.");
					this.logger.debug(t.getName() + ": " + progress + "%.");
				}
			}
			if ( t instanceof PatternConfidenceMeasureThread ) {
				
				int progress = Integer.valueOf( ((PatternConfidenceMeasureThread)t).getProgress().replaceAll("%", "") );
				overallProgress += progress;
				
				if ( progress != 100 ) {
					
					System.out.println(t.getName() + ": " + progress + "%.");
					this.logger.debug(t.getName() + ": " + progress + "%.");
				}
			}
		}
		System.out.println("Overall progress " + NumberFormat.getPercentInstance().format((double)overallProgress / (double)(this.threadList.size()*100)) + " at " + DateFormat.getTimeInstance().format(new Date()));
		this.logger.debug("Overall progress " + NumberFormat.getPercentInstance().format((double)overallProgress / (double)(this.threadList.size()*100)) + " at " + DateFormat.getTimeInstance().format(new Date()));
		System.out.println("########################################################################################");
		this.logger.debug("########################################################################################");
	}
}
