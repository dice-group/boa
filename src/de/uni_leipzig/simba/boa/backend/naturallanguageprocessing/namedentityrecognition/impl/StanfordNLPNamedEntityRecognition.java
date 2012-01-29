/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.namedentityrecognition.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.namedentityrecognition.NamedEntityRecognition;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.namedentityrecognition.NamedEntityTagNormalizer;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation;
import edu.stanford.nlp.ling.CoreLabel;


/**
 * @author gerb
 *
 */
public final class StanfordNLPNamedEntityRecognition implements NamedEntityRecognition {

	private final NLPediaLogger logger	= new NLPediaLogger(StanfordNLPNamedEntityRecognition.class);
	private final String classifierPath	= NLPediaSettings.BOA_BASE_DIRECTORY + NLPediaSettings.getInstance().getSetting("namendEntityRecognizerClassifier");
	
	private CRFClassifier<CoreLabel> classifier;

	/**
	 * 
	 */
	public StanfordNLPNamedEntityRecognition() {
		
		try {
			
			this.classifier = CRFClassifier.getClassifier(new File(classifierPath));
		}
		catch (ClassCastException e) {
			
			this.logger.fatal("Wrong classifier specified in config file.", e);
			e.printStackTrace();
			throw new RuntimeException("Wrong classifier specified in config file.", e);
		}
		catch (IOException e) {

			this.logger.fatal("Could not read trained model!", e);
			e.printStackTrace();
			throw new RuntimeException("Could not read trained model!", e);
		}
		catch (ClassNotFoundException e) {
			
			this.logger.fatal("Wrong classifier specified in config file.", e);
			e.printStackTrace();
			throw new RuntimeException("Wrong classifier specified in config file.", e);
		} 
	}
	
	@Override
	public String getAnnotatedString(String string) {

		List<String> sentenceTokens = new ArrayList<String>();
		
		for ( List<CoreLabel> sentence : ((List<List<CoreLabel>>) classifier.classify(string)) ) {
		
			for ( CoreLabel word : sentence ) {
				
				String normalizedTag = NamedEntityTagNormalizer.NAMED_ENTITY_TAG_MAPPINGS.get(word.get(AnswerAnnotation.class));
				sentenceTokens.add(word.word() + Constants.NAMED_ENTITY_TAG_DELIMITER + normalizedTag);
			}
		}
		return StringUtils.join(sentenceTokens, " ");
	}

	@Override
	public String getAnnotations(String patternString) {

		List<String> sentenceTokens = new ArrayList<String>();
		
		List<List<CoreLabel>> classifiedString = classifier.classify(patternString);
		for ( List<CoreLabel> sentence : classifiedString) {
		
			for ( CoreLabel word : sentence ) {
				
				sentenceTokens.add(NamedEntityTagNormalizer.NAMED_ENTITY_TAG_MAPPINGS.get(word.get(AnswerAnnotation.class)));
			}
		}
		return StringUtils.join(sentenceTokens, " ");
	}
}
