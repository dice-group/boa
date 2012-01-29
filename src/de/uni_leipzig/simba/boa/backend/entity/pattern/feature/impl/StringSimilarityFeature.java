package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.Feature;
import de.uni_leipzig.simba.boa.backend.featureextraction.FeatureExtractionPair;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.wordnet.similarity.SimilarityAssessor;
import de.uni_leipzig.simba.boa.backend.wordnet.similarity.WordNotFoundException;


public class StringSimilarityFeature implements Feature {

	private SimilarityAssessor similarityAssessor = new SimilarityAssessor();
	private NLPediaLogger logger = new NLPediaLogger(StringSimilarityFeature.class);
	
	@Override
	public void score(FeatureExtractionPair pair) {

		// we calculate the qgram distance between the NLR and the label of the property
			
		// get the NLR and remove all stopwords
		String naturalLanguageRepresentation = pair.getPattern().getNaturalLanguageRepresentationWithoutVariables();
		Set<String> tokens = new HashSet<String>(Arrays.asList(naturalLanguageRepresentation.split(" ")));
		tokens.removeAll(Constants.STOP_WORDS);
		
		double similarity = 0D;
		
		List<String> wordsToCompare;
		
		if ( !pair.getMapping().getProperty().retrieveSynsetsForLabel().isEmpty() ) {
			
			wordsToCompare = pair.getMapping().getProperty().retrieveSynsetsForLabel();
		}
		else {
			
			wordsToCompare = Arrays.asList(pair.getMapping().getProperty().getLabel().split(" "));
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
					
//						this.logger.debug("Word not found: " + e);
				}
			}
		}
		pair.getPattern().getFeatures().put(de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature.WORDNET_DISTANCE, similarity >= 0 ? similarity : 0);
	}
}
