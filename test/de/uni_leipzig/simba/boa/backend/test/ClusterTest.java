package de.uni_leipzig.simba.boa.backend.test;

import java.util.Map.Entry;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.entity.Cluster;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;


public class ClusterTest {

	// initialize logging and settings
	NLPediaSetup setup = null;
	NLPediaLogger logger = null;
	
	public static junit.framework.Test suite() {
		
		return new JUnit4TestAdapter(ClusterTest.class);
	}
	
	@Before
	public void setUp() {
		
		this.setup = new NLPediaSetup(true);
		this.logger = new NLPediaLogger(ClusterTest.class);
	}

	@After
	public void cleanUpStreams() {
	    
		this.setup.destroy();
	}
	
	@Test
	public void testCluster() {
		
		Cluster cluster = new Cluster();
		cluster.setName("This is a test cluster");
		
		PatternMapping mapping1 = new PatternMapping();
		mapping1.setUri("type");
		
		Pattern p1 = new Pattern();
		p1.setNaturalLanguageRepresentation("x is a y");
		p1.setNumberOfOccurrences(4);
		p1.setPatternMapping(mapping1);
		
		Pattern p2 = new Pattern();
		p2.setNaturalLanguageRepresentation("x has a y");
		p2.setNumberOfOccurrences(4);
		p2.setPatternMapping(mapping1);

		mapping1.addPattern(p1);
		mapping1.addPattern(p2);
		
		PatternMapping mapping2 = new PatternMapping();
		mapping2.setUri("city");
		
		Pattern p3 = new Pattern();
		p3.setNaturalLanguageRepresentation("x is never y");
		p3.setNumberOfOccurrences(13);
		p3.setPatternMapping(mapping2);
		
		mapping2.addPattern(p3);
		
		cluster.addPattern(p1).addPattern(p2).addPattern(p3);
		cluster.calculateUriAffiliationPropabilities();
		for (Entry<String,Double> entry :  cluster.getUriAffiliationPropabilities().entrySet() ){
			
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
	}
}
