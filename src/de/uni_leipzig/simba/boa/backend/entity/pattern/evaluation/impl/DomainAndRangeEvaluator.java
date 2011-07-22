/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.entity.pattern.evaluation.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;

import de.uni_leipzig.simba.boa.backend.configuration.Initializeable;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.PatternEvaluationCommand;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternDao;
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
	private final int maxNumberOfEvaluationSentences 	= Integer.valueOf(NLPediaSettings.getInstance().getSetting("maxNumberOfEvalDocuments"));
	private PatternDao patternDao						= (PatternDao) DaoFactory.getInstance().createDAO(PatternDao.class);
	
	private PatternSearcher patternSearcher;
	
	// used for sentence segmentation
	private Reader stringReader;
	private DocumentPreprocessor preprocessor;
	private StringBuilder stringBuilder;
	
	public DomainAndRangeEvaluator() {}
	
	/* (non-Javadoc)
	 * @see simba.nlpedia.entity.pattern.evaluation.PatternEvaluator#evaluatePattern(simba.nlpedia.entity.pattern.PatternMapping)
	 */
	@Override
	public void evaluatePattern(PatternMapping patternMapping) {
		
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
					
					System.out.print("\nPattern: " + pattern.getNaturalLanguageRepresentation());
					
					boolean beginsWithDomain = pattern.getNaturalLanguageRepresentation().startsWith("?D?") ? true : false;
					String patternWithOutVariables = this.segmentString(pattern.getNaturalLanguageRepresentation().substring(0, pattern.getNaturalLanguageRepresentation().length() - 3).substring(3).trim());
							
					long start = new Date().getTime();
					List<String> sentences = new ArrayList<String>(patternSearcher.getExactMatchSentences(patternWithOutVariables, maxNumberOfEvaluationSentences));//.getSentencesWithString(patternWithOutVariables, this.maxNumberOfDocuments));
					System.out.println("Querying index for pattern \"" + patternWithOutVariables + "\" took "+((new Date().getTime() - start )/1000 )+"s and returned " +sentences.size() + " sentences.");
					this.logger.debug("Querying index for pattern \"" + patternWithOutVariables + "\" took "+((new Date().getTime() - start )/1000 )+"s and returned " +sentences.size() + " sentences.");
					this.logger.debug("Pattern \"" + patternWithOutVariables + "\" returned " + sentences.size() + " results.");
					
					int correctDomain	= 0;
					int correctRange	= 0;
					
					for (String foundString : sentences.size() >= this.maxNumberOfEvaluationSentences ? sentences.subList(0,this.maxNumberOfEvaluationSentences - 1) : sentences) {
						
						start = new Date().getTime();
						nerTagged = this.ner.recognizeEntitiesInString(foundString);
						System.out.println("NER tag string: \"" + foundString + "\" took "+((new Date().getTime() - start )/1000 )+"s.");
						System.out.println("NER tag string: " + nerTagged);
						this.logger.debug("NER tag string: \"" + foundString + "\" took "+((new Date().getTime() - start )/1000 )+"s.");
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
					
					double typicity = 0D;
					double support = 0D;
					double specificity = 0D;
					
					typicity = (domainCorrectness + rangeCorrectness) / (2D);//* (double) sentences.size());
					typicity = Double.isNaN(typicity) ? 0d : typicity * (double) (Math.log((int)(sentences.size() + 1)) / Math.log(2));
					
					specificity = (Math.log(PatternEvaluationCommand.NUMBER_OF_PATTERN_MAPPINGS / this.getNumberOfPatternMappingsWithPattern(pattern.getNaturalLanguageRepresentation())) / Math.log(2));
					
					support = (double) (Math.log((int)(pattern.retrieveMaxLearnedFrom() + 1)) / Math.log(2)) * 
								(double) (Math.log((pattern.retrieveCountLearnedFrom() + 1)) / Math.log(2));
					
					pattern.setSupport(support);
					pattern.setTypicity(typicity);
					pattern.setSpecificity(specificity);
					
					System.out.println(pattern.getNaturalLanguageRepresentation());
					System.out.println("Support: \t\t" + pattern.getSupport());
					System.out.println("Specificity:\t" + pattern.getSpecificity());
					System.out.println("Typicity:\t" + pattern.getTypicity());
					
					pattern.updateTempConfidence(support * typicity * specificity);
					
					// ########################################################
					
					// we wont need to see them in the view
					if ( pattern.getConfidence() <= 0 ) pattern.setUseForPatternEvaluation(false);
					
					if ( pattern.getConfidence() <= 0 ) {
						
						System.out.println(" does not fit in domain/range.\n"); // continued from upper system.out.print()
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

	private int getNumberOfPatternMappingsWithPattern(String naturalLanguageRepresentation) {

		return this.patternDao.countPatternMappingsWithSameNaturalLanguageRepresenation(naturalLanguageRepresentation);
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
