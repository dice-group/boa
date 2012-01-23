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
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.namedentityrecognition.NamedEntityRecognition;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.namedentityrecognition.impl.StanfordNLPNamedEntityRecognition;


/**
 * @author gerb
 *
 */
public class StanfordNLPNamedEntityRecognitionTest extends TestCase {

	// initialize logging and settings
	NLPediaSetup setup = null;
	NLPediaLogger logger = null;
	
	public static junit.framework.Test suite() {

		return new JUnit4TestAdapter(StanfordNLPNamedEntityRecognitionTest.class);
	}

	@Before
	public void setUp() {

		this.setup = new NLPediaSetup(true);
		this.logger = new NLPediaLogger(StanfordNLPNamedEntityRecognitionTest.class);
	}

	@After
	public void cleanUpStreams() {

		this.setup.destroy();
	}
	
	@Test
	public void testGetAnnotations() {
		
		String string = "Walter Isaacson wrote the book on the Apple Inc. founder and deceased Steve Jobs who lived in Cupertino.";
		
		NamedEntityRecognition ned = new StanfordNLPNamedEntityRecognition();
		assertEquals("PERSON PERSON OTHER OTHER OTHER OTHER OTHER ORGANIZATION ORGANIZATION OTHER OTHER OTHER PERSON PERSON OTHER OTHER OTHER PLACE OTHER", ned.getAnnotations(string));
	}
	
	@Test
	public void testGetAnnotatedSentence() {
		
		String string = "Walter Isaacson wrote the book on the Apple Inc. founder and deceased Steve Jobs who lived in Cupertino.";
		
		NamedEntityRecognition ned = new StanfordNLPNamedEntityRecognition();
		assertEquals("Walter_PERSON Isaacson_PERSON wrote_OTHER the_OTHER book_OTHER on_OTHER the_OTHER Apple_ORGANIZATION Inc._ORGANIZATION founder_OTHER and_OTHER deceased_OTHER Steve_PERSON Jobs_PERSON who_OTHER lived_OTHER in_OTHER Cupertino_PLACE ._OTHER", ned.getAnnotatedString(string));
	}
}
