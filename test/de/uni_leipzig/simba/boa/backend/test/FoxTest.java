package de.uni_leipzig.simba.boa.backend.test;

import junit.framework.JUnit4TestAdapter;

import nlpbox.nlpbox.NLPBox;
import nlpbox.util.Entity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

public class FoxTest {

	// initialize logging and settings
	NLPediaSetup setup = null;
	NLPediaLogger logger = null;

	public static junit.framework.Test suite() {

		return new JUnit4TestAdapter(FoxTest.class);
	}

	@Before
	public void setUp() {

		this.setup = new NLPediaSetup(true);
		this.logger = new NLPediaLogger(FoxTest.class);
	}

	@After
	public void cleanUpStreams() {

		this.setup.destroy();
	}

	@Test
	public void testFox() {

		String test = "During the reign of Maria Theresa, Neustift once suffered a particularly bad wine harvest.";
		System.out.println(test);
		
		NLPBox fox =  new NLPBox();
		
		System.out.println("test");
		for ( Entity entity : fox.getNER(test) ){
			
			System.out.println(entity);
		}
	}
}