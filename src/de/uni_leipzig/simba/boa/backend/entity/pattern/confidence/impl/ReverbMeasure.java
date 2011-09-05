package de.uni_leipzig.simba.boa.backend.entity.pattern.confidence.impl;

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
import de.uni_leipzig.simba.boa.backend.entity.pattern.confidence.ConfidenceMeasure;
import de.uni_leipzig.simba.boa.backend.search.PatternSearcher;
import edu.washington.cs.knowitall.extractor.ReVerbExtractor;
import edu.washington.cs.knowitall.extractor.conf.ConfidenceFunctionException;
import edu.washington.cs.knowitall.extractor.conf.ReVerbConfFunction;
import edu.washington.cs.knowitall.nlp.ChunkedSentence;
import edu.washington.cs.knowitall.nlp.extraction.ChunkedBinaryExtraction;
import edu.washington.cs.knowitall.util.DefaultObjects;

public class ReverbMeasure implements ConfidenceMeasure {

	static {
		// this is a hack to load the training data for reverb
		DefaultObjects.setPath(NLPediaSettings.getInstance().getSetting("reverbTrainingDirectory"));
	}
	
	private ReVerbExtractor extractor;
	private ReVerbConfFunction scoreFunc;
	
	private PatternSearcher searcher;

	/**
	 * init the ReVerb-Toolkit
	 */
	public ReverbMeasure() {

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
	}

	@Override
	public void measureConfidence(PatternMapping mapping) {

		try {
			
			for ( Pattern p : mapping.getPatterns()) {
				
				if ( !p.isUseForPatternEvaluation() ) continue;

				Set<Double> scores		= new HashSet<Double>();
				Set<String> relations	= new HashSet<String>();
				
				// for all sentences we found the pattern in
				for (String sentence : getReverbMeasureEvaluationSentences(p)) {
					
					// let ReVerb create the chunked sentences
					for (ChunkedSentence sent : DefaultObjects.getDefaultSentenceReader(new StringReader(sentence)).getSentences()) {
						
						// and extract all binary relations
						for (ChunkedBinaryExtraction extr : extractor.extract(sent)) {
	
							// we only want to add scores of relations, which are substring of our relations
							if ( StringUtil.isSubstringOf(extr.getRelation().toString(), p.getNaturalLanguageRepresentation()) ) {
	
								scores.add(scoreFunc.getConf(extr));
								relations.add(extr.getRelation().toString());
							}
						}
					}
				}
				// update the pattern
				p.setReverb(MathUtil.getAverage(scores));
				p.setGeneralizedPattern(StringUtil.getLongestSubstring(relations));
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ConfidenceFunctionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
