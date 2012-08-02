package de.uni_leipzig.simba.boa.backend.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.JUnit4TestAdapter;

import org.apache.lucene.queryParser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.impl.SubjectPredicateObjectPattern;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.search.impl.DefaultPatternSearcher;


public class PatternTest {

	// initialize logging and settings
	NLPediaSetup setup = null;
	NLPediaLogger logger = null;
	
	
	public static junit.framework.Test suite() {
		
		return new JUnit4TestAdapter(PatternTest.class);
	}
	
	@Before
	public void setUp() {
		
		this.setup = new NLPediaSetup(true);
		this.logger = new NLPediaLogger(PatternTest.class);
	}

	@After
	public void cleanUpStreams() {
	    
		this.setup.destroy();
	}
	
	@Test
	public void testPattern() {
	
		Pattern p0 = new SubjectPredicateObjectPattern("?X? is a ?Y?");
		p0.setScore(0.3);
		p0.setNumberOfOccurrences(766);
		p0.setUseForPatternEvaluation(true);
		
		Pattern p1 = new SubjectPredicateObjectPattern("?X? is an ?Y?");
		p1.setScore(0.34);
		p1.setNumberOfOccurrences(76);
		p1.setUseForPatternEvaluation(false);
		
		Pattern p2 = new SubjectPredicateObjectPattern("?X? is an ?Y?");
		p2.setScore(0.345);
		p2.setNumberOfOccurrences(6);
		p2.setUseForPatternEvaluation(false);
		
		assertFalse(p0.equals(p2));
		assertTrue(p1.equals(p2));
	}
	
	@Test
    public void testIsSuitablePattern() throws IOException, ParseException {

	    DefaultPatternSearcher searcher = new DefaultPatternSearcher();
	    
        List<String> badPatterns = new ArrayList<String>();
        badPatterns.add("?D? is a ?R?"); // only stopwords
        badPatterns.add("winner ?R?"); // wrong format
        badPatterns.add("?D? ?D? winner ?R?"); // too many domains
        badPatterns.add("?D? , ?R?"); // to real words
        badPatterns.add("?D? ?R?"); // no words
        badPatterns.add("?D? 's ?R?"); // no words
        badPatterns.add("?R? 's ?D?"); // no words
        badPatterns.add("?R?  ?D?"); // no words
        
        for (String badPattern : badPatterns ) 
            assertFalse(searcher.isPatternSuitable(badPattern));       
        
        List<String> goodPatterns = new ArrayList<String>();
        goodPatterns.add("?D? winner ?R?"); // only stopwords
        
        for (String goodPattern : goodPatterns ) 
            assertTrue(searcher.isPatternSuitable(goodPattern));
    }
	
	@Test
	public void testGetCorrectCaseNLR() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
	    
	    DefaultPatternSearcher searcher = new DefaultPatternSearcher();
	    
	    Method method = DefaultPatternSearcher.class.getDeclaredMethod("getCorrectCaseNLR", String.class, String.class, String.class, Set.class);
	    method.setAccessible(true);
	    String normalCase = "In 1994 he and Edward Feigenbaum received the ACM Turing Award `` For pioneering the design and construction of large scale artificial intelligence systems , demonstrating the practical importance and potential commercial impact of artificial intelligence technology '' .";
	    String lowerCase = normalCase.toLowerCase(); 
	    String pattern = "?D? feigenbaum received the acm ?R?";
	    Set<String> allLabels = new HashSet<String>();
	    allLabels.addAll(Arrays.asList("Ed Feigenbaum","Feigenbaum","Edward Albert Feigenbaum","Edward Feigenbaum", "Edward A. Feigenbaum"));
	    allLabels.addAll(Arrays.asList("Turing Prize","Turing Awards","Turing award","Turing Award","Turing","A. M. Turing Award","List of Turing Award laureates","Alan turing award","Turing Award laureates","Turing Award Laureate","A.M. Turing Award","ACM Turing Award"));
	    
	    System.out.println(method.invoke(searcher, lowerCase, normalCase, pattern, allLabels));
	    assertEquals("?D? received the ?R?", method.invoke(searcher, lowerCase, normalCase, pattern, allLabels));
	}
}
