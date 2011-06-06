package de.uni_leipzig.simba.boa.backend.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.JUnit4TestAdapter;

import org.apache.lucene.queryParser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.crawl.RelationFinder;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.search.PatternSearcher;


public class PatternSearcherTest {

	
	// initialize logging and settings
	NLPediaSetup setup = null;
	NLPediaLogger logger = null;
	
	
	public static junit.framework.Test suite() {
		
		return new JUnit4TestAdapter(PatternSearcherTest.class);
	}
	
	@Before
	public void setUp() {
		
		this.setup = new NLPediaSetup(true);
		this.logger = new NLPediaLogger(PatternSearcherTest.class);
	}

	@After
	public void cleanUpStreams() {
	    
		this.setup.destroy();
	}
	
	@Test
	public void testPattern() {
		
		try {
			
			PatternSearcher searcher = new PatternSearcher(NLPediaSettings.getInstance().getSetting("sentenceIndexDirectory"));
			
			List<String[]> labels =  RelationFinder.getRelationFromFile(NLPediaSettings.getInstance().getSetting(""));
			
			for (int i = 0; i < labels.size() ; i++) {
				
				if ( !labels.get(i)[0].equals(labels.get(i)[2]) && !labels.get(i)[0].contains("?") && !labels.get(i)[2].contains("?")) {
					
					searcher.queryPattern(labels.get(i)[0], labels.get(i)[2], labels.get(i)[1], labels.get(i)[3], labels.get(i)[4], true);
				}
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
