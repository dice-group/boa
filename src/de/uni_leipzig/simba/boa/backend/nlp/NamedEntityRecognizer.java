package de.uni_leipzig.simba.boa.backend.nlp;

import java.util.Arrays;
import java.util.List;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.NERClassifierCombiner;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation;
import edu.stanford.nlp.ling.CoreLabel;

/**
 * 
 * @author Daniel Gerber
 */
public class NamedEntityRecognizer {

	private final NLPediaLogger logger = new NLPediaLogger(NamedEntityRecognizer.class);
	private final AbstractSequenceClassifier classifier;
	
	public static final String DELIMITER = "_";
	
	private StringBuilder buffer;
	
	private final List<String> classes = Arrays.asList("B-PER", "B-LOC", "B-ORG", "B-MISC", "DATE");
	
	public NamedEntityRecognizer() {
		
		this.classifier = new NERClassifierCombiner(
				CRFClassifier.getClassifierNoExceptions(NLPediaSettings.getInstance().getSetting("namendEntityRecognizerClassifier")));//, 
//				CRFClassifier.getClassifierNoExceptions("/Users/gerb/Development/workspaces/java-ws/nlpedia/data/training/classifier/ner-date-model.ser.gz"));
	}
	
	/**
	 * This method translates a given string in a new string with entity tags 
	 * from the stanford NER library. Typical tags used for this are "B-PER", 
	 * "B-LOC", "B-ORG". For example the string "Elizabeth Priscilla Cooper 
	 * was born in" will be translated as follows: "B-PER I-PER I-PER was born in"
	 * 
	 * This means entites are replaced with placeholder other words are not.
	 * 
	 * @param patternString - the string to be tagged
	 * @return the NER tagged string
	 */
	public String recognizeEntitiesInPattern(String patternString) {

		String begin = patternString.substring(0, 3);
		String end = patternString.substring(patternString.length() - 4, patternString.length());
		
		StringBuilder buffer = new StringBuilder();
		buffer.append(begin + " ");
		
		List<List<CoreLabel>> classifiedString = classifier.classify(patternString.replace("?X?","").replace("?Y?", ""));
		for ( List<CoreLabel> sentence : classifiedString) {
		
			for ( CoreLabel word : sentence ) {
				
				if ( word.get(AnswerAnnotation.class).equals("O") ) {
					
					buffer.append(word.word() + " ");
				}
				else {
					
					buffer.append(word.get(AnswerAnnotation.class) + " ");
				}
			}
		}
		return buffer.append(end + " ").toString();
	}
	
	/**
	 * Returns a new string which equals the given one, except that all words have
	 * the NER tag attached.
	 * 
	 * 
	 * @param sentence
	 * @return
	 */
	public String recognizeEntitiesInString(String sentence) {
		
		this.buffer = new StringBuilder();
		
		for ( List<CoreLabel> thisSentence : ((List<List<CoreLabel>>) classifier.classify(sentence)) ) {
		
			for ( CoreLabel word : thisSentence ) {
				
				if ( word.get(AnswerAnnotation.class).equals("O") ) {
					
					this.buffer.append(word.word() + NamedEntityRecognizer.DELIMITER + "O ");
				}
				else {
					
					this.buffer.append(word.word() + NamedEntityRecognizer.DELIMITER + word.get(AnswerAnnotation.class) + " ");
				}
			}
		}
		return this.buffer.toString();
	}
	
	
	/**
	 * This method tests a given string if it contains any named entities.
	 * The entity classes for which the test is run are: B-LOC (Location),
	 * B-ORG (Organisation), B-PER (Person) and B-MISC (everthing else). Those
	 * classes are taken from the stanford ner library.
	 * 
	 * Be aware that this method needs an already tag sentence as input, you
	 * can set tagSentenceFirst to true if you want the sentence to be NER tagged 
	 * first.
	 * 
	 * @param sentence - the tagged sentence
	 * @param tagSentenceFirst - true if you want to tag the sentence first and check then
	 * @return true, if it contains entities
	 */
	public boolean containsEntities(String sentence, boolean tagSentenceFirst) {

		if ( tagSentenceFirst ) sentence = this.recognizeEntitiesInString(sentence);
		for ( String clazz : this.classes ) {
			
			if ( sentence.endsWith(NamedEntityRecognizer.DELIMITER + clazz) ) return true;
		}
		return false;
	}
}
