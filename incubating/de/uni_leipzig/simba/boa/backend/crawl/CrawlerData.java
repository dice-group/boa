package de.uni_leipzig.simba.boa.backend.crawl;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Daniel Gerber
 */
public class CrawlerData {

	private List<String> sentences;
	
	/**
	 * 
	 */
	public CrawlerData() {
		
		this.sentences = new ArrayList<String>();
	}
	
	/**
	 * 
	 * @param sentence
	 */
	public void addSentence(String sentence) {
		
		this.sentences.add(sentence);
	}

	/**
	 * @return the sentences
	 */
	public List<String> getSentences() {
	
		return sentences;
	}

	/**
	 * @param sentences the sentences to set
	 */
	public void setSentences(List<String> sentences) {
	
		this.sentences = sentences;
	}
}
