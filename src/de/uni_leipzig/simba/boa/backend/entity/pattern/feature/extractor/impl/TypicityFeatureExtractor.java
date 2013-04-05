/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.impl;

import java.util.Date;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import de.uni_leipzig.simba.boa.backend.concurrent.PatternMappingGeneralizedPatternPair;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.context.Context;
import de.uni_leipzig.simba.boa.backend.entity.context.LeftContext;
import de.uni_leipzig.simba.boa.backend.entity.context.RightContext;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.AbstractFeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl.Feature;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.search.impl.DefaultPatternSearcher;
import de.uni_leipzig.simba.boa.backend.util.TimeUtil;

/**
 * @author Daniel Gerber
 *
 */
public class TypicityFeatureExtractor extends AbstractFeatureExtractor {

	private final NLPediaLogger logger					= new NLPediaLogger(TypicityFeatureExtractor.class);
	private final int maxNumberOfEvaluationSentences 	= NLPediaSettings.getIntegerSetting("maxNumberOfTypicityConfidenceMeasureDocuments");
	
	private DefaultPatternSearcher patternSearcher;
	
	/* (non-Javadoc)
	 * @see simba.nlpedia.entity.pattern.evaluation.PatternEvaluator#evaluatePattern(simba.nlpedia.entity.pattern.PatternMapping)
	 */
	@Override
	public void score(PatternMappingGeneralizedPatternPair pair) {
		
		long start = new Date().getTime();
		
		String domainUri	= pair.getMapping().getProperty().getRdfsDomain();
		String rangeUri		= pair.getMapping().getProperty().getRdfsRange();
		
		// load the named entity tool and the pattern searcher as late as possible
		if ( this.patternSearcher == null ) this.patternSearcher = new DefaultPatternSearcher();
		
		SummaryStatistics domainStat	= new SummaryStatistics();
		SummaryStatistics rangeStat		= new SummaryStatistics();
		SummaryStatistics sentencesStat = new SummaryStatistics();
		SummaryStatistics typicityStat	= new SummaryStatistics();

		for ( Pattern pattern : pair.getGeneralizedPattern().getPatterns() ) {

			String patternWithOutVariables = pattern.getNaturalLanguageRepresentationWithoutVariables();
			
			Map<String,String> sentences = patternSearcher.getExactMatchSentencesTagged(patternWithOutVariables, maxNumberOfEvaluationSentences);
			
			double correctDomain  = 0;
			double correctRange   = 0;
			int sentenceCount     = sentences.size();
			
			// go through all sentences which were found in the index containing the pattern
			for (Map.Entry<String,String> entry : sentences.entrySet()) {
				
			    // left of the pattern can't be any named entity
				if ( entry.getKey().toLowerCase().startsWith(patternWithOutVariables.toLowerCase())) continue;
				
				String nerTagged = entry.getValue();
				String sentence	 = entry.getKey();
				
				if ( nerTagged != null && sentence != null && patternWithOutVariables != null &&
						nerTagged.length() > 0 && sentence.length() > 0 && patternWithOutVariables.length() > 0 ) {
					
					Context leftContext     = this.createContext(LeftContext.class, nerTagged, sentence, patternWithOutVariables);
					Context rightContext    = this.createContext(RightContext.class, nerTagged, sentence, patternWithOutVariables);
					
					// there are sentence which can not be parsed
					if ( leftContext == null || rightContext == null ) {
					    
					    sentenceCount--; // we don't want to include broken sentences in the statistics
					    continue;
					}
					
					// to the left of the pattern is the domain resource, to the right the range resource
					if ( pattern.isDomainFirst() ) {
						
						if ( leftContext.containsSuitableEntity(domainUri) ) correctDomain++;
						if ( rightContext.containsSuitableEntity(rangeUri) ) correctRange++; 
					}
					// vice versa
					else {
						
						if ( leftContext.containsSuitableEntity(rangeUri) ) correctRange++;
						if ( rightContext.containsSuitableEntity(domainUri) ) correctDomain++;
					}
				}
			}
		
			double domainCorrectness = (double) correctDomain / (double) sentenceCount;
			double rangeCorrectness = (double) correctRange / (double) sentenceCount;
			double typicity = ((domainCorrectness + rangeCorrectness) / 2) * Math.log(sentenceCount + 1);
			
			setValue(pattern, "TYPICITY_CORRECT_DOMAIN_NUMBER", domainCorrectness >= 0 ? domainCorrectness : 0, domainStat);
			setValue(pattern, "TYPICITY_CORRECT_RANGE_NUMBER", rangeCorrectness >= 0 ? rangeCorrectness : 0, rangeStat);
			setValue(pattern, "TYPICITY_SENTENCES", Math.log(sentenceCount + 1)  >= 0 ? Math.log(sentenceCount + 1) : 0, sentencesStat);
			setValue(pattern, "TYPICITY", typicity >= 0 ? typicity : 0, typicityStat);
			
			this.logger.debug("Typicity feature for " + pair.getMapping().getProperty().getLabel() + "/\"" + pattern.getNaturalLanguageRepresentation() + "\"  finished in " + TimeUtil.convertMilliSeconds((new Date().getTime() - start)) + ".");
		}
		
		Map<Feature,Double> features = pair.getGeneralizedPattern().getFeatures();
		features.put(FeatureFactory.getInstance().getFeature("TYPICITY_CORRECT_DOMAIN_NUMBER"), domainStat.getMean());
		features.put(FeatureFactory.getInstance().getFeature("TYPICITY_CORRECT_RANGE_NUMBER"), rangeStat.getMean());
		features.put(FeatureFactory.getInstance().getFeature("TYPICITY_SENTENCES"), sentencesStat.getMean());
		features.put(FeatureFactory.getInstance().getFeature("TYPICITY"), typicityStat.getMean());
	}
	
	/**
	 * 
	 * @param clazz
	 * @param nerTagged
	 * @param foundString
	 * @param segmentedPattern
	 * @return
	 */
	private Context createContext(Class<? extends Context> clazz, String nerTagged, String foundString, String segmentedPattern) {

	    Context context = null;
	    
	    try {
	        
	        if ( clazz.equals(LeftContext.class) ) context = new LeftContext(nerTagged, foundString, segmentedPattern);
	        else {
	            
	            if ( clazz.equals(RightContext.class) ) context = new RightContext(nerTagged, foundString, segmentedPattern);
	            else {
	                
	                throw new RuntimeException("Not appropriate class given: " + clazz);
	            }
	        }
	    }
	    catch (java.lang.IllegalArgumentException e) {
	        
	        this.logger.debug("Could not create context!", e);
	    }
	    catch (java.lang.StringIndexOutOfBoundsException e) {
	        
	        this.logger.debug("Could not create context!", e);
	    }
	    catch (java.lang.ArrayIndexOutOfBoundsException e) {
	        
	        this.logger.debug("Could not create context!", e);
	    }
	    return context;
    }

	public void close() {
		
		this.patternSearcher.close();
	}
}
