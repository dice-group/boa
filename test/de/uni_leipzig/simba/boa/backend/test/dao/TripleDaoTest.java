package de.uni_leipzig.simba.boa.backend.test.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.rdf.TripleDao;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Resource;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;

public class TripleDaoTest {

	// initialize logging and settings
	NLPediaSetup setup = null;
	NLPediaLogger logger = null;
	
	public static junit.framework.Test suite() {
		
		return new JUnit4TestAdapter(TripleDaoTest.class);
	}
	
	@Before
	public void setUp() {
		
		this.setup = new NLPediaSetup(true);
		this.logger = new NLPediaLogger(TripleDaoTest.class);
	}

	@After
	public void cleanUpStreams() {
	    
		this.setup.destroy();
	}
	
//	@Test
	public void testDoesTripleExist(){

		TripleDao tripleDao = (TripleDao) DaoFactory.getInstance().createDAO(TripleDao.class);
		
		Triple tripleOne = new Triple();
		tripleOne.setSubject(new Resource("http://subject.de"));
		tripleOne.setProperty(new Property("http://property.de"));
		tripleOne.setObject(new Resource("http://object.de"));
		tripleDao.updateTriple(tripleOne);
		
		assertTrue("Both triples should be equal!", tripleDao.exists(tripleOne));
	}
	
//	@Test
	public void testQueryTopNPattern(){

		TripleDao tripleDao = (TripleDao) DaoFactory.getInstance().createDAO(TripleDao.class);
		
		for (int i = 1 ; i <= 1000; i++ ){

			Triple tripleOne = new Triple();
			tripleOne.setSubject(new Resource("http://subject"+i+".de"));
			tripleOne.setProperty(new Property("http://property"+i+".de"));
			tripleOne.setObject(new Resource("http://object"+i+".de"));
			tripleOne.setCorrect(false);
			tripleOne.setConfidence(1D/(double)i);

			System.out.println(i);
			
			if ( i > 50 ) {
				tripleOne.setCorrect(true);
			}
			tripleDao.updateTriple(tripleOne);
		}
		
		List<Triple> topNTriple = tripleDao.queryTopNTriples(100);
		assertEquals(50, topNTriple.size());
		assertEquals(1, topNTriple.get(0).getConfidence(), 0);
		assertEquals(0.5, topNTriple.get(1).getConfidence(), 0.5);
		assertEquals(1D/50D, topNTriple.get(topNTriple.size()-1).getConfidence(), 0);
	}
	
	@Test
	public void testBatchSave(){

		TripleDao tripleDao = (TripleDao) DaoFactory.getInstance().createDAO(TripleDao.class);
		
		List<Triple> tripleList = new ArrayList<Triple>();
		
		for (int i = 1 ; i <= 1000; i++ ){

			Triple tripleOne = new Triple();
			tripleOne.setSubject(new Resource("http://subject"+i+".de"));
			tripleOne.setProperty(new Property("http://property"+i+".de"));
			tripleOne.setObject(new Resource("http://object"+i+".de"));
			tripleOne.setCorrect(false);
			tripleOne.setConfidence(1D/(double)i);

			tripleList.add(tripleOne);
		}
		
		tripleDao.batchSaveOrUpdate(tripleList);
		// no assert this just throws an exception if it does not work
	}
}
