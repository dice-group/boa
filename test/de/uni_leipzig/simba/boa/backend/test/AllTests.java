package de.uni_leipzig.simba.boa.backend.test;

import junit.framework.Test;
import junit.framework.TestSuite;


public class AllTests {

	public static Test suite() {

		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTest(PatternDaoTest.suite());
		suite.addTest(PatternTest.suite());
		suite.addTest(ClusterTest.suite());
		suite.addTest(LanguageDetectionTest.suite());
		suite.addTest(SentenceFilterTest.suite());
		suite.addTest(NamedEntityRecognizerTest.suite());
		suite.addTest(PatternSimilarityCalculatorTest.suite());
		suite.addTest(PatternMappingTest.suite());
		suite.addTest(PatternFilterTest.suite());
		//$JUnit-END$
		return suite;
	}

}
