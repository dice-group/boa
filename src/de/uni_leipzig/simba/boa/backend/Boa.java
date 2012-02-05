package de.uni_leipzig.simba.boa.backend;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.pipeline.Pipeline;


public class Boa {

    public static void main(String[] args) {

        // Initialize logging, settings, factories etc., needs to be FIRST call!!
        NLPediaSetup setup = new NLPediaSetup(false);
        NLPediaLogger logger = new NLPediaLogger(NLPedia.class);
        
        Pipeline pipeline = new Pipeline();
        pipeline.run();
    }
}
