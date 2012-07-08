package de.uni_leipzig.simba.boa.backend;

import java.util.Timer;

import de.uni_leipzig.simba.boa.backend.concurrent.PrintJvmMemoryTimerTask;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.pipeline.Pipeline;

/**
 * 
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public class Boa {

    public static void main(String[] args) {

        System.out.println("Starting BOA Framework!");
        
        // Initialize logging, settings, factories etc., needs to be FIRST call!!
        NLPediaSetup setup = new NLPediaSetup(false);
        NLPediaLogger logger = new NLPediaLogger(NLPedia.class);
        
        // this logs the jvm heap usage 
        Timer timer = new Timer();
        PrintJvmMemoryTimerTask memoryTimer = new PrintJvmMemoryTimerTask();
        timer.schedule(memoryTimer, 10000, 30000);
        
        Pipeline pipeline = new Pipeline();
        pipeline.run();
        
        timer.cancel();
        
        System.out.println("Stopping BOA Framework!");
    }
}
