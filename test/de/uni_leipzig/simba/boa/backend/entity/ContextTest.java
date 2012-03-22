package de.uni_leipzig.simba.boa.backend.entity;

import static org.junit.Assert.assertEquals;
import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.entity.context.Context;
import de.uni_leipzig.simba.boa.backend.entity.context.LeftContext;
import de.uni_leipzig.simba.boa.backend.entity.context.RightContext;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;


public class ContextTest {

	// initialize logging and settings
	NLPediaSetup setup = null;
	NLPediaLogger logger = null;
	
	public static junit.framework.Test suite() {
		
		return new JUnit4TestAdapter(ContextTest.class);
	}
	
	@Before
	public void setUp() {
		
		this.setup = new NLPediaSetup(true);
		this.logger = new NLPediaLogger(ContextTest.class);
	}

	@After
	public void cleanUpStreams() {
	    
		this.setup.destroy();
	}
	
	@Test
	public void testFastLeftContext1() throws Exception {
	
		String sentence		= "The Corinthian is a fictional character in Neil Gaiman 's comic book series `` The Sandman '' .";
		String nerTagged	= "The_OTHER Corinthian_MISC is_OTHER a_OTHER fictional_OTHER character_OTHER in_OTHER Neil_PERSON Gaiman_PERSON 's_OTHER comic_OTHER book_OTHER series_OTHER ``_OTHER The_OTHER Sandman_MISC ''_OTHER ._OTHER";
		String pattern		= "'s comic book series ``";
		
		LeftContext flc = new LeftContext(nerTagged, sentence, pattern);
		
		assertEquals(13, flc.getCleanWords().size());
		assertEquals("series", flc.getCleanWords().get(flc.getCleanWords().size() -1 ));
		assertEquals("series_OTHER", flc.getTaggedWords().get(flc.getTaggedWords().size() -1 ));
		assertEquals("Neil Gaiman", flc.getSuitableEntity("http://dbpedia.org/ontology/Person"));
		assertEquals(0, flc.getSuitableEntityDistance("http://dbpedia.org/ontology/Person"));
		
		RightContext frc = new RightContext(nerTagged, sentence, pattern);
		
		assertEquals(8, frc.getCleanWords().size());
		assertEquals("comic", frc.getCleanWords().get(0));
		assertEquals("comic_OTHER", frc.getTaggedWords().get(0));
		assertEquals("Sandman", frc.getSuitableEntity("http://dbpedia.org/ontology/Work"));
		assertEquals(1, frc.getSuitableEntityDistance("http://dbpedia.org/ontology/Work"));
	}
	
	@Test
	public void testFastLeftContext2() throws Exception {
	
		String sentence		= "Uprock was created in Brooklyn , N.Y. and breaking was created in the Bronx .";
		String nerTagged	= "Uprock_OTHER was_OTHER created_OTHER in_OTHER Brooklyn_PLACE ,_OTHER N.Y._PLACE and_OTHER breaking_OTHER was_OTHER created_OTHER in_OTHER the_PLACE Bronx_PLACE ._OTHER";
		String pattern		= "created in the";
		
		LeftContext flc = new LeftContext(nerTagged, sentence, pattern);
		
		assertEquals(12, flc.getCleanWords().size());
		assertEquals("in", flc.getCleanWords().get(flc.getCleanWords().size() -1 ));
		assertEquals("in_OTHER", flc.getTaggedWords().get(flc.getTaggedWords().size() -1 ));
		assertEquals("N.Y.", flc.getSuitableEntity("http://dbpedia.org/ontology/Place"));
		assertEquals(3, flc.getSuitableEntityDistance("http://dbpedia.org/ontology/Place"));
		
		RightContext frc = new RightContext(nerTagged, sentence, pattern);
		
		assertEquals(4, frc.getCleanWords().size());
		assertEquals("in", frc.getCleanWords().get(0) );
		assertEquals("in_OTHER", frc.getTaggedWords().get(0));
		assertEquals("the Bronx", frc.getSuitableEntity("http://dbpedia.org/ontology/Place"));
		assertEquals(0, frc.getSuitableEntityDistance("http://dbpedia.org/ontology/Place"));
	}
	
	@Test
	public void testFastLeftContext3() throws Exception {
	
		String sentence		= "Uprock was created in Brooklyn .";
		String nerTagged	= "Uprock_MISC was_OTHER created_OTHER in_OTHER Brooklyn_PLACE ._OTHER";
		String pattern		= "was created in";
		
		LeftContext flc = new LeftContext(nerTagged, sentence, pattern);
		
		assertEquals(3, flc.getCleanWords().size());
		assertEquals("created", flc.getCleanWords().get(flc.getCleanWords().size() -1 ));
		assertEquals("created_OTHER", flc.getTaggedWords().get(flc.getTaggedWords().size() -1 ));
		assertEquals("Uprock", flc.getSuitableEntity("http://dbpedia.org/ontology/MusicalWork"));
		assertEquals(0, flc.getSuitableEntityDistance("http://dbpedia.org/ontology/MusicalWork"));
		
		RightContext frc = new RightContext(nerTagged, sentence, pattern);
		
		assertEquals(4, frc.getCleanWords().size());
		assertEquals("created", frc.getCleanWords().get(0) );
		assertEquals("created_OTHER", frc.getTaggedWords().get(0));
		assertEquals("Brooklyn", frc.getSuitableEntity("http://dbpedia.org/ontology/Place"));
		assertEquals(0, frc.getSuitableEntityDistance("http://dbpedia.org/ontology/Place"));
	}
	
	@Test
	public void testFastLeftContext4() throws Exception {
	
		String sentence		= "Has a of Josephine of Daughter , who was born in Germany .";
		String nerTagged	= "Has_OTHER a_OTHER of_OTHER Josephine_PERSON of_OTHER Daughter_PERSON ,_OTHER who_OTHER was_OTHER born_OTHER in_OTHER Germany_PLACE ._OTHER";
		String pattern		= ", who was born in";
		
		LeftContext flc = new LeftContext(nerTagged, sentence, pattern);
		
		assertEquals(10, flc.getCleanWords().size());
		assertEquals("born", flc.getCleanWords().get(flc.getCleanWords().size() -1 ));
		assertEquals("born_OTHER", flc.getTaggedWords().get(flc.getTaggedWords().size() -1 ));
		assertEquals("Daughter", flc.getSuitableEntity("http://dbpedia.org/ontology/Person"));
		assertEquals(0, flc.getSuitableEntityDistance("http://dbpedia.org/ontology/Person"));
		
		RightContext frc = new RightContext(nerTagged, sentence, pattern);
		
		assertEquals(6, frc.getCleanWords().size());
		assertEquals("who", frc.getCleanWords().get(0) );
		assertEquals("who_OTHER", frc.getTaggedWords().get(0));
		assertEquals("Germany", frc.getSuitableEntity("http://dbpedia.org/ontology/Country"));
		assertEquals(0, frc.getSuitableEntityDistance("http://dbpedia.org/ontology/Country"));
	}
	
	@Test
	public void testFastLeftContext5() throws Exception {
	
		String sentence		= "The Chernaya River is on the outskirts of Sevastopol .";
		String nerTagged	= "The_OTHER Chernaya_PLACE River_PLACE is_OTHER on_OTHER the_OTHER outskirts_OTHER of_OTHER Sevastopol_PLACE ._OTHER";
		String pattern		= "is on the outskirts of";
		
		LeftContext flc = new LeftContext(nerTagged, sentence, pattern);
		
		assertEquals(7, flc.getCleanWords().size());
		assertEquals("outskirts", flc.getCleanWords().get(flc.getCleanWords().size() -1 ));
		assertEquals("outskirts_OTHER", flc.getTaggedWords().get(flc.getTaggedWords().size() -1 ));
		assertEquals("Chernaya River", flc.getSuitableEntity("http://dbpedia.org/ontology/Place"));
		assertEquals(0, flc.getSuitableEntityDistance("http://dbpedia.org/ontology/Place"));
		
		RightContext frc = new RightContext(nerTagged, sentence, pattern);
		
		assertEquals(6, frc.getCleanWords().size());
		assertEquals("on", frc.getCleanWords().get(0) );
		assertEquals("on_OTHER", frc.getTaggedWords().get(0));
		assertEquals("Sevastopol", frc.getSuitableEntity("http://dbpedia.org/ontology/Place"));
		assertEquals(0, frc.getSuitableEntityDistance("http://dbpedia.org/ontology/Place"));
	}
	
	@Test
	public void testFastLeftContext6() throws Exception {
	
		String sentence		= "At stake were the 450 seats in the State Duma -LRB- Gosudarstvennaya Duma -RRB- , the lower house of the Federal Assembly of Russia -LRB- The legislature -RRB- .";
		String nerTagged	= "At_OTHER stake_OTHER were_OTHER the_OTHER 450_OTHER seats_OTHER in_OTHER the_OTHER State_ORGANIZATION Duma_ORGANIZATION -LRB-_OTHER Gosudarstvennaya_PERSON Duma_PERSON -RRB-_OTHER ,_OTHER the_OTHER lower_OTHER house_OTHER of_OTHER the_OTHER Federal_ORGANIZATION Assembly_ORGANIZATION of_OTHER Russia_PLACE -LRB-_OTHER The_OTHER legislature_OTHER -RRB-_OTHER ._OTHER";
		String pattern		= "-LRB- Gosudarstvennaya Duma -RRB- , the lower house of the";
		
		LeftContext flc = new LeftContext(nerTagged, sentence, pattern);
		
		assertEquals(19, flc.getCleanWords().size());
		assertEquals("of", flc.getCleanWords().get(flc.getCleanWords().size() -1 ));
		assertEquals("of_OTHER", flc.getTaggedWords().get(flc.getTaggedWords().size() -1 ));
		assertEquals("State Duma", flc.getSuitableEntity("http://dbpedia.org/ontology/Organisation"));
		assertEquals(0, flc.getSuitableEntityDistance("http://dbpedia.org/ontology/Organisation"));
		
		RightContext frc = new RightContext(nerTagged, sentence, pattern);
		
		assertEquals(18, frc.getCleanWords().size());
		assertEquals("Gosudarstvennaya", frc.getCleanWords().get(0) );
		assertEquals("Gosudarstvennaya_PERSON", frc.getTaggedWords().get(0));
		assertEquals("Federal Assembly", frc.getSuitableEntity("http://dbpedia.org/ontology/Organisation"));
		assertEquals(0, frc.getSuitableEntityDistance("http://dbpedia.org/ontology/Organisation"));
	}
	
	@Test
	public void testFastLeftContext7() throws Exception { 
	
		String sentence		= "Vygotsky was born in Orsha , in the Russian Empire -LRB- today in Belarus -RRB- into a nonreligious Jewish family .";
		String nerTagged	= "Vygotsky_PERSON was_OTHER born_OTHER in_OTHER Orsha_PLACE ,_OTHER in_OTHER the_OTHER Russian_MISC Empire_MISC -LRB-_OTHER today_OTHER in_OTHER Belarus_PLACE -RRB-_OTHER into_OTHER a_OTHER nonreligious_OTHER Jewish_MISC family_OTHER ._OTHER";
		String pattern		= "in the Russian Empire -LRB- today in";
		
		LeftContext flc = new LeftContext(nerTagged, sentence, pattern);
		
		assertEquals(12, flc.getCleanWords().size());
		assertEquals("today", flc.getCleanWords().get(flc.getCleanWords().size() -1 ));
		assertEquals("today_OTHER", flc.getTaggedWords().get(flc.getTaggedWords().size() -1 ));
		assertEquals("Orsha", flc.getSuitableEntity("http://dbpedia.org/ontology/Place"));
		assertEquals(1, flc.getSuitableEntityDistance("http://dbpedia.org/ontology/Place"));
		
		RightContext frc = new RightContext(nerTagged, sentence, pattern);
		
		assertEquals(14, frc.getCleanWords().size());
		assertEquals("the", frc.getCleanWords().get(0) );
		assertEquals("the_OTHER", frc.getTaggedWords().get(0));
		assertEquals("Belarus", frc.getSuitableEntity("http://dbpedia.org/ontology/Place"));
		assertEquals(0, frc.getSuitableEntityDistance("http://dbpedia.org/ontology/Place"));
	}
	
	@Test
    public void testFastLeftContext8() throws Exception { 
    
        String sentence     = "The island was discovered from the air by Rear Admiral Byrd on February 27 , 1940 , who named it for W. Harris Thurston , New York textile manufacturer , designer of the windproof `` Byrd Cloth '' and sponsor of Antarctic expeditions .";
        String nerTagged    = "The_OTHER island_OTHER was_OTHER discovered_OTHER from_OTHER the_OTHER air_OTHER by_OTHER Rear_ORGANIZATION Admiral_ORGANIZATION Byrd_ORGANIZATION on_OTHER February_OTHER 27_OTHER ,_OTHER 1940_OTHER ,_OTHER who_OTHER named_OTHER it_OTHER for_OTHER W._PERSONSON Harris_PERSONSON Thurston_PERSONSON ,_OTHER New_PLACE York_PLACE textile_OTHER manufacturer_OTHER ,_OTHER designer_OTHER of_OTHER the_OTHER windproof_OTHER ``_OTHER Byrd_PERSONSON Cloth_PERSONSON ''_OTHER and_OTHER sponsor_OTHER of_OTHER Antarctic_MISC expeditions_OTHER ._OTHER";
        String pattern      = ", designer of the";
        
        LeftContext flc = new LeftContext(nerTagged, sentence, pattern);
        
        assertEquals("today", flc.getCleanWords().get(flc.getCleanWords().size() -1 ));
        assertEquals("today_OTHER", flc.getTaggedWords().get(flc.getTaggedWords().size() -1 ));
        assertEquals("Orsha", flc.getSuitableEntity("http://dbpedia.org/ontology/Place"));
        assertEquals(1, flc.getSuitableEntityDistance("http://dbpedia.org/ontology/Place"));
        
        RightContext frc = new RightContext(nerTagged, sentence, pattern);
        
        assertEquals(14, frc.getCleanWords().size());
        assertEquals("the", frc.getCleanWords().get(0) );
        assertEquals("the_OTHER", frc.getTaggedWords().get(0));
        assertEquals("Belarus", frc.getSuitableEntity("http://dbpedia.org/ontology/Place"));
        assertEquals(0, frc.getSuitableEntityDistance("http://dbpedia.org/ontology/Place"));
    }
	
	@Test
	public void testTimeForContextCreation() throws Exception {
		
		long total = 0;
		long iterations = 100000;
		long max = 0;
		long min = 1000000;
		
		for ( int i = 0 ; i < iterations ; i++ ) {

			long start = System.nanoTime();
			
			String sentence		= "Vygotsky was born in Orsha , in the Russian Empire -LRB- today in Belarus -RRB- into a nonreligious Jewish family .";
			String nerTagged	= "Vygotsky_PERSON was_OTHER born_OTHER in_OTHER Orsha_PLACE ,_OTHER in_OTHER the_OTHER Russian_MISC Empire_MISC -LRB-_OTHER today_OTHER in_OTHER Belarus_PLACE -RRB-_OTHER into_OTHER a_OTHER nonreligious_OTHER Jewish_MISC family_OTHER ._OTHER";
			String pattern		= "in the Russian Empire -LRB- today in";
			
			LeftContext flc = new LeftContext(nerTagged, sentence, pattern);
			
			assertEquals(12, flc.getCleanWords().size());
			assertEquals("today", flc.getCleanWords().get(flc.getCleanWords().size() -1 ));
			assertEquals("today_OTHER", flc.getTaggedWords().get(flc.getTaggedWords().size() -1 ));
			assertEquals("Orsha", flc.getSuitableEntity("http://dbpedia.org/ontology/Place"));
			assertEquals(1, flc.getSuitableEntityDistance("http://dbpedia.org/ontology/Place"));
			
			RightContext frc = new RightContext(nerTagged, sentence, pattern);
			
			assertEquals(14, frc.getCleanWords().size());
			assertEquals("the", frc.getCleanWords().get(0) );
			assertEquals("the_OTHER", frc.getTaggedWords().get(0));
			assertEquals("Belarus", frc.getSuitableEntity("http://dbpedia.org/ontology/Place"));
			assertEquals(0, frc.getSuitableEntityDistance("http://dbpedia.org/ontology/Place"));
			
			long end = System.nanoTime() - start;
			max = Math.max(max, end);
			min = Math.min(min, end);
			total += end;
		}
		
		System.out.println((double) total / (double) 1000000000);
		System.out.println((double) max / (double) 1000000000);
		System.out.println((double) min / (double) 1000000000);
		System.out.println(((double) total / (double) iterations) / 1000000000);
	}
	
	@Test
    public void testContext() {
        
        String testAnnotated            = "Has_OTHER a_OTHER of_OTHER Josephine_PERSON of_OTHER Daughter_PERSON ,_OTHER who_OTHER was_OTHER born_OTHER in_OTHER Germany_PLACE ._OTHER";
        String test                     = "Has a of Josephine of Daughter , who was born in Germany .";
        String patternWithOutVariables1 = ", who was born in";
        
//      Context leftContext1 = new LeftContext(testAnnotated, test, patternWithOutVariables1);
//      Context rightContext1 = new RightContext(testAnnotated, test, patternWithOutVariables1);
//      
//      assertEquals("Daughter", leftContext1.getSuitableEntity("http://dbpedia.org/ontology/Person"));
//      assertTrue("Germany", rightContext1.getSuitableEntity("http://dbpedia.org/ontology/Country"));
        
        // ######################################################################
        
        patternWithOutVariables1    = "is on the outskirts of";
        testAnnotated               = "The_OTHER Chernaya_PLACE River_PLACE is_OTHER on_OTHER the_OTHER outskirts_OTHER of_OTHER Sevastopol_PLACE ._OTHER";
        test                        = "The Chernaya River is on the outskirts of Sevastopol ._OTHER";
        
        Context leftContext2 = new LeftContext(testAnnotated,test, patternWithOutVariables1);
        Context rightContext2 = new RightContext(testAnnotated,test, patternWithOutVariables1);
        
        assertEquals("Chernaya River", leftContext2.getSuitableEntity("http://dbpedia.org/ontology/Place"));
        assertEquals("Sevastopol", rightContext2.getSuitableEntity("http://dbpedia.org/ontology/Place"));
        
        // ######################################################################
        
        testAnnotated               = "At_OTHER stake_OTHER were_OTHER the_OTHER 450_OTHER seats_OTHER in_OTHER the_OTHER State_ORGANIZATION Duma_ORGANIZATION -LRB-_OTHER Gosudarstvennaya_PERSON Duma_PERSON -RRB-_OTHER ,_OTHER the_OTHER lower_OTHER house_OTHER of_OTHER the_OTHER Federal_ORGANIZATION Assembly_ORGANIZATION of_OTHER Russia_PLACE -LRB-_OTHER The_OTHER legislature_OTHER -RRB-_OTHER ._OTHER"; 
        test                        = "At stake were the 450 seats in the State Duma -LRB- Gosudarstvennaya Duma -RRB- , the lower house of the Federal Assembly of Russia -LRB- The legislature -RRB- .";
        patternWithOutVariables1    = "-LRB- Gosudarstvennaya Duma -RRB- , the lower house of the";
        
        Context leftContext3 = new LeftContext(testAnnotated,test, patternWithOutVariables1);
        Context rightContext3 = new RightContext(testAnnotated,test, patternWithOutVariables1);
        
        assertEquals("State Duma", leftContext3.getSuitableEntity("http://dbpedia.org/ontology/Legislature"));
        assertEquals("Federal Assembly", rightContext3.getSuitableEntity("http://dbpedia.org/ontology/Legislature"));
        
        // ######################################################################

        testAnnotated               = "In_OTHER 1989_OTHER ,_OTHER a_OTHER new_OTHER subsidiary_OTHER American_ORGANIZATION Drug_ORGANIZATION Stores_ORGANIZATION ,_ORGANIZATION Inc._ORGANIZATION was_OTHER formed_OTHER and_OTHER consisted_OTHER of_OTHER American_MISC Stores_MISC drugstore_OTHER holdings_OTHER of_OTHER Osco_ORGANIZATION Drug_ORGANIZATION ,_OTHER Sav-on_PERSON Drugs_PERSON ,_OTHER the_OTHER Osco_PLACE side_OTHER of_OTHER the_OTHER Jewel_MISC Osco_MISC food-drug_OTHER combination_OTHER stores_OTHER and_OTHER RxAmerica_ORGANIZATION ._OTHER"; 
        test                        = "In 1989 , a new subsidiary American Drug Stores , Inc. was formed and consisted of American Stores drugstore holdings of Osco Drug , Sav-on Drugs , the Osco side of the Jewel Osco food-drug combination stores and RxAmerica .";
        patternWithOutVariables1    = "drugstore holdings of";
        
        Context leftContext5 = new LeftContext(testAnnotated,test, patternWithOutVariables1);
        Context rightContext5 = new RightContext(testAnnotated,test, patternWithOutVariables1);
        
        assertEquals("American Drug Stores , Inc.", leftContext5.getSuitableEntity("http://dbpedia.org/ontology/Legislature"));
        assertEquals("Osco Drug", rightContext5.getSuitableEntity("http://dbpedia.org/ontology/Legislature"));

        // ######################################################################

        testAnnotated               = "Dale_PERSON Steyn_PERSON ,_OTHER the_OTHER South_MISC African_MISC right-arm_OTHER fast_OTHER bowler_OTHER ,_OTHER was_OTHER optimistic_OTHER about_OTHER South_PLACE Africa_PLACE 's_OTHER chances_OTHER of_OTHER winning_OTHER the_OTHER series_OTHER ._OTHER";
        test                        = "Dale Steyn , the South African right-arm fast bowler , was optimistic about South Africa 's chances of winning the series .";
        patternWithOutVariables1    = "fast bowler";
        
        Context leftContext6 = new LeftContext(testAnnotated,test, patternWithOutVariables1);
        Context rightContext6 = new RightContext(testAnnotated,test, patternWithOutVariables1);
        
        assertEquals("Dale Steyn", leftContext6.getSuitableEntity("http://dbpedia.org/ontology/Person"));
        assertEquals("South Africa", rightContext6.getSuitableEntity("http://dbpedia.org/ontology/Place"));
        
        // ######################################################################

        testAnnotated               = "Vygotsky_PERSON was_OTHER born_OTHER in_OTHER Orsha_PLACE ,_OTHER in_OTHER the_OTHER Russian_MISC Empire_MISC -LRB-_OTHER today_OTHER in_OTHER Belarus_PLACE -RRB-_OTHER into_OTHER a_OTHER nonreligious_OTHER Jewish_MISC family_OTHER ._OTHER";
        test                        = "Vygotsky was born in Orsha , in the Russian Empire -LRB- today in Belarus -RRB- into a nonreligious Jewish family .";
        patternWithOutVariables1    = "in the Russian Empire -LRB- today in";
        
        Context leftContext7 = new LeftContext(testAnnotated,test, patternWithOutVariables1);
        Context rightContext7 = new RightContext(testAnnotated,test, patternWithOutVariables1);
        
        assertEquals("Orsha", leftContext7.getSuitableEntity("http://dbpedia.org/ontology/PopulatedPlace"));
        assertEquals("Belarus", rightContext7.getSuitableEntity("http://dbpedia.org/ontology/City"));

        // ######################################################################
        
        testAnnotated               = "Uprock_OTHER was_OTHER created_OTHER in_OTHER Brooklyn_PLACE ,_OTHER N.Y._PLACE and_OTHER breaking_OTHER was_OTHER created_OTHER in_OTHER the_PLACE Bronx_PLACE ._OTHER";
        test                        = "Uprock was created in Brooklyn , N.Y. and breaking was created in the Bronx .";
        patternWithOutVariables1    = "created in the";
        
        Context leftContext8 = new LeftContext(testAnnotated,test, patternWithOutVariables1);
        Context rightContext8 = new RightContext(testAnnotated,test, patternWithOutVariables1);
        
        assertEquals("N.Y.", leftContext8.getSuitableEntity("http://dbpedia.org/ontology/Place"));
        assertEquals("the Bronx", rightContext8.getSuitableEntity("http://dbpedia.org/ontology/Place"));

        // ######################################################################

        test = "The Corinthian is a fictional character in Neil Gaiman 's comic book series `` The Sandman '' .";
        testAnnotated = "The_OTHER Corinthian_MISC is_OTHER a_OTHER fictional_OTHER character_OTHER in_OTHER Neil_PERSON Gaiman_PERSON 's_OTHER comic_OTHER book_OTHER series_OTHER ``_OTHER The_OTHER Sandman_MISC ''_OTHER ._OTHER";
        patternWithOutVariables1 = "'s comic book series ``";
        
        Context leftContext9 = new LeftContext(testAnnotated,test, patternWithOutVariables1);
        Context rightContext9 = new RightContext(testAnnotated,test, patternWithOutVariables1);
        
        assertEquals("Neil Gaiman", leftContext9.getSuitableEntity("http://dbpedia.org/ontology/Person"));
        assertEquals("Sandman", rightContext9.getSuitableEntity("http://dbpedia.org/ontology/Work"));
    }
}
