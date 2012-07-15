/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.partofspeechtagger;

import java.util.List;

import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.NaturalLanguageProcessingTool;


/**
 * @author gerb
 *
 */
public interface PartOfSpeechTagger extends NaturalLanguageProcessingTool {

	/**
	 * 
	 * @param string
	 * @return
	 */
	public String getAnnotatedString(String string);
	
	/**
	 * 
	 * @param string
	 * @return
	 */
	public String getAnnotations(String string);
	
	/**
	 * 
	 * @param sentence
	 * @return
	 */
	public List<String> getNounPhrases(String sentence);
}
