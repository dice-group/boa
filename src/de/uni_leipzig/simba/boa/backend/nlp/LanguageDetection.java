package de.uni_leipzig.simba.boa.backend.nlp;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;

import java.util.List;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;

import net.olivo.lc4j.LanguageCategorization;

/**
 * This class uses the approach presented in:
 * 
 *  Cavnar, W. B. and J. M. Trenkle, "N-Gram-Based Text Categorization"
 *	In Proceedings of Third Annual Symposium on Document Analysis and
 *	Information Retrieval, Las Vegas, NV, UNLV Publications/Reprographics,
 *	pp. 161-175, 11-13 April 1994.
 *
 * implemented in the framework lc4j.
 *  
 * @author Daniel Gerber
 */
public class LanguageDetection {

	private static final LanguageCategorization languageCategorization = new LanguageCategorization();
	
	static {
		
		languageCategorization.setLanguageModelsDir(NLPediaSettings.getInstance().getSetting("languageModelsForLanguageDetection"));
		languageCategorization.setUnknownThreshold(1.01f);
	}
	
	/** 
	 * @param sentence - the sentence to analyse for language
	 * @return the most probable language the sentence is written in
	 */
	public static String getLanguageOfSentence(String sentence) {
		
		List<String> languages = languageCategorization.findLanguage(new ByteArrayList(sentence.getBytes()));

		if ( languages.size() > 0 ) {
			
			String mostProbableLanguage = languages.get(0);
			
			if ( mostProbableLanguage.equals("english-utf.lm") 
					|| mostProbableLanguage.equals("english.lm") ) {
				
				return Constants.ENGLISH_LANGUAGE;
			}
			
			if ( mostProbableLanguage.equals("chinese-big5.lm") 
					|| mostProbableLanguage.equals("chinese-gb2312.lm")
					|| mostProbableLanguage.equals("chinese-utf.lm") ) {
				
				return Constants.MANDARIN_CHINESE_LANGUAGE;
			}
			
			if ( mostProbableLanguage.equals("spanish-utf.lm") 
					|| mostProbableLanguage.equals("spanish.lm") ) {
				
				return Constants.SPANISH_LANGUAGE;
			}
			
			if ( mostProbableLanguage.equals("japanese-euc_jp.lm") 
					|| mostProbableLanguage.equals("japanese-shift_jis.lm")
					|| mostProbableLanguage.equals("japanese-utf.lm") ) {
				
				return Constants.JAPANESE_LANGUAGE;
			}
			
			if ( mostProbableLanguage.equals("portuguese.lm")
					|| mostProbableLanguage.equals("portuguese-utf.lm") ) {
				
				return Constants.PORTUGESE_LANGUAGE;
			}
			
			if ( mostProbableLanguage.equals("german-utf.lm") 
					|| mostProbableLanguage.equals("german.lm") ) {
				
				return Constants.GERMAN_LANGUAGE;
			}
			
			if ( mostProbableLanguage.equals("arabic-iso8859_6.lm") 
					|| mostProbableLanguage.equals("arabic-utf.lm")
					|| mostProbableLanguage.equals("arabic-windows1256.lm") ) {
				
				return Constants.ARABIC_LANGUAGE;
			}
			
			if ( mostProbableLanguage.equals("french-utf.lm") 
					|| mostProbableLanguage.equals("french.lm") ) {
				
				return Constants.FRENCH_LANGUAGE;
			}
			
			if ( mostProbableLanguage.equals("russian-utf.lm")
					|| mostProbableLanguage.equals("russian-windows1251.lm")
					|| mostProbableLanguage.equals("russian-koi8_r.lm")
					|| mostProbableLanguage.equals("russian-iso8859_5.lm") ) {
				
				return Constants.RUSSIAN_LANGUAGE;
			}
			
			if ( mostProbableLanguage.equals("korean.lm")
					|| mostProbableLanguage.equals("korean-utf.lm") ) {
				
				return Constants.KOREAN_LANGUAGE;
			}
		}
		return Constants.NOT_SUPPORTED_LANGUAGE;
	}
}
