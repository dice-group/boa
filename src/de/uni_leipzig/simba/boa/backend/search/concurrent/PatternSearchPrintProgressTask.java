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
    private int totalNumber = 0;

    public PatternSearchPrintProgressTask(List<Callable<Collection<SearchResult>>> callableList) {

        this.callableList = callableList;

        // we need this to calculate the total number of done searches for all
        // callables
        for (Callable<Collection<SearchResult>> callable : callableList)
            totalNumber += ((PatternSearchCallable) callable).getNumberTotal();
    }

    @Override
    public void run() {

        this.logger.info("########################################");
        int totalProgress = 0;

        for (Callable<Collection<SearchResult>> patternSearchCallable : this.callableList) {

            PatternSearchCallable patternSearchThread = (PatternSearchCallable) patternSearchCallable;

            int progress = Integer.valueOf(format.format(patternSearchThread.getProgress() * 100));
            totalProgress += patternSearchThread.getNumberDone();

            if (progress != 100 && (patternSearchThread.getProgress() > 0 && patternSearchThread.getProgress() < 100)) {

                this.logger.info(patternSearchThread.getName() + ": " + progress + "%. Found: " + patternSearchThread.getNumberOfResultsSoFar() + "  (" + patternSearchThread.getNumberDone() + "/"
                        + patternSearchThread.getNumberTotal() + ")");
            }
        }
        Double i = ((double) totalProgress / totalNumber) * 100;
        if ( i.isInfinite() || i.isNaN() ) i = 0D;
        this.logger.info(Integer.valueOf(format.format(i)) + "% (" + totalProgress + "/" + totalNumber + ")");
        this.logger.info("########################################");
    }
}