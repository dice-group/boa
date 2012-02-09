/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.concurrent;

import java.util.TimerTask;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;


/**
 * @author gerb
 *
 */
public class PrintJvmMemoryTimerTask extends TimerTask {

    private NLPediaLogger logger = new NLPediaLogger(PrintJvmMemoryTimerTask.class);
    
    /* (non-Javadoc)
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {

        this.logger.info("##### Heap utilization statistics #####");
        this.logger.info("Used Memory:\t"     + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / NLPediaSettings.MEGABYTE);
        this.logger.info("Free Memory:\t"     + Runtime.getRuntime().freeMemory() / NLPediaSettings.MEGABYTE);
        this.logger.info("Total Memory:\t"    + Runtime.getRuntime().totalMemory() / NLPediaSettings.MEGABYTE);
        this.logger.info("Max Memory:\t"      + Runtime.getRuntime().maxMemory() / NLPediaSettings.MEGABYTE);
    }
}
