package de.uni_leipzig.simba.boa.backend.test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternDao;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.dao.rdf.PropertyDao;
import de.uni_leipzig.simba.boa.backend.dao.rdf.ResourceDao;
import de.uni_leipzig.simba.boa.backend.dao.rdf.TripleDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Resource;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;


public class PatternMappingTest {

	// initialize logging and settings
	NLPediaSetup setup = null;
	NLPediaLogger logger = null;
	
	
	public static junit.framework.Test suite() {
		
		return new JUnit4TestAdapter(PatternMappingTest.class);
	}
	
	@Before
	public void setUp() {
		
		this.setup = new NLPediaSetup(true);
		this.logger = new NLPediaLogger(PatternMappingTest.class);
	}

	@After
	public void cleanUpStreams() {
	    
		this.setup.destroy();
	}
	
	@Test
	public void testPattern() {
		
		PropertyDao propertyDao	= (PropertyDao) DaoFactory.getInstance().createDAO(PropertyDao.class);
		Property p1 = propertyDao.createNewEntity();
		p1.setUri("http://dbpedia.org/ontology/property1");
		propertyDao.updateProperty(p1);
		Property p2 = propertyDao.createNewEntity();
		p2.setUri("http://dbpedia.org/ontology/property2");
		propertyDao.updateProperty(p2);
		
		PatternMappingDao patternMappingDao	= (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
		PatternMapping mapping1 = patternMappingDao.createNewEntity(p1);
		PatternMapping mapping2 = patternMappingDao.createNewEntity(p1);
		
		assertTrue(patternMappingDao.findAllPatternMappings().size() == 2);
		assertTrue(patternMappingDao.findPatternMapping(1).getProperty().getUri().equals(mapping1.getProperty().getUri()));

		PatternDao patternDao	= (PatternDao) DaoFactory.getInstance().createDAO(PatternDao.class);
		
		Pattern pa1 = patternDao.createNewEntity();
		pa1.setNaturalLanguageRepresentation("the 1st pattern");
		pa1.setPatternMapping(mapping1);
		patternDao.updatePattern(pa1);
		
		Pattern pa2 = patternDao.createNewEntity();
		pa2.setNaturalLanguageRepresentation("the 2nd pattern");
		pa2.setPatternMapping(mapping1);
		patternDao.updatePattern(pa2);
		
		Pattern pa3 = patternDao.createNewEntity();
		pa3.setNaturalLanguageRepresentation("the 3rd pattern");
		pa3.setPatternMapping(mapping2);
		patternDao.updatePattern(pa3);
		
		patternMappingDao.updatePatternMapping(mapping1);
		patternMappingDao.updatePatternMapping(mapping2);
		
		ResourceDao resourceDao	= (ResourceDao) DaoFactory.getInstance().createDAO(ResourceDao.class);
		Resource r1 = resourceDao.createNewEntity();
		r1.setUri("http://r1.uri");
		Resource r2 = resourceDao.createNewEntity();
		r2.setUri("http://r1.uri");
		resourceDao.updateResource(r1);
		resourceDao.updateResource(r2);
		
		TripleDao tripleDao	= (TripleDao) DaoFactory.getInstance().createDAO(TripleDao.class);
		Triple triple1 = tripleDao.createNewEntity();
		Triple triple2 = tripleDao.createNewEntity();
		
		triple1.setSubject(r1);
		triple1.setProperty(p1);
		triple1.setObject(r2);
		
		triple2.setSubject(r1);
		triple2.setProperty(p2);
		triple2.setObject(r2);
		
		tripleDao.updateTriple(triple1);
		tripleDao.updateTriple(triple2);
		
		Set<Pattern> patternList = new HashSet<Pattern>();
		Collections.addAll(patternList, pa1,pa2,pa3);
		triple1.setLearnedFromPatterns(patternList);
		triple2.setLearnedFromPatterns(patternList);
		
		tripleDao.updateTriple(triple1);
		tripleDao.updateTriple(triple2);
		
		assertTrue(tripleDao.findAllTriples().size() == 2);
		assertTrue(tripleDao.findTriple(1).getLearnedFromPatterns().size() == 3);
	}
}
