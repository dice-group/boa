/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pattern;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.search.impl.DefaultPatternSearcher;
import junit.framework.JUnit4TestAdapter;
import junit.framework.TestCase;


/**
 * @author gerb
 *
 */
public class PatternTest extends TestCase {

    // initialize logging and settings
    NLPediaSetup setup = null;
    NLPediaLogger logger = null;
    
    public static junit.framework.Test suite() {

        return new JUnit4TestAdapter(PatternTest.class);
    }

    @Before                                      
    public void setUp() {

        this.setup = new NLPediaSetup(true);
        this.logger = new NLPediaLogger(PatternTest.class);
    }

    @After
    public void cleanUpStreams() {

        this.setup.destroy();
    }
    
    @Test
    public void testIsSuitablePattern() throws IOException, ParseException {
        
        List<String> badPatterns = new ArrayList<String>();
        badPatterns.add("?D? is a ?R?"); // only stopwords
        badPatterns.add("winner ?R?"); // wrong format
        badPatterns.add("?D? ?D? winner ?R?"); // too many domains
        badPatterns.add("?D? , ?R?"); // to real words
        badPatterns.add("?D? ?R?"); // no words
        badPatterns.add("?D? 's ?R?"); // no words
        badPatterns.add("?R? 's ?D?"); // no words
        
        List<String> goodPatterns = new ArrayList<String>();
        goodPatterns.add("?D? winner ?R?"); // only stopwords
        
        DefaultPatternSearcher searcher = new DefaultPatternSearcher();
        
        for (String goodPattern : goodPatterns ) 
            assertTrue(searcher.isPatternSuitable(goodPattern));
                
        for (String badPattern : badPatterns ) 
            assertFalse(searcher.isPatternSuitable(badPattern));
    }
}
