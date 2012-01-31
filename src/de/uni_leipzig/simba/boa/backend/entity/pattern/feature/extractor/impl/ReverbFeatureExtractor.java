package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.impl;

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
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.AbstractFeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.FeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.featureextraction.FeatureExtractionPair;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
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
		DefaultObjects.setPath(NLPediaSettings.BOA_BASE_DIRECTORY + NLPediaSettings.getInstance().getSetting("reverbTrainingDirectory"));
	}
	
	private ReVerbExtractor extractor;
	private ReVerbConfFunction scoreFunc;
	private DefaultPatternSearcher searcher;
	
	private NLPediaLogger logger = new NLPediaLogger(ReverbFeatureExtractor.class);

	/**
	 * init the ReVerb-Toolkit
	 */
	public ReverbFeatureExtractor() {

	}
	
	@Override
	public void score(FeatureExtractionPair pair) {
	    
	    try {
            
            searcher    = new DefaultPatternSearcher();
            extractor   = new ReVerbExtractor();
            scoreFunc   = new ReVerbConfFunction();
        }
        catch (IOException e) {
            
            e.printStackTrace();
            String error = "Could not load ReVerb";
            logger.fatal(error, e);
            throw new RuntimeException(error, e);
        }

		Set<Double> scores		= new HashSet<Double>();
		Set<String> relations	= new HashSet<String>();
		
		try {
			
			// for all sentences we found the pattern in
			for (String sentence : getReverbMeasureEvaluationSentences(pair.getPattern())) {
				
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
                    
                    String error = "OpenNLP-NullPpinterException: \"" + pair.getPattern().getNaturalLanguageRepresentation() + "\"";
                    this.logger.error(error, e);
//                  throw new RuntimeException(error, e);
                }
			}
		}
		catch (ParseException e) {
			
            String error = "Could not parse query: \"" + pair.getPattern().getNaturalLanguageRepresentation() + "\"";
            this.logger.error(error, e);
            throw new RuntimeException(error, e);
		}
        catch (IOException e) {
            
            String error = "Reverb-IOException: \"" + pair.getPattern().getNaturalLanguageRepresentation() + "\"";
            this.logger.error(error, e);
            throw new RuntimeException(error, e);
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

	/**
	 * Returns all sentences the pattern has been found in.
	 * 
	 * @param p the pattern
	 * @return a list of sentences containing the pattern
	 * @throws IOException
	 * @throws ParseException
	 */
	private List<String> getReverbMeasureEvaluationSentences(Pattern p) throws IOException, ParseException {

		return searcher.getSentencesByIds(p.retrieveLuceneDocIdsAsList());
	}
}



//catch (ArrayIndexOutOfBoundsException aioobe) {
//
//this.logger.error(sentence, aioobe);
//}
//catch (IllegalArgumentException iae){
//
//this.logger.error(sentence, iae);
//}
//catch (NullPointerException npe) {
//
//this.logger.error(pattern.getNaturalLanguageRepresentation());
//this.logger.error(sentence);
//this.logger.error("There was a NullPointerException in reverbmeasure", npe);
////npe.printStackTrace();
//}