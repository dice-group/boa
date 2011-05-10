package de.uni_leipzig.simba.boa.backend.test.rdf;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.Model;
import de.uni_leipzig.simba.boa.backend.rdf.Property;
import de.uni_leipzig.simba.boa.backend.rdf.PropertyDao;
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
	public void testPropertyCreation() {
		
		PropertyDao dao = new PropertyDao(this.store.createModelIfNotExists("rdf_test_model"));

		Property property1 = dao.createAndSaveProperty("http://example.com/property/language", Constants.OWL_DATATYPE_PROPERTY, "the language of a label", "http://example.com/class/Article", "http://example.com/class/Language");
		Property property2 = dao.findPropertyByUriWithDomainRangeLabelType("http://example.com/property/language");
		
		assertTrue(property1 != null);
		assertTrue(property2 != null);
		assertTrue(property1.getUri() + " vs. " + property2.getUri(),		property1.getUri() == property2.getUri());
		assertTrue(property1.getDomain() + " vs. " + property2.getDomain(),	property1.getDomain() == property2.getDomain());
		assertTrue(property1.getRange() + " vs. " + property2.getRange(),	property1.getRange() == property2.getRange());
		assertTrue(property1.getLabel() + " vs. " + property2.getLabel(),	property1.getLabel().equals(property2.getLabel()));
		assertTrue(property1.getType() + " vs. " + property2.getType(),		property1.getType() == property2.getType());
	}
	
	@Test
	public void testModelDrop() {
		
		Model model1 = this.store.createModelIfNotExists("rdf_test_model");
		this.store.removeModel("rdf_test_model");
		
		assertTrue("Store should be null:", this.store.getModel("rdf_test_model") == null);
	}
}
