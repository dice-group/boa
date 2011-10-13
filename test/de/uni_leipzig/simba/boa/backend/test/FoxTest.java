package de.uni_leipzig.simba.boa.backend.test;

import java.util.Arrays;
import java.util.List;

import junit.framework.JUnit4TestAdapter;

import nlpbox.nlpbox.NLPBox;
import nlpbox.util.Entity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.entity.pattern.confidence.impl.help.SentenceIterator;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

public class FoxTest {

	// initialize logging and settings
	NLPediaSetup setup = null;
	NLPediaLogger logger = null;

	public static junit.framework.Test suite() {

		return new JUnit4TestAdapter(FoxTest.class);
	}

	@Before
	public void setUp() {

		this.setup = new NLPediaSetup(true);
		this.logger = new NLPediaLogger(FoxTest.class);
	}

	@After
	public void cleanUpStreams() {

		this.setup.destroy();
	}

	@Test
	public void testFox() {

		List<String> testSentences = Arrays.asList(
				"The Nobel Prize-winning chemist and physicist Francis William Aston was born in Harborne in 1877.",
				"Dos Passos was born in Chicago, Illinois, the illegitimate son of John Randolph Dos Passos (1844 - 1917).",
				"Walter Guinness was born in Dublin, Ireland, the third son of the 1st Earl of Iveagh ."
				);

		NLPBox fox =  new NLPBox();
		
		for (String sentence :  testSentences) {
			
			for ( Entity entity : fox.getNER(sentence) ) {
				
				String entityLabel	= entity.text;
				String entityType	= entity.type;
				
				
			}
		}
	}
}