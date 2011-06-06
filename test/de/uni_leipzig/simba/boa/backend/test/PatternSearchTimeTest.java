package de.uni_leipzig.simba.boa.backend.test;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.PatternSearchCommand;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;


public class PatternSearchTimeTest {
	
	// initialize logging and settings
	private final NLPediaSetup setup	= new NLPediaSetup(true);
	private final NLPediaLogger logger	= new NLPediaLogger(PatternSearchTimeTest.class);
	
	
	public static junit.framework.Test suite() {
		
		return new JUnit4TestAdapter(PatternSearchTimeTest.class);
	}
	
	@Before
	public void setUp() {
		
		logger.info("Starting PatternSimilarityCalculatorTest..");
	}

	@After
	public void cleanUpStreams() {
	    
		logger.info("Stopping PatternSimilarityCalculatorTest..");
		this.setup.destroy();
	}
	
	@Test
	public void testPatternSaving() {
		
		long start = System.currentTimeMillis();
		Command patternSearchCommand = new PatternSearchCommand();
		((PatternSearchCommand)patternSearchCommand).setFoundInIteration(0);
		patternSearchCommand.execute();
		System.out.println("Time took "+ (System.currentTimeMillis() - start));
	}
}
