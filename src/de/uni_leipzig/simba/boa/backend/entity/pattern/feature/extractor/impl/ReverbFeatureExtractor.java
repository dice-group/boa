package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.search.IndexSearcher;

import com.github.gerbsen.math.MathUtil;
import com.github.gerbsen.string.StringUtil;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.concurrent.PatternMappingPatternPair;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.AbstractFeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.lucene.LuceneIndexHelper;
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
	public void score(PatternMappingPatternPair pair) {
	    
		if ( !NLPediaSettings.BOA_LANGUAGE.equals("en") ) return;
	    if ( !this.initialized ) this.init();
	    
		Set<Double> scores    = new HashSet<Double>();
		Set<String> relations = new HashSet<String>();
		
		// for all sentences we found the pattern in
		for (String sentence : patternSearcher.getSentencesWithLimit(pair.getPattern().getFoundInSentences(), maxNumberOfEvaluationSentences)) {
			
			try {
			
				// and extract all binary relations
				for (ChunkedBinaryExtraction extr : extractor.extract(chunker.chunkSentence(sentence))) {

					double score = scoreFunc.getConf(extr);
					if ( !Double.isInfinite(score) && !Double.isNaN(score) ) {
					    
						// we only want to add scores of relations, which are substring of our relations
						// to avoid relation like "is" to appear in strings like "?R? district of Kent , ?D?" look for " relation "
						if ( StringUtil.isSubstringOf(" " + extr.getRelation().toString() + " ", pair.getPattern().getNaturalLanguageRepresentation()) ) {
							
							scores.add(score);
							relations.add(extr.getRelation().toString());
						}
					}
				}
			}
			catch (ConfidenceFunctionException e) {
				
			    String error = "Reverb-ConfidenceFuntionException: \"" + pair.getPattern().getNaturalLanguageRepresentation() + "\"";
                this.logger.error(error, e);
                throw new RuntimeException(error, e);
			}
			catch (NullPointerException e) {
                
                String error = "OpenNLP-NullPointerException: \"" + pair.getPattern().getNaturalLanguageRepresentation() + "\" and sentence: " + sentence;
                this.logger.error(error, e);
            }
			catch (Exception e) {
			    
			    String error = "Unknown-Exception: \"" + pair.getPattern().getNaturalLanguageRepresentation() + "\"" + "\" and sentence: " + sentence;
                this.logger.fatal(error, e);
			}
		}
		
		double score = MathUtil.getAverage(scores);
		// update the pattern
		pair.getPattern().getFeatures().put(FeatureFactory.getInstance().getFeature("REVERB"), score >= 0 ? score : 0); // -1 is not useful for confidence
		pair.getPattern().setGeneralizedPattern(StringUtil.getLongestSubstring(relations));
	}
	
	public void close() {
		
		this.patternSearcher.close();
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