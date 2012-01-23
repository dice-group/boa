package de.uni_leipzig.simba.boa.backend.test;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.filter.PatternFilter;
import de.uni_leipzig.simba.boa.backend.entity.pattern.filter.PatternFilterFactory;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

/**
 * 
 * @author Daniel Gerber
 */
public class PatternFilterTest {

	// initialize logging and settings
	NLPediaSetup setup = null;
	NLPediaLogger logger = null;

	public static junit.framework.Test suite() {
		
		return new JUnit4TestAdapter(PatternFilterTest.class);
	}
	
	@Before
	public void setUp() {
		
		this.setup = new NLPediaSetup(true);
		this.logger = new NLPediaLogger(PatternFilterTest.class);
	}

	@After
	public void cleanUpStreams() {
	    
		this.setup.destroy();
	}
	
	@Test
	public void testStopWordPatternEvaluator() {
		
		Pattern p1 = new Pattern("?D? impoundment of the ?R?");
		p1.setUseForPatternEvaluation(true);
		p1.setNumberOfOccurrences(4);
		p1.addLearnedFrom("ASD" + "-;-" + "ASD");
		p1.addLearnedFrom("ASD" + "-;-" + "ASD");
		p1.addLearnedFrom("ASD" + "-;-" + "ASD");
		
		Pattern p2 = new Pattern("?R? , which flows into ?D?");
		p2.setUseForPatternEvaluation(true);
		p2.setNumberOfOccurrences(4);
		p2.addLearnedFrom("ASD" + "-;-" + "ASD");
		p2.addLearnedFrom("ASD" + "-;-" + "ASD");
		p2.addLearnedFrom("ASD" + "-;-" + "ASD");
		p2.addLearnedFrom("ASD" + "-;-" + "ASD");
		
		PatternMapping pm = new PatternMapping();
		pm.getProperty().setUri("http://dbpedia.org/ontology/asdasd");
		pm.getProperty().setRdfsDomain("http://dbpedia.org/ontology/Company");
		pm.getProperty().setRdfsRange("http://dbpedia.org/ontology/Company");
		pm.addPattern(p1).addPattern(p2);
		
		System.out.println(p1.getLearnedFrom());
		
		for ( PatternFilter pe : PatternFilterFactory.getInstance().getPatternFilterMap().values()) {
			
			pe.filterPattern(pm);
		}
		
//		System.out.println("p1: " + p1.isUseForPatternEvaluation());
//		System.out.println("logconf: " +p1.getWithLogConfidence() + "  conf:" +p1.getWithoutLogConfidence());
//		assertTrue(p1.isUseForPatternEvaluation());
	}
}
