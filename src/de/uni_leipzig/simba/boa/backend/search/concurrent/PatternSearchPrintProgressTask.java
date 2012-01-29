package de.uni_leipzig.simba.boa.backend.search.concurrent;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.search.result.SearchResult;


public class PatternSearchPrintProgressTask extends TimerTask {

	private DecimalFormat format = new DecimalFormat("##");
	private List<Callable<Collection<SearchResult>>> callableList;
	private final NLPediaLogger logger = new NLPediaLogger(PatternSearchPrintProgressTask.class);
	
	public PatternSearchPrintProgressTask(List<Callable<Collection<SearchResult>>> callableList) {
		
		this.callableList = callableList;
	}
	
	@Override
	public void run() {

		for (Callable<Collection<SearchResult>> patternSearchCallable : this.callableList) {

			PatternSearchCallable patternSearchThread = (PatternSearchCallable) patternSearchCallable;

			int progress	= Integer.valueOf(format.format(patternSearchThread.getProgress() * 100));

			if (progress != 100 && (patternSearchThread.getProgress() > 0 && patternSearchThread.getProgress() < 100) ) {

				this.logger.info(patternSearchThread.getName() + ": " + progress + "%. " +
						"(" + patternSearchThread.getNumberDone() + "/" + patternSearchThread.getNumberTotal() + ")");
			}
		}
		this.logger.info("########################################################################################");
	}
}