/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.concurrent;

import java.util.TimerTask;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

/**
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 *
 */
public class PrintJvmMemoryTimerTask extends TimerTask {

    private NLPediaLogger logger = new NLPediaLogger(PrintJvmMemoryTimerTask.class);
    
    /* (non-Javadoc)
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {

        StringBuffer output = new StringBuffer();
        
        output.append("##### Heap utilization statistics #####\n").
        append("Used Memory:\t\t"     + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / NLPediaSettings.MEGABYTE + "MB\n").
        append("Free Memory:\t\t"     + Runtime.getRuntime().freeMemory() / NLPediaSettings.MEGABYTE + "MB\n").
        append("Total Memory:\t"    + Runtime.getRuntime().totalMemory() / NLPediaSettings.MEGABYTE + "MB\n").
        append("Max Memory:\t\t"      + Runtime.getRuntime().maxMemory() / NLPediaSettings.MEGABYTE + "MB\n");
        
        this.logger.info(output.toString());
    }
}
