package de.uni_leipzig.simba.boa.backend.entity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.search.result.SearchResult;
import junit.framework.JUnit4TestAdapter;
import junit.framework.TestCase;


public class SearchResultTest extends TestCase {


    // initialize logging and settings
    NLPediaSetup setup = null;
    NLPediaLogger logger = null;
    
    
    public static junit.framework.Test suite() {
        
        return new JUnit4TestAdapter(SearchResultTest.class);
    }
    
    @Before
    public void setUp() {
        
        this.setup = new NLPediaSetup(true);
        this.logger = new NLPediaLogger(SearchResultTest.class);
    }

    @After
    public void cleanUpStreams() {
        
        this.setup.destroy();
    }
    
    @Test
    public void testSearchResultToString() {
        
        SearchResult result = new SearchResult();
        result.setProperty(new Property("http://dbpedia.org/ontology/capital", "http://dbpedia.org/ontology/range1", "http://dbpedia.org/ontology/domain1"));
        result.setSentence(12);
        result.setNaturalLanguageRepresentation("?D? is the capital of ?R?");
        result.setFirstLabel("berlin");
        result.setSecondLabel("germany");
        
        SearchResult resultFromString = new SearchResult(result.toString());
        assertEquals(result, resultFromString);
    }
}
