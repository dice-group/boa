/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.entity.pattern.confidence.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.context.Context;
import de.uni_leipzig.simba.boa.backend.entity.context.LeftContext;
import de.uni_leipzig.simba.boa.backend.entity.context.RightContext;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.confidence.ConfidenceMeasure;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.nlp.NamedEntityRecognizer;
import de.uni_leipzig.simba.boa.backend.search.PatternSearcher;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;

/**
 * @author Daniel Gerber
 *
 */
public class TypicityMeasure implements ConfidenceMeasure {

	private final NLPediaLogger logger					= new NLPediaLogger(TypicityMeasure.class);
	private NamedEntityRecognizer ner;
	private final int maxNumberOfEvaluationSentences 	= Integer.valueOf(NLPediaSettings.getInstance().getSetting("maxNumberOfTypicityConfidenceMeasureDocuments"));
	
	private PatternSearcher patternSearcher;
	
	// used for sentence segmentation
	private Reader stringReader;
	private DocumentPreprocessor preprocessor;
	private StringBuilder stringBuilder;
	
	public TypicityMeasure() {}
	
	/* (non-Javadoc)
	 * @see simba.nlpedia.entity.pattern.evaluation.PatternEvaluator#evaluatePattern(simba.nlpedia.entity.pattern.PatternMapping)
	 */
	@Override
	public void measureConfidence(PatternMapping patternMapping) {
		
		long start = new Date().getTime();
		
		try {
			
			this.patternSearcher = new PatternSearcher(NLPediaSettings.getInstance().getSetting("sentenceIndexDirectory"));
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			
			String domainUri	= patternMapping.getProperty().getRdfsDomain();
			String rangeUri		= patternMapping.getProperty().getRdfsRange();
			
			double domainCorrectness;
			double rangeCorrectness;
			
			String nerTagged;
			String segmentedFoundString;
			String segmentedPattern;
			
			Context leftContext;
			Context rightContext;
			
			if ( this.ner == null ) this.ner = new NamedEntityRecognizer();
			
			for (Pattern pattern : patternMapping.getPatterns()) {
				
				if ( !pattern.isUseForPatternEvaluation() ) continue;
				
				boolean beginsWithDomain = pattern.getNaturalLanguageRepresentation().startsWith("?D?") ? true : false;
				String patternWithOutVariables = this.segmentString(pattern.getNaturalLanguageRepresentation().substring(0, pattern.getNaturalLanguageRepresentation().length() - 3).substring(3).trim());
						
				List<String> sentences = new ArrayList<String>(patternSearcher.getExactMatchSentences(patternWithOutVariables, maxNumberOfEvaluationSentences));//.getSentencesWithString(patternWithOutVariables, this.maxNumberOfDocuments));
				
				double correctDomain	= 0;
				double correctRange		= 0;
				
				for (String foundString : sentences.size() >= this.maxNumberOfEvaluationSentences ? sentences.subList(0,this.maxNumberOfEvaluationSentences - 1) : sentences) {
					
					nerTagged = this.ner.recognizeEntitiesInString(foundString);
					segmentedFoundString = this.segmentString(foundString);
					segmentedPattern = this.segmentString(patternWithOutVariables);
					
					try {

						leftContext = new LeftContext(nerTagged, segmentedFoundString, segmentedPattern);
						rightContext = new RightContext(nerTagged, segmentedFoundString, segmentedPattern);
						
						if ( beginsWithDomain ) {
							
							if ( leftContext.containsSuitableEntity(domainUri) ) {
								
								correctDomain += (1D / (double)leftContext.getSuitableEntityDistance(domainUri));
							}
							if ( rightContext.containsSuitableEntity(rangeUri) ) {
								
								correctRange += (1D / (double)rightContext.getSuitableEntityDistance(rangeUri));
							}
						}
						else {
							
							if ( leftContext.containsSuitableEntity(rangeUri) ) {
								
								correctRange += (1D / (double)leftContext.getSuitableEntityDistance(rangeUri));
							}
							if ( rightContext.containsSuitableEntity(domainUri) ) {
								
								correctDomain += (1D / (double)rightContext.getSuitableEntityDistance(domainUri));
							}
						}
					}
					catch ( IndexOutOfBoundsException ioob ) {
						//ioob.printStackTrace();
						this.logger.error("Could not create context for string " + segmentedFoundString + ". NER tagged: " + nerTagged + " pattern: "  + patternWithOutVariables);
					}
				}
				
				domainCorrectness = (double) correctDomain / (double) sentences.size();
				rangeCorrectness = (double) correctRange / (double) sentences.size();
				
				double typicity = 0D;
				
				typicity = (domainCorrectness + rangeCorrectness) / (2D);//* (double) sentences.size());
				typicity = Double.isNaN(typicity) ? 0d : typicity * (double) (Math.log((int)(sentences.size() + 1)) / Math.log(2));
				
				pattern.setTypicity(typicity);
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
		System.out.println("Typicity measuring for pattern_mapping: " + patternMapping.getProperty().getUri() + " finished in " + (new Date().getTime() - start) + "ms.");
	}

	private String segmentString(String sentence) {
		
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
		return "";
	}
}
