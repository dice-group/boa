package de.uni_leipzig.simba.boa.backend.test;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.nlp.NamedEntityRecognizer;


public class NamedEntityRecognizerTest {

	// initialize logging and settings
	NLPediaSetup setup = null;
	NLPediaLogger logger = null;
	
	public static junit.framework.Test suite() {
		
		return new JUnit4TestAdapter(NamedEntityRecognizerTest.class);
	}
	
	@Before
	public void setUp() {
		
		this.setup = new NLPediaSetup(true);
		this.logger = new NLPediaLogger(NamedEntityRecognizerTest.class);
	}

	@After
	public void cleanUpStreams() {
	    
		this.setup.destroy();
	}
	
	@Test
	public void testParseDatePattern() {
		
		String dir = "/Users/gerb/Development/workspaces/java-ws/boa/WebContent/WEB-INF/data/training/classifier/";
		String[] models = new String[]{
			"dewac_175m_600.crf.ser.gz", "muc.7class.distsim.crf.ser.gz", "conll.closed.iob2.crf.ser.gz", "hgc_175m_600.crf.ser.gz", "ner-de-hgc_175M_600.ser.gz"	
		};
		
		for (String s : models) {
			
			NLPediaSettings.getInstance().setSetting("namendEntityRecognizerClassifier", dir + s);
			NamedEntityRecognizer ner = new NamedEntityRecognizer();
			
			String test1 = "Die Glühbirne wurde von Thomas Alva Edison in Milan entwickelt, der für die Firma Glühlampen Enterprises gearbeitet hat. " +
					" Er hat für die Teile 15 Euro ausgegeben. Er hat sie ungefähr am 15. Dezember 1871 erfunden. Nur 15% der Firma gehören zu seiner Tochter. Eines morgens ist er gegen 9 Uhr aufgewacht.";
			String test2 = "The light bulb was invented by Thomas Alva Edison in Milan, who worked for the company Lightbulb Enterprises. He" +
					" spent 15$ for the parts. He invented it about 15th December 1871. Only 15% percent of the company belonged to his daughter. One morning he woke up at 9 am.";
			
			System.out.println(ner.recognizeEntitiesInString(test1));
			System.out.println(ner.recognizeEntitiesInString(test2));
		}
	}
}
