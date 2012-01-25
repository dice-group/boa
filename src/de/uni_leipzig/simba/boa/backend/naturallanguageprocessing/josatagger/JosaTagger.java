package de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.josatagger;

/**
 * This class separates JOSA from its body.
 * 
 * @author cdh4696
 *
 */
public interface JosaTagger {
	/**
	 * This class gets a Korean sentence as input, and returns the JOSA-separated Korean sentence as its result.
	 *  
	 * @param origSen The original sentence
	 * @return
	 */
	public String getJosaSeparatedSentence(String origSen);
	
}
