/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.naturallanguageprocessing;

import java.util.List;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.josatagger.JosaTagger;
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
	
	private String defaultJosaTagger;
	private String defaultPartOfSpeechTagger;
	private String defaultNamedEntityRecognition;
	private String defaultSentenceBoundaryDisambiguation;
	
	private List<String> josaTaggerTools;
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
	 * @return the instantiated class
	 * @throws RuntimeExcpetion if no class could be found
	 */
	public PartOfSpeechTagger createPartOfSpeechTagger(Class<? extends PartOfSpeechTagger> partOfSpeechTaggerClass) {

		if ( this.partOfSpeechTools.contains(partOfSpeechTaggerClass.getName()) ) {
			
			return (PartOfSpeechTagger) createNewInstance(partOfSpeechTaggerClass);
		}
		this.logger.error("Could not load pos tagger " + partOfSpeechTaggerClass);
		throw new RuntimeException("Could not load pos tagger " + partOfSpeechTaggerClass);
	}
	
	/**
	 * Returns a new instance of the specified partOfSpeechTaggerClass.
	 * The specific implementations of the taggers have to be declared
	 * in the nlpedia_setup.xml file.
	 * 
	 * @param partOfSpeechTaggerClass - the partOfSpeechTaggerClass to be instantiated
	 * @return the instantiated class
	 * @throws RuntimeExcpetion if no class could be found
	 */
	public JosaTagger createJosaTagger(Class<? extends JosaTagger> josaTaggerClass) {

		if ( this.josaTaggerTools.contains(josaTaggerClass.getName()) ) {
			
			return (JosaTagger) createNewInstance(josaTaggerClass);
		}
		this.logger.error("Could not load josa tagger " + josaTaggerClass);
		throw new RuntimeException("Could not load josa tagger " + josaTaggerClass);
	}
	
	/**
	 * @return the default part of speech tagger
	 */
	@SuppressWarnings("unchecked")
	public PartOfSpeechTagger createDefaultPartOfSpeechTagger() {

		try {
			
			return (PartOfSpeechTagger) createNewInstance(
					(Class<? extends NaturalLanguageProcessingTool>) Class.forName(defaultPartOfSpeechTagger));
		}
		catch (ClassNotFoundException e) {

			e.printStackTrace();
			this.logger.error("Could not load default pos tagger " + defaultPartOfSpeechTagger);
			throw new RuntimeException("Could not load default pos tagger " + defaultPartOfSpeechTagger);
		}
	}
	
	/**
	 * Returns a new instance of the specified namedEntityDisambiguationClass.
	 * The specific implementations of the taggers have to be declared
	 * in the nlpedia_setup.xml file.
	 * 
	 * @param namedEntityDisambiguationClass - the namedEntityDisambiguationClass to be instantiated
	 * @return the instantiated class
	 * @throws RuntimeExcpetion if no class could be found
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
	 * @return the default named entity recognition
	 */
	@SuppressWarnings("unchecked")
	public NamedEntityRecognition createDefaultNamedEntityRecognition() {

		try {
			
			return (NamedEntityRecognition) createNewInstance(
					(Class<? extends NaturalLanguageProcessingTool>) Class.forName(defaultNamedEntityRecognition));
		}
		catch (ClassNotFoundException e) {

			e.printStackTrace();
			this.logger.error("Could not load default named entity recognition " + defaultNamedEntityRecognition);
			throw new RuntimeException("Could not load default named entity recognition " + defaultNamedEntityRecognition);
		}
	}
	
	/**
	 * Returns a new instance of the specified sentenceBoundaryDisambiguationClass.
	 * The specific implementations of the sentence boundary disambiguations have to be declared
	 * in the nlpedia_setup.xml file.
	 * 
	 * @param sentenceBoundaryDisambiguationClass - the sentenceBoundaryDisambiguationClass to be instantiated
	 * @return the instantiated class
	 * @throws RuntimeExcpetion if no class could be found
	 */
	public SentenceBoundaryDisambiguation createSentenceBoundaryDisambiguation(Class<? extends SentenceBoundaryDisambiguation> sentenceBoundaryDisambiguationClass) {

		if ( this.sentenceBoundaryDisambiguationTools.contains(sentenceBoundaryDisambiguationClass.getName()) ) {
			
			return (SentenceBoundaryDisambiguation) createNewInstance(sentenceBoundaryDisambiguationClass);
		}
		this.logger.error("Could not load sentence boundary disambiguation " + sentenceBoundaryDisambiguationClass);
		throw new RuntimeException("Could not load sentence boundary disambiguation " + sentenceBoundaryDisambiguationClass);
	}

	/**
	 * @return the default sentence boundary disambiguation
	 */
	@SuppressWarnings("unchecked")
	public SentenceBoundaryDisambiguation createDefaultSentenceBoundaryDisambiguation() {

		try {
			
			return (SentenceBoundaryDisambiguation) createNewInstance(
					(Class<? extends NaturalLanguageProcessingTool>) Class.forName(defaultSentenceBoundaryDisambiguation));
		}
		catch (ClassNotFoundException e) {

			e.printStackTrace();
			this.logger.error("Could not load default sentence boundary disambiguation " + defaultSentenceBoundaryDisambiguation);
			throw new RuntimeException("Could not load default sentence boundary disambiguation " + defaultSentenceBoundaryDisambiguation);
		}
	}
	
	/**
	 * @return the default josa tagger
	 */
	@SuppressWarnings("unchecked")
	public JosaTagger createDefaultJosaTagger() {

		try {
			
			return (JosaTagger) createNewInstance(
					(Class<? extends NaturalLanguageProcessingTool>) Class.forName(defaultJosaTagger));
		}
		catch (ClassNotFoundException e) {

			e.printStackTrace();
			this.logger.error("Could not load default josa tagger " + defaultSentenceBoundaryDisambiguation);
			throw new RuntimeException("Could not load default josa tagger " + defaultSentenceBoundaryDisambiguation);
		}
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
	 * @return the defaultPartOfSpeechTagger
	 */
	public String getDefaultPartOfSpeechTagger() {
	
		return defaultPartOfSpeechTagger;
	}

	
	/**
	 * @param defaultPartOfSpeechTagger the defaultPartOfSpeechTagger to set
	 */
	public void setDefaultPartOfSpeechTagger(String defaultPartOfSpeechTagger) {
	
		this.defaultPartOfSpeechTagger = defaultPartOfSpeechTagger;
	}

	
	/**
	 * @return the defaultNamedEntityRecognition
	 */
	public String getDefaultNamedEntityRecognition() {
	
		return defaultNamedEntityRecognition;
	}

	
	/**
	 * @param defaultNamedEntityRecognition the defaultNamedEntityRecognition to set
	 */
	public void setDefaultNamedEntityRecognition(String defaultNamedEntityRecognition) {
	
		this.defaultNamedEntityRecognition = defaultNamedEntityRecognition;
	}

	
	/**
	 * @return the defaultSentenceBoundaryDisambiguation
	 */
	public String getDefaultSentenceBoundaryDisambiguation() {
	
		return defaultSentenceBoundaryDisambiguation;
	}

	
	/**
	 * @param defaultSentenceBoundaryDisambiguation the defaultSentenceBoundaryDisambiguation to set
	 */
	public void setDefaultSentenceBoundaryDisambiguation(String defaultSentenceBoundaryDisambiguation) {
	
		this.defaultSentenceBoundaryDisambiguation = defaultSentenceBoundaryDisambiguation;
	}

	
	/**
	 * @return the defaultJosaTagger
	 */
	public String getDefaultJosaTagger() {
	
		return defaultJosaTagger;
	}

	
	/**
	 * @param defaultJosaTagger the defaultJosaTagger to set
	 */
	public void setDefaultJosaTagger(String defaultJosaTagger) {
	
		this.defaultJosaTagger = defaultJosaTagger;
	}

	
	/**
	 * @return the josaTaggerTools
	 */
	public List<String> getJosaTaggerTools() {
	
		return josaTaggerTools;
	}

	
	/**
	 * @param josaTaggerTools the josaTaggerTools to set
	 */
	public void setJosaTaggerTools(List<String> josaTaggerTools) {
	
		this.josaTaggerTools = josaTaggerTools;
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
//		NamedEntityRecognition a = NaturalLanguageProcessingToolFactory.getInstance().createNamedEntityRecognition(StanfordNLPNamedEntityRecognition.class);
//		a.getAnnotatedString("This is a sentence which was written by Daniel Gerber.");
		
		PartOfSpeechTagger postagger = NaturalLanguageProcessingToolFactory.getInstance().createDefaultPartOfSpeechTagger();
		postagger.getAnnotations("This is a very simple sentence!");
	}
}
