/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.concurrent;

import java.util.TimerTask;

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
        
        output.append("Memory consumption: ").
        append(((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / NLPediaSettings.MEGABYTE) + "MB / ").
        append((Runtime.getRuntime().maxMemory() / NLPediaSettings.MEGABYTE) + "MB\n");
        
        this.logger.info(output.toString());
    }
}
