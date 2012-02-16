package de.uni_leipzig.simba.boa.backend.evaluation;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import junit.framework.JUnit4TestAdapter;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Resource;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;

public class EvaluationTest {

	// initialize logging and settings
	NLPediaSetup setup = null;
	NLPediaLogger logger = null;
	
	public static junit.framework.Test suite() {
		
		return new JUnit4TestAdapter(EvaluationTest.class);
	}
	
	@Before
	public void setUp() {
		
		this.setup = new NLPediaSetup(true);
		this.logger = new NLPediaLogger(EvaluationTest.class);
	}

	@After
	public void cleanUpStreams() {
	    
		this.setup.destroy();
	}
	
	@Test
	public void testEqualSets(){
		
		PrecisionRecallFMeasure prm;
		
		// we load both times the same model so this should be equal
		prm = new PrecisionRecallFMeasure(buildGoldStandard(), buildTestData1());
		assertEquals(1D, prm.getPrecision(), 0);
		assertEquals(1D, prm.getRecall(), 0);
		assertEquals(1D, prm.getFMeasure(), 0);
		System.out.println();
		
		// testdata only has half of the triples from GS but all are correct
		prm = new PrecisionRecallFMeasure(buildGoldStandard(), buildTestData2());
		assertEquals(1D, prm.getPrecision(), 0);
		assertEquals(0.5D, prm.getRecall(), 0);
		assertEquals(0.66D, prm.getFMeasure(), 0.007);
		System.out.println();
		
		// test-data contains more triples than GS but all the correct ones are test data
		prm = new PrecisionRecallFMeasure(buildGoldStandard(), buildTestData3());
		assertEquals(0.8D, prm.getPrecision(), 0);
		assertEquals(1D, prm.getRecall(), 0);
		assertEquals(0.88888D, prm.getFMeasure(), 0.0001);
		System.out.println();
		
		// test-data contains not all triples from GS (66% / 10Triples) but does contain more (33% / 5triples)
		prm = new PrecisionRecallFMeasure(buildGoldStandard(), buildTestData4());
		assertEquals(0.66666D, prm.getPrecision(), 0.0001);
		assertEquals(0.5D, prm.getRecall(), 0.001);
		assertEquals(0.571D, prm.getFMeasure(), 0.001);
		System.out.println();
		
		// the test data does not contain anything so precision, recall and fmeasure should be zero
		prm = new PrecisionRecallFMeasure(buildGoldStandard(), buildTestData5());
		assertEquals(0D, prm.getPrecision(), 0.0001);
		assertEquals(0D, prm.getRecall(), 0.001);
		assertEquals(0D, prm.getFMeasure(), 0.001);
		System.out.println();
	}
	
	@Test
	public void testQueryEvaluationIndex() throws CorruptIndexException, IOException, ParseException{

//		new EvaluationFileLoader().loadGoldStandard(ExcludeRdfTypeStatements.YES);
//		Directory idx = EvaluationIndexCreator.createGoldStandardIndex();
//		IndexSearcher indexSearcher = new IndexSearcher(idx, true);
//		Analyzer analyzer = new WhitespaceAnalyzer();
//		QueryParser parser = new QueryParser(Version.LUCENE_30, "sentence-lc", analyzer);
//		
//		assertTrue("There should be more than thousend sentences in index!", indexSearcher.maxDoc() > 1000 );
//		
//		boolean containsSentences = false;
//		ScoreDoc[] hits = indexSearcher.search(parser.parse("+sentence:\"capital\""), null, 100).scoreDocs;
//		for (int i = hits.length - 1; i >= 0; i--) {
//
//			// get the indexed string and put it in the result
//			if ( indexSearcher.doc(hits[i].doc).get("sentence").
//					equals("Sujiatun District is a district of Shenyang , the capital of Liaoning province , People 's Republic of China .")) {
//				containsSentences = true;
//			}
//		}
//		assertTrue("The index should contain a sentence with capital!", hits.length > 1 );
//		assertTrue("The index should contain: ”Sujiatun District is a district of Shenyang , the capital of Liaoning province , People 's Republic of China .”", containsSentences );
	}
	
	private Set<Triple> buildTestData1() {

		Set<Triple> testData = new HashSet<Triple>();
		testData.add(new Triple(new Resource("r1"), new Property("p1"), new Resource("r21")));
		testData.add(new Triple(new Resource("r2"), new Property("p1"), new Resource("r22")));
		testData.add(new Triple(new Resource("r3"), new Property("p2"), new Resource("r23")));
		testData.add(new Triple(new Resource("r4"), new Property("p2"), new Resource("r24")));
		testData.add(new Triple(new Resource("r5"), new Property("p3"), new Resource("r25")));
		
		testData.add(new Triple(new Resource("r6"), new Property("p3"), new Resource("r26")));
		testData.add(new Triple(new Resource("r7"), new Property("p4"), new Resource("r27")));
		testData.add(new Triple(new Resource("r8"), new Property("p4"), new Resource("r28")));
		testData.add(new Triple(new Resource("r9"), new Property("p5"), new Resource("r29")));
		testData.add(new Triple(new Resource("r10"), new Property("p5"), new Resource("r30")));
		
		testData.add(new Triple(new Resource("r11"), new Property("p6"), new Resource("r31")));
		testData.add(new Triple(new Resource("r12"), new Property("p6"), new Resource("r32")));
		testData.add(new Triple(new Resource("r13"), new Property("p7"), new Resource("r33")));
		testData.add(new Triple(new Resource("r14"), new Property("p7"), new Resource("r34")));
		testData.add(new Triple(new Resource("r15"), new Property("p8"), new Resource("r35")));
		
		testData.add(new Triple(new Resource("r16"), new Property("p8"), new Resource("r36")));
		testData.add(new Triple(new Resource("r17"), new Property("p9"), new Resource("r37")));
		testData.add(new Triple(new Resource("r18"), new Property("p9"), new Resource("r38")));
		testData.add(new Triple(new Resource("r19"), new Property("p10"), new Resource("r39")));
		testData.add(new Triple(new Resource("r20"), new Property("p10"), new Resource("r40")));
		return testData;
	}
	
	private Set<Triple> buildTestData2() {

		Set<Triple> testData = new HashSet<Triple>();
		testData.add(new Triple(new Resource("r1"), new Property("p1"), new Resource("r21")));
		testData.add(new Triple(new Resource("r2"), new Property("p1"), new Resource("r22")));
		testData.add(new Triple(new Resource("r3"), new Property("p2"), new Resource("r23")));
		testData.add(new Triple(new Resource("r4"), new Property("p2"), new Resource("r24")));
		testData.add(new Triple(new Resource("r5"), new Property("p3"), new Resource("r25")));
		
		testData.add(new Triple(new Resource("r6"), new Property("p3"), new Resource("r26")));
		testData.add(new Triple(new Resource("r7"), new Property("p4"), new Resource("r27")));
		testData.add(new Triple(new Resource("r8"), new Property("p4"), new Resource("r28")));
		testData.add(new Triple(new Resource("r9"), new Property("p5"), new Resource("r29")));
		testData.add(new Triple(new Resource("r10"), new Property("p5"), new Resource("r30")));
		
		return testData;
	}
	
	private Set<Triple> buildTestData3() {

		Set<Triple> testData = new HashSet<Triple>();
		testData.add(new Triple(new Resource("r1"), new Property("p1"), new Resource("r21")));
		testData.add(new Triple(new Resource("r2"), new Property("p1"), new Resource("r22")));
		testData.add(new Triple(new Resource("r3"), new Property("p2"), new Resource("r23")));
		testData.add(new Triple(new Resource("r4"), new Property("p2"), new Resource("r24")));
		testData.add(new Triple(new Resource("r5"), new Property("p3"), new Resource("r25")));
		
		testData.add(new Triple(new Resource("r6"), new Property("p3"), new Resource("r26")));
		testData.add(new Triple(new Resource("r7"), new Property("p4"), new Resource("r27")));
		testData.add(new Triple(new Resource("r8"), new Property("p4"), new Resource("r28")));
		testData.add(new Triple(new Resource("r9"), new Property("p5"), new Resource("r29")));
		testData.add(new Triple(new Resource("r10"), new Property("p5"), new Resource("r30")));
		
		testData.add(new Triple(new Resource("r11"), new Property("p6"), new Resource("r31")));
		testData.add(new Triple(new Resource("r12"), new Property("p6"), new Resource("r32")));
		testData.add(new Triple(new Resource("r13"), new Property("p7"), new Resource("r33")));
		testData.add(new Triple(new Resource("r14"), new Property("p7"), new Resource("r34")));
		testData.add(new Triple(new Resource("r15"), new Property("p8"), new Resource("r35")));
		
		testData.add(new Triple(new Resource("r16"), new Property("p8"), new Resource("r36")));
		testData.add(new Triple(new Resource("r17"), new Property("p9"), new Resource("r37")));
		testData.add(new Triple(new Resource("r18"), new Property("p9"), new Resource("r38")));
		testData.add(new Triple(new Resource("r19"), new Property("p10"), new Resource("r39")));
		testData.add(new Triple(new Resource("r20"), new Property("p10"), new Resource("r40")));
		
		testData.add(new Triple(new Resource("r21"), new Property("p11"), new Resource("r41")));
		testData.add(new Triple(new Resource("r22"), new Property("p11"), new Resource("r42")));
		testData.add(new Triple(new Resource("r23"), new Property("p12"), new Resource("r43")));
		testData.add(new Triple(new Resource("r24"), new Property("p12"), new Resource("r44")));
		testData.add(new Triple(new Resource("r25"), new Property("p13"), new Resource("r45")));
		
		return testData;
	}
	
	private Set<Triple> buildTestData4() {

		Set<Triple> testData = new HashSet<Triple>();
		testData.add(new Triple(new Resource("r1"), new Property("p1"), new Resource("r21")));
		testData.add(new Triple(new Resource("r2"), new Property("p1"), new Resource("r22")));
		testData.add(new Triple(new Resource("r3"), new Property("p2"), new Resource("r23")));
		testData.add(new Triple(new Resource("r4"), new Property("p2"), new Resource("r24")));
		testData.add(new Triple(new Resource("r5"), new Property("p3"), new Resource("r25")));
		
		testData.add(new Triple(new Resource("r6"), new Property("p3"), new Resource("r26")));
		testData.add(new Triple(new Resource("r7"), new Property("p4"), new Resource("r27")));
		testData.add(new Triple(new Resource("r8"), new Property("p4"), new Resource("r28")));
		testData.add(new Triple(new Resource("r9"), new Property("p5"), new Resource("r29")));
		testData.add(new Triple(new Resource("r10"), new Property("p5"), new Resource("r30")));
		
		testData.add(new Triple(new Resource("r21"), new Property("p11"), new Resource("r41")));
		testData.add(new Triple(new Resource("r22"), new Property("p11"), new Resource("r42")));
		testData.add(new Triple(new Resource("r23"), new Property("p12"), new Resource("r43")));
		testData.add(new Triple(new Resource("r24"), new Property("p12"), new Resource("r44")));
		testData.add(new Triple(new Resource("r25"), new Property("p13"), new Resource("r45")));
		
		return testData;
	}
	
	private Set<Triple> buildTestData5() {

		return new HashSet<Triple>();
	}

	private Set<Triple> buildGoldStandard() {

		Set<Triple> goldStandard = new HashSet<Triple>();
		goldStandard.add(new Triple(new Resource("r1"), new Property("p1"), new Resource("r21")));
		goldStandard.add(new Triple(new Resource("r2"), new Property("p1"), new Resource("r22")));
		goldStandard.add(new Triple(new Resource("r3"), new Property("p2"), new Resource("r23")));
		goldStandard.add(new Triple(new Resource("r4"), new Property("p2"), new Resource("r24")));
		goldStandard.add(new Triple(new Resource("r5"), new Property("p3"), new Resource("r25")));
		
		goldStandard.add(new Triple(new Resource("r6"), new Property("p3"), new Resource("r26")));
		goldStandard.add(new Triple(new Resource("r7"), new Property("p4"), new Resource("r27")));
		goldStandard.add(new Triple(new Resource("r8"), new Property("p4"), new Resource("r28")));
		goldStandard.add(new Triple(new Resource("r9"), new Property("p5"), new Resource("r29")));
		goldStandard.add(new Triple(new Resource("r10"), new Property("p5"), new Resource("r30")));
		
		goldStandard.add(new Triple(new Resource("r11"), new Property("p6"), new Resource("r31")));
		goldStandard.add(new Triple(new Resource("r12"), new Property("p6"), new Resource("r32")));
		goldStandard.add(new Triple(new Resource("r13"), new Property("p7"), new Resource("r33")));
		goldStandard.add(new Triple(new Resource("r14"), new Property("p7"), new Resource("r34")));
		goldStandard.add(new Triple(new Resource("r15"), new Property("p8"), new Resource("r35")));
		
		goldStandard.add(new Triple(new Resource("r16"), new Property("p8"), new Resource("r36")));
		goldStandard.add(new Triple(new Resource("r17"), new Property("p9"), new Resource("r37")));
		goldStandard.add(new Triple(new Resource("r18"), new Property("p9"), new Resource("r38")));
		goldStandard.add(new Triple(new Resource("r19"), new Property("p10"), new Resource("r39")));
		goldStandard.add(new Triple(new Resource("r20"), new Property("p10"), new Resource("r40")));
		return goldStandard;
	}
}
