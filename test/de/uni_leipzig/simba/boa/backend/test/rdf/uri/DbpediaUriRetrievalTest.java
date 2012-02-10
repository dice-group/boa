package de.uni_leipzig.simba.boa.backend.test.rdf.uri;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.uri.UriRetrieval;
import de.uni_leipzig.simba.boa.backend.rdf.uri.impl.DbpediaUriRetrieval;

public class DbpediaUriRetrievalTest {


	// initialize logging and settings
	NLPediaSetup setup = null;
	NLPediaLogger logger = null;
	
	public static junit.framework.Test suite() {
		
		return new JUnit4TestAdapter(DbpediaUriRetrievalTest.class);
	}
	
	@Before
	public void setUp() {
		
		this.setup = new NLPediaSetup(true);
		this.logger = new NLPediaLogger(DbpediaUriRetrievalTest.class);
	}

	@After
	public void cleanUpStreams() {
	    
		this.setup.destroy();
	}
	
	@Test
	public void testContext() {
		
		UriRetrieval uriRetrieval = new DbpediaUriRetrieval();
		
		System.out.println(uriRetrieval.getUri("Leipzig"));
		System.out.println(uriRetrieval.getUri("Bishkek"));
		System.out.println(uriRetrieval.getUri("Islamic Republic of Afghanistan"));
	}
}
