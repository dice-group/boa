package de.uni_leipzig.simba.boa.backend;

import junit.framework.Test;
import junit.framework.TestSuite;
import de.uni_leipzig.simba.boa.backend.entity.BackgroundKnowledgeTest;
import de.uni_leipzig.simba.boa.backend.entity.ContextTest;
import de.uni_leipzig.simba.boa.backend.entity.PatternFilterTest;
import de.uni_leipzig.simba.boa.backend.entity.PatternTest;
import de.uni_leipzig.simba.boa.backend.entity.TripleTest;
import de.uni_leipzig.simba.boa.backend.evaluation.EvaluationTest;
import de.uni_leipzig.simba.boa.backend.feature.FeatureTest;
import de.uni_leipzig.simba.boa.backend.lucene.LuceneTest;
import de.uni_leipzig.simba.boa.backend.nlp.StanfordNLPNamedEntityRecognitionTest;
import de.uni_leipzig.simba.boa.backend.nlp.StanfordNLPPartOfSpeechTaggerTest;
import de.uni_leipzig.simba.boa.backend.rdf.UriRetrievalTest;


public class AllTests {

	public static Test suite() {

		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTest(PatternTest.suite());
		suite.addTest(PatternFilterTest.suite());
		suite.addTest(ContextTest.suite());
		suite.addTest(TripleTest.suite());
		suite.addTest(EvaluationTest.suite());
		suite.addTest(FeatureTest.suite());
		suite.addTest(LuceneTest.suite());
		suite.addTest(UriRetrievalTest.suite());
		suite.addTest(StanfordNLPNamedEntityRecognitionTest.suite());
		suite.addTest(StanfordNLPPartOfSpeechTaggerTest.suite());
		suite.addTest(BackgroundKnowledgeTest.suite());
		//$JUnit-END$
		return suite;
	}

}
