package de.uni_leipzig.simba.boa.backend.search.concurrent;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.lucene.queryParser.ParseException;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
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

public class CreateKnowledgeCallable { //implements Callable<Collection<Triple>> {

	private final NLPediaLogger logger 		= new NLPediaLogger(CreateKnowledgeCallable.class);
	private final PatternMapping mapping;

	private Map<Integer,Triple> tripleMap = new HashMap<Integer,Triple>();
	
	public CreateKnowledgeCallable(PatternMapping mapping) {
		
		this.mapping = mapping;
	}

//	@Override
	public Collection<Triple> call() { // throws Exception {
		
		try {

			Set<Pattern> patterns = mapping.getPatterns();
			
			if ( patterns != null && patterns.size() > 0 ) {
				
				// take the top n scored patterns
				List<Pattern> patternList	= PatternUtil.getTopNPattern(patterns, PatternSelectionStrategy.ALL, Integer.valueOf(NLPediaSettings.getInstance().getSetting("top.n.pattern")), Double.valueOf(NLPediaSettings.getInstance().getSetting("score.threshold.create.knowledge")));
				this.logger.info(String.format("Creating knowledge for mapping: %s and top-%s patterns", mapping.getProperty().getUri(), patternList.size()));

				if ( !patternList.isEmpty() ) {
					
					NamedEntityRecognizer ner = new NamedEntityRecognizer();
					PatternSearcher patternSearcher = new PatternSearcher(NLPediaSettings.getInstance().getSetting("sentenceIndexDirectory"));
					
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
					for ( Triple triple : this.tripleMap.values() ) {
						
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
		
		this.logger.info("Finished creating knowledge for: "  + this.mapping.getProperty().getUri() + " with " + this.tripleMap.values().size() + " triples.");
		
		return this.tripleMap.values();
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
						
						// replace it if it already exists
						if ( this.tripleMap.containsKey(triple.hashCode()) ) {
							
							triple = this.tripleMap.get(triple.hashCode());
						}
						triple.addLearnedFromSentences(sentence);
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
						
						Resource subject = ResourceManager.getInstance().getResource(subjectUri, subjectLabel, domainUri);
						if ( subject == null) throw new RuntimeException("2. subject null for uri:" + subjectUri+ " label: " + subjectLabel + " type:" + domainUri);

						Resource object = ResourceManager.getInstance().getResource(objectUri, objectLabel, rangeUri);
						if ( object == null) throw new RuntimeException("2. object null for uri:" + objectUri+ " label: " + objectLabel + " type:" + rangeUri);
						
						Triple triple = new Triple();
						triple.setSubject(subject);
						triple.setProperty(mapping.getProperty());
						triple.setObject(object);
						
						// replace it if it already exists
						if ( this.tripleMap.containsKey(triple.hashCode()) ) {
							
							triple = this.tripleMap.get(triple.hashCode());
						}
						triple.addLearnedFromSentences(sentence);
						triple.addLearnedFromPattern(pattern);
						// put the new one in
						this.tripleMap.put(triple.hashCode(), triple);
					}
				}
			}
		}
		catch (IndexOutOfBoundsException ioob) {

			this.logger.error("Could not create context for string " + sentence + ". NER tagged: " + nerTaggedSentence + " pattern: " + pattern.getNaturalLanguageRepresentationWithoutVariables(), ioob);
		}
		catch (Exception e) {
			
			this.logger.error("Could not create context for string " + sentence + ". NER tagged: " + nerTaggedSentence + " pattern: " + pattern.getNaturalLanguageRepresentationWithoutVariables(), e);
		}
	}
}
