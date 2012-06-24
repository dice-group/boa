package de.uni_leipzig.simba.boa.backend.rdf;

import static org.junit.Assert.assertEquals;
import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.uri.UriRetrieval;
import de.uni_leipzig.simba.boa.backend.rdf.uri.impl.MeshupUriRetrieval;

public class UriRetrievalTest {


	// initialize logging and settings
	NLPediaSetup setup = null;
	NLPediaLogger logger = null;
	
	public static junit.framework.Test suite() {
		
		return new JUnit4TestAdapter(UriRetrievalTest.class);
	}
	
	@Before
	public void setUp() {
		
		this.setup = new NLPediaSetup(true);
		this.logger = new NLPediaLogger(UriRetrievalTest.class);
	}

	@After
	public void cleanUpStreams() {
	    
		this.setup.destroy();
	}
	
	@Test
	public void testContext() {

	    // note that this is a webservice and will only work if the index is working
		UriRetrieval uriRetrieval = new MeshupUriRetrieval();
		assertEquals("http://dbpedia.org/resource/Leipzig", uriRetrieval.getUri("Leipzig"));
		assertEquals("http://dbpedia.org/resource/Bishkek", uriRetrieval.getUri("Bishkek"));
		assertEquals("http://dbpedia.org/resource/Islamic_Republic_of_Afghanistan", uriRetrieval.getUri("Islamic Republic of Afghanistan"));
		assertEquals("http://boa.aksw.org/resource/This_will_not_be_in_wikipedia", uriRetrieval.getUri("This will not be in wikipedia"));
	}
}
