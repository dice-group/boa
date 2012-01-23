/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.partofspeechtagger;

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
}
