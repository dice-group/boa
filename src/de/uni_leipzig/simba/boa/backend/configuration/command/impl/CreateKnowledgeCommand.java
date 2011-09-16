package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.queryParser.ParseException;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.dao.rdf.ResourceDao;
import de.uni_leipzig.simba.boa.backend.dao.rdf.TripleDao;
import de.uni_leipzig.simba.boa.backend.entity.context.Context;
import de.uni_leipzig.simba.boa.backend.entity.context.LeftContext;
import de.uni_leipzig.simba.boa.backend.entity.context.RightContext;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.nlp.NamedEntityRecognizer;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Resource;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;
import de.uni_leipzig.simba.boa.backend.rdf.uri.UriRetrieval;
import de.uni_leipzig.simba.boa.backend.rdf.uri.impl.DbpediaUriRetrieval;
import de.uni_leipzig.simba.boa.backend.search.PatternSearcher;

/**
 * 
 * @author Daniel Gerber
 */
public class CreateKnowledgeCommand implements Command {

	private final NLPediaLogger logger = new NLPediaLogger(CreateKnowledgeCommand.class);
	
	private final PatternMappingDao patternMappingDao	= (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
	private final ResourceDao resourceDao				= (ResourceDao) DaoFactory.getInstance().createDAO(ResourceDao.class);
	private final TripleDao tripleDao					= (TripleDao) DaoFactory.getInstance().createDAO(TripleDao.class);
	
	private final NamedEntityRecognizer ner 			= new NamedEntityRecognizer();
	private Map<Integer,Triple> tripleMap 				= new HashMap<Integer,Triple>();

	private List<PatternMapping> patternMappingList;

	public CreateKnowledgeCommand(List<PatternMapping> mappings, Map<Integer,Triple> tripleMap) {

		if ( mappings != null ) this.patternMappingList = mappings;
		else this.patternMappingList = patternMappingDao.findAllPatternMappings();
		if ( tripleMap != null ) this.tripleMap = tripleMap;
		else this.tripleMap = this.createTripleMap();
	}

	/**
	 * 
	 */
	@Override
	public void execute() {

		try {

			PatternSearcher patternSearcher = new PatternSearcher(NLPediaSettings.getInstance().getSetting("sentenceIndexDirectory"));

			for (PatternMapping mapping : this.patternMappingList) {

				// take the top n scored patterns
				List<Pattern> patternList	= this.getTopNPattern(mapping);
				
				System.out.println(String.format("Creating knowledge for mapping: %s and top-%s patterns", mapping.getProperty().getUri(), patternList.size()));

				if ( !patternList.isEmpty() ) {
					
					for (Pattern pattern : patternList) {
						
						Set<String> sentences = patternSearcher.getExactMatchSentences(pattern.getNaturalLanguageRepresentationWithoutVariables(), Integer.valueOf(NLPediaSettings.getInstance().getSetting("max.number.of.documents.generation")));

						for (String sentence : sentences) {

							String nerTaggedSentence = this.ner.recognizeEntitiesInString(sentence);

							createKnowledge(mapping, 
											pattern, 
											sentence, 
											nerTaggedSentence, 
											mapping.getProperty().getRdfsDomain(), 
											mapping.getProperty().getRdfsRange());
						}
					}
					
					double maxConfidenceForTriple = 0D;
					
					// confidence of triple is number of patterns the triple has been learned from times the sum of their confidences
					for ( Triple triple : this.tripleMap.values() ) {
						
						// new knowledge (knowledge not put in as background knowledge) is always not correct
						if ( !triple.isCorrect() ) {
							
							int numberOfPatternsLearnedFrom = triple.getLearnedFromPatterns().size();
							
							double sumOfConfidence = 0;
							for ( Pattern patternLearnedFrom : triple.getLearnedFromPatterns() ) {
								
								sumOfConfidence += patternLearnedFrom.getConfidenceForIteration(IterationCommand.CURRENT_ITERATION_NUMBER);
							}
							triple.setConfidence(sumOfConfidence * numberOfPatternsLearnedFrom);
							maxConfidenceForTriple = Math.max(triple.getConfidence(), maxConfidenceForTriple);
						}
					}
					// calculate global confidence scores
					for ( Triple triple : this.tripleMap.values() ) {
						
						if ( !triple.isCorrect() ) {
							
							triple.setConfidence(triple.getConfidence() / maxConfidenceForTriple);
							tripleDao.updateTriple(triple);
						}
					}
				}
			}
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void createKnowledge(PatternMapping mapping, Pattern pattern, String sentence, String nerTaggedSentence, String domainUri, String rangeUri) {
		
		try {

			Context leftContext = new LeftContext(nerTaggedSentence, sentence, pattern.getNaturalLanguageRepresentationWithoutVariables());
			Context rightContext = new RightContext(nerTaggedSentence, sentence, pattern.getNaturalLanguageRepresentationWithoutVariables());

			boolean beginsWithDomain = pattern.getNaturalLanguageRepresentation().startsWith("?D?") ? true : false;

			if (beginsWithDomain) {

				if (leftContext.containsSuitableEntity(domainUri) && rightContext.containsSuitableEntity(rangeUri)) {

					if (leftContext.getSuitableEntityDistance(domainUri) <= 3 && rightContext.getSuitableEntityDistance(rangeUri) <= 3 ) {
						
						String subjectLabel = leftContext.getSuitableEntity(domainUri);
						String objectLabel = rightContext.getSuitableEntity(rangeUri);
						
						UriRetrieval uriRetrieval = new DbpediaUriRetrieval();
						String subjectUri = uriRetrieval.getUri(subjectLabel);
						String objectUri = uriRetrieval.getUri(objectLabel);
						
						Resource subject = resourceDao.findResourceByUri(subjectUri);
						if ( subject == null ) {
							
							subject = new Resource();
							subject.setUri(subjectUri);
							subject.setLabel(subjectLabel);
							subject.setType(domainUri);
							resourceDao.createAndSaveResource(subject);
						}
						Resource object = resourceDao.findResourceByUri(objectUri);
						if ( object == null ) {
							
							object = new Resource();
							object.setUri(objectUri);
							object.setLabel(objectLabel);
							object.setType(rangeUri);
							resourceDao.createAndSaveResource(object);
						}
						
						Triple triple = new Triple();
						triple.setSubject(subject);
						triple.setProperty(mapping.getProperty());
						triple.setObject(object);
						// replace it if it already exists
						if ( this.tripleMap.containsKey(triple.hashCode()) ) {
							
							triple = this.tripleMap.get(triple.hashCode());
						}
						triple.setLearnedInIteration(IterationCommand.CURRENT_ITERATION_NUMBER);
						triple.addLearnedFromPattern(pattern);
						// put the new one in
						this.tripleMap.put(triple.hashCode(), triple);
					}
				}
			}
			else {

				if (leftContext.containsSuitableEntity(rangeUri) && rightContext.containsSuitableEntity(domainUri)) {
					
					// left context contains object, right context contains subject
					if (leftContext.getSuitableEntityDistance(rangeUri) <= 3 && rightContext.getSuitableEntityDistance(domainUri) <= 3 ) {
						
						String objectLabel = leftContext.getSuitableEntity(rangeUri);
						String subjectLabel = rightContext.getSuitableEntity(domainUri);
						
						UriRetrieval uriRetrieval = new DbpediaUriRetrieval();
						String objectUri = uriRetrieval.getUri(objectLabel);
						String subjectUri = uriRetrieval.getUri(subjectLabel);
						
						Resource subject = resourceDao.findResourceByUri(subjectUri);
						if ( subject == null ) {
							
							subject = new Resource();
							subject.setUri(subjectUri);
							subject.setLabel(subjectLabel);
							subject.setType(domainUri);
							resourceDao.createAndSaveResource(subject);
						}
						Resource object = resourceDao.findResourceByUri(objectUri);
						if ( object == null ) {
							
							object = new Resource();
							object.setUri(objectUri);
							object.setLabel(objectLabel);
							object.setType(rangeUri);
							resourceDao.createAndSaveResource(object);
						}
						
						Triple triple = new Triple();
						triple.setSubject(subject);
						triple.setProperty(mapping.getProperty());
						triple.setObject(object);
						// replace it if it already exists
						if ( this.tripleMap.containsKey(triple.hashCode()) ) {
							
							triple = this.tripleMap.get(triple.hashCode());
						}
						triple.setLearnedInIteration(IterationCommand.CURRENT_ITERATION_NUMBER);
						triple.addLearnedFromPattern(pattern);
						// put the new one in
						this.tripleMap.put(triple.hashCode(), triple);
					}
				}
			}
		}
		catch (IndexOutOfBoundsException ioob) {

			this.logger.error("Could not create context for string " + sentence + ". NER tagged: " + nerTaggedSentence + " pattern: " + pattern.getNaturalLanguageRepresentationWithoutVariables());
		}
	}
	

	private List<Pattern> getTopNPattern(PatternMapping mapping) {

		List<Pattern> patternList = new ArrayList<Pattern>();

		for (Pattern p : mapping.getPatterns()) {

			if ( p.getConfidence() > 0.6 ) {

				patternList.add(p);
			}
		}
		
		int topN = 0;
		// too few patterns so we wont create anything for this pattern
		if ( patternList.size() < 3 ) return Collections.<Pattern>emptyList();
		else {
			
			if ( patternList.size() >= 3 && patternList.size() < 5) topN = 1;
			if ( patternList.size() >= 5 && patternList.size() < 10 ) topN = 2;
			if ( patternList.size() >= 10 && patternList.size() < 20 ) topN = 3;
			if ( patternList.size() >= 20 ) topN = 5;

			// sort the pattern by their confidence in descending order
			Collections.sort(patternList, new Comparator<Pattern>() {

				@Override
				public int compare(Pattern pattern1, Pattern pattern2) {

					return pattern2.getConfidenceForIteration(IterationCommand.CURRENT_ITERATION_NUMBER).compareTo(pattern1.getConfidenceForIteration(IterationCommand.CURRENT_ITERATION_NUMBER));
				}
			});

			return patternList.size() > topN ? patternList.subList(0, topN) : patternList;

		}
	}
	
	private Map<Integer, Triple> createTripleMap() {

		List<Triple> triples = this.tripleDao.findAllTriples(); 
		for ( Triple t : triples ) {
			this.tripleMap.put(t.hashCode(), t);
		}
		return this.tripleMap;
	}
}
