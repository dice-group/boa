package de.uni_leipzig.simba.boa.backend.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import junit.framework.JUnit4TestAdapter;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Resource;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;
import de.uni_leipzig.simba.boa.backend.search.PatternSearcher;
import de.uni_leipzig.simba.boa.backend.search.SearchResult;


public class NerTagUtf8Test {

	// initialize logging and settings
	NLPediaSetup setup = null;
	NLPediaLogger logger = null;
	
	public static junit.framework.Test suite() {
		
		return new JUnit4TestAdapter(NerTagUtf8Test.class);
	}
	
	@Before
	public void setUp() {
		
		this.setup = new NLPediaSetup(true);
		this.logger = new NLPediaLogger(NerTagUtf8Test.class);
	}

	@After
	public void cleanUpStreams() {
		this.setup.destroy();
		
	}
	
	@Test
	public void testEncoding() {
		
		try {
			
			Directory index	= FSDirectory.open(new File("/Users/gerb/Development/workspaces/experimental/nlpedia/de_wiki/index/test"));
			
			IndexSearcher indexSearcher = new IndexSearcher(index, true);
			for ( int i = 0; i < indexSearcher.maxDoc(); i++) {
				
				System.out.println(indexSearcher.doc(i).get("sentence"));
			}
			
			PatternSearcher searcher = new PatternSearcher("/Users/gerb/Development/workspaces/experimental/nlpedia/de_wiki/index/test");
			
			Property prop = new Property();
			prop.setUri("http://property/test.de");
			prop.setRdfsDomain("http://domain.de");
			prop.setRdfsRange("http:/range.de");
			
			Resource s = new Resource();
			s.setLabel("Serie MacGyver");
			Resource o = new Resource();
			o.setLabel("Alan Smithee");
			
			Triple triple = new Triple();
			triple.setSubject(s);
			triple.setProperty(prop);
			triple.setObject(o);
			
			searcher.queryPattern(triple);
			
			PatternMappingDao pmDao = (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
			
			PatternMapping mapping = new PatternMapping();
			mapping.setProperty(new Property());
			
			for ( SearchResult result : searcher.getResults() ) {
				
				mapping.getProperty().setUri(result.getProperty());
				mapping.getProperty().setRdfsDomain(result.getRdfsDomain());
				mapping.getProperty().setRdfsRange(result.getRdfsRange());
				
				Pattern p = new Pattern();
				p.setNaturalLanguageRepresentation(result.getNaturalLanguageRepresentation());
				mapping.addPattern(p);
				pmDao.createAndSavePatternMapping(mapping);
			}
			
			PatternMapping pm = pmDao.findPatternMapping(mapping.getId());
			
			System.out.println(pm.getPatterns().iterator().next().getNaturalLanguageRepresentation());

			QueryParser exactMatchParser = new QueryParser(Version.LUCENE_30, "sentence", new SimpleAnalyzer());
			ScoreDoc[] hits = indexSearcher.search(exactMatchParser.parse("\"Schulmädchen\""), null, 10).scoreDocs;
			
			assertTrue("One sentence contains the keyword:", hits.length == 1);
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (LockObtainFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
