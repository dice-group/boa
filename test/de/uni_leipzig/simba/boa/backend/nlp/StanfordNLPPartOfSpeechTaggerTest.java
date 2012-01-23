/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.nlp;

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
}
