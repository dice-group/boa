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
		
		Pattern p1 = new Pattern("?X? is a horrible ?Y?", "?X? is a horrible ?Y?");
		Pattern p2 = new Pattern("?X? is a ?Y?", "?X? is a ?Y?");
		Pattern p3 = new Pattern("?X?, ?Y?", "?X?, ?Y?");
		Pattern p4 = new Pattern("?X?, like ?Y?", "?X?, like ?Y?");
		Pattern p5 = new Pattern("?X? before ?Y?", "?X? before ?Y?");
		Pattern p6 = new Pattern("?X? and, if possible, the west bank of the ?Y?", "?X? and, if possible, the west bank of the ?Y?");
		Pattern p7 = new Pattern("?X?, and ?Y?", "?X?, and ?Y?");
		Pattern p8 = new Pattern("?X? is a great ?Y?", "?X? is a great ?Y?");
		
		p1.setNumberOfOccurrences(4);
		p2.setNumberOfOccurrences(4);
		p3.setNumberOfOccurrences(4);
		p4.setNumberOfOccurrences(4);
		p5.setNumberOfOccurrences(4);
		p6.setNumberOfOccurrences(4);
		p7.setNumberOfOccurrences(4);
		p8.setNumberOfOccurrences(1);
		
		PatternMapping pm = new PatternMapping();
		pm.addPattern(p1).addPattern(p2).addPattern(p3).addPattern(p4).addPattern(p5).addPattern(p6).addPattern(p7).addPattern(p8);
		
		for ( PatternEvaluator pe : PatternEvaluatorFactory.getInstance().getPatternEvaluatorMap().values()) {
			
			pe.evaluatePattern(pm);
		}
		
		System.out.println("p1" + p1.isUseForPatternEvaluation());
		System.out.println("p2" + p2.isUseForPatternEvaluation());
		System.out.println("p3" + p3.isUseForPatternEvaluation());
		System.out.println("p4" + p4.isUseForPatternEvaluation());
		System.out.println("p5" + p5.isUseForPatternEvaluation());
		System.out.println("p6" + p6.isUseForPatternEvaluation());
		System.out.println("p7" + p6.isUseForPatternEvaluation());
		
		assertTrue(p1.isUseForPatternEvaluation());
		assertFalse(p2.isUseForPatternEvaluation());
		assertFalse(p3.isUseForPatternEvaluation());
		assertFalse(p4.isUseForPatternEvaluation());
		assertFalse(p5.isUseForPatternEvaluation());
		assertFalse(p6.isUseForPatternEvaluation());
		assertFalse(p7.isUseForPatternEvaluation());
		assertFalse(p8.isUseForPatternEvaluation());
	}
}
