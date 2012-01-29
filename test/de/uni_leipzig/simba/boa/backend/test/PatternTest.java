package de.uni_leipzig.simba.boa.backend.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;


public class PatternTest {

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
	public void testPattern() {
	
		Pattern p0 = new Pattern("?X? is a ?Y?");
		p0.setScore(0.3);
		p0.setNumberOfOccurrences(766);
		p0.setUseForPatternEvaluation(true);
		
		Pattern p1 = new Pattern("?X? is an ?Y?");
		p1.setScore(0.34);
		p1.setNumberOfOccurrences(7626);
		p1.setUseForPatternEvaluation(false);
		
		Pattern p2 = new Pattern("?X? is an ?Y?");
		p2.setScore(0.34);
		p2.setNumberOfOccurrences(7626);
		p2.setUseForPatternEvaluation(false);
		
		assertFalse(p0.equals(p2));
		assertTrue(p1.equals(p2));
	}
}
