package de.uni_leipzig.simba.boa.backend.test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.nlp.SentenceDetection;
import de.uni_leipzig.simba.boa.backend.nlp.SentenceFilter;

/**
 * 
 * @author Daniel Gerber
 *
 */
public class SentenceFilterTest {

	private NLPediaLogger logger = null;
	private NLPediaSetup setup = null;
	
	
	public static junit.framework.Test suite() {
		
		return new JUnit4TestAdapter(SentenceFilterTest.class);
	}

	@Before
	public void setUpStreams() {
		
		this.setup = new NLPediaSetup(true);
		this.logger = new NLPediaLogger(SentenceFilterTest.class);
	}

	@After
	public void cleanUpStreams() {
	    
		this.setup.destroy();
	}
	
	@Test
	public void splitSentencesTest(){
		
		String test = "This is a test!Let's see if this helps anything.";
		String regex = "\\p{Ll}\\p{P}\\p{Lu}";
		
//		Pattern pattern = Pattern.compile(regex);
//		Matcher matcher = pattern.matcher(test);
//		
//		String match = "";
//		while ( matcher.find() ) {
//			
//			match += matcher.group();
//		}
//		int i = test.indexOf(match);
//		System.out.println(match +" " + i + " " + test.charAt(i+1));
//		
//		assertTrue(matcher.find());
		
		SentenceDetection detector = new SentenceDetection();
		
		List<String> sentences = detector.getSentences(test, Constants.SENTENCE_BOUNDARY_DISAMBIGUATION_OPEN_NLP);
		assertEquals(2, sentences.size());
	}
	
	@Test
	public void testSentenceFiltering() {
		
		String[] sentences	= new String[]{
			"This is a correct sentence.",
			"this is a sentence with lowercase beginning.",
			". this is a wrong sentence.",
			"+ this is a utf8 math sign.",
			"( this is a sentence with beginning with a braket.",
			"_ this is a sentence with beginning with a braket.",
			", this is a sentence with beginning with a braket.",
			"? this is a sentence with beginning with a braket.",
			", this is a sentence with beginning with a braket.",
			") this is a sentence with beginning with a braket.",
			"[ this is a sentence with beginning with a braket.",
			"] This is a sentence with beginning with a braket.",
			"This is a sentence with beginning with a braket",
			"This is a s e n t e n c e with beginning with a braket.",
			"Th,is ,i,s a ,sent,en,ce wi,t,h ,b,egin,n,i,ng with a braket.",
			"This  is  a  sentence  with beginning with a braket.",
			"Th.is i.s a s.e.ntenc.e .with. .begi.nn.in.g. wi.th a braket.",
			"This is a sentence with beginning with a braket This is a sentence with beginning with a braket This is a sentence with beginning with a braket This is a sentence with beginning with a braket This is a sentence with beginning with a braket This is a sentence with beginning with a braket This is a sentence with beginning with a braket This is a sentence with beginning with a braket This is a sentence with beginning with a braket This is a sentence with beginning with a braket This is a sentence with beginning with a braket This is a sentence with beginning with a braket This is a sentence with beginning with a braket This is a sentence with beginning with a braket.",
			"This is.",
			"Th is is a se n te n ce wi th b eg in n in g wi th a bra k et.",
			"This is a sentence with << beginning with a braket.",
			"This is a >> sentence with beginning with a braket.",
			"This is a sentence | with beginning with a braket.",
			"This is a sen te nce with beg in ni ng with a bra ket This is a sen tence with beg in ni ng with a bra ket This is a sen ten ce with begi nning with a brak et Thi s is a se nt en ce with beg inn in g with a br ak et.",
			"This is a sentence with [Êbeginning with a braket.",
			"This is a sentence with ] beginning with a braket.",
			"Th/is i/s a s/ente/nce w/ith b/egi/nni/ng wi/th a br/aket.",
			"This i:s a sente:nce wit:h begin:ning wit:h a bra:ket.",
			"This i(s a sente(nce wit(h begin(ning wit(h a bra(ket.",
			"This i)s a sente)nce wit)h begin)ning wit)h a bra)ket.",
			"This i:s a sente:nce wit:h begin:ning wit:h a bra:ket.",
			"This is a sentence wket. . .",
			"This is a sentence ... with beginning",
			"This is a correct !! sentence.",
			"This is a correct ?? sentence.",
			"This is a correct ?! sentence.",
			"This is a correct Sentence Sentence Sentence Sentence Sentence Sentence Sentence Sentence Sentence Sentence Sentence.",
			"Sentence",
			"This is a http correct http sentence.",
			"This is a www. correct www. sentence.",
			"In 1770, he anonymously released Annette, his first collection of poems."
		};
		
		SentenceFilter sf = new SentenceFilter();
		List<String> realWorldSentences	= sf.filterSentences(Arrays.asList(sentences));
		assertEquals("Result", 6, realWorldSentences.size());
	}
	
	@Test
	public void replaceMultipleWhiteSpaceCharacters(){
		
		String test = "Asd asdj				asdasd";
		String regex = "\\s{2,}";
		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(test);
		
		assertTrue("Asd asdj asdasd".equals(test.replaceAll(regex, " ")));
		assertTrue(matcher.find());
	}
}
