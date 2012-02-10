/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.util;

import java.util.concurrent.TimeUnit;


/**
 * @author gerb
 *
 */
public class TimeUtil {

    /**
     * Converts a time span in ms to something like this:
     * "12 min, 5s" 
     * 
     * @param millis
     * @return
     */
    public static String convertMilliSeconds(long millis) {
        
        return String.format("%d min, %d sec", 
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) - 
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }
}
