package de.uni_leipzig.simba.boa.backend.nlp;

import java.io.FileInputStream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;


public class PosTagger {

	private InputStream posTaggerModelFile = null;
	private InputStream tokenModelFile = null;
	
	private POSTagger posTagger;
	private Tokenizer tokenizer;
	
	private final NLPediaLogger logger = new NLPediaLogger(PosTagger.class);
	
	public PosTagger() {
	
		try {
		
			this.logger.info("Reading training data for POS tagger! ");
			posTaggerModelFile = new FileInputStream("data/training/en-pos-maxent.bin");

			POSModel posModel = new POSModel(posTaggerModelFile);
			this.posTagger = new POSTaggerME(posModel);
			
			this.logger.info("Reading training data for tokenizing! ");
			tokenModelFile = new FileInputStream("data/training/en-token.bin");

			TokenizerModel tokenizerModel = new TokenizerModel(tokenModelFile);
			this.tokenizer = new TokenizerME(tokenizerModel);
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {	
			
			if (posTaggerModelFile != null) {
			
				try {
					posTaggerModelFile.close();
					
				}
				catch(Exception e) { e.printStackTrace(); } // nothing to do here
			}
			if (tokenModelFile != null) {
				
				try {
					tokenModelFile.close();
					
				}
				catch(Exception e) { e.printStackTrace(); } // nothing to do here
			}
		}
	}
	
	/**
	 * Tags a sentence with POS Tags from the OpenNLP framework. The provided sentence
	 * does not get modified. The output provided by this method looks something like this:
	 * "House/NN is/VP red/AP".  
	 * 
	 * @param sentence - the sentence to be tagged
	 * @return a new string with POS tags 
	 */
	public String posTagSentence(String sentence){
		
		StringBuffer tokenizedSentence = new StringBuffer();
		
		String[] tokens = this.tokenizer.tokenize(sentence);
		String[] tags	= this.posTagger.tag(tokens);
		
		if ( tokens.length == tags.length ) {
			
			for ( int i = 0; i < tokens.length; i++) {
				
				tokenizedSentence.append(tokens[i] + "/" + tags[i] + " ");
			}
			return tokenizedSentence.toString();
		}
		return null;
	}
	
	/**
	 * This method returns true if the provided sentence contains a noun phrase.
	 * The tags to check if this is true are "_NN", "_NN$", "_NNS", "_NNS$",
	 * "_NP", "_NP$", "_NPS", "_NPS$", "_NR" from the OpenNLP Framework. This
	 * method does not modify the given sentence.  
	 * 
	 * @param sentence - String the sentence to check
	 * @return true if the sentence contains a noun phrase, false otherwise
	 */
	public boolean containsNounPhrase(String sentence) {
		
		String[] possibleSubjectTags = new String[]{"/NN", "/NN$", "/NNS", "/NNS$",	"/NP", "/NP$", "/NPS", "/NPS$", "/NR"};
		
		for ( String nounPhrase : possibleSubjectTags) {
			
			if ( sentence.contains(nounPhrase) ) return true;
		}
		return false;
	}
}
