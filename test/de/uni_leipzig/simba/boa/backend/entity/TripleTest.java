/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Resource;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;


/**
 * @author gerb
 *
 */
public class TripleTest {

    // initialize logging and settings
    NLPediaSetup setup = null;
    NLPediaLogger logger = null;
    
    public static junit.framework.Test suite() {

        return new JUnit4TestAdapter(TripleTest.class);
    }

    @Before
    public void setUp() {

        this.setup = new NLPediaSetup(true);
        this.logger = new NLPediaLogger(TripleTest.class);
    }

    @After
    public void cleanUpStreams() {

        this.setup.destroy();
    }
    
    @Test
    public void testTriple() {
        
        Resource subject1   = new Resource("s1", "sLabel1");
        Property property1  = new Property("p1", "range", "domain");
        Resource object1    = new Resource("o1", "oLabel1");
        
        Triple t1 = new Triple(subject1, property1, object1);
        
        Resource subject2   = new Resource("s1", "sLabel2");
        Property property2  = new Property("p1", "range", "domain");
        Resource object2    = new Resource("o1", "oLabel2");
        
        Triple t2 = new Triple(subject2, property2, object2);
        
        assertEquals(t1,t2);
        
        List<Triple> triples = new ArrayList<Triple>();
        triples.add(t1);
        
        assertTrue(triples.contains(t2));
    }
}
