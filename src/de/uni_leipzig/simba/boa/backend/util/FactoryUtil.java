/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.util;

import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.tool.BoaTool;


/**
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 *
 */
public class FactoryUtil {

    private static final NLPediaLogger logger = new NLPediaLogger(FactoryUtil.class);
    
    /**
     * Instantiates a natural language processing tool.
     * 
     * @param tool the tool to be instantiated
     * @return a new instance of the tool
     * @throw RuntimeException if something wents wrong
     */
    public static BoaTool createNewInstance(Class<? extends BoaTool> tool){
        
        try {
            
            return tool.newInstance();
        }
        catch (InstantiationException e) {

            e.printStackTrace();
            FactoryUtil.logger.fatal("Could not instantiate class: " + tool, e);
            throw new RuntimeException("Could not instantiate class: " + tool, e);
        }
        catch (IllegalAccessException e) {
            
            e.printStackTrace();
            FactoryUtil.logger.fatal("Could not instantiate class: " + tool, e);
            throw new RuntimeException("Could not instantiate class: " + tool, e);
        }
    }
}
