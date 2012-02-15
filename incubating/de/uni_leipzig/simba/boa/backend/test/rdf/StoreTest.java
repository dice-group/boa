package de.uni_leipzig.simba.boa.backend.test.rdf;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.Model;
import de.uni_leipzig.simba.boa.backend.rdf.store.Store;

public class StoreTest {

	// initialize logging and settings
	NLPediaSetup setup = null;
	NLPediaLogger logger = null;
	
	Store store = null;
	
	public static junit.framework.Test suite() {
		
		return new JUnit4TestAdapter(StoreTest.class);
	}
	
	@Before
	public void setUp() {
		
		this.setup = new NLPediaSetup(true);
		this.logger = new NLPediaLogger(StoreTest.class);
		this.store = new Store();
	}

	@After
	public void cleanUpStreams() {
	    
		this.setup.destroy();
		this.store.dropDatabase();
	}
	
	@Test
	public void testStore() {
		
		Model model1 = this.store.createModelIfNotExists("en_wiki");
		assertTrue(model1 != null); // we found or created a model
	}
	
	@Test
	public void testModel() {
		
		Model model1 = this.store.createModelIfNotExists("en_wiki");

//		model1.addStatement(model1.createStatement(model1.createResource("http://person.de/daniel"), model1.createProperty("http://person.de/likes"), model1.createResource("http://person.de/jules")));
		assertFalse(model1.getNumberOfStatements() == 1);
	}
	
	@Test
	public void testModelDrop() {
		
		Model model1 = this.store.createModelIfNotExists("rdf_test_model");
		this.store.removeModel("rdf_test_model");
		
		assertTrue("Store should be null:", this.store.getModel("rdf_test_model") == null);
	}
}
