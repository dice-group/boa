package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.concurrent.PatternMappingPatternPair;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.AbstractFeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.wordnet.similarity.SimilarityAssessor;
import de.uni_leipzig.simba.boa.backend.wordnet.similarity.WordNotFoundException;


public class StringSimilarityFeatureExtractor extends AbstractFeatureExtractor {

	private SimilarityAssessor similarityAssessor = null;
	private NLPediaLogger logger = new NLPediaLogger(StringSimilarityFeatureExtractor.class);
	
	@Override
	public void score(PatternMappingPatternPair pair) {

	    if ( this.similarityAssessor == null ) this.similarityAssessor = new SimilarityAssessor();
		// we calculate the qgram distance between the NLR and the label of the property
			
		// get the NLR and remove all stopwords
		String naturalLanguageRepresentation = pair.getPattern().getNaturalLanguageRepresentationWithoutVariables();
		Set<String> tokens = new HashSet<String>(Arrays.asList(naturalLanguageRepresentation.split(" ")));
		tokens.removeAll(Constants.STOP_WORDS);
		
		double similarity = 0D;
		
		Set<String> wordsToCompare;
		
		if ( !pair.getMapping().getProperty().getSynsets().isEmpty() ) {
			
			wordsToCompare = pair.getMapping().getProperty().getSynsets();
		}
		else {
			
			wordsToCompare = new HashSet<String>(Arrays.asList(pair.getMapping().getProperty().getLabel().split(" ")));
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
					
						this.logger.debug("Word not found: " + e);
				}
			}
		}
		pair.getPattern().getFeatures().put(FeatureFactory.getInstance().getFeature("WORDNET_DISTANCE"), similarity >= 0 ? similarity : 0);
	}
}
