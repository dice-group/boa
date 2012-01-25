package de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.josatagger;

import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.NaturalLanguageProcessingTool;

/**
 * This class separates JOSA from its body.
 * 
 * @author cdh4696
 *
 */
public interface JosaTagger extends NaturalLanguageProcessingTool {
	/**
	 * This class gets a Korean sentence as input, and returns the JOSA-separated Korean sentence as its result.
	 *  
	 * @param origSen The original sentence
	 * @return
	 */
	public String getJosaSeparatedSentence(String origSen);
	
}
