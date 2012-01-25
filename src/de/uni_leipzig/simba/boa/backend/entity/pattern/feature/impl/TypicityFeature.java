/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryParser.ParseException;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.entity.context.Context;
import de.uni_leipzig.simba.boa.backend.entity.context.LeftContext;
import de.uni_leipzig.simba.boa.backend.entity.context.RightContext;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.Feature;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.NaturalLanguageProcessingToolFactory;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.namedentityrecognition.NamedEntityRecognition;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.namedentityrecognition.impl.StanfordNLPNamedEntityRecognition;
import de.uni_leipzig.simba.boa.backend.search.PatternSearcher;
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
	
	private PatternSearcher patternSearcher;
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
	
	@Override
	public void score(List<PatternMapping> mapping) {

		// nothing to do here
	}
	
	/* (non-Javadoc)
	 * @see simba.nlpedia.entity.pattern.evaluation.PatternEvaluator#evaluatePattern(simba.nlpedia.entity.pattern.PatternMapping)
	 */
	@Override
	public void scoreMapping(PatternMapping mapping) {
		
		long start = new Date().getTime();
		
		if ( this.patternSearcher == null ) {
			
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
		
		String domainUri	= mapping.getProperty().getRdfsDomain();
		String rangeUri		= mapping.getProperty().getRdfsRange();
		
		double domainCorrectness;
		double rangeCorrectness;
		
		String nerTagged;
		String segmentedFoundString;
		String segmentedPattern;
		
		Context leftContext;
		Context rightContext;
		
		if ( this.ner == null ) this.ner = NaturalLanguageProcessingToolFactory.getInstance().
			createNamedEntityRecognition(StanfordNLPNamedEntityRecognition.class);

		for (Pattern pattern : mapping.getPatterns()) {
			
			try {
			
				boolean beginsWithDomain = pattern.isDomainFirst();
				String patternWithOutVariables = this.segmentString(pattern.getNaturalLanguageRepresentationWithoutVariables());
				
				final List<String> sentences = new ArrayList<String>(patternSearcher.getExactMatchSentences(patternWithOutVariables, maxNumberOfEvaluationSentences));
				List<String> sentencesToEvaluate = sentences.size() >= this.maxNumberOfEvaluationSentences ? sentences.subList(0, this.maxNumberOfEvaluationSentences) : sentences;
				
				double correctDomain	= 0;
				double correctRange		= 0;
				
				for (String foundString : sentencesToEvaluate) {
					
					if ( foundString.toLowerCase().startsWith(patternWithOutVariables.toLowerCase())) continue;
					
					nerTagged = this.ner.getAnnotatedString(this.replaceBrackets(foundString));
					
					// this is a quick hack for ISSUE 4 (http://code.google.com/p/boa/issues/detail?id=4) TODO FIX
					if ( NLPediaSettings.getInstance().getSetting("hibernateConnectionUrl").contains("wiki") ) {
						
						segmentedFoundString = foundString;
					}
					else {
					
						segmentedFoundString = this.segmentString(foundString);
					}
					
					segmentedPattern = this.segmentString(patternWithOutVariables);
					
					if ( nerTagged != null && segmentedFoundString != null && segmentedPattern != null &&
							nerTagged.length() > 0 && segmentedFoundString.length() > 0 && segmentedPattern.length() > 0 ) {
						
						try {
							
							leftContext = new LeftContext(nerTagged, segmentedFoundString, segmentedPattern);
							rightContext = new RightContext(nerTagged, segmentedFoundString, segmentedPattern);
							
							if ( beginsWithDomain ) {
								
								if ( leftContext.containsSuitableEntity(domainUri) ) 
									correctDomain++; //+= (1D / (double)leftContext.getSuitableEntityDistance(domainUri));
//								else
//									correctDomain--;
								
								if ( rightContext.containsSuitableEntity(rangeUri) )
									correctRange++; //+= (1D / (double)rightContext.getSuitableEntityDistance(rangeUri));
//								else
//									correctRange--;
							}
							else {
								
								if ( leftContext.containsSuitableEntity(rangeUri) )
									correctRange++; // += (1D / (double)leftContext.getSuitableEntityDistance(rangeUri));
//								else
//									correctRange--;
								
								if ( rightContext.containsSuitableEntity(domainUri) )
									correctDomain++; // += (1D / (double)rightContext.getSuitableEntityDistance(domainUri));
//								else
//									correctDomain--;
							}
						}
						catch ( IndexOutOfBoundsException ioob ) {
							//ioob.printStackTrace();
							this.logger.error("Could not create context for string " + segmentedFoundString + ". NER tagged: " + nerTagged + " pattern: "  + patternWithOutVariables, ioob);
						}
						catch (NullPointerException npe) {
							
							this.logger.error("IOExcpetion", npe);
						}
						catch (IllegalArgumentException e) {
							
							this.logger.debug("Could not create context for string " + segmentedFoundString + ". NER tagged: " + nerTagged + " pattern: "  + patternWithOutVariables, e);
						}
						catch (Exception e) {
							
							throw new RuntimeException("Some bug", e);
						}
					}
				}
			
				domainCorrectness = (double) correctDomain / (double) sentencesToEvaluate.size();
				rangeCorrectness = (double) correctRange / (double) sentencesToEvaluate.size();
				
				double typicity = ((domainCorrectness + rangeCorrectness) / 2) * Math.log(sentences.size() + 1);
				
				pattern.getFeatures().put(de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature.TYPICITY_CORRECT_DOMAIN_NUMBER, domainCorrectness >= 0 ? domainCorrectness : 0);
				pattern.getFeatures().put(de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature.TYPICITY_CORRECT_RANGE_NUMBER, rangeCorrectness >= 0 ? rangeCorrectness : 0);
				pattern.getFeatures().put(de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature.TYPICITY_SENTENCES, Math.log(sentences.size() + 1)  >= 0 ? Math.log(sentences.size() + 1) : 0);
				pattern.getFeatures().put(de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature.TYPICITY, typicity >= 0 ? typicity : 0 );
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				this.logger.error("IOExcpetion: ", e);
			}
			catch (ParseException e) {
				// TODO Auto-generated catch block
				this.logger.error("ParseException: ", e);
			}
			catch (NullPointerException npe) {
				
				this.logger.error("NullPointerException: ", npe);
			}
			catch (java.lang.ArrayIndexOutOfBoundsException aioobe) {
				
				this.logger.error("ArrayIndexOutOfBoundsException: ", aioobe);
			}
		}
		this.logger.info("Typicity measuring for pattern_mapping: " + mapping.getProperty().getUri() + " finished in " + (new Date().getTime() - start) + "ms.");
	}
	
	private String replaceBrackets(String foundString) {

		for (Map.Entry<String, String> bracket : TypicityFeature.BRACKETS.entrySet()) {
			
			if ( foundString.contains(bracket.getKey())) {
	
				foundString = foundString.replace(bracket.getKey(), bracket.getValue());
			}
		}
		return foundString;
	}

	public static void main(String[] args) {

		NLPediaSetup setup = new NLPediaSetup(true);
		String[] sentences = new String[]{
				"Anton Vandieken was born on the 4 July 1909 .",
				"Apolo Nsibambi was born on 27 November 1938 .",
				"Arcadie Gherasim was born on August 24 , 1957 .",
				"Archilochus was born on the island of Paros .",
				"Arulappa was born on 28 August 1924 to Smt .",
				"Asencio was born on 5 April 1919 .",
				"Ashiqur Rahman was born on December 18 , 1986 .",
				"Ashley Billington was born on January 16 , 1969 .",
				"Ashley Bowen was born on January 8 , 1728 .",
				"Atif was born on 15 February 1988 .",
				"Aurifici was born on 9 April 1739 at Ucria .",
				"Austin was born on October 20 , 1920 .",
				"Avery was born on July 25 , 1912 .",
				"Ayodele Arise was born on 5 October 1956 .",
				"BIOGRAPHYHe was born on June 2 , 1959 .",
				"Bache was born on 21 August 1839 .",
				"Bandy was born on July 14 , 1893 .",
				"Banks was born on July 28 , 1949 .",
				"Barreto was born on 1957 in Rio de Janeiro .",
				"Barta was born on June 17 , 1957 .",
				"Barthe was born on 5 March 1986 in Avignon .",
				"Barwell F.C. badgeHe was born on 2 October 1936 .",
				"Basil Cave was born on 14 November 1865 .",
				"Bassey Ewa-Henshaw was born on 4 May 1943 .",
				"I love baseball."};
	}
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
