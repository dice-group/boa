/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.featureextraction.concurrent;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import de.uni_leipzig.simba.boa.backend.concurrent.PatternMappingPatternPair;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.search.concurrent.PatternSearchPrintProgressTask;


/**
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 *
 */
public class PatternFeatureExtractionPrintProgressTask extends TimerTask {

    private DecimalFormat format = new DecimalFormat("##");
    private List<Callable<Collection<PatternMappingPatternPair>>> callableList;
    private final NLPediaLogger logger = new NLPediaLogger(PatternSearchPrintProgressTask.class);
    
    public PatternFeatureExtractionPrintProgressTask(List<Callable<Collection<PatternMappingPatternPair>>> callableList) {
        
        this.callableList = callableList;
    }
    
    /* (non-Javadoc)
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {

        this.logger.info("########################################");
        
        for (Callable<Collection<PatternMappingPatternPair>> patternFeatureExtractionCallable : this.callableList) {

            PatternFeatureExtractionCallable patternFeatureExtractionThread = (PatternFeatureExtractionCallable) patternFeatureExtractionCallable;

            int progress    = Integer.valueOf(format.format(patternFeatureExtractionThread.getProgress() * 100));

            if (progress != 100 && (patternFeatureExtractionThread.getProgress() > 0 && patternFeatureExtractionThread.getProgress() < 100) ) {

                this.logger.info(patternFeatureExtractionThread.getName() + ": " + progress + "%. " +
                        "(" + patternFeatureExtractionThread.getNumberDone() + "/" + patternFeatureExtractionThread.getNumberTotal() + ")");
            }
        }
        this.logger.info("########################################");
    }
}
