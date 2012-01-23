/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.naturallanguageprocessing;

import java.util.List;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.namedentityrecognition.NamedEntityRecognition;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.namedentityrecognition.impl.StanfordNLPNamedEntityRecognition;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.partofspeechtagger.PartOfSpeechTagger;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.sentenceboundarydisambiguation.SentenceBoundaryDisambiguation;

/**
 * Singleton - initialized by the spring framework
 * 
 * Use this factory to create DAOs.
 * 
 * The create a new Dao just use the createDAO() method by referring the
 * Dao interface. The factory will return the specific implementation of this Dao interface.
 * 
 * Available implementations of the DAOs are specified in the jawa_extensions.xml file.
 */
public class NaturalLanguageProcessingToolFactory {
	
	private static NaturalLanguageProcessingToolFactory INSTANCE;
	
	private final NLPediaLogger logger = new NLPediaLogger(NaturalLanguageProcessingToolFactory.class);
	
	private List<String> partOfSpeechTools;
	private List<String> sentenceBoundaryDisambiguationTools;
	private List<String> namedEntityRecognitionTools;
	
	/**
	 * Singleton
	 */
	private NaturalLanguageProcessingToolFactory() {}
	
	/**
	 * @return the instance of this singleton
	 */
	public static NaturalLanguageProcessingToolFactory getInstance() {
		
		if ( INSTANCE == null ) {
			
			INSTANCE = new NaturalLanguageProcessingToolFactory();
		}
		return INSTANCE;
	}
	
	/**
	 * Returns a new instance of the specified partOfSpeechTaggerClass.
	 * The specific implementations of the taggers have to be declared
	 * in the nlpedia_setup.xml file.
	 * 
	 * @param partOfSpeechTaggerClass - the partOfSpeechTaggerClass to be instantiated
	 * @return the instantiated dao 
	 * @throws RuntimeExcpetion if no dao could be found
	 */
	public PartOfSpeechTagger createPartOfSpeechTagger(Class<? extends PartOfSpeechTagger> partOfSpeechTaggerClass) {

		if ( this.partOfSpeechTools.contains(partOfSpeechTaggerClass.getName()) ) {
			
			return (PartOfSpeechTagger) createNewInstance(partOfSpeechTaggerClass);
		}
		this.logger.error("Could not load pos tagger " + partOfSpeechTaggerClass);
		throw new RuntimeException("Could not load pos tagger " + partOfSpeechTaggerClass);
	}
	
	/**
	 * Returns a new instance of the specified namedEntityDisambiguationClass.
	 * The specific implementations of the taggers have to be declared
	 * in the nlpedia_setup.xml file.
	 * 
	 * @param namedEntityDisambiguationClass - the namedEntityDisambiguationClass to be instantiated
	 * @return the instantiated dao
	 * @throws RuntimeExcpetion if no dao could be found
	 */
	public NamedEntityRecognition createNamedEntityRecognition(Class<? extends NamedEntityRecognition> namedEntityDisambiguationClass) {

		System.out.println(namedEntityDisambiguationClass.getName());
		
		if ( this.namedEntityRecognitionTools.contains(namedEntityDisambiguationClass.getName()) ) {
			
			return (NamedEntityRecognition) createNewInstance(namedEntityDisambiguationClass);
		}
		this.logger.error("Could not load named entity recognition " + namedEntityDisambiguationClass);
		throw new RuntimeException("Could not load named entity recognition " + namedEntityDisambiguationClass);
	}
	
	/**
	 * Returns a new instance of the specified sentenceBoundaryDisambiguationClass.
	 * The specific implementations of the sentence boundary disambiguations have to be declared
	 * in the nlpedia_setup.xml file.
	 * 
	 * @param sentenceBoundaryDisambiguationClass - the sentenceBoundaryDisambiguationClass to be instantiated
	 * @return the instantiated dao
	 * @throws RuntimeExcpetion if no dao could be found
	 */
	public SentenceBoundaryDisambiguation createSentenceBoundaryDisambiguation(Class<? extends SentenceBoundaryDisambiguation> sentenceBoundaryDisambiguationClass) {

		if ( this.sentenceBoundaryDisambiguationTools.contains(sentenceBoundaryDisambiguationClass.getName()) ) {
			
			return (SentenceBoundaryDisambiguation) createNewInstance(sentenceBoundaryDisambiguationClass);
		}
		this.logger.error("Could not load sentence boundary disambiguation " + sentenceBoundaryDisambiguationClass);
		throw new RuntimeException("Could not load sentence boundary disambiguation " + sentenceBoundaryDisambiguationClass);
	}

	
	/**
	 * @return the partOfSpeechTools
	 */
	public List<String> getPartOfSpeechTools() {
	
		return partOfSpeechTools;
	}

	
	/**
	 * @param partOfSpeechTools the partOfSpeechTools to set
	 */
	public void setPartOfSpeechTools(List<String> partOfSpeechTools) {
	
		this.partOfSpeechTools = partOfSpeechTools;
	}

	
	/**
	 * @return the sentenceBoundaryDisambiguationTools
	 */
	public List<String> getSentenceBoundaryDisambiguationTools() {
	
		return sentenceBoundaryDisambiguationTools;
	}

	
	/**
	 * @param sentenceBoundaryDisambiguationTools the sentenceBoundaryDisambiguationTools to set
	 */
	public void setSentenceBoundaryDisambiguationTools(List<String> sentenceBoundaryDisambiguationTools) {
	
		this.sentenceBoundaryDisambiguationTools = sentenceBoundaryDisambiguationTools;
	}

	
	/**
	 * @return the namedEntityRecognitionTools
	 */
	public List<String> getNamedEntityRecognitionTools() {
	
		return namedEntityRecognitionTools;
	}

	
	/**
	 * @param namedEntityRecognitionTools the namedEntityRecognitionTools to set
	 */
	public void setNamedEntityRecognitionTools(List<String> namedEntityRecognitionTools) {
	
		this.namedEntityRecognitionTools = namedEntityRecognitionTools;
	}

	/**
	 * Instantiates a natural language processing tool.
	 * 
	 * @param tool the tool to be instantiated
	 * @return a new instance of the tool
	 * @throw RuntimeException if something wents wrong
	 */
	private NaturalLanguageProcessingTool createNewInstance(Class<? extends NaturalLanguageProcessingTool> tool){
		
		try {
			
			return tool.newInstance();
		}
		catch (InstantiationException e) {

			e.printStackTrace();
			this.logger.fatal("Could not instantiate class: " + tool, e);
			throw new RuntimeException("Could not instantiate class: " + tool, e);
		}
		catch (IllegalAccessException e) {
			
			e.printStackTrace();
			this.logger.fatal("Could not instantiate class: " + tool, e);
			throw new RuntimeException("Could not instantiate class: " + tool, e);
		}
	}
	
	public static void main(String[] args) {

		NLPediaSetup setup = new NLPediaSetup(true);
		NamedEntityRecognition a = NaturalLanguageProcessingToolFactory.getInstance().createNamedEntityRecognition(StanfordNLPNamedEntityRecognition.class);
		a.getAnnotatedString("This is a sentence which was written by Daniel Gerber.");
	}
}
