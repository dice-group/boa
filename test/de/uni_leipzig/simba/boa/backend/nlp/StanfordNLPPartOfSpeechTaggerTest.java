/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.nlp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.partofspeechtagger.PartOfSpeechTagger;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.partofspeechtagger.impl.StanfordNLPPartOfSpeechTagger;


/**
 * @author gerb
 *
 */
public class StanfordNLPPartOfSpeechTaggerTest extends TestCase {

	// initialize logging and settings
	NLPediaSetup setup = null;
	NLPediaLogger logger = null;
	
	public static junit.framework.Test suite() {

		return new JUnit4TestAdapter(StanfordNLPPartOfSpeechTaggerTest.class);
	}

	@Before
	public void setUp() {

		this.setup = new NLPediaSetup(true);
		this.logger = new NLPediaLogger(StanfordNLPPartOfSpeechTaggerTest.class);
	}

	@After
	public void cleanUpStreams() {

		this.setup.destroy();
	}
	
	@Test
	public void testGetAnnotations() {
		
		String string = "This is a really good way to; \"ensure software quality.\"";
		
		PartOfSpeechTagger ned = new StanfordNLPPartOfSpeechTagger();
		assertEquals("DT VBZ DT RB JJ NN TO : `` VB NN NN . ''", ned.getAnnotations(string));
	}
	
	@Test
	public void testGetAnnotatedSentence() {
		
		String string = "This is a really good way to; \"ensure software quality.\"";
		
		PartOfSpeechTagger ned = new StanfordNLPPartOfSpeechTagger();
		assertEquals("This_DT is_VBZ a_DT really_RB good_JJ way_NN to_TO ;_: ``_`` ensure_VB software_NN quality_NN ._. ''_''", 
				ned.getAnnotatedString(string));
	}
	
	@Test
	public void testGetNounPhrases() {
	    
	    String string = "Critics often dub CCTV as ``Big Brother surveillance'', a reference to George Orwell's novel ``Nineteen Eighty-Four'', which featured a two-way telescreen in every home through which The Party would monitor the populace.";
	    PartOfSpeechTagger ned = new StanfordNLPPartOfSpeechTagger();
	    List<String> expected = new ArrayList<String>(Arrays.asList("CCTV", "George Orwell", "Nineteen Eighty-Four", "Party"));

	    assertEquals(ned.getNounPhrases(string), expected);
	}
	
	@Test
	public void testGetNounPhrases2() {
	    
	    String string = "William Shatner was born in Montreal, Quebec, Canada.";
        PartOfSpeechTagger ned = new StanfordNLPPartOfSpeechTagger();
        List<String> expected = new ArrayList<String>(Arrays.asList("CCTV", "George Orwell", "Nineteen Eighty-Four", "Party"));

        assertEquals(ned.getNounPhrases(string), expected);
	}
}
