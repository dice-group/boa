package de.uni_leipzig.simba.boa.backend.entity.pattern.confidence.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.confidence.ConfidenceMeasure;


public class TfIdfMeasure implements ConfidenceMeasure {

	private String text;
	
	@Override
	public void measureConfidence(PatternMapping mapping) {

		Set<String> distinctStrings = createDistinctStrings(mapping);
		Map<String,Token> tokens = createTokens(mapping, distinctStrings);
		
		for (Pattern p : mapping.getPatterns() ) {
			
			double tfIdfScore = 0;
			for (String s : p.getNaturalLanguageRepresentationWithoutVariables().split(" ") ) {
				
				// we don't want stop-words
				if ( tokens.containsKey(s) ) {
					
					double score = tokens.get(s).getTfIdf(mapping.getPatterns().size());
					if ( !Double.isInfinite(score) && !Double.isNaN(score) ) {
						
						tfIdfScore += score;
					}
				}
			}
			p.setTfIdf(tfIdfScore);
		}
	}
	
	class Token {
		
		private int frequency;
		private int documentFrequency;
		
		public double getTfIdf(int numberOfDocuments) {
			
			return Math.sqrt(frequency) * (Math.log(numberOfDocuments/(documentFrequency + 1)) + 1);
		}
	}
	
	private Map<String,Token> createTokens(PatternMapping mapping, Set<String> distinctStrings) {

		// create the tokens and calculate their (document) frequency
		Map<String,Token> tokens = new HashMap<String,Token>();
		for (String token : distinctStrings ) {
			
			Token tok = new Token();
			tok.frequency = StringUtils.countMatches(this.text, token);
			for (Pattern p : mapping.getPatterns() ) {
				
				if (p.getNaturalLanguageRepresentation().contains(token)) tok.documentFrequency++;
			}
			tokens.put(token, tok);
		}
		return tokens;
	}
	
	private Set<String> createDistinctStrings(PatternMapping mapping) {
		
		// we build a long string, by concatenating all patterns
		StringBuilder patternText = new StringBuilder(); 
		for (Pattern p : mapping.getPatterns()) {
			
			patternText.append(p.getNaturalLanguageRepresentationWithoutVariables() + " ");
		}
		this.text = patternText.toString();
		// then we split the text at whitespace and remove all stopwords
		Set<String> tokens = new HashSet<String>(Arrays.asList(this.text.split(" ")));
		tokens.removeAll(Constants.STOP_WORDS);
		return tokens;
	}
}
