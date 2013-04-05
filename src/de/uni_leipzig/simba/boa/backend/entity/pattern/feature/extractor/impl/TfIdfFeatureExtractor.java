package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.concurrent.PatternMappingGeneralizedPatternPair;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.AbstractFeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl.Feature;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;


public class TfIdfFeatureExtractor extends AbstractFeatureExtractor {

	private NLPediaLogger logger = new NLPediaLogger(TfIdfFeatureExtractor.class);
	private Map<PatternMapping,String> documents = new HashMap<PatternMapping,String>();
	private Map<String,Set<String>> mappingToText = new HashMap<String, Set<String>>();
	private Map<Set<String>,Map<String,Token>> cache = new HashMap<Set<String>, Map<String,Token>>();
	
	/**
	 * 
	 */
	private void init() {
	    
	    for ( PatternMapping mapping : this.mappings ) {
            
            StringBuffer buffer = new StringBuffer();
            for ( Pattern p : mapping.getPatterns() ) {
                
                buffer.append(p.getNaturalLanguageRepresentationWithoutVariables() + " ");
            }
            documents.put(mapping, buffer.toString());
        }
	}
	
	@Override
	public void score(PatternMappingGeneralizedPatternPair pair) {
	    
	    if ( this.documents.size() == 0 ) this.init();
		
		Set<String> distinctStringsForSingleDocument = new HashSet<String>(createDistinctStrings(pair.getMapping()));
		Map<String,Token> tokensInSingleDocument = createDocumentFrequencyAndFrequencyForTokens(distinctStringsForSingleDocument);
		
		SummaryStatistics tfStat = new SummaryStatistics();
		SummaryStatistics idfStat = new SummaryStatistics();
		SummaryStatistics tfIdfStat = new SummaryStatistics();
		
		for ( Pattern pattern : pair.getGeneralizedPattern().getPatterns() ) {
			
			double idfScore = 0;
			double tfScore = 0;
			for (String s : pattern.getNaturalLanguageRepresentationWithoutVariables().split(" ") ) {
				
				// should be always true since every word has been indexed, except stop words
				if ( tokensInSingleDocument.containsKey(s) ) {
					
					double scoreIdf = tokensInSingleDocument.get(s).getIdf(documents.size());
					if ( !Double.isInfinite(scoreIdf) && !Double.isNaN(scoreIdf) ) idfScore += scoreIdf;
					
					double scoreTf = tokensInSingleDocument.get(s).getTf();
					if ( !Double.isInfinite(scoreTf) && !Double.isNaN(scoreTf) ) tfScore += scoreTf;
				}
				else if ( !Constants.STOP_WORDS.contains(s) ) this.logger.error("There was a token not analyzed: " + s);
			}
			setValue(pattern, "TF_IDF_TFIDF",	tfScore*idfScore >= 0 ? tfScore*idfScore : 0, tfIdfStat);
			setValue(pattern, "TF_IDF_TF",		tfScore			 >= 0 ? tfScore			 : 0, tfStat);
			setValue(pattern, "TF_IDF_IDF",		idfScore		 >= 0 ? idfScore		 : 0, idfStat);
		}
		Map<Feature,Double> features = pair.getGeneralizedPattern().getFeatures();
		features.put(FeatureFactory.getInstance().getFeature("TF_IDF_TFIDF"), tfStat.getMean());
		features.put(FeatureFactory.getInstance().getFeature("TF_IDF_TF"), idfStat.getMean());
		features.put(FeatureFactory.getInstance().getFeature("TF_IDF_IDF"), tfIdfStat.getMean());
	}
	
	/**
	 * 
	 * @author gerb
	 *
	 */
	private class Token {
		
		private int frequency;
		private int documentFrequency;
		
		public double getIdf(int numberOfDocuments) {

			return (Math.log(numberOfDocuments/(documentFrequency + 1)) + 1);
		}

		public double getTf() {

			return Math.sqrt(frequency);
		}
	}
	
	/**
	 * 
	 * @param documents
	 * @param distinctStrings
	 * @return
	 */
	private Map<String,Token> createDocumentFrequencyAndFrequencyForTokens(Set<String> distinctStrings) {

		if ( !cache.containsKey(distinctStrings) ) {
			
			// create the tokens and calculate their (document) frequency
			Map<String,Token> tokens = new HashMap<String,Token>();
			for (String term : distinctStrings ) {
				
				Token tok = new Token();
				for (String document : documents.values()) {
					
					tok.frequency += StringUtils.countMatches(document, term);
					if ( document.contains(term) ) tok.documentFrequency++;
				}
				tokens.put(term, tok);
			}
			cache.put(distinctStrings, tokens);
		}
		
		return cache.get(distinctStrings);
	}
	
	/**
	 * 
	 * @param mapping
	 * @return
	 */
	private Set<String> createDistinctStrings(PatternMapping mapping) {
		
		if ( !this.mappingToText.containsKey(mapping.getProperty().getUri()) ) {
			
			// we build a long string, by concatenating all patterns
			StringBuilder patternText = new StringBuilder(); 
			for (Pattern p : mapping.getPatterns()) {
				
				patternText.append(p.getNaturalLanguageRepresentationWithoutVariables() + " ");
			}
			Set<String> tokens = new HashSet<String>(Arrays.asList(patternText.toString().split(" ")));
			tokens.removeAll(Constants.STOP_WORDS);
			this.mappingToText.put(mapping.getProperty().getUri(), tokens);
		}
		
		return this.mappingToText.get(mapping.getProperty().getUri());
	}
}
