package de.uni_leipzig.simba.boa.backend.test;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.nlp.NamedEntityRecognizer;


public class NamedEntityRecognizerTest {

	// initialize logging and settings
	NLPediaSetup setup = null;
	NLPediaLogger logger = null;
	
	public static junit.framework.Test suite() {
		
		return new JUnit4TestAdapter(NamedEntityRecognizerTest.class);
	}
	
	@Before
	public void setUp() {
		
		this.setup = new NLPediaSetup(true);
		this.logger = new NLPediaLogger(NamedEntityRecognizerTest.class);
	}

	@After
	public void cleanUpStreams() {
	    
		this.setup.destroy();
	}
	
	@Test
	public void testParseDatePattern() {
		
		String test1 = "?X? assistant manager with Terry Butcher until September 2009.Craig Sinclair Gordon (born 31 December 1982 in ?Y?";
		String test2 = "?X? from September 10, 1842 to June 26, 1844. Elizabeth Priscilla Cooper was born in ?Y?";
		String test3 = "?X? (July 12, 1730 - January 3, 1795, born in Burslem, ?Y?";
		String test4 = "?X? (born December 13, 1951 in ?Y?";
		String test5 = "?X? eat pasty buns today at 2pm ?Y?";
		
		NamedEntityRecognizer ner = new NamedEntityRecognizer();
		
		System.out.println(ner.recognizeEntitiesInPattern(test1));
		System.out.println(ner.recognizeEntitiesInPattern(test2));
		System.out.println(ner.recognizeEntitiesInPattern(test3));
		System.out.println(ner.recognizeEntitiesInPattern(test4));
		System.out.println(ner.recognizeEntitiesInPattern(test5));
	}
}
