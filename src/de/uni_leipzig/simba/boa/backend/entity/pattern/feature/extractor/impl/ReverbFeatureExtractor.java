package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;

import de.danielgerber.math.MathUtil;
import de.danielgerber.string.StringUtil;
import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.concurrent.PatternMappingPatternPair;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.AbstractFeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.FeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.lucene.LuceneIndexHelper;
import de.uni_leipzig.simba.boa.backend.search.impl.DefaultPatternSearcher;
import edu.washington.cs.knowitall.extractor.ReVerbExtractor;
import edu.washington.cs.knowitall.extractor.SentenceExtractor;
import edu.washington.cs.knowitall.extractor.conf.ConfidenceFunctionException;
import edu.washington.cs.knowitall.extractor.conf.ReVerbConfFunction;
import edu.washington.cs.knowitall.extractor.mapper.BracketsRemover;
import edu.washington.cs.knowitall.extractor.mapper.SentenceEndFilter;
import edu.washington.cs.knowitall.extractor.mapper.SentenceLengthFilter;
import edu.washington.cs.knowitall.extractor.mapper.SentenceStartFilter;
import edu.washington.cs.knowitall.nlp.ChunkedSentence;
import edu.washington.cs.knowitall.nlp.ChunkedSentenceReader;
import edu.washington.cs.knowitall.nlp.extraction.ChunkedBinaryExtraction;
import edu.washington.cs.knowitall.util.DefaultObjects;


public class ReverbFeatureExtractor extends AbstractFeatureExtractor {

	static {
		// this is a hack to load the training data for reverb
		DefaultObjects.setPath(NLPediaSettings.BOA_BASE_DIRECTORY + NLPediaSettings.getSetting("reverbTrainingDirectory"));
	}
	
	private ReVerbExtractor extractor;
	private ReVerbConfFunction scoreFunc;
	
	private NLPediaLogger logger                = new NLPediaLogger(ReverbFeatureExtractor.class);
    private int maxNumberOfEvaluationSentences  = 100;
    private IndexSearcher searcher;

	/**
	 * init the ReVerb-Toolkit
	 */
	public ReverbFeatureExtractor() {

	    try {
            
	        searcher    = LuceneIndexHelper.getIndexSearcher(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.INDEX_CORPUS_PATH);
            extractor   = new ReVerbExtractor();
            scoreFunc   = new ReVerbConfFunction();
        }
        catch (IOException e) {
            
            e.printStackTrace();
            String error = "Could not load ReVerb";
            logger.fatal(error, e);
            throw new RuntimeException(error, e);
        }
	}
	
	@Override
	public void score(PatternMappingPatternPair pair) {
	    
		Set<Double> scores    = new HashSet<Double>();
		Set<String> relations = new HashSet<String>();
		
		List<String> sentences = LuceneIndexHelper.getFieldValueByIds(this.searcher, pair.getPattern().getFoundInSentences(), "sentence");
		sentences = sentences.size() >= this.maxNumberOfEvaluationSentences  ? sentences.subList(0, this.maxNumberOfEvaluationSentences) : sentences;
		
		// for all sentences we found the pattern in
		for (String sentence : sentences) {
			
			try {
			
				// let ReVerb create the chunked sentences
				for (ChunkedSentence sent : this.createDefaultSentenceReader(sentence).getSentences()) {
					
					// and extract all binary relations
					for (ChunkedBinaryExtraction extr : extractor.extract(sent)) {

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
			}
			catch (ConfidenceFunctionException e) {
				
			    String error = "Reverb-ConfidenceFuntionException: \"" + pair.getPattern().getNaturalLanguageRepresentation() + "\"";
                this.logger.error(error, e);
                throw new RuntimeException(error, e);
			}
            catch (IOException e) {
                
                String error = "Reverb-IOException: \"" + pair.getPattern().getNaturalLanguageRepresentation() + "\"";
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

	private ChunkedSentenceReader createDefaultSentenceReader(String sentence) throws IOException {

		SentenceExtractor extractor = new SentenceExtractor();
		extractor.addMapper(new BracketsRemover());
		extractor.addMapper(new SentenceEndFilter());
		extractor.addMapper(new SentenceStartFilter());
		extractor.addMapper(SentenceLengthFilter.minFilter(4));
		ChunkedSentenceReader reader = new ChunkedSentenceReader(new StringReader(sentence), extractor);
		return reader;
	}
}