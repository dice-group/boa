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


public class PatternSimilarityCalculatorTest {

	// initialize logging and settings
	private final NLPediaSetup setup	= new NLPediaSetup(true);
	private final NLPediaLogger logger	= new NLPediaLogger(PatternDaoTest.class);
	
	
	public static junit.framework.Test suite() {
		
		return new JUnit4TestAdapter(PatternSimilarityCalculatorTest.class);
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
		
		Map<Integer,String> sourceAndTargetPatterns = fillTestMap();
		PatternSimilarityCalculator psc = new PatternSimilarityCalculator(sourceAndTargetPatterns);
		psc.runLinking();
		
		List<SimilarityStatement> acc = psc.getStatementsAccepted();
		List<SimilarityStatement> rev = psc.getStatementsToReview();
		
		for ( SimilarityStatement s : acc ) {
			System.out.println(sourceAndTargetPatterns.get(new Integer(s.getSubject())).replaceAll(",", " KOMMA") + "," + s.getSimilarity() + "," + sourceAndTargetPatterns.get(new Integer(s.getObject())).replaceAll(",", " KOMMA"));
		}
		for ( SimilarityStatement s : rev ) {
			
			System.out.println(sourceAndTargetPatterns.get(new Integer(s.getSubject())).replaceAll(",", " KOMMA") + "," + s.getSimilarity() + "," + sourceAndTargetPatterns.get(new Integer(s.getObject())).replaceAll(",", " KOMMA"));
		}
	}
	
	private Map<Integer,String> fillTestMap() {
		
		Map<Integer,String> sourceAndTargetPatterns = new HashMap<Integer,String>();
		
		final PatternDao patternDao = (PatternDao) DaoFactory.getInstance().createDAO(PatternDao.class);
		
		return sourceAndTargetPatterns;
	}
}
