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
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.sentenceboundarydisambiguation.SentenceBoundaryDisambiguation;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.sentenceboundarydisambiguation.impl.StanfordNLPSentenceBoundaryDisambiguation;


/**
 * @author gerb
 *
 */
public class StanfordNLPSentenceBoundaryDisambiguationTest extends TestCase {

	// initialize logging and settings
	NLPediaSetup setup = null;
	NLPediaLogger logger = null;
	
	public static junit.framework.Test suite() {

		return new JUnit4TestAdapter(StanfordNLPSentenceBoundaryDisambiguationTest.class);
	}

	@Before
	public void setUp() {

		this.setup = new NLPediaSetup(true);
		this.logger = new NLPediaLogger(StanfordNLPSentenceBoundaryDisambiguationTest.class);
	}

	@After
	public void cleanUpStreams() {

		this.setup.destroy();
	}
	
	@Test
	public void testChunking() {
		
		String string = ", 1999 NA April 18, 2000[1] Genre(s) Racing Mode(s) Single player, multiplayer Rating(s) ELSPA: 3+ ESRB: E OFLC: G. Speed Freaks (released as Speed Punks outside Europe and Australia) is a racing video game for the PlayStation for up to 2 players (4 with a multi-tap for PlayStation). The game involves racing around a variety of tracks while using several weapons; including items that make the racer";
		
		SentenceBoundaryDisambiguation sbd = new StanfordNLPSentenceBoundaryDisambiguation();
		for ( String s : sbd.getSentences(string) ) System.out.println(s);
	}
}
