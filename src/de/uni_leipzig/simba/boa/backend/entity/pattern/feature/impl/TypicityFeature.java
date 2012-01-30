/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.context.Context;
import de.uni_leipzig.simba.boa.backend.entity.context.LeftContext;
import de.uni_leipzig.simba.boa.backend.entity.context.RightContext;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.Feature;
import de.uni_leipzig.simba.boa.backend.featureextraction.FeatureExtractionPair;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.NaturalLanguageProcessingToolFactory;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.namedentityrecognition.NamedEntityRecognition;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.namedentityrecognition.impl.StanfordNLPNamedEntityRecognition;
import de.uni_leipzig.simba.boa.backend.search.impl.DefaultPatternSearcher;
import de.uni_leipzig.simba.boa.backend.util.TimeUtil;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;

/**
 * @author Daniel Gerber
 *
 */
public class TypicityFeature implements Feature {

	private final NLPediaLogger logger					= new NLPediaLogger(TypicityFeature.class);
	private NamedEntityRecognition ner;
	private final int maxNumberOfEvaluationSentences 	= Integer.valueOf(NLPediaSettings.getInstance().getSetting("maxNumberOfTypicityConfidenceMeasureDocuments"));
	
	private DefaultPatternSearcher patternSearcher;
	private static final Map<String,String> BRACKETS = new HashMap<String,String>();
	static {
		
		BRACKETS.put("-LRB-", "(");
		BRACKETS.put("-RRB-", ")");
		BRACKETS.put("-LQB-", "{");
		BRACKETS.put("-RQB-", "}");
	}
	
	// used for sentence segmentation
	private Reader stringReader;
	private DocumentPreprocessor preprocessor;
	private StringBuilder stringBuilder;
	
	public TypicityFeature() {}
	
	/* (non-Javadoc)
	 * @see simba.nlpedia.entity.pattern.evaluation.PatternEvaluator#evaluatePattern(simba.nlpedia.entity.pattern.PatternMapping)
	 */
	@Override
	public void score(FeatureExtractionPair pair) {
		
		long start = new Date().getTime();
		
		String domainUri	= pair.getMapping().getProperty().getRdfsDomain();
		String rangeUri		= pair.getMapping().getProperty().getRdfsRange();
		
		// load the named entity tool and the pattern searcher as late as possible
		if ( this.ner == null ) this.ner = NaturalLanguageProcessingToolFactory.getInstance().
			createNamedEntityRecognition(StanfordNLPNamedEntityRecognition.class);
		if ( this.patternSearcher == null ) this.patternSearcher = new DefaultPatternSearcher();

	    Pattern pattern = pair.getPattern();
	    
		boolean beginsWithDomain = pattern.isDomainFirst();
		String patternWithOutVariables = this.segmentString(pattern.getNaturalLanguageRepresentationWithoutVariables());
		
		final List<String> sentences = new ArrayList<String>(patternSearcher.getExactMatchSentences(patternWithOutVariables, maxNumberOfEvaluationSentences));
		List<String> sentencesToEvaluate = sentences.size() >= this.maxNumberOfEvaluationSentences ? sentences.subList(0, this.maxNumberOfEvaluationSentences) : sentences;
		
		double correctDomain	= 0;
		double correctRange		= 0;
		
		// go through all sentences which were found in the index containing the pattern
		for (String foundString : sentencesToEvaluate) {
			
		    // left of the pattern can't be any named entity
			if ( foundString.toLowerCase().startsWith(patternWithOutVariables.toLowerCase())) continue;
			
			String nerTagged = this.ner.getAnnotatedString(this.replaceBrackets(foundString));
			String segmentedPattern = this.segmentString(patternWithOutVariables);
			
			if ( nerTagged != null && foundString != null && segmentedPattern != null &&
					nerTagged.length() > 0 && foundString.length() > 0 && segmentedPattern.length() > 0 ) {
				
				Context leftContext = new LeftContext(nerTagged, foundString, segmentedPattern);
				Context rightContext = new RightContext(nerTagged, foundString, segmentedPattern);
				
				// to the left of the pattern is the domain resource, to the right the range resource
				if ( beginsWithDomain ) {
					
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
	
		double domainCorrectness = (double) correctDomain / (double) sentencesToEvaluate.size();
		double rangeCorrectness = (double) correctRange / (double) sentencesToEvaluate.size();
		double typicity = ((domainCorrectness + rangeCorrectness) / 2) * Math.log(sentences.size() + 1);
		
		pattern.getFeatures().put(de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature.TYPICITY_CORRECT_DOMAIN_NUMBER, domainCorrectness >= 0 ? domainCorrectness : 0);
		pattern.getFeatures().put(de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature.TYPICITY_CORRECT_RANGE_NUMBER, rangeCorrectness >= 0 ? rangeCorrectness : 0);
		pattern.getFeatures().put(de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature.TYPICITY_SENTENCES, Math.log(sentences.size() + 1)  >= 0 ? Math.log(sentences.size() + 1) : 0);
		pattern.getFeatures().put(de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature.TYPICITY, typicity >= 0 ? typicity : 0 );
		
		this.logger.debug("Typicity feature for " + pair.getMapping().getProperty().getLabel() + "/\"" + pattern.getNaturalLanguageRepresentation() + "\"  finished in " + TimeUtil.convertMilliSeconds((new Date().getTime() - start)) + ".");
	}
	
	/**
	 * Replaces the abbreviations from Lucene with regular brackets.
	 * This is done to improve POS-Tag quality.
	 * 
	 * @param foundString
	 * @return
	 */
	private String replaceBrackets(String foundString) {

		for (Map.Entry<String, String> bracket : TypicityFeature.BRACKETS.entrySet()) {
			
			if ( foundString.contains(bracket.getKey())) {
	
				foundString = foundString.replace(bracket.getKey(), bracket.getValue());
			}
		}
		return foundString;
	}
	
	/**
	 * TODO export to library
	 * 
	 * @param sentence
	 * @return
	 */
	private String segmentString(String sentence) {
		
		try {
			
			this.stringReader = new StringReader(sentence);
			this.preprocessor = new DocumentPreprocessor(stringReader,  DocumentPreprocessor.DocType.Plain);
			
			Iterator<List<HasWord>> iter = this.preprocessor.iterator();
			while ( iter.hasNext() ) {
				
				stringBuilder = new StringBuilder();
				
				for ( HasWord word : iter.next() ) {
					stringBuilder.append(word.toString() + " ");
				}
				return stringBuilder.toString().trim();
			}
		}
		catch (ArrayIndexOutOfBoundsException aioobe) {
			
			logger.debug("Could not segment string...", aioobe);
		}
		return "";
	}
}
