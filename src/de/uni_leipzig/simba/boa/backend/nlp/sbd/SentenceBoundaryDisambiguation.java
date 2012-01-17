package de.uni_leipzig.simba.boa.backend.nlp.sbd;

import java.util.List;


public interface SentenceBoundaryDisambiguation {

	/**
	 * This method takes a text in a given language and splits
	 * it into sentences.
	 * 
	 * @param text - the text which should be split
	 * @return a list of sentences
	 */
	public List<String> splitTextIntoSentences(String text);
}
