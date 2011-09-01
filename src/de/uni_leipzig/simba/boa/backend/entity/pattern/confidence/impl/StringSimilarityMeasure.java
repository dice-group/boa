package de.uni_leipzig.simba.boa.backend.entity.pattern.confidence.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.ListUtils;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.QGramsDistance;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.confidence.ConfidenceMeasure;
import de.uni_leipzig.simba.boa.backend.search.PatternSearcher;
import de.uni_leipzig.simba.boa.backend.wordnet.similarity.SimilarityAssessor;
import de.uni_leipzig.simba.boa.backend.wordnet.similarity.WordNotFoundException;


public class StringSimilarityMeasure implements ConfidenceMeasure {

	private SimilarityAssessor similarityAssessor = new SimilarityAssessor();
	
	@Override
	public void measureConfidence(PatternMapping mapping) {

		// we calculate the qgram distance between the NLR and the label of the property
		for ( Pattern pattern : mapping.getPatterns() ) {
			
			if ( pattern.isUseForPatternEvaluation() ) {
				
				// get the NLR and remove all stopwords
				String naturalLanguageRepresentation = pattern.retrieveNaturalLanguageRepresentationWithoutVariables();
				Set<String> tokens = new HashSet<String>(Arrays.asList(naturalLanguageRepresentation.split(" ")));
				tokens.removeAll(PatternSearcher.STOP_WORDS);
				
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
					
					System.out.println("token: " + token);

					for ( String wordFrom : wordsToCompare ) {
					
						System.out.println("\twordform: " + wordFrom);
						try {
							
							similarity = Math.max(similarity, this.similarityAssessor.getSimilarity(wordFrom, token));
							System.out.println("\tsimilarity: " + similarity);
						}
						catch (WordNotFoundException e) {
							
							System.out.println("Word not found: " + e);
						}
					}
				}
				pattern.setSimilarity(similarity);
			}
//			AbstractStringMetric metric = new QGramsDistance();
//			pattern.setSimilarity(
//					Double.valueOf(
//							metric.getSimilarity(
//									pattern.retrieveNaturalLanguageRepresentationWithoutVariables(), 
//									mapping.getProperty().getLabel())));
		}
	}
}
