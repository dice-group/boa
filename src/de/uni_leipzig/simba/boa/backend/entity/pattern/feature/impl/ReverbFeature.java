package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.queryParser.ParseException;

import de.danielgerber.math.MathUtil;
import de.danielgerber.string.StringUtil;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.Feature;
import de.uni_leipzig.simba.boa.backend.search.PatternSearcher;
import edu.washington.cs.knowitall.extractor.ReVerbExtractor;
import edu.washington.cs.knowitall.extractor.conf.ConfidenceFunctionException;
import edu.washington.cs.knowitall.extractor.conf.ReVerbConfFunction;
import edu.washington.cs.knowitall.nlp.ChunkedSentence;
import edu.washington.cs.knowitall.nlp.extraction.ChunkedBinaryExtraction;
import edu.washington.cs.knowitall.util.DefaultObjects;


public class ReverbFeature implements Feature {

	static {
		// this is a hack to load the training data for reverb
		DefaultObjects.setPath(NLPediaSettings.getInstance().getSetting("reverbTrainingDirectory"));
	}
	
	private boolean isInitialized = false;
	
	private ReVerbExtractor extractor;
	private ReVerbConfFunction scoreFunc;
	private PatternSearcher searcher;

	/**
	 * init the ReVerb-Toolkit
	 */
	public ReverbFeature() {}

	private void init() {
		
		try {
			
			searcher	= new PatternSearcher(NLPediaSettings.getInstance().getSetting("sentenceIndexDirectory"));
			extractor	= new ReVerbExtractor();
			scoreFunc	= new ReVerbConfFunction();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.isInitialized = true;
	}
	
	@Override
	public void score(List<PatternMapping> mappings) {

		// nothing to do here
	}
	
	@Override
	public void scoreMapping(PatternMapping mapping) {

		if ( !this.isInitialized ) this.init();
		
			for ( Pattern pattern : mapping.getPatterns()) {
				
				if ( !pattern.isUseForPatternEvaluation() ) continue;
				
				Set<Double> scores		= new HashSet<Double>();
				Set<String> relations	= new HashSet<String>();
				
				try {
					
					// for all sentences we found the pattern in
					for (String sentence : getReverbMeasureEvaluationSentences(pattern)) {
						
						try {
						
							// let ReVerb create the chunked sentences
							for (ChunkedSentence sent : DefaultObjects.getDefaultSentenceReader(new StringReader(sentence)).getSentences()) {
								
								// and extract all binary relations
								for (ChunkedBinaryExtraction extr : extractor.extract(sent)) {
		
									double score = scoreFunc.getConf(extr); 
									if ( !Double.isInfinite(score) && !Double.isNaN(score) ) {
										
										// we only want to add scores of relations, which are substring of our relations
										// to avoid relation like "is" to appear in strings like "?R? district of Kent , ?D?" look for " relation "
										if ( StringUtil.isSubstringOf(" " + extr.getRelation().toString() + " ", pattern.getNaturalLanguageRepresentation()) ) {
											
											scores.add(score);
											relations.add(extr.getRelation().toString());
										}
									}
								}
							}
						}
						catch (NullPointerException npe) {
							// TODO Auto-generated catch block
							npe.printStackTrace();
						}
						catch (ConfidenceFunctionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				double score = MathUtil.getAverage(scores);
				// update the pattern
				pattern.getFeatures().put(de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature.REVERB, score >= 0 ? score : 0); // -1 is not useful for confidence
				pattern.setGeneralizedPattern(StringUtil.getLongestSubstring(relations));
			}
	}

	/**
	 * Returns all sentences the pattern has been found in.
	 * 
	 * @param p the pattern
	 * @return a list of sentences containing the pattern
	 * @throws IOException
	 * @throws ParseException
	 */
	private List<String> getReverbMeasureEvaluationSentences(Pattern p) throws IOException, ParseException {

		return searcher.getSentences(p.retrieveLuceneDocIdsAsList());
	}
}
