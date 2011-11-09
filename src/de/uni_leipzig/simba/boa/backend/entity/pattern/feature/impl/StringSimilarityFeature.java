package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.Feature;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.wordnet.similarity.SimilarityAssessor;
import de.uni_leipzig.simba.boa.backend.wordnet.similarity.WordNotFoundException;


public class StringSimilarityFeature implements Feature {

	private SimilarityAssessor similarityAssessor = new SimilarityAssessor();
	private NLPediaLogger logger = new NLPediaLogger(StringSimilarityFeature.class);
	
	@Override
	public void score(List<PatternMapping> mappings) {

		// nothing to do here
	}
	
	@Override
	public void scoreMapping(PatternMapping mapping) {

		// we calculate the qgram distance between the NLR and the label of the property
		for ( Pattern pattern : mapping.getPatterns() ) {
			
			if ( !pattern.isUseForPatternEvaluation() ) continue;
			
			// get the NLR and remove all stopwords
			String naturalLanguageRepresentation = pattern.getNaturalLanguageRepresentationWithoutVariables();
			Set<String> tokens = new HashSet<String>(Arrays.asList(naturalLanguageRepresentation.split(" ")));
			tokens.removeAll(Constants.STOP_WORDS);
			
			double similarity = 0D;
			
			List<String> wordsToCompare;
			
			if ( !mapping.getProperty().retrieveSynsetsForLabel().isEmpty() ) {
				
				wordsToCompare = mapping.getProperty().retrieveSynsetsForLabel();
			}
			else {
				
				wordsToCompare = Arrays.asList(mapping.getProperty().getLabel().split(" "));
			}
			
			// go through all words and synset combination and sum up the similarity
			for ( String token : tokens ) {
				
				for ( String wordFrom : wordsToCompare ) {
				
					try {
						
						double sim = this.similarityAssessor.getSimilarity(wordFrom, token);
						if ( !Double.isInfinite(sim) && !Double.isNaN(sim) ) {
							
							similarity = Math.max(similarity, sim);
						}
					}
					catch (WordNotFoundException e) {
						
						this.logger.error("Word not found: " + e);
					}
				}
			}
			pattern.getFeatures().put(de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature.WORDNET_DISTANCE, similarity >= 0 ? similarity : 0);
		}
	}
}
