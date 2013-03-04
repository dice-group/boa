package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.concurrent.PatternMappingPatternPair;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.AbstractFeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.wordnet.similarity.SimilarityAssessor;
import de.uni_leipzig.simba.boa.backend.wordnet.similarity.WordNotFoundException;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.WordTag;
import edu.stanford.nlp.process.Morphology;


public class StringSimilarityFeatureExtractor extends AbstractFeatureExtractor {

	private SimilarityAssessor similarityAssessor = null;
	private NLPediaLogger logger = new NLPediaLogger(StringSimilarityFeatureExtractor.class);
	
	private Morphology lemmatizer = new Morphology();
	
	@Override
	public void score(PatternMappingPatternPair pair) {

	    if ( this.similarityAssessor == null ) this.similarityAssessor = new SimilarityAssessor();
		// we calculate the qgram distance between the NLR and the label of the property
			
		// get the NLR and remove all stopwords
		String naturalLanguageRepresentation = pair.getPattern().getNaturalLanguageRepresentationWithoutVariables();
		List<String> tokens = new ArrayList<String>(Arrays.asList(naturalLanguageRepresentation.split(" ")));
		
		double similarity = 0D;
		
		Set<String> wordsToCompare;
		
		if ( !pair.getMapping().getProperty().getSynsets().isEmpty() ) {
			
			wordsToCompare = pair.getMapping().getProperty().getSynsets();
		}
		else {
			
			wordsToCompare = new HashSet<String>(Arrays.asList(pair.getMapping().getProperty().getLabel().split(" ")));
		}
		
		String[] posTags = pair.getPattern().getPosTaggedString().split(" ");
		
		if ( tokens.size() == posTags.length ) {

			// go through all words and synset combination and sum up the similarity
			for ( int i = 0; i < tokens.size() ; i++ ) {
				
				for ( String wordForm : wordsToCompare ) {
				
					// no need to calculate sim for stop words
					if ( Constants.STOP_WORDS.contains(tokens.get(i)) ) continue;
					
					// remove the annoying output from the command line :(
					PrintStream standardErrorStream = System.err;
		            System.setErr(new PrintStream(new ByteArrayOutputStream()));

		            String token = this.lemmatizer.lemmatize(new WordTag(tokens.get(i), posTags[i])).lemma();
					String wordFormLemmaNoun = this.lemmatizer.lemmatize(new WordTag(wordForm, "NN")).lemma();
					String wordFormLemmaVerbGerund = this.lemmatizer.lemmatize(new WordTag(wordForm, "VBG")).lemma();
					String wordFormLemmaVerbSingular = this.lemmatizer.lemmatize(new WordTag(wordForm, "VBZ")).lemma();
		            
		            // revert to original standard error stream
		            System.setErr(standardErrorStream);

					double simNoun = 0D, simGer = 0D, simSing = 0;
					
					try {
						
						simNoun = this.similarityAssessor.getSimilarity(wordFormLemmaNoun, token);
					}
					catch (WordNotFoundException e) {}
					try {
						
						simGer = this.similarityAssessor.getSimilarity(wordFormLemmaVerbGerund, token);
					}
					catch (WordNotFoundException e) {}
					try {
						
						simSing = this.similarityAssessor.getSimilarity(wordFormLemmaVerbSingular, token);
					}
					catch (WordNotFoundException e) {}
					
					double sim =  Math.max(simSing, Math.max(simNoun, simGer));
					
					if ( !Double.isInfinite(sim) && !Double.isNaN(sim) ) {
						
						similarity = Math.max(similarity, sim);
					}
				}
			}
		}
		pair.getPattern().getFeatures().put(FeatureFactory.getInstance().getFeature("WORDNET_DISTANCE"), similarity >= 0 ? similarity : 0);
	}
}
