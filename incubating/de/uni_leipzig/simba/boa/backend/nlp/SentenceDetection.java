package de.uni_leipzig.simba.boa.backend.nlp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import org.springframework.util.StringUtils;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.sentenceboundarydisambiguation.SentenceBoundaryDisambiguation;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.sentenceboundarydisambiguation.impl.KoreanSentenceBoundaryDisambiguation;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;


/**
 * DEL
 * @author Daniel Gerber
 */
public class SentenceDetection {

	private NLPediaLogger logger = null;
	
	private SentenceDetector sentenceDetector;
	private Tokenizer tokenizer;
	private POSTagger posTagger;

	private SentenceFilter sentenceFilter; 
	
	/**
	 * private constructor, use singleton get instance method!
	 */
	public SentenceDetection(){
		
		this.logger = new NLPediaLogger(SentenceDetection.class);

		InputStream sentenceModelFile = null;
		InputStream tokenModelFile = null;
		InputStream posTaggerModelFile = null;
		
		this.sentenceFilter = new SentenceFilter();

		try {
			
			this.logger.info("Reading training data for sentence detection! ");
			sentenceModelFile = new FileInputStream("data/training/en-sent.bin");
			
			SentenceModel sentenceModel = new SentenceModel(sentenceModelFile);
			this.sentenceDetector = new SentenceDetectorME(sentenceModel);
			
			this.logger.info("Reading training data for tokenizing! ");
			tokenModelFile = new FileInputStream("data/training/en-token.bin");

			TokenizerModel tokenizerModel = new TokenizerModel(tokenModelFile);
			this.tokenizer = new TokenizerME(tokenizerModel);
			
			this.logger.info("Reading training data for POS tagger! ");
			posTaggerModelFile = new FileInputStream("data/training/en-pos-maxent.bin");
			
			POSModel posModel = new POSModel(posTaggerModelFile);
			this.posTagger = new POSTaggerME(posModel);
		}
		catch (FileNotFoundException fnfe) {

			logger.fatal("Training model not found!" + fnfe.getLocalizedMessage(), fnfe);
		}
		catch (IOException e) {

			logger.fatal("An IOexcepion occured", e);
		}
		finally {

			if (sentenceModelFile != null) {

				try {

					sentenceModelFile.close();
				}
				catch(Exception e) { e.printStackTrace(); } // nothing to do here 
			}
			if (tokenModelFile != null) {
				
				try {
					tokenModelFile.close();
					
				}
				catch(Exception e) { e.printStackTrace(); } // nothing to do here 
			}
			if (posTaggerModelFile != null) {
				
				try {
					tokenModelFile.close();
					
				}
				catch(Exception e) { e.printStackTrace(); } // nothing to do here
			}
		}
	}
	
	/**
	 * This method uses the OpenNLP framework (and a trained model) for sentence detection.
	 * 
	 * @param text - the text to detect the sentences
	 * @return a list of sentences, one per list entry
	 */
	public List<String> getSentences(String text, String method) {

		List<String> sentences = new ArrayList<String>();
		
		if ( method.equals(Constants.SENTENCE_BOUNDARY_DISAMBIGUATION_OPEN_NLP) ) {
			
			Date startDate = new Date();
			sentences = Arrays.asList(this.sentenceDetector.sentDetect(text));
			this.logger.debug("It took " + (new Date().getTime() - startDate.getTime()) + "ms to detect sentences in the text with a length: " + text.length());
		}
		if ( method.equals(Constants.SENTENCE_BOUNDARY_DISAMBIGUATION_STANFORD_NLP) ) {
			
			Reader stringReader = new StringReader(text);
			DocumentPreprocessor preprocessor = new DocumentPreprocessor(stringReader,  DocumentPreprocessor.DocType.Plain);
			
			Iterator<List<HasWord>> iter = preprocessor.iterator();
			while ( iter.hasNext() ) {
				
				StringBuilder sentence = new StringBuilder();
				
				for ( HasWord word : iter.next() ) {
					sentence.append(word.toString() + " ");
				}
				sentences.add(sentence.toString().trim());
			}
		}
		if ( method.equals(Constants.SENTENCE_BOUNDARY_DISAMBIGUATION_KOREAN) ) {
			
			SentenceBoundaryDisambiguation sbd = new KoreanSentenceBoundaryDisambiguation();
			sentences.addAll(sbd.splitTextIntoSentences(text));
		}
		
		List<String> tokenizedSentences = new ArrayList<String>();
		
		// return only those sentences which go through the filters
//		for (String sentence : this.sentenceFilter.filterSentences(sentences) ) tokenizedSentences.add(sentence.trim());
//		return tokenizedSentences;
		return sentences;
	}

	/**
	 * This method is used to filter out invalid sentences. A sentence is valid
	 * if it contains at least two noun phrases (subject and object) and a verb 
	 * phrase.
	 * 
	 * @param sentence the pos anotated sentence
	 * @return true if sentence is valid, else false
	 */
	private boolean isValidSentence(String sentence) {

		boolean containsSubjectPhrase	= false;
		boolean containsVerbPhrase		= false;
		
		String[] possibleSubjectTags = new String[]{"_NN", "_NN$", "_NNS", "_NNS$",	"_NP", "_NP$", "_NPS", "_NPS$", "_NR"};
		String[] possibleVerbTags = new String[]{"_VB", "_VBD", "_VBG", "_VBN", "_VBZ"};
		
		this.logger.debug("Validation sentence: " + sentence);
		
		for ( String verbPhrase : possibleVerbTags ) {
			
			// look for verb phrase and quit comparison if found
			containsVerbPhrase |= sentence.indexOf(verbPhrase) > -1;
			if (containsVerbPhrase) {
				
				this.logger.debug("Found verb phrase.");
				break;
			}
		}
		
		// we don't need to look for noun phrases if we didn't found any verbs
		if ( containsVerbPhrase ) {
			
			int numberOfOccurrences = 0;
			
			for ( String nounPhrase : possibleSubjectTags) {
				
				numberOfOccurrences += StringUtils.countOccurrencesOf(sentence, nounPhrase);
				
				if ( numberOfOccurrences > 1 ) {
					
					this.logger.debug("Found more than two noun phrases.");
					containsSubjectPhrase = true;
					break;
				}
			}
		}
		
		return containsSubjectPhrase && containsVerbPhrase;
	}
}
