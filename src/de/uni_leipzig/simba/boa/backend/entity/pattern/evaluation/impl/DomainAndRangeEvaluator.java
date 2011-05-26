/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.entity.pattern.evaluation.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.lucene.queryParser.ParseException;

import de.uni_leipzig.simba.boa.backend.configuration.Initializeable;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.entity.context.Context;
import de.uni_leipzig.simba.boa.backend.entity.context.LeftContext;
import de.uni_leipzig.simba.boa.backend.entity.context.RightContext;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.evaluation.PatternEvaluator;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.nlp.NamedEntityRecognizer;
import de.uni_leipzig.simba.boa.backend.search.PatternSearcher;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;

/**
 * @author Daniel Gerber
 *
 */
public class DomainAndRangeEvaluator extends Initializeable implements PatternEvaluator {

	private final NLPediaLogger logger					= new NLPediaLogger(DomainAndRangeEvaluator.class);
	private NamedEntityRecognizer ner;
	private final int maxNumberOfDocuments 				= Integer.valueOf(NLPediaSettings.getInstance().getSetting("maxNumberOfDocuments"));
	private final int maxNumberOfEvaluationSentences 	= 5000;
	
	private PatternSearcher patternSearcher;
	
	// used for sentence segmentation
	private Reader stringReader;
	private DocumentPreprocessor preprocessor;
	private StringBuilder stringBuilder;
	
	public DomainAndRangeEvaluator() {
		
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
	}
	
	/* (non-Javadoc)
	 * @see simba.nlpedia.entity.pattern.evaluation.PatternEvaluator#evaluatePattern(simba.nlpedia.entity.pattern.PatternMapping)
	 */
	@Override
	public void evaluatePattern(PatternMapping patternMapping) {

		try {
			
			String domainUri	= patternMapping.getRdfsDomain();
			String rangeUri		= patternMapping.getRdfsRange();
			
			double domainCorrectness;
			double rangeCorrectness;
			
			String nerTagged;
			String segmentedFoundString;
			String segmentedPattern;
			
			Context leftContext;
			Context rightContext;
			
			if ( this.ner == null ) this.ner = new NamedEntityRecognizer();
			
			for (Pattern pattern : patternMapping.getPatterns()) {
				
				if ( pattern.isUseForPatternEvaluation() ) {
					
					System.out.print("Pattern: " + pattern.getNaturalLanguageRepresentation());
					
					boolean beginsWithDomain = pattern.getNaturalLanguageRepresentation().startsWith("?D?") ? true : false;
					String patternWithOutVariables = this.segmentString(pattern.getNaturalLanguageRepresentation().substring(0, pattern.getNaturalLanguageRepresentation().length() - 3).substring(3).trim());
					
					this.logger.debug("Querying index for pattern \"" + patternWithOutVariables + "\".");
					List<String> sentences = new ArrayList<String>(patternSearcher.getSentencesWithString(patternWithOutVariables, this.maxNumberOfDocuments));
					this.logger.debug("Pattern \"" + patternWithOutVariables + "\" returned " + sentences.size() + " results.");
					
					int correctDomain	= 0;
					int correctRange	= 0;
					
					for (String foundString : sentences.size() >= this.maxNumberOfEvaluationSentences ? sentences.subList(0,this.maxNumberOfEvaluationSentences - 1) : sentences) {
						
						nerTagged = this.ner.recognizeEntitiesInString(foundString);
						segmentedFoundString = this.segmentString(foundString);
						segmentedPattern = this.segmentString(patternWithOutVariables);
						
						try {
	
							leftContext = new LeftContext(nerTagged, segmentedFoundString, segmentedPattern);
							rightContext = new RightContext(nerTagged, segmentedFoundString, segmentedPattern);
							
							if ( beginsWithDomain ) {
								
								if ( leftContext.containsSuitableEntity(domainUri) ) {
									
									correctDomain++;
								}
								if ( rightContext.containsSuitableEntity(rangeUri) ) {
									
									correctRange++;
								}
							}
							else {
								
								if ( leftContext.containsSuitableEntity(rangeUri) ) {
									
									correctDomain++;
								}
								if ( rightContext.containsSuitableEntity(domainUri) ) {
									
									correctRange++;
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
					
					// ########################################################
					
					double confidenceWithLog = (domainCorrectness + rangeCorrectness) / 2;
					confidenceWithLog = Double.isNaN(confidenceWithLog) ? 0d : confidenceWithLog * (Math.log((double)(sentences.size() + 1)) / Math.log(2d));
					
//					System.out.println("MAX_LEARNED:" +pattern.retrieveMaxLearnedFrom());
					
					pattern.setWithLogConfidence(confidenceWithLog);
					pattern.setWithLogWithLogLearndFromConfidence(confidenceWithLog * (Math.log((double)(pattern.retrieveMaxLearnedFrom() + 1)) / Math.log(2d)));
					pattern.setWithLogWithoutLogLearndFromConfidence(confidenceWithLog * pattern.retrieveMaxLearnedFrom());
					
//					System.out.println(pattern.getWithLogConfidence());
//					System.out.println(pattern.getWithLogWithLogLearndFromConfidence());
//					System.out.println(pattern.getWithLogWithoutLogLearndFromConfidence());
					
					// ########################################################
					
					double confidenceWithoutLog = (domainCorrectness + rangeCorrectness) / 2;
					confidenceWithoutLog = Double.isNaN(confidenceWithoutLog) ? 0d : confidenceWithoutLog * sentences.size();
					
					pattern.setWithoutLogConfidence(confidenceWithoutLog);
					pattern.setWithoutLogWithLogLearndFromConfidence(confidenceWithoutLog * (Math.log((double)(pattern.retrieveMaxLearnedFrom() + 1)) / Math.log(2d)));
					pattern.setWithoutLogWithoutLogLearndFromConfidence(confidenceWithoutLog * pattern.retrieveMaxLearnedFrom());
					
//					System.out.println(pattern.getWithoutLogConfidence());
//					System.out.println(pattern.getWithoutLogWithLogLearndFromConfidence());
//					System.out.println(pattern.getWithoutLogWithoutLogLearndFromConfidence());
					
					// ########################################################
					
					// we wont need to see them in the view
					if ( confidenceWithoutLog == 0 && confidenceWithLog == 0 ) pattern.setUseForPatternEvaluation(false);
					
					if ( pattern.getWithLogConfidence() == 0 || pattern.getWithoutLogConfidence() == 0) {
						
						System.out.println(" does not fit in domain/range."); // continued from upper system.out.print()
						this.logger.debug("Pattern " +  pattern.getNaturalLanguageRepresentation() + " does not fit in domain/range.");
					}
				}
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

	@Override
	public void initialize() {

		// TODO Auto-generated method stub
		
	}
}
