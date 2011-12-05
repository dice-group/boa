package de.uni_leipzig.simba.boa.backend.test;

import static org.junit.Assert.assertEquals;
import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.entity.context.FastLeftContext;
import de.uni_leipzig.simba.boa.backend.entity.context.FastRightContext;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;


public class FastContextTest {

	// initialize logging and settings
	NLPediaSetup setup = null;
	NLPediaLogger logger = null;
	
	public static junit.framework.Test suite() {
		
		return new JUnit4TestAdapter(FastContextTest.class);
	}
	
	@Before
	public void setUp() {
		
		this.setup = new NLPediaSetup(true);
		this.logger = new NLPediaLogger(FastContextTest.class);
	}

	@After
	public void cleanUpStreams() {
	    
		this.setup.destroy();
	}
	
	@Test
	public void testFastLeftContext1() {
	
		String sentence		= "The Corinthian is a fictional character in Neil Gaiman 's comic book series `` The Sandman '' .";
		String nerTagged	= "The_O Corinthian_B-MISC is_O a_O fictional_O character_O in_O Neil_B-PER Gaiman_I-PER 's_O comic_O book_O series_O ``_O The_O Sandman_B-MISC ''_O ._O";
		String pattern		= "'s comic book series ``";
		
		FastLeftContext flc = new FastLeftContext(nerTagged, sentence, pattern);
		
		assertEquals(13, flc.getCleanWords().size());
		assertEquals("series", flc.getCleanWords().get(flc.getCleanWords().size() -1 ));
		assertEquals("series_O", flc.getTaggedWords().get(flc.getTaggedWords().size() -1 ));
		assertEquals("Neil Gaiman", flc.getSuitableEntity("http://dbpedia.org/ontology/Person"));
		assertEquals(0, flc.getSuitableEntityDistance("http://dbpedia.org/ontology/Person"));
		
		FastRightContext frc = new FastRightContext(nerTagged, sentence, pattern);
		
		assertEquals(8, frc.getCleanWords().size());
		assertEquals("comic", frc.getCleanWords().get(0));
		assertEquals("comic_O", frc.getTaggedWords().get(0));
		assertEquals("Sandman", frc.getSuitableEntity("http://dbpedia.org/ontology/Work"));
		assertEquals(1, frc.getSuitableEntityDistance("http://dbpedia.org/ontology/Work"));
	}
	
	@Test
	public void testFastLeftContext2() {
	
		String sentence		= "Uprock was created in Brooklyn , N.Y. and breaking was created in the Bronx .";
		String nerTagged	= "Uprock_O was_O created_O in_O Brooklyn_B-LOC ,_O N.Y._B-LOC and_O breaking_O was_O created_O in_O the_B-LOC Bronx_I-LOC ._O";
		String pattern		= "created in the";
		
		FastLeftContext flc = new FastLeftContext(nerTagged, sentence, pattern);
		
		assertEquals(12, flc.getCleanWords().size());
		assertEquals("in", flc.getCleanWords().get(flc.getCleanWords().size() -1 ));
		assertEquals("in_O", flc.getTaggedWords().get(flc.getTaggedWords().size() -1 ));
		assertEquals("N.Y.", flc.getSuitableEntity("http://dbpedia.org/ontology/Place"));
		assertEquals(3, flc.getSuitableEntityDistance("http://dbpedia.org/ontology/Place"));
		
		FastRightContext frc = new FastRightContext(nerTagged, sentence, pattern);
		
		assertEquals(4, frc.getCleanWords().size());
		assertEquals("in", frc.getCleanWords().get(0) );
		assertEquals("in_O", frc.getTaggedWords().get(0));
		assertEquals("the Bronx", frc.getSuitableEntity("http://dbpedia.org/ontology/Place"));
		assertEquals(0, frc.getSuitableEntityDistance("http://dbpedia.org/ontology/Place"));
	}
	
	@Test
	public void testFastLeftContext3() {
	
		String sentence		= "Uprock was created in Brooklyn .";
		String nerTagged	= "Uprock_MISC was_O created_O in_O Brooklyn_B-LOC ._O";
		String pattern		= "was created in";
		
		FastLeftContext flc = new FastLeftContext(nerTagged, sentence, pattern);
		
		assertEquals(3, flc.getCleanWords().size());
		assertEquals("created", flc.getCleanWords().get(flc.getCleanWords().size() -1 ));
		assertEquals("created_O", flc.getTaggedWords().get(flc.getTaggedWords().size() -1 ));
		assertEquals("Uprock", flc.getSuitableEntity("http://dbpedia.org/ontology/MusicalWork"));
		assertEquals(0, flc.getSuitableEntityDistance("http://dbpedia.org/ontology/MusicalWork"));
		
		FastRightContext frc = new FastRightContext(nerTagged, sentence, pattern);
		
		assertEquals(4, frc.getCleanWords().size());
		assertEquals("created", frc.getCleanWords().get(0) );
		assertEquals("created_O", frc.getTaggedWords().get(0));
		assertEquals("Brooklyn", frc.getSuitableEntity("http://dbpedia.org/ontology/Place"));
		assertEquals(0, frc.getSuitableEntityDistance("http://dbpedia.org/ontology/Place"));
	}
}
