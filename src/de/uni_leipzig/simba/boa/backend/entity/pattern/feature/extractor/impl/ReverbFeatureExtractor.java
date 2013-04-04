package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import com.github.gerbsen.math.MathUtil;
import com.github.gerbsen.string.StringUtil;

import de.uni_leipzig.simba.boa.backend.concurrent.PatternMappingGeneralizedPatternPair;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.AbstractFeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl.Feature;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.search.impl.DefaultPatternSearcher;
import edu.washington.cs.knowitall.extractor.ReVerbExtractor;
import edu.washington.cs.knowitall.extractor.conf.ConfidenceFunctionException;
import edu.washington.cs.knowitall.extractor.conf.ReVerbConfFunction;
import edu.washington.cs.knowitall.nlp.OpenNlpSentenceChunker;
import edu.washington.cs.knowitall.nlp.extraction.ChunkedBinaryExtraction;
import edu.washington.cs.knowitall.util.DefaultObjects;


public class ReverbFeatureExtractor extends AbstractFeatureExtractor {

	static {
		// this is a hack to load the training data for reverb
		DefaultObjects.setPath(NLPediaSettings.BOA_BASE_DIRECTORY + NLPediaSettings.getSetting("reverbTrainingDirectory"));
	}
	
	private ReVerbExtractor extractor;
	private ReVerbConfFunction scoreFunc;
	private OpenNlpSentenceChunker chunker;
	
	private NLPediaLogger logger                = new NLPediaLogger(ReverbFeatureExtractor.class);
    private int maxNumberOfEvaluationSentences  = NLPediaSettings.getIntegerSetting("reverbTrainingSentences");
    private boolean initialized = false;
    DefaultPatternSearcher patternSearcher = new DefaultPatternSearcher();

	@Override
	public void score(PatternMappingGeneralizedPatternPair pair) {
	    
		if ( !NLPediaSettings.BOA_LANGUAGE.equals("en") ) return;
	    if ( !this.initialized ) this.init();
	    
	    SummaryStatistics reverbStat = new SummaryStatistics();
	    
	    for ( Pattern pattern : pair.getGeneralizedPattern().getPatterns() ) {
	    	
	    	Set<Double> scores    = new HashSet<Double>();
			Set<String> relations = new HashSet<String>();
			
			// for all sentences we found the pattern in
			for (String sentence : patternSearcher.getSentencesWithLimit(pattern.getFoundInSentences(), maxNumberOfEvaluationSentences)) {
				
				try {
				
					// and extract all binary relations
					for (ChunkedBinaryExtraction extr : extractor.extract(chunker.chunkSentence(sentence))) {

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
				catch (ConfidenceFunctionException e) {
					
				    String error = "Reverb-ConfidenceFuntionException: \"" + pattern.getNaturalLanguageRepresentation() + "\"";
	                this.logger.error(error, e);
	                throw new RuntimeException(error, e);
				}
				catch (NullPointerException e) {
	                
	                String error = "OpenNLP-NullPointerException: \"" + pattern.getNaturalLanguageRepresentation() + "\" and sentence: " + sentence;
	                this.logger.error(error, e);
	            }
				catch (Exception e) {
				    
				    String error = "Unknown-Exception: \"" + pattern.getNaturalLanguageRepresentation() + "\"" + "\" and sentence: " + sentence;
	                this.logger.fatal(error, e);
				}
			}
			
			// update the pattern
			double avgScore = MathUtil.getAverage(scores);
			setValue(pattern, "REVERB", avgScore >= 0 ? avgScore : 0, reverbStat); // -1 is not useful for confidence
			pattern.setGeneralizedPattern(StringUtil.getLongestSubstring(relations));
	    }
	    
	    Map<Feature,Double> features = pair.getGeneralizedPattern().getFeatures();
		features.put(FeatureFactory.getInstance().getFeature("REVERB"), reverbStat.getMean());
	}
	
	public void close() {
		
		if ( this.patternSearcher != null ) this.patternSearcher.close();
		this.extractor = null;
		this.scoreFunc = null;
		this.chunker = null;
	}

    private void init() {

        try {
            
            extractor   = new ReVerbExtractor();
            scoreFunc   = new ReVerbConfFunction();
            chunker     = new OpenNlpSentenceChunker();
        }
        catch (IOException e) {
            
            e.printStackTrace();
            String error = "Could not load ReVerb";
            logger.fatal(error, e);
            throw new RuntimeException(error, e);
        }
        
        this.initialized = true;
    }
}