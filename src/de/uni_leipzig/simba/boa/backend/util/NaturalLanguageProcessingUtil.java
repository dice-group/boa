/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.util;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;


/**
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 *
 */
public class NaturalLanguageProcessingUtil {

    private static final NLPediaLogger logger = new NLPediaLogger(NaturalLanguageProcessingUtil.class);
    
    /**
     * TODO export to library
     * 
     * @param sentence
     * @return
     */
    public static String segmentString(String sentence) {
        
        try {
            
            StringReader stringReader = new StringReader(sentence);
            DocumentPreprocessor preprocessor = new DocumentPreprocessor(stringReader,  DocumentPreprocessor.DocType.Plain);
            
            Iterator<List<HasWord>> iter = preprocessor.iterator();
            while ( iter.hasNext() ) {
                
                StringBuilder stringBuilder = new StringBuilder();
                
                for ( HasWord word : iter.next() ) {
                    stringBuilder.append(word.toString() + " ");
                }
                return stringBuilder.toString().trim();
            }
        }
        catch (ArrayIndexOutOfBoundsException aioobe) {
            
            logger.debug("Could not segment string...", aioobe);
        }
        return "";
    }
}
