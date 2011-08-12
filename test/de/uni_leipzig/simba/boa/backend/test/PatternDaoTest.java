package de.uni_leipzig.simba.boa.backend.test;

import static org.junit.Assert.assertEquals;

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
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.persistance.hibernate.HibernateFactory;


public class PatternDaoTest {

	// initialize logging and settings
	NLPediaSetup setup = null;
	NLPediaLogger logger = null;
	
	public static junit.framework.Test suite() {
		
		return new JUnit4TestAdapter(PatternDaoTest.class);
	}
	
	@Before
	public void setUp() {
		
		this.setup = new NLPediaSetup(false);
		this.logger = new NLPediaLogger(PatternDaoTest.class);
	}

	@After
	public void cleanUpStreams() {
	    
		this.setup.destroy();
	}
	
//	@Test
	public void testPatternSaving() {
		
		PatternDao patternDao 				= (PatternDao) DaoFactory.getInstance().createDAO(PatternDao.class);
		PatternMappingDao patternMappingDao	= (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
	    
	    Set<String> sentences = new HashSet<String>();
	    sentences.add("In dem Satz kommts vor.");
	    sentences.add("In dem Satz kommts auch vor.");
	    sentences.add("Und in dem Satz kommts vor.");
	    
	    Pattern pattern1 = new Pattern("x is a y", "x is a y");
	    
	    pattern1.setConfidenceForIteration(1,5.4);
	    
	    Pattern pattern2 = new Pattern("x is a y", "x is a y");
	    
	    pattern2.setConfidenceForIteration(1,5.4);
	    
	    PatternMapping mapping = (PatternMapping) patternMappingDao.createNewEntity();
	    
	    mapping.getProperty().setUri("http://rdf/ns/.xml.as");
	    mapping.addPattern(pattern1);
	    mapping.addPattern(pattern2);
	    
	    patternMappingDao.updatePatternMapping(mapping);
	    
	    List<PatternMapping> mappings = patternMappingDao.findAllPatternMappings();
	    
//	    for (PatternMapping map : mappings) {
//	    	
//	    	System.out.println(map.getUri());
//	    	System.out.println(map.getId());
//	    	for ( Pattern pa : map.getPatterns() ) {
//	    		
//	    		System.out.println(pa.getId());
//	    		System.out.println(pa.getConfidence());
//	    		System.out.println(pa.getNaturalLanguageRepresentation());
//	    		
//	    		for ( String p : pa.getSentences() ) {
//	    			System.out.println(p);
//	    			
//	    		}
//	    	}
//	    }
//	    System.out.println(mapping.getId());
//	    PatternMapping mapping2 = (PatternMapping) patternMappingDao.findPatternMapping(mapping.getId());
//	    System.out.println(mapping2);
//	    assertEquals(mapping2.getUri(), "http://rdf/ns/.xml.as");
//	    assertEquals(2, mappings.get(0).getPatterns().size());
	}
	
//	@Test
//	public void testDatabaseSwitch() {
//		
//		HibernateFactory.changeConnection("en_wiki");
//	}
	
//	@Test
	public void testGetPatternMappingsWithoutPatterns() {
		
		PatternMappingDao patternMappingDao	= (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
		List<PatternMapping> mappings = patternMappingDao.findPatternMappingsWithoutPattern(null);
		
		for (PatternMapping mapping : mappings) {
			
			System.out.println(mapping.getProperty().getUri());
			System.out.println(mapping.getProperty().getRdfsRange());
			System.out.println(mapping.getProperty().getRdfsDomain());
			System.out.println();
		}
		
		HibernateFactory.changeConnection("simple_en_wiki");
		
		mappings = patternMappingDao.findPatternMappingsWithoutPattern(null);
		
		for (PatternMapping mapping : mappings) {
			
			System.out.println(mapping.getProperty().getUri());
			System.out.println(mapping.getProperty().getRdfsRange());
			System.out.println(mapping.getProperty().getRdfsDomain());
			System.out.println();
		}
	}
	
	@Test
	public void testCountNumberOfPatternWithNLR() {
		
		PatternDao patternDao 				= (PatternDao) DaoFactory.getInstance().createDAO(PatternDao.class);
		PatternMappingDao patternMappingDao	= (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
		
		Pattern pattern1 = new Pattern("?D? in the county of ?R?", "");
	    Pattern pattern2 = new Pattern("?D? in the county of ?R?", "");
	    
	    PatternMapping mapping1 = (PatternMapping) patternMappingDao.createNewEntity();
	    PatternMapping mapping2 = (PatternMapping) patternMappingDao.createNewEntity();
	    
	    mapping1.addPattern(pattern1);
	    mapping2.addPattern(pattern2);
	    
	    mapping1.getProperty().setUri("http://rdf/ns/.xml.as");
	    mapping1.getProperty().setUri("http://rdf/ns/.xml.ab");
	    
	    patternMappingDao.updatePatternMapping(mapping1);
	    patternMappingDao.updatePatternMapping(mapping2);
	}
}
