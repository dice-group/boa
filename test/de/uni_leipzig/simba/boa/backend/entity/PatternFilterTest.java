package de.uni_leipzig.simba.boa.backend.entity;

import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.filter.PatternFilter;
import de.uni_leipzig.simba.boa.backend.entity.pattern.filter.PatternFilterFactory;
import de.uni_leipzig.simba.boa.backend.entity.pattern.impl.SubjectPredicateObjectPattern;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;

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
		
	    // only overcomes: number.of.occurrence.threshold
		Pattern p1 = new SubjectPredicateObjectPattern("?D? impoundment of the ?R?");
		p1.setUseForPatternEvaluation(true);
		p1.setNumberOfOccurrences(4);
		p1.addLearnedFrom("ASD1" + "-;-" + "ASD");
		p1.addLearnedFrom("ASD2" + "-;-" + "ASD");
		p1.addLearnedFrom("ASD3" + "-;-" + "ASD");
		
		// only overcomes: number.of.unique.occurrence.threshold
		Pattern p2 = new SubjectPredicateObjectPattern("?R? , which flows into ?D?");
		p2.setUseForPatternEvaluation(true);
		p2.setNumberOfOccurrences(1);
		p2.addLearnedFrom("ASD" + "-;-" + "ASD");
		p2.addLearnedFrom("ASD" + "-;-" + "ASD");
		p2.addLearnedFrom("ASD" + "-;-" + "ASD");
		p2.addLearnedFrom("ASD" + "-;-" + "ASD");
		
		// only overcomes: number.of.learned.pairs
		Pattern p3 = new SubjectPredicateObjectPattern("?R? , which will never flow into ?D?");
		p3.setUseForPatternEvaluation(true);
		p3.setNumberOfOccurrences(1);
        p3.addLearnedFrom("ASD1" + "-;-" + "ASD");
        p3.addLearnedFrom("ASD2" + "-;-" + "ASD");
        p3.addLearnedFrom("ASD3" + "-;-" + "ASD");
        p3.addLearnedFrom("ASD4" + "-;-" + "ASD");
        
        // overcomes all filters
        Pattern p4 = new SubjectPredicateObjectPattern("?R? , which flows somewhere into ?D?");
        p4.setUseForPatternEvaluation(true);
        p4.setNumberOfOccurrences(5);
        p4.addLearnedFrom("ASD" + "-;-" + "ASD");
        p4.addLearnedFrom("ASD" + "-;-" + "ASD");
        p4.addLearnedFrom("ASD1" + "-;-" + "ASD");
        p4.addLearnedFrom("ASD1" + "-;-" + "ASD");
        p4.addLearnedFrom("ASD2" + "-;-" + "ASD");
        p4.addLearnedFrom("ASD2" + "-;-" + "ASD");
        p4.addLearnedFrom("ASD3" + "-;-" + "ASD");
        p4.addLearnedFrom("ASD3" + "-;-" + "ASD");
		
		PatternMapping pm = new PatternMapping();
		pm.setProperty(new Property());
		pm.getProperty().setUri("http://dbpedia.org/ontology/asdasd");
		pm.addPattern(p1).addPattern(p2).addPattern(p3).addPattern(p4);
		
		NLPediaSettings.setSetting("number.of.occurrence.threshold", "2");
	    NLPediaSettings.setSetting("number.of.unique.occurrence.threshold", "2");
	    NLPediaSettings.setSetting("number.of.learned.pairs", "4");
		
		for ( PatternFilter pe : PatternFilterFactory.getInstance().getPatternFilterMap().values() ) {
			
			pe.filterPattern(pm);
		}
		assertTrue(pm.getPatterns().size() == 1);
	}
}
