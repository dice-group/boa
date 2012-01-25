/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.namedentityrecognition;

import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.NaturalLanguageProcessingTool;


/**
 * @author gerb
 *
 */
public interface NamedEntityRecognition extends NaturalLanguageProcessingTool {

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
