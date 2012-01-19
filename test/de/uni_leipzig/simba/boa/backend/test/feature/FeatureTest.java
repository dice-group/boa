package de.uni_leipzig.simba.boa.backend.test.feature;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl.TotalOccurrenceFeature;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.search.PatternSearcher;
import junit.framework.JUnit4TestAdapter;

public class FeatureTest {

	// initialize logging and settings
	NLPediaSetup setup = null;
	NLPediaLogger logger = null;
	
	int numberOfMappings = 10;
	int[] numberOfMaxPatterns = {55, 82, 94, 54, 12, 5, 45,22, 44, 102};

	public static junit.framework.Test suite() {

		return new JUnit4TestAdapter(FeatureTest.class);
	}

	@Before
	public void setUp() {

		this.setup = new NLPediaSetup(true);
		this.logger = new NLPediaLogger(FeatureTest.class);
	}

	@After
	public void cleanUpStreams() {

		this.setup.destroy();
	}
	
	@Test
	public void testTotalOccurrenceTest() throws IOException, ParseException{
		
		TotalOccurrenceFeature totalOccurrenceFeature = new TotalOccurrenceFeature();
		List<PatternMapping> mappings = createTestData(this.numberOfMappings, this.numberOfMaxPatterns);
		Directory idx = this.createSentenceIndex();
		
		for (PatternMapping mapping : mappings){
			
			totalOccurrenceFeature.scoreMapping(mapping, new PatternSearcher(idx));
		}
		
		Pattern pm0p0 = new ArrayList<Pattern>(mappings.get(0).getPatterns()).get(0);
		Pattern pm0p10 = new ArrayList<Pattern>(mappings.get(0).getPatterns()).get(10);
		Pattern pm9p101 = new ArrayList<Pattern>(mappings.get(9).getPatterns()).get(101);

		assertEquals("pm0p0.TO == 98", 98, (int) pm0p0.getFeatures().get(Feature.TOTAL_OCCURRENCE).doubleValue());
		assertEquals("pm0p10-TO == 99", 99, (int) pm0p10.getFeatures().get(Feature.TOTAL_OCCURRENCE).doubleValue());
		assertEquals("pm9p101-TO == 98", 98, (int) pm9p101.getFeatures().get(Feature.TOTAL_OCCURRENCE).doubleValue());
	}
	
	@Test
	public void testCreateTestData() {
		
		List<PatternMapping> mappings = this.createTestData(numberOfMappings, numberOfMaxPatterns);
		
		assertEquals(numberOfMappings + " mappings", numberOfMappings, mappings.size());
		for (int i = 0; i < numberOfMappings ; i++) {
			
			assertEquals(numberOfMaxPatterns[i] + " patterns for mapping " + i, numberOfMaxPatterns[i], mappings.get(i).getPatterns().size());
		}
	}
	
	public List<PatternMapping> createTestData(int numberOfMappings, int[] numberOfMaxPatterns){
		
		List<PatternMapping> mappings = new ArrayList<PatternMapping>();
		
		for (int i = 0 ; i < numberOfMappings; i++) {
			
			PatternMapping mapping = new PatternMapping();
			Property property = new Property();
			property.setLabel("label " + i);
			property.setSynsets("label " + i + ", label " + (i+1));
			property.setRdfsDomain("http://url.com/domain");
			property.setRdfsRange("http://url.com/range");
			property.setUri("http://url.com/uri " + i);
			mapping.setProperty(property);
			
			Set<Pattern> patterns = new HashSet<Pattern>();
			for (int j = 0; j < numberOfMaxPatterns[i]; j++){
				
				Pattern pattern = new Pattern();
				pattern.setNumberOfOccurrences(1000 / (j + 1));
				
				if ( j % 3 == 0 ) pattern.setNaturalLanguageRepresentation("?R? is a "+j+" nlr of ?D?");
				else pattern.setNaturalLanguageRepresentation("?D? is a "+j+" nlr of ?R?");
				
				Map<String,Integer> learnedFrom = new HashMap<String,Integer>();
				learnedFrom.put("label1-;-label2", j+i % 10);
				learnedFrom.put("label3-;-label2", 2*j+i+4 % 10);
				learnedFrom.put("label2-;-label4", 4*j+i+7 % 10);
				pattern.setLearnedFrom(learnedFrom);
				
				patterns.add(pattern);
			}
			mapping.setPatterns(patterns);
			
			mappings.add(mapping);
		}
		return mappings;
	}
	
	public Directory createSentenceIndex() {
		
		Directory idx = null; 
		
		try {
			
			idx = new RAMDirectory();
			Analyzer analyzer = new WhitespaceAnalyzer(Version.LUCENE_30);
			IndexWriter writer = new IndexWriter(idx, analyzer, true, IndexWriter.MaxFieldLength.LIMITED);

			for (int i = 0 ; i <= 10000 ; i++ ) {
				
				String sentence = "This is the "+i+"th sentence which contains is a "+i%102+" nlr of a test blubb rofl.";
				
				Document doc = new Document();
				doc.add(new Field("sentence", sentence, Field.Store.YES, Field.Index.NOT_ANALYZED, Field.TermVector.NO));
				doc.add(new Field("sentence-lc", sentence.toLowerCase(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO));
				writer.addDocument(doc);
			}
			writer.optimize();
			writer.close();
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
		
		return idx;
	}
}