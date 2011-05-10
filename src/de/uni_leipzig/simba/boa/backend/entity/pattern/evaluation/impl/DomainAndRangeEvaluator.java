/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.entity.pattern.evaluation.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
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
	private final PatternMappingDao patternMappingDao	= (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
	private NamedEntityRecognizer ner;
	private final int maxNumberOfDocuments = Integer.valueOf(NLPediaSettings.getInstance().getSetting("maxNumberOfDocuments"));

	private double max = -1;
	private double min = 1;
	private PatternSearcher patternSearcher;
	
	// used for sentence segmentation
	private Reader stringReader;
	private DocumentPreprocessor preprocessor;
	
	public DomainAndRangeEvaluator() {
		
		try {
			
			this.patternSearcher = new PatternSearcher(NLPediaSettings.getInstance().getSetting("sentenceIndexDirectory"), null, null);
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
			double confidence;
			
			String nerTagged;
			String segmentedFoundString;
			String segmentedPattern;
			
			Context leftContext;
			Context rightContext;
			
			if ( this.ner == null ) this.ner = new NamedEntityRecognizer();
			
			for (Pattern pattern : patternMapping.getPatterns()) {
				
				if ( pattern.isUseForPatternEvaluation() ) {
					
					String patternWithOutVariables = pattern.getNaturalLanguageRepresentation().substring(0, pattern.getNaturalLanguageRepresentation().length() - 3).substring(3).trim();
					
					this.logger.debug("Querying index for pattern \"" + patternWithOutVariables + "\".");
					Set<String> sentences = patternSearcher.getSentencesWithString(patternWithOutVariables, this.maxNumberOfDocuments);
					this.logger.debug("Pattern \"" + patternWithOutVariables + "\" returned " + sentences.size() + " results.");
					
					int correctDomain	= 0;
					int correctRange	= 0;
					
					for (String foundString : sentences) {
						
						nerTagged = this.ner.recognizeEntitiesInString(foundString);
						segmentedFoundString = this.segmentString(foundString);
						segmentedPattern = this.segmentString(patternWithOutVariables);
						
						try {
	
							leftContext = new LeftContext(nerTagged, segmentedFoundString, segmentedPattern);
							rightContext = new RightContext(nerTagged, segmentedFoundString, segmentedPattern);
							
							if ( leftContext.containsSuitableEntity(domainUri) ) {
								
								correctDomain++;
							}
							if ( rightContext.containsSuitableEntity(rangeUri) ) {
								
								correctRange++;
							}
						}
						catch ( IndexOutOfBoundsException ioob ) {
							//ioob.printStackTrace();
							this.logger.error("Could not create context for string " + segmentedFoundString + ". NER tagged: " + nerTagged + " pattern: "  + patternWithOutVariables);
						}
					}
					
					domainCorrectness = (double) correctDomain / (double) sentences.size();
					rangeCorrectness = (double) correctRange / (double) sentences.size();
					
					confidence = (domainCorrectness + rangeCorrectness) / 2;
					confidence = Double.isNaN(confidence) ? 0d : confidence * (Math.log((double)(sentences.size() + 1)) / Math.log(2d));
					
					this.min = Math.min(confidence, min);
					this.max = Math.max(confidence, max);
					
					pattern.setConfidence(confidence);
					
					if ( pattern.getConfidence() == 0 ) {
						
						System.out.println("Pattern " + pattern.getNaturalLanguageRepresentation() + " does not fit in domain/range.");
						this.logger.debug("Pattern " +  pattern.getNaturalLanguageRepresentation() + " does not fit in domain/range.");
					}
					
					this.patternMappingDao.updatePatternMapping(patternMapping);
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
			
			StringBuilder string = new StringBuilder();
			
			for ( HasWord word : iter.next() ) {
				string.append(word.toString() + " ");
			}
			return string.toString().trim();
		}
		return "";
	}

	@Override
	public void initialize() {

		// TODO Auto-generated method stub
		
	}
	
	public double getMin() {
		
		return this.min;
	}
	
	public double getMax() {
		
		return this.max;
	}
}
