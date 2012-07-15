package de.uni_leipzig.simba.boa.backend.search.concurrent;

import java.io.Reader;
import java.io.StringReader;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.store.Directory;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.CreateKnowledgeCommand;
import de.uni_leipzig.simba.boa.backend.entity.context.Context;
import de.uni_leipzig.simba.boa.backend.entity.context.LeftContext;
import de.uni_leipzig.simba.boa.backend.entity.context.RightContext;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.namedentityrecognition.NamedEntityRecognition;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Resource;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;
import de.uni_leipzig.simba.boa.backend.rdf.uri.UriRetrieval;
import de.uni_leipzig.simba.boa.backend.rdf.uri.impl.MeshupUriRetrieval;
import de.uni_leipzig.simba.boa.backend.search.impl.DefaultPatternSearcher;
import de.uni_leipzig.simba.boa.backend.util.PatternUtil;
import de.uni_leipzig.simba.boa.backend.util.PatternUtil.PatternSelectionStrategy;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;

public class CreateKnowledgeThread { //extends Thread {

	private final NamedEntityRecognition ner = null;
	private final NLPediaLogger logger 		= new NLPediaLogger(CreateKnowledgeThread.class);
	private final List<PatternMapping> mappings;
	
	private Map<Integer,Triple> newTripleMap = new HashMap<Integer,Triple>();
	private Map<Integer,Triple> knownTripleMap = new HashMap<Integer,Triple>();
	private Directory idx = null;
	private int numberOfDoneSearchOperations = 0;
	private int numberOfAllSearchOperations = 0;
	
	// used for sentence segmentation
	private Reader stringReader;
	private DocumentPreprocessor preprocessor;
	private StringBuilder stringBuilder;
	
	/**
	 * DO ONLY USE THIS FOR EVALUATION
	 * 
	 * @param mapping
	 * @param idx
	 */
	public CreateKnowledgeThread(List<PatternMapping> mappings, Directory idx) {
		
		this.mappings = mappings;
		this.calculateNumberOfSearchOperations();
		this.idx = idx;
	}
	
	public CreateKnowledgeThread(List<PatternMapping> mappings) {
		
		this.mappings = mappings;
		this.calculateNumberOfSearchOperations();
	}
	
	public void run() {
		
		try {
			
			for (PatternMapping mapping : this.mappings) { 
				
				Set<Pattern> patterns = mapping.getPatterns();
				
				if ( patterns != null && patterns.size() > 0 ) {
					
					// take the top n scored patterns
					List<Pattern> patternList	= PatternUtil.getTopNPattern(patterns, PatternSelectionStrategy.ALL, Integer.valueOf(NLPediaSettings.getInstance().getSetting("top.n.pattern")), Double.valueOf(NLPediaSettings.getInstance().getSetting("score.threshold.create.knowledge")));
					this.logger.info(String.format("Creating knowledge for mapping: %s and top-%s patterns", mapping.getProperty().getUri(), patternList.size()));

					if ( !patternList.isEmpty() ) {
						
						// this is solely for the evaluation
						DefaultPatternSearcher patternSearcher;
						if ( this.idx == null ) patternSearcher = new DefaultPatternSearcher(NLPediaSettings.getInstance().getSetting("sentenceIndexDirectory"));
						else patternSearcher = new DefaultPatternSearcher(this.idx);
						
						for (Pattern pattern : patternList) {
							
							Set<String> sentences = patternSearcher.getExactMatchSentences(pattern.getNaturalLanguageRepresentationWithoutVariables(), Integer.valueOf(NLPediaSettings.getInstance().getSetting("max.number.of.documents.generation")));
							this.logger.debug("\tQuering pattern \"" + pattern.getNaturalLanguageRepresentation() + "\" with " + sentences.size() + " sentences");
							
							this.numberOfDoneSearchOperations++;
							
							for (String sentence : sentences) {
								
								// this is a quick hack for ISSUE 4 (http://code.google.com/p/boa/issues/detail?id=4) TODO FIX
								if ( !NLPediaSettings.getInstance().getSetting("hibernateConnectionUrl").contains("wiki") ) {
									
									sentence = this.segmentString(sentence);
								} 
								
								// there will never be a left argument if the sentence begins with the pattern
								if ( sentence.toLowerCase().startsWith(pattern.getNaturalLanguageRepresentationWithoutVariables().toLowerCase())) continue;
								this.logger.debug("\t" + sentence);
								
								try {
									
									createKnowledge(mapping, pattern, sentence, ner.getAnnotatedString(sentence));
								}
								catch (java.lang.ArrayIndexOutOfBoundsException e ) {
									
									this.logger.error("named entity recognizer failed for sentence: " + sentence, e);
								}
							}
						}
						// close the searcher or you get a ioexception because too many files are open
						patternSearcher.close();
					}
				}
				this.logger.info("Finished creating knowledge for: "  + mapping.getProperty().getUri() + " with " + newTripleMap.values().size() + " triples.");
			}
		}
		catch (Exception e) {
			
			this.logger.error("Excpetion", e);
			throw new RuntimeException(e);
		}
	}
	
	private void createKnowledge(PatternMapping mapping, Pattern pattern, String sentence, String nerTaggedSentence) {
		
		try {

			String domainUri	= mapping.getProperty().getRdfsDomain();
			String rangeUri		= mapping.getProperty().getRdfsRange();
			
			Context leftContext = new LeftContext(nerTaggedSentence, sentence, pattern.getNaturalLanguageRepresentationWithoutVariables());
			Context rightContext = new RightContext(nerTaggedSentence, sentence, pattern.getNaturalLanguageRepresentationWithoutVariables());

			boolean beginsWithDomain = pattern.getNaturalLanguageRepresentation().startsWith("?D?") ? true : false;

			if (beginsWithDomain) {

				if (leftContext.containsSuitableEntity(domainUri) && rightContext.containsSuitableEntity(rangeUri)) {

					if ( leftContext.getSuitableEntityDistance(domainUri) <= 4 && rightContext.getSuitableEntityDistance(rangeUri) <= 4 ) {
						
						String subjectLabel = leftContext.getSuitableEntity(domainUri);
						String objectLabel = rightContext.getSuitableEntity(rangeUri);
						
						UriRetrieval uriRetrieval = new MeshupUriRetrieval();
						String subjectUri = uriRetrieval.getUri(subjectLabel);
						String objectUri = uriRetrieval.getUri(objectLabel);
						
						if ( !subjectUri.startsWith("http://dbpedia.org/resource/") &&
								!subjectUri.startsWith("http://boa.akws.org/resource/") )  throw new RuntimeException("Wired URI:'" + subjectUri+ "' label: " + subjectLabel + " type:" + domainUri);
						if ( !objectUri.startsWith("http://dbpedia.org/resource/")  &&
								!objectUri.startsWith("http://boa.akws.org/resource/") ) throw new RuntimeException("Wired URI:'" + objectUri+ "' label: " + objectLabel + " type:" + rangeUri);
						
						Resource subject = ResourceManager.getInstance().getResource(subjectUri, subjectLabel, domainUri);
						if ( subject == null) throw new RuntimeException("1. Subject null for uri:" + subjectUri+ " label: " + subjectLabel + " type:" + domainUri);
						
						Resource object = ResourceManager.getInstance().getResource(objectUri, objectLabel, rangeUri);
						if ( object == null) throw new RuntimeException("1. Object null for uri:" + objectUri+ " label: " + objectLabel + " type:" + rangeUri);
						
						if ( !subject.getUri().startsWith("http://dbpedia.org/resource/") &&
								!subject.getUri().startsWith("http://boa.akws.org/resource/") ) throw new RuntimeException("Wired URI after ResourceManager:" + subject.getUri()+ " label: " + subjectLabel + " type:" + domainUri);
						if ( !object.getUri().startsWith("http://dbpedia.org/resource/") &&
								!object.getUri().startsWith("http://boa.akws.org/resource/") ) throw new RuntimeException("Wired URI after ResourceManager:" + object.getUri()+ " label: " + objectLabel + " type:" + rangeUri);
						
						this.addTriple(subject, mapping.getProperty(), object, sentence, pattern);
					}
				}
			}
			else {

				if (leftContext.containsSuitableEntity(rangeUri) && rightContext.containsSuitableEntity(domainUri)) {
					
					// left context contains object, right context contains subject
					if (leftContext.getSuitableEntityDistance(rangeUri) <= 4 && rightContext.getSuitableEntityDistance(domainUri) <= 4 ) {
						
						String objectLabel = leftContext.getSuitableEntity(rangeUri);
						String subjectLabel = rightContext.getSuitableEntity(domainUri);
						
						UriRetrieval uriRetrieval = new MeshupUriRetrieval();
						String objectUri = uriRetrieval.getUri(objectLabel);
						String subjectUri = uriRetrieval.getUri(subjectLabel);
						
						if ( !subjectUri.startsWith("http://dbpedia.org/resource/") &&
								!subjectUri.startsWith("http://boa.akws.org/resource/") ) throw new RuntimeException("Wired URI:" + subjectUri+ " label: " + subjectLabel + " type:" + domainUri);
						if ( !objectUri.startsWith("http://dbpedia.org/resource/")  &&
								!objectUri.startsWith("http://boa.akws.org/resource/") ) throw new RuntimeException("Wired URI:" + objectUri+ " label: " + objectLabel + " type:" + rangeUri);
						
						Resource subject = ResourceManager.getInstance().getResource(subjectUri, subjectLabel, domainUri);
						if ( subject == null) throw new RuntimeException("2. subject null for uri:" + subjectUri+ " label: " + subjectLabel + " type:" + domainUri);

						Resource object = ResourceManager.getInstance().getResource(objectUri, objectLabel, rangeUri);
						if ( object == null) throw new RuntimeException("2. object null for uri:" + objectUri+ " label: " + objectLabel + " type:" + rangeUri);
						
						if ( !subject.getUri().startsWith("http://dbpedia.org/resource/") &&
								!subject.getUri().startsWith("http://boa.akws.org/resource/") ) throw new RuntimeException("Wired URI after ResourceManager:" + subject.getUri()+ " label: " + subjectLabel + " type:" + domainUri);
						if ( !object.getUri().startsWith("http://dbpedia.org/resource/") &&
								!object.getUri().startsWith("http://boa.akws.org/resource/") ) throw new RuntimeException("Wired URI after ResourceManager:" + object.getUri()+ " label: " + objectLabel + " type:" + rangeUri);
						
						this.addTriple(subject, mapping.getProperty(), object, sentence, pattern);
					}
				}
			}
		}
		catch (IndexOutOfBoundsException ioob) {

			this.logger.debug("Could not create context for string " + sentence + ". NER tagged: " + nerTaggedSentence + " pattern: " + pattern.getNaturalLanguageRepresentationWithoutVariables(), ioob);
		}
		catch (IllegalArgumentException e) {
			
			this.logger.debug("Could not create context for string " + sentence + ". NER tagged: " + nerTaggedSentence + " pattern: " + pattern.getNaturalLanguageRepresentationWithoutVariables(), e);
		}
		catch (Exception e) {
			
			throw new RuntimeException("Some bug ", e);
		}
	}

	private void addTriple(Resource subject, Property property, Resource object, String sentence, Pattern pattern) {

		Triple triple = new Triple();
		triple.setSubject(subject);
		triple.setProperty(property);
		triple.setObject(object);
		
		// triple is already present background knowledge, so disregard it
		if ( CreateKnowledgeCommand.tripleMap.containsKey(triple.hashCode()) ) {
			
			this.knownTripleMap.put(triple.hashCode(), triple);
			System.out.println("Triple already exists: " + triple);
			this.logger.info("Triple already exists: " + triple);
		}
		// the triple is new with respect to the background knowledge
		else {
			
			// do we have found it with a different pattern already
			// if so then get this pattern
			if ( this.newTripleMap.containsKey(triple.hashCode()) ) {
				
				triple = newTripleMap.get(triple.hashCode());
			}
			triple.addLearnedFromSentences(sentence);
			triple.addLearnedFromPattern(pattern);
			// put the new one in
			this.newTripleMap.put(triple.hashCode(), triple);
		}
	}
	
	public String getProgress() {

		String percent = ""; 
		try {

			percent = NumberFormat.getPercentInstance().format(((double) numberOfDoneSearchOperations / (double)numberOfAllSearchOperations));
		}
		catch (java.lang.NumberFormatException nfe) {
			
			percent = "0%";
			logger.error("Could not determine progress for thread this: "/* + this.getName()*/, nfe);
		}
		return percent;
	}

	private void calculateNumberOfSearchOperations() {

		for (PatternMapping mapping : this.mappings ) {
			
			this.numberOfAllSearchOperations += PatternUtil.getTopNPattern(mapping.getPatterns(), PatternSelectionStrategy.ALL, Integer.valueOf(NLPediaSettings.getInstance().getSetting("top.n.pattern")), Double.valueOf(NLPediaSettings.getInstance().getSetting("score.threshold.create.knowledge"))).size(); 
		}
	}
	
	public int getNumberOfAllSearchOperations() {
		
		return this.numberOfAllSearchOperations;
	}
	
	public int getNumberOfDoneSearchOperations() {
		
		return this.numberOfDoneSearchOperations;
	}
	
	/**
	 * @return the newTripleMap
	 */
	public Map<Integer, Triple> getNewTripleMap() {
	
		return newTripleMap;
	}

	
	/**
	 * @param newTripleMap the newTripleMap to set
	 */
	public void setNewTripleMap(Map<Integer, Triple> newTripleMap) {
	
		this.newTripleMap = newTripleMap;
	}


	public Map<Integer, Triple> getKnownTripleMap() {

		return this.knownTripleMap;
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
