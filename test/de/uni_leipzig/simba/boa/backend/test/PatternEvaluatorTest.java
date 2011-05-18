package de.uni_leipzig.simba.boa.backend.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.evaluation.PatternEvaluator;
import de.uni_leipzig.simba.boa.backend.entity.pattern.evaluation.PatternEvaluatorFactory;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

/**
 * 
 * @author Daniel Gerber
 */
public class PatternEvaluatorTest {

	// initialize logging and settings
	NLPediaSetup setup = null;
	NLPediaLogger logger = null;

	public static junit.framework.Test suite() {
		
		return new JUnit4TestAdapter(PatternEvaluatorTest.class);
	}
	
	@Before
	public void setUp() {
		
		this.setup = new NLPediaSetup(true);
		this.logger = new NLPediaLogger(PatternEvaluatorTest.class);
	}

	@After
	public void cleanUpStreams() {
	    
		this.setup.destroy();
	}
	
	@Test
	public void testStopWordPatternEvaluator() {
		
		Pattern p1 = new Pattern("?X? was purchased by ?Y?", "");
		p1.setUseForPatternEvaluation(true);
		p1.setNumberOfOccurrences(4);
		p1.addLearnedFrom("ASD" + "-;-" + "ASD");
		p1.addLearnedFrom("ASD" + "-;-" + "ASD");
		p1.addLearnedFrom("ASD" + "-;-" + "ASD");
		
		PatternMapping pm = new PatternMapping();
		pm.setRdfsDomain("http://dbpedia.org/ontology/Company");
		pm.setRdfsRange("http://dbpedia.org/ontology/Company");
		pm.addPattern(p1);
		
		System.out.println(p1.getLearnedFrom());
		
		for ( PatternEvaluator pe : PatternEvaluatorFactory.getInstance().getPatternEvaluatorMap().values()) {
			
			pe.evaluatePattern(pm);
		}
		
		System.out.println("p1: " + p1.isUseForPatternEvaluation());
		System.out.println("logconf: " +p1.getWithLogConfidence() + "  conf:" +p1.getWithoutLogConfidence());
		assertTrue(p1.isUseForPatternEvaluation());
	}
}
