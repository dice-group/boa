package de.uni_leipzig.simba.boa.backend.entity;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.backgroundknowledge.BackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.impl.DatatypePropertyBackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;


public class BackgroundKnowledgeTest extends TestCase {

 // initialize logging and settings
    NLPediaSetup setup = null;
    NLPediaLogger logger = null;
    
    public static junit.framework.Test suite() {
        
        return new JUnit4TestAdapter(BackgroundKnowledgeTest.class);
    }
    
    @Before
    public void setUp() {
        
        this.setup = new NLPediaSetup(true);
        this.logger = new NLPediaLogger(BackgroundKnowledgeTest.class);
    }

    @After
    public void cleanUpStreams() {
        
        this.setup.destroy();
    }
    
    @Test
    public void testBackgroundKnowledge() {
        
        String uri11 = "http://dbpedia.org/resource/";
        String uri12 = "Leipzig";
        String uri21 = "http://dbpedia.org/resource#";
        String uri22 = "Deutschland";
        
        BackgroundKnowledge dbk = new DatatypePropertyBackgroundKnowledge();
        dbk.setSubjectPrefixAndLocalname(uri11 + uri12);
        dbk.setObjectPrefixAndLocalname(uri21 + uri22);
        
        assertEquals(uri11 + uri12, dbk.getSubjectUri());
        assertEquals(uri11, dbk.getSubjectPrefix());
        assertEquals(uri12, dbk.getSubjectLocalname());
        
        assertEquals(uri21 + uri22, dbk.getObjectUri());
        assertEquals(uri21, dbk.getObjectPrefix());
        assertEquals(uri22, dbk.getObjectLocalname());
    }
}
