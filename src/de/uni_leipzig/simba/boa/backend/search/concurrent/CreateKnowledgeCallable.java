package de.uni_leipzig.simba.boa.backend.search.concurrent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.store.Directory;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.rdf.TripleDao;
import de.uni_leipzig.simba.boa.backend.entity.context.Context;
import de.uni_leipzig.simba.boa.backend.entity.context.FastLeftContext;
import de.uni_leipzig.simba.boa.backend.entity.context.FastRightContext;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.nlp.NamedEntityRecognizer;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Resource;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;
import de.uni_leipzig.simba.boa.backend.rdf.uri.UriRetrieval;
import de.uni_leipzig.simba.boa.backend.rdf.uri.impl.DbpediaUriRetrieval;
import de.uni_leipzig.simba.boa.backend.search.PatternSearcher;
import de.uni_leipzig.simba.boa.backend.util.PatternUtil;
import de.uni_leipzig.simba.boa.backend.util.PatternUtil.PatternSelectionStrategy;

public class CreateKnowledgeCallable implements Callable<Collection<Triple>> {

	public static final NamedEntityRecognizer ner = new NamedEntityRecognizer();
	private final NLPediaLogger logger 		= new NLPediaLogger(CreateKnowledgeCallable.class);
	private final PatternMapping mapping;
	
	private TripleDao tripleDao = (TripleDao) DaoFactory.getInstance().createDAO(TripleDao.class);
	private static Map<Integer,Triple> tripleMap = null;
	private Map<Integer,Triple> newTripleMap = new HashMap<Integer,Triple>();
	private Directory idx = null;
	
	/**
	 * DO ONLY USE THIS FOR EVALUATION
	 * 
	 * @param mapping
	 * @param idx
	 */
	public CreateKnowledgeCallable(PatternMapping mapping, Directory idx) {
		
		this.mapping = mapping;
		this.idx = idx;
		this.buildTripleMap();
	}
	
	private void buildTripleMap() {

		if ( tripleMap == null ) {
			
			if ( !new File("/home/gerber/nlpedia-data/files/relation/bk.out").exists() ) {
				
				Map<Integer,Triple> tripleMap = new HashMap<Integer,Triple>();
				for (Triple t : tripleDao.findAllTriples()) {
					
					tripleMap.put(t.hashCode(), t);
				}
				try {
					
					ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("/home/gerber/nlpedia-data/files/relation/bk.out")));
					oos.writeObject(tripleMap);
					oos.close();
				}
				catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				
				try {
					
					ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("/home/gerber/nlpedia-data/files/relation/bk.out")));
					tripleMap = (HashMap<Integer,Triple>) ois.readObject();
					ois.close();
				}
				catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public CreateKnowledgeCallable(PatternMapping mapping) {
		
		this.mapping = mapping;
		this.buildTripleMap();
	}

	@Override
	public Collection<Triple> call() {
		
		try {

			Set<Pattern> patterns = mapping.getPatterns();
			
			if ( patterns != null && patterns.size() > 0 ) {
				
				// take the top n scored patterns
				List<Pattern> patternList	= PatternUtil.getTopNPattern(patterns, PatternSelectionStrategy.ALL, Integer.valueOf(NLPediaSettings.getInstance().getSetting("top.n.pattern")), Double.valueOf(NLPediaSettings.getInstance().getSetting("score.threshold.create.knowledge")));
				this.logger.info(String.format("Creating knowledge for mapping: %s and top-%s patterns", mapping.getProperty().getUri(), patternList.size()));

				if ( !patternList.isEmpty() ) {
					
					// this is solely for the evaluation
					PatternSearcher patternSearcher;
					if ( this.idx == null ) patternSearcher = new PatternSearcher(NLPediaSettings.getInstance().getSetting("sentenceIndexDirectory"));
					else patternSearcher = new PatternSearcher(this.idx);
					
					for (Pattern pattern : patternList) {
						
						Set<String> sentences = patternSearcher.getExactMatchSentences(pattern.getNaturalLanguageRepresentationWithoutVariables(), Integer.valueOf(NLPediaSettings.getInstance().getSetting("max.number.of.documents.generation")));
						this.logger.debug("\tQuering pattern \"" + pattern.getNaturalLanguageRepresentation() + "\" with " + sentences.size() + " sentences");
						
						for (String sentence : sentences) {
							
							// there will never be a left argument if the sentence begins with the pattern
							if ( sentence.toLowerCase().startsWith(pattern.getNaturalLanguageRepresentationWithoutVariables().toLowerCase())) continue;
							this.logger.debug("\t" + sentence);

							createKnowledge(mapping, pattern, sentence, ner.recognizeEntitiesInString(sentence)); 
						}
					}
					// close the searcher or you get a ioexception because too many files are open
					patternSearcher.close();
					
					// confidence of triple is number of patterns the triple has been learned from times the sum of their confidences
					for ( Triple triple : newTripleMap.values() ) {
						
						// new knowledge (knowledge not put in as background knowledge) is always not correct
						if ( !triple.isCorrect() ) {
							
							double confidence = 0;
							for ( Pattern patternLearnedFrom : triple.getLearnedFromPatterns() ) {
								
								confidence += patternLearnedFrom.getConfidence();
							}
							// sigmoid function shifted to the right to boost pattern which are learned from more than one pattern
							triple.setConfidence(1D / (1D + Math.pow(Math.E, - confidence * triple.getLearnedFromPatterns().size() + 1)));
						}
					}
				}
			}
		}
		catch (ParseException e) {
			
			this.logger.error("ParseExcpetion while index search!", e);
			throw new RuntimeException(e);
		}
		catch (IOException e) {
			
			this.logger.error("IOException while index search!", e);
			throw new RuntimeException(e);
		}
		catch (Exception e) {
			
			this.logger.error("Excpetion", e);
			throw new RuntimeException(e);
		}
		
		this.logger.info("Finished creating knowledge for: "  + this.mapping.getProperty().getUri() + " with " + newTripleMap.values().size() + " triples.");
		
		return this.newTripleMap.values();
	}
	
	private void createKnowledge(PatternMapping mapping, Pattern pattern, String sentence, String nerTaggedSentence) {
		
		try {

			String domainUri	= mapping.getProperty().getRdfsDomain();
			String rangeUri		= mapping.getProperty().getRdfsRange();
			
			Context leftContext = new FastLeftContext(nerTaggedSentence, sentence, pattern.getNaturalLanguageRepresentationWithoutVariables());
			Context rightContext = new FastRightContext(nerTaggedSentence, sentence, pattern.getNaturalLanguageRepresentationWithoutVariables());

			boolean beginsWithDomain = pattern.getNaturalLanguageRepresentation().startsWith("?D?") ? true : false;

			if (beginsWithDomain) {

				if (leftContext.containsSuitableEntity(domainUri) && rightContext.containsSuitableEntity(rangeUri)) {

					if ( leftContext.getSuitableEntityDistance(domainUri) <= 4 && rightContext.getSuitableEntityDistance(rangeUri) <= 4 ) {
						
						String subjectLabel = leftContext.getSuitableEntity(domainUri);
						String objectLabel = rightContext.getSuitableEntity(rangeUri);
						
						UriRetrieval uriRetrieval = new DbpediaUriRetrieval();
						String subjectUri = uriRetrieval.getUri(subjectLabel);
						String objectUri = uriRetrieval.getUri(objectLabel);
						
						Resource subject = ResourceManager.getInstance().getResource(subjectUri, subjectLabel, domainUri);
						if ( subject == null) throw new RuntimeException("1. Subject null for uri:" + subjectUri+ " label: " + subjectLabel + " type:" + domainUri);
						
						Resource object = ResourceManager.getInstance().getResource(objectUri, objectLabel, rangeUri);
						if ( object == null) throw new RuntimeException("1. Object null for uri:" + objectUri+ " label: " + objectLabel + " type:" + rangeUri);
						
						Triple triple = new Triple();
						triple.setSubject(subject);
						triple.setProperty(mapping.getProperty());
						triple.setObject(object);
						
						if ( !tripleMap.containsKey(triple.hashCode()) ) {
							
							// replace it if it already exists
							if ( this.newTripleMap.containsKey(triple.hashCode()) ) {
								
								triple = newTripleMap.get(triple.hashCode());
							}
							triple.addLearnedFromSentences(sentence);
							triple.addLearnedFromPattern(pattern);
							// put the new one in
							this.newTripleMap.put(triple.hashCode(), triple);
						}
						else {
							
							System.out.println("Triple already exists: " + triple);
							this.logger.info("Triple already exists: " + triple);
						}
					}
				}
			}
			else {

				if (leftContext.containsSuitableEntity(rangeUri) && rightContext.containsSuitableEntity(domainUri)) {
					
					// left context contains object, right context contains subject
					if (leftContext.getSuitableEntityDistance(rangeUri) <= 4 && rightContext.getSuitableEntityDistance(domainUri) <= 4 ) {
						
						String objectLabel = leftContext.getSuitableEntity(rangeUri);
						String subjectLabel = rightContext.getSuitableEntity(domainUri);
						
						UriRetrieval uriRetrieval = new DbpediaUriRetrieval();
						String objectUri = uriRetrieval.getUri(objectLabel);
						String subjectUri = uriRetrieval.getUri(subjectLabel);
						
						Resource subject = ResourceManager.getInstance().getResource(subjectUri, subjectLabel, domainUri);
						if ( subject == null) throw new RuntimeException("2. subject null for uri:" + subjectUri+ " label: " + subjectLabel + " type:" + domainUri);

						Resource object = ResourceManager.getInstance().getResource(objectUri, objectLabel, rangeUri);
						if ( object == null) throw new RuntimeException("2. object null for uri:" + objectUri+ " label: " + objectLabel + " type:" + rangeUri);
						
						Triple triple = new Triple();
						triple.setSubject(subject);
						triple.setProperty(mapping.getProperty());
						triple.setObject(object);
						
						if ( !tripleMap.containsKey(triple.hashCode()) ) {
							
							// replace it if it already exists
							if ( this.newTripleMap.containsKey(triple.hashCode()) ) {
								
								triple = this.newTripleMap.get(triple.hashCode());
							}
							triple.addLearnedFromSentences(sentence);
							triple.addLearnedFromPattern(pattern);
							// put the new one in
							this.newTripleMap.put(triple.hashCode(), triple);
						}
						else {
							
							System.out.println("Triple already exists: " + triple);
							this.logger.info("Triple already exists: " + triple);
						}
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
}
