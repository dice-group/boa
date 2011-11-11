package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.Feature;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;


public class TfIdfFeature implements Feature {

	private NLPediaLogger logger = new NLPediaLogger(TfIdfFeature.class);
	
	@Override
	public void score(List<PatternMapping> mappings) {

		// create list of terms		
		Set<String> distinctTokens = new HashSet<String>();
		for ( PatternMapping mapping : mappings ) {
			
			distinctTokens.addAll(createDistinctStrings(mapping));
		}
		
		// create list of documents
		Map<PatternMapping,String> documents = new HashMap<PatternMapping,String>();
		for ( PatternMapping mapping : mappings ) {
			
			StringBuffer buffer = new StringBuffer();
			for ( Pattern p : mapping.getPatterns() ) {
				
				buffer.append(p.getNaturalLanguageRepresentationWithoutVariables() + " ");
			}
			documents.put(mapping, buffer.toString());
		}
		
		Map<String,Token> tokens = createTokens(documents.values(), distinctTokens);
		
		for (PatternMapping mapping : mappings ) {
			
			for (Pattern p : mapping.getPatterns() ) {
				
				double tfIdfScore = 0;
				double idfScore = 0;
				double tfScore = 0;
				for (String s : p.getNaturalLanguageRepresentationWithoutVariables().split(" ") ) {
					
					// should be always true since every word has been index
					if ( tokens.containsKey(s) ) {
						
						double score = tokens.get(s).getTfIdf(documents.size());
						if ( !Double.isInfinite(score) && !Double.isNaN(score) ) tfIdfScore += score;
						
						double scoreIdf = tokens.get(s).getIdf(documents.size());
						if ( !Double.isInfinite(scoreIdf) && !Double.isNaN(scoreIdf) ) idfScore += scoreIdf;
						
						double scoreTf = tokens.get(s).getTf();
						if ( !Double.isInfinite(scoreTf) && !Double.isNaN(scoreTf) ) tfScore += scoreTf;
					}
					else {
						
						this.logger.error("There was a token not analyzed: " + s);
					}
				}
				p.getFeatures().put(de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature.TF_IDF_TFIDF, 	tfIdfScore	>= 0 ? tfIdfScore : 0);
				p.getFeatures().put(de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature.TF_IDF_TF, 		tfScore		>= 0 ? tfScore : 0);
				p.getFeatures().put(de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature.TF_IDF_IDF, 		idfScore	>= 0 ? idfScore : 0);
			}
		}
	}
	
	@Override
	public void scoreMapping(PatternMapping mapping) {

		// nothing to do here
	}
	
	class Token {
		
		private int frequency;
		private int documentFrequency;
		
		public double getTfIdf(int numberOfDocuments) {
			
			return Math.sqrt(frequency) * (Math.log(numberOfDocuments/(documentFrequency + 1)) + 1);
		}

		public double getIdf(int numberOfDocuments) {

			return (Math.log(numberOfDocuments/(documentFrequency + 1)) + 1);
		}

		public double getTf() {

			return Math.sqrt(frequency);
		}
	}
	
	private Map<String,Token> createTokens(Collection<String> documents, Set<String> distinctStrings) {

		// create the tokens and calculate their (document) frequency
		Map<String,Token> tokens = new HashMap<String,Token>();
		for (String term : distinctStrings ) {
			
			Token tok = new Token();
			for (String document : documents) {
				
				tok.frequency += StringUtils.countMatches(document, term);
				if ( document.contains(term) ) tok.documentFrequency++;
			}
			tokens.put(term, tok);
		}
		return tokens;
	}
	
	private Set<String> createDistinctStrings(PatternMapping mapping) {
		
		// we build a long string, by concatenating all patterns
		StringBuilder patternText = new StringBuilder(); 
		for (Pattern p : mapping.getPatterns()) {
			
			patternText.append(p.getNaturalLanguageRepresentationWithoutVariables() + " ");
		}
		Set<String> tokens = new HashSet<String>(Arrays.asList(patternText.toString().split(" ")));
//		tokens.removeAll(Constants.STOP_WORDS);
		return tokens;
	}
}
