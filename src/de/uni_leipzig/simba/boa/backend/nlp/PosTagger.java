package de.uni_leipzig.simba.boa.backend.nlp;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * 
 * @author Daniel Gerber
 */
public class PosTagger {

	private MaxentTagger tagger;
	
	private final NLPediaLogger logger = new NLPediaLogger(PosTagger.class);
	
	public PosTagger() {
	
		try {
		
			this.tagger = new MaxentTagger(NLPediaSettings.getInstance().getSetting("pos.tagger.model"));
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param sentence
	 * @return
	 */
	public String tagSentence(String sentence)  {
		
		return tagger.tagString(sentence);
	}
	
	/**
	 * all input strings get trimmed
	 * 
	 * @param sentence the sentence to be tagged in this case a pattern most likely
	 * @param label1 the label left of the pattern
	 * @param label2 the label right of the pattern
	 * @return an pos annotated string
	 */
	public String getPosTagsForSentence(String sentence, String label1, String label2) {
		
		// add the labels ot the front/end to improve accuracy and tag it 
		String[] taggedSentence = this.tagSentence(label1.trim() + " " + sentence.trim() + " " + label2.trim()).split(" ");
		String[] sentenceWithoutLabels = // remove the tagged labels 
			Arrays.copyOfRange(taggedSentence, label1.split(" ").length, taggedSentence.length - label2.split(" ").length );
		// remove the words, to only have the pos tags
		StringBuilder builder = new StringBuilder();
		for ( String s : sentenceWithoutLabels ) {
			
			builder.append(s.substring(s.lastIndexOf("/") + 1) + " ");
		}
		return builder.toString().trim();
	}
}
