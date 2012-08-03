package de.uni_leipzig.simba.boa.backend.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.search.result.SearchResult;
import de.uni_leipzig.simba.boa.backend.search.result.comparator.SearchResultComparator;
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
        result.setProperty("http://dbpedia.org/ontology/capital");
        result.setSentence(12);
        result.setNaturalLanguageRepresentation("?D? is the capital of ?R?");
        result.setFirstLabel("berlin");
        result.setSecondLabel("germany");
        
        SearchResult resultFromString = new SearchResult(result.toString());
        assertEquals(result, resultFromString);
    }
    
    @Test
    public void testSearchResultComparator() {

        SearchResultComparator comp = new SearchResultComparator();
        List<SearchResult> res = new ArrayList<SearchResult>();
        
        SearchResult s1 = new SearchResult();
        s1.setProperty("http://dbpedia.org/ontology/birthPlace");
        s1.setNaturalLanguageRepresentation("?D? was born in the ?R?");
        s1.setSentence(12);
        res.add(s1);
        
        SearchResult s2 = new SearchResult();
        s2.setProperty("http://dbpedia.org/ontology/airthPlace");
        s2.setNaturalLanguageRepresentation("?D? b ?R?");
        s2.setSentence(123);
        res.add(s2);
        
        SearchResult s3 = new SearchResult();
        s3.setProperty("http://dbpedia.org/ontology/airthPlace");
        s3.setNaturalLanguageRepresentation("?D? a ?R?");
        s3.setSentence(124);
        res.add(s3);
        
        SearchResult s4 = new SearchResult();
        s4.setProperty("http://dbpedia.org/ontology/cirthPlace");
        s4.setNaturalLanguageRepresentation("?D? was born in the ?R?");
        s4.setSentence(125);
        res.add(s4);
        
        Collections.sort(res, comp);
                
        assertTrue(res.get(0) == s3);
        assertTrue(res.get(1) == s2);
        assertTrue(res.get(2) == s1);
        assertTrue(res.get(3) == s4);
    }
}
