package de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.partofspeechtagger.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import cern.colt.Arrays;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.partofspeechtagger.PartOfSpeechTagNormalizer;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.partofspeechtagger.PartOfSpeechTagger;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * 
 * @author gerb
 */
public final class StanfordNLPPartOfSpeechTagger implements PartOfSpeechTagger {

	private final NLPediaLogger logger	= new NLPediaLogger(StanfordNLPPartOfSpeechTagger.class);
	private MaxentTagger tagger;

	public StanfordNLPPartOfSpeechTagger() {
		
		try {
			
		    // this is used to surpress the "error" messages from stanford etc.
            PrintStream standardErrorStream = System.err;
            System.setErr(new PrintStream(new ByteArrayOutputStream()));
		    
			this.tagger = new MaxentTagger(NLPediaSettings.BOA_BASE_DIRECTORY + NLPediaSettings.getSetting("pos.tagger.model"));
			
			// revert to original standard error stream
            System.setErr(standardErrorStream);
		}
		catch (ClassNotFoundException e) {
			
			this.logger.fatal("Wrong classifier specified in config file.", e);
			e.printStackTrace();
			throw new RuntimeException("Wrong classifier specified in config file.", e);
		}
		catch (IOException e) {
			
			this.logger.fatal("Could not read trained model!", e);
			e.printStackTrace();
			throw new RuntimeException("Could not read trained model!", e);
		}
	}
	
	@Override
	public String getAnnotatedString(String string) {

		List<String> sentence = new ArrayList<String>();
		for ( String taggedWord : tagger.tagString(string).split(" ")) {
			
			int lastIndex	= taggedWord.lastIndexOf("/");
			String posTag	= taggedWord.substring(lastIndex + 1);
			String token	= taggedWord.substring(0, lastIndex);
			
			sentence.add(token + Constants.PART_OF_SPEECH_TAG_DELIMITER + PartOfSpeechTagNormalizer.PART_OF_SPEECH_TAG_MAPPINGS.get(posTag));
			
			if ( PartOfSpeechTagNormalizer.PART_OF_SPEECH_TAG_MAPPINGS.get(posTag) == null )
				System.err.println("No tag mapping for tag: \"" + posTag + "\" found");
		}
		return StringUtils.join(sentence, " ");
	}

	@Override
	public String getAnnotations(String string) {

		// add the surfaceForms ot the front/end to improve accuracy and tag it 
		String[] taggedSentence = this.getAnnotatedString(string).split(" ");
		// remove the words, to only have the pos tags
		List<String> tags = new ArrayList<String>();
		for ( String s : taggedSentence ) {
			
			tags.add(s.substring(s.lastIndexOf(Constants.PART_OF_SPEECH_TAG_DELIMITER) + 1));
		}
		return StringUtils.join(tags, " ");
	}
	
	/**
	 * 
	 * @param sentence
	 * @return
	 */
	public List<String> getNounPhrases(String sentence) {

	    String[] taggedSentence    = this.getAnnotatedString(sentence).split(" ");
	    List<String> nounPhrases   = new ArrayList<String>();
	    
	    List<String> currentNounPhrase = new ArrayList<String>();
	    System.out.println(this.getAnnotatedString(sentence));
	    for ( String taggedWord : taggedSentence) {
	        
	        // do we have a proper noun in singular or plural
            if ( taggedWord.matches(".+_NNP?S?") ) {
                
                currentNounPhrase.add(taggedWord.substring(0, taggedWord.indexOf("_")));
            }
            else {
                
                if ( !currentNounPhrase.isEmpty() ) {
                    
                    nounPhrases.add(StringUtils.join(currentNounPhrase, " "));
                    currentNounPhrase = new ArrayList<String>();
                }
            }
	    }
	    System.out.println(nounPhrases);
	    return nounPhrases;
	}
}
