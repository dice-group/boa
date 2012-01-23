package de.uni_leipzig.simba.boa.backend.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.nlp.LanguageDetection;


public class LanguageDetectionTest {

	// initialize logging and settings
	NLPediaSetup setup = null;
	NLPediaLogger logger = null;
	
	public static junit.framework.Test suite() {
		
		return new JUnit4TestAdapter(LanguageDetectionTest.class);
	}
	
	@Before
	public void setUp() {
		
		this.setup = new NLPediaSetup(true);
		this.logger = new NLPediaLogger(LanguageDetectionTest.class);
	}

	@After
	public void cleanUpStreams() {
	    
		this.setup.destroy();
	}
	
	@Test
	public void testLanguageDetection() {
		
		List<String> sentences =  Arrays.asList(
				"The start of the war is generally held to be 1 September 1939, beginning with the German invasion of Poland; Britain and France declared war on Germany two days later. Other dates for the beginning of war include the",
				"Vor, während und nach dem Krieg wurden Grenzen in Mittel- und Osteuropa, im Nahen Osten und in anderen Regionen teils mehrfach neu gezogen.",
				"La somme des dégâts matériels n’a jamais pu être chiffrée de façon sûre, mais il est certain qu’elle dépasse les destructions cumulées de l’ensemble des conflits connus par le genre humain depuis son apparition. Le traumatisme moral ne fut pas moins considérable, la violence ayant pris des proportions inédites.",
				"De oorlog kenmerkte zich door een tot op dat moment ongekende bruutheid. In vorige oorlogen was over het algemeen een principieel onderscheid gemaakt tussen burgers en militairen, waarbij de burgers zoveel mogelijk ontzien werden, of in ieder geval geen primair doel vormden.",
				"戰爭高潮嗰時間，有六十一多隻國家同到一齊打，有十七多億人拕捲進去。打仗雙方係以美國、蘇聯、中華民國、英國、法國等國組成嗰反法西斯同盟，同到以德國、日本、意大利等法西斯國家組成嘅軸心國集團。戰禍遍及歐洲、亞洲、美洲、非洲同到大洋洲五大洲；打仗雙方同時到大西洋、太平洋、印度洋同北冰洋四大洋展開戰鬥。");
		
		assertEquals(Constants.ENGLISH_LANGUAGE, LanguageDetection.getLanguageOfSentence(sentences.get(0)));
		assertEquals(Constants.GERMAN_LANGUAGE, LanguageDetection.getLanguageOfSentence(sentences.get(1)));
		assertEquals(Constants.FRENCH_LANGUAGE, LanguageDetection.getLanguageOfSentence(sentences.get(2)));
//		assertEquals(Constants.NOT_SUPPORTED_LANGUAGE, LanguageDetection.getLanguageOfSentence(sentences.get(3)));
//		assertEquals(Constants.MANDARIN_CHINESE_LANGUAGE, LanguageDetection.getLanguageOfSentence(sentences.get(4)));
	}
}
