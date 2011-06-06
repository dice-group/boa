package de.uni_leipzig.simba.boa.backend.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternDao;
import de.uni_leipzig.simba.boa.backend.limes.PatternSimilarityCalculator;
import de.uni_leipzig.simba.boa.backend.limes.SimilarityStatement;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.util.BoyerMooreUtil;


public class PatternSearchTest {

	// initialize logging and settings
	private final NLPediaSetup setup	= new NLPediaSetup(true);
	private final NLPediaLogger logger	= new NLPediaLogger(PatternSearchTest.class);
	
	
	public static junit.framework.Test suite() {
		
		return new JUnit4TestAdapter(PatternSearchTest.class);
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
	
		String[] sentences = new String[]{"Paul Kantner left the band in 1984 , forming KBC Band with former bandmates Balin and Casady .",
		"In early 2000 , the label and Biafra were named in a lawsuit brought by his former Dead Kennedys bandmates .",
		"Anderson insisted that he still stays in contact with his former bandmates and has not ruled out a reunion .",
		"In recent years , Jones performed with his former bandmates in reunion tours and appeared in several productions of Oliver !",
		"The two former Blind Faith bandmates met again for a series of 14 concerts throughout the United States in June 2009 .",
		"Photos also showed Williams with his new tattoo of the Take That logo on his right arm , in tribute to his former bandmates .",
		"Also , Jeff Beck reunited with his former bandmates on the song `` My Blind Life '' .",
		"Farrait married Leyda Barreto in the 1990s and joined his former bandmates for a comeback tour named El Reencuentro .",
		"The break-up of the band was not amicable and subsequently a war of words was waged between the former bandmates via the heavy metal and musical press .",
		"As the nineties began , tempers had cooled between former bandmates Carlos Cavazo and Kevin Dubrow , and they started to communicate again .",
		"Paul Kantner left the band in 1984 , forming KBC Band with former bandmates Balin and Casady .",
		"In early 2000 , the label and Biafra were named in a lawsuit brought by his former Dead Kennedys bandmates .",
		"Anderson insisted that he still stays in contact with his former bandmates and has not ruled out a reunion .",
		"`` In the years following the breakup , Corgan began elaborating more on the circumstances of the breakup , and specifically , his disdain for his former bandmates .",
		"Finally , Sharp charged his former bandmates with breach of fiduciary duty , legal malpractice , dissolution of partnership , and declaratory relief .",
		"Williams ' funeral was held on August 24 , with his family , friends , and former bandmates in attendance .",
		"In 2002 , Cochrane reunited with his former Red Rider bandmates Greer and Jones and continues to perform with them today .",
		"At present , there seems to be little chance of Nishikawa playing with his former bandmates ever again .",
		"The album features appearances by former bandmates such as Alain Johannes , Flea , Eddie Vedder , Stone Gossard , Jeff Ament , and Les Claypool .",
		"Outside of Guns N ' Roses , Reed played on albums for his former bandmates Slash , Duff McKagan , and Gilby Clarke ."};
		
		String pattern = "former bandmates";
		int numberOfIterations = 1000000;
		
		long startTimeBoyerMoore = System.currentTimeMillis();
		
		BoyerMooreUtil boyerMooreUtil = new BoyerMooreUtil(pattern);
		for (int i = 0; i < numberOfIterations ; i++) {
			
			for (String s : sentences) {
				
				boyerMooreUtil.match(s);
			}
		}
		System.out.println("Time for boyer moore: " + (System.currentTimeMillis() - startTimeBoyerMoore) + "ms.");
		
		long startTimeNaive = System.currentTimeMillis();
		
		for (int i = 0; i < numberOfIterations ; i++) {
			
			for (String s : sentences) {
				
				s.contains(pattern);
			}
		}
		System.out.println("Time for naive: " + (System.currentTimeMillis() - startTimeNaive) + "ms.");
	}
}
