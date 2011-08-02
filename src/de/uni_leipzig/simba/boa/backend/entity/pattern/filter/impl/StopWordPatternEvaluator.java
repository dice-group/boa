package de.uni_leipzig.simba.boa.backend.entity.pattern.filter.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.Initializeable;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.filter.PatternFilter;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

/**
 * 
 * 
 * @author Daniel Gerber
 */
public class StopWordPatternEvaluator extends Initializeable implements PatternFilter {
	
	private final NLPediaLogger logger = new NLPediaLogger(StopWordPatternEvaluator.class);

	private Map<String, List<String>> stopWordsByLanguage = new HashMap<String, List<String>>();
	
	/**
	 * Creates a StopWordPatternEvaluator with the configured stop word list 
	 * for the configured languages.
	 */
	public StopWordPatternEvaluator() {}
		
	@Override
	public void initialize() {

		// nothing to be done here
	}
	
	@Override
	public void filterPattern(PatternMapping patternMapping) {
		
		for ( Pattern p : patternMapping.getPatterns() ) {
			
			String patternInLanguage = p.getNaturalLanguageRepresentation().substring(0, p.getNaturalLanguageRepresentation().length() - 3).substring(3).trim();
			String[] tokens = patternInLanguage.split(" ");
			
			int numberOfStopWordsInPattern = 0;
			
			for ( String token : tokens ) {
				if ( this.stopWordsByLanguage.get(Constants.ENGLISH_LANGUAGE).contains(token) ) numberOfStopWordsInPattern++;
			}
			
			// true or correct if the number of stop-words in the pattern is not equal to the number of tokens
			// patterns containing only stop-words can't be used, because they are way to general 
			p.setUseForPatternEvaluation(tokens.length != numberOfStopWordsInPattern);
			
			if ( !p.isUseForPatternEvaluation() ) {
				
				System.out.println("Pattern " + p.getNaturalLanguageRepresentation() + " consists only of stop words.");
				this.logger.debug("Pattern " + p.getNaturalLanguageRepresentation() + " consists only of stop words.");
			}
		}
	}

	/**
	 * @param stopWordsByLanguage the stopWordsByLanguage to set
	 */
	public void setStopWordsByLanguage(Map<String, List<String>> stopWordsByLanguage) {

		this.stopWordsByLanguage = stopWordsByLanguage;
		
		for (String language : this.stopWordsByLanguage.keySet() ) { 
			
			this.logger.debug("Size of stop-words for language: " + language + " is " + this.stopWordsByLanguage.get(language).size());
		}
	}

	/**
	 * @return the stopWordsByLanguage
	 */
	public Map<String, List<String>> getStopWordsByLanguage() {

		return stopWordsByLanguage;
	}
}
