package de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.sentenceboundarydisambiguation;

import java.util.List;

import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.NaturalLanguageProcessingTool;


public interface SentenceBoundaryDisambiguation extends NaturalLanguageProcessingTool {

	/**
	 * This method takes a text in a given language and splits
	 * it into sentences.
	 * 
	 * @param text - the text which should be split
	 * @return a list of sentences
	 */
	public List<String> getSentences(String text);
}
