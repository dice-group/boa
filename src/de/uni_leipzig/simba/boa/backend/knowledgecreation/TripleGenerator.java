/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.knowledgecreation;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.entity.context.Context;
import de.uni_leipzig.simba.boa.backend.entity.context.LeftContext;
import de.uni_leipzig.simba.boa.backend.entity.context.ProperNounPhraseLeftContext;
import de.uni_leipzig.simba.boa.backend.entity.context.ProperNounPhraseRightContext;
import de.uni_leipzig.simba.boa.backend.entity.context.RightContext;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.impl.SubjectPredicateObjectPattern;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.NaturalLanguageProcessingToolFactory;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.namedentityrecognition.NamedEntityRecognition;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.partofspeechtagger.PartOfSpeechTagger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Resource;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;
import de.uni_leipzig.simba.boa.backend.rdf.uri.UriRetrieval;
import de.uni_leipzig.simba.boa.backend.rdf.uri.impl.MeshupUriRetrieval;
import de.uni_leipzig.simba.boa.backend.rdf.uri.impl.ClassesUriRetrieval;


/**
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 *
 */
public class TripleGenerator {
    
    private final NLPediaLogger logger = new NLPediaLogger(TripleGenerator.class);
    
    private NamedEntityRecognition nerTagger;
    private PartOfSpeechTagger posTagger;
	private ClassesUriRetrieval classesUriRetrieval;
    
    public TripleGenerator() {
        
        if ( NLPediaSettings.getBooleanSetting("useProperNounPhraseExtraction") )
            this.nerTagger  = NaturalLanguageProcessingToolFactory.getInstance().createDefaultNamedEntityRecognition();
        else 
            this.posTagger  = NaturalLanguageProcessingToolFactory.getInstance().createDefaultPartOfSpeechTagger();

		// @author Maciej Janicki
		if (NLPediaSettings.getBooleanSetting("rdfTypeKnowledgeGeneration"))
			this.classesUriRetrieval = new ClassesUriRetrieval();
    }
    
    /**
     * 
     * @param mapping
     * @param pattern
     * @param sentence
     * @return
     */
    public Triple createTriple(PatternMapping mapping, Pattern pattern, String sentence) {
     
        if ( NLPediaSettings.getBooleanSetting("useProperNounPhraseExtraction") ) 
            return this.createTripleWithNamedEntityRecognition(mapping, pattern, sentence);
        else 
            return this.createTripleWithPartOfSpeechTagging(mapping, pattern, sentence);
    }

    /**
     * 
     * @param mapping
     * @param pattern
     * @param sentence
     * @return
     */
    private Triple createTripleWithPartOfSpeechTagging(PatternMapping mapping, Pattern pattern, String sentence) {

        String posTaggedSentence    = this.posTagger.getAnnotatedString(sentence);
        
        System.out.println("Noun Phrases: " + this.posTagger.getNounPhrases(sentence));
        
		try {
			Context leftContext     = new ProperNounPhraseLeftContext(posTaggedSentence, sentence, pattern.getNaturalLanguageRepresentationWithoutVariables(), this.posTagger.getNounPhrases(sentence));
			Context rightContext    = new ProperNounPhraseRightContext(posTaggedSentence, sentence, pattern.getNaturalLanguageRepresentationWithoutVariables(), this.posTagger.getNounPhrases(sentence));
        	return this.extractTriple(leftContext, rightContext, sentence, posTaggedSentence, mapping, pattern);
		} catch (StringIndexOutOfBoundsException ex) {	// @author Maciej Janicki
            this.logger.debug("Could not create context for string " + sentence + ". TAGGED: " + posTaggedSentence + " pattern: " + pattern.getNaturalLanguageRepresentationWithoutVariables(), ex);
			return null;
		}
    }
    
    public static void main(String[] args) {

        NLPediaSetup s = new NLPediaSetup(true); 
        String sentence3 = "The versificator is a fictional device employed by Ingsoc in the novel `` Nineteen Eighty-Four '' by George Orwell .";
        String sentence2 = "The title is a play on James Joyce 's semi-autobiographical novel `` A Portrait of the Artist as a Young Man '' .";
        String sentence1 = "The title is a play on James Joyce 's semi-autobiographical novel A Portrait of the Artist as a Young Man .";
        
        Pattern pattern = new SubjectPredicateObjectPattern("?R? 's semi-autobiographical novel `` A ?D?");
        Property property = new Property("uri", "range", "domain");
        PatternMapping mapping = new PatternMapping();
        mapping.setProperty(property);
        mapping.addPattern(pattern);
        
        TripleGenerator tg = new TripleGenerator();
        System.out.println(tg.createTripleWithPartOfSpeechTagging(mapping, pattern, sentence1));
        System.out.println(tg.createTripleWithPartOfSpeechTagging(mapping, pattern, sentence2));
    }

    /**
     * 
     * @param mapping
     * @param pattern
     * @param sentence
     * @return
     */
    private Triple createTripleWithNamedEntityRecognition(PatternMapping mapping, Pattern pattern, String sentence) {

        String nerTaggedSentence    = this.nerTagger.getAnnotatedString(sentence);
        
        Context leftContext     = new LeftContext(nerTaggedSentence, sentence, pattern.getNaturalLanguageRepresentationWithoutVariables());
        Context rightContext    = new RightContext(nerTaggedSentence, sentence, pattern.getNaturalLanguageRepresentationWithoutVariables());
        
        return this.extractTriple(leftContext, rightContext, sentence, nerTaggedSentence, mapping, pattern);
    }
    
    /**
     * 
     * @param leftContext
     * @param rightContext
     * @param sentence
     * @param taggedSentence
     * @param mapping
     * @param pattern
     * @return
     */
    private Triple extractTriple(Context leftContext, Context rightContext, String sentence, String taggedSentence, PatternMapping mapping, Pattern pattern) {
       
        try {
            
            boolean beginsWithDomain = pattern.getNaturalLanguageRepresentation().startsWith("?D?") ? true : false;
            String domainUri         = mapping.getProperty().getRdfsDomain();
            String rangeUri          = mapping.getProperty().getRdfsRange();

			System.out.println(pattern.getNaturalLanguageRepresentation());
			System.out.println(taggedSentence);
    
            if (beginsWithDomain) {
    
                if (leftContext.containsSuitableEntity(domainUri) && rightContext.containsSuitableEntity(rangeUri)) {
    
                    if ( leftContext.getSuitableEntityDistance(domainUri) <= NLPediaSettings.getIntegerSetting("contextLookAhead") 
                            	&& rightContext.getSuitableEntityDistance(rangeUri) <= NLPediaSettings.getIntegerSetting("contextLookAhead") ) {
                        
                        String subjectLabel = leftContext.getSuitableEntity(domainUri);
                        String objectLabel = rightContext.getSuitableEntity(rangeUri);
                        
                        UriRetrieval uriRetrieval = new MeshupUriRetrieval();
                        String subjectUri   = uriRetrieval.getUri(subjectLabel);
                        //String objectUri    = uriRetrieval.getUri(objectLabel);
						// @author Maciej Janicki
						String objectUri = null;
						if (NLPediaSettings.getBooleanSetting("rdfTypeKnowledgeGeneration")) {
							objectUri	= this.classesUriRetrieval.getUri(objectLabel);
						} else {
                        	objectUri    = uriRetrieval.getUri(objectLabel);
						}
						if (objectUri == null)
							return null;
                        
                        Resource subject    = new Resource(subjectUri, subjectLabel, domainUri);
                        Resource object     = new Resource(objectUri, objectLabel, rangeUri);
                        
                        Triple triple = new Triple(subject, mapping.getProperty(), object);
                        triple.addLearnedFromPattern(pattern);
                        triple.addLearnedFromSentences(sentence);
                        
                        System.out.println(triple);
                        
                        return triple;
                    }
                }
            }
            else {
    
                if (leftContext.containsSuitableEntity(rangeUri) && rightContext.containsSuitableEntity(domainUri)) {
                    
                    // left context contains object, right context contains subject
                    if (leftContext.getSuitableEntityDistance(rangeUri) <= NLPediaSettings.getIntegerSetting("contextLookAhead") 
                            && rightContext.getSuitableEntityDistance(domainUri) <= NLPediaSettings.getIntegerSetting("contextLookAhead") ) {
                        
                        String objectLabel = leftContext.getSuitableEntity(rangeUri);
                        String subjectLabel = rightContext.getSuitableEntity(domainUri);
                        
                        UriRetrieval uriRetrieval = new MeshupUriRetrieval();
                        //String objectUri = uriRetrieval.getUri(objectLabel);
						// @author Maciej Janicki
						String objectUri = null;
						if (NLPediaSettings.getBooleanSetting("rdfTypeKnowledgeGeneration")) {
							objectUri	= this.classesUriRetrieval.getUri(objectLabel);
						} else {
                        	objectUri    = uriRetrieval.getUri(objectLabel);
						}
						if (objectUri == null)
							return null;
                        String subjectUri = uriRetrieval.getUri(subjectLabel);
                        
                        Resource subject    = new Resource(subjectUri, subjectLabel, domainUri);
                        Resource object     = new Resource(objectUri, objectLabel, rangeUri);
    
                        Triple triple = new Triple(subject, mapping.getProperty(), object);
                        triple.addLearnedFromPattern(pattern);
                        triple.addLearnedFromSentences(sentence);
                        
                        System.out.println(triple);
                        
                        return triple;
                    }
                }
            }
        }
        catch (IllegalArgumentException e) {
            
            this.logger.debug("Could not create context for string " + sentence + ". TAGGED: " + taggedSentence + " pattern: " + pattern.getNaturalLanguageRepresentationWithoutVariables(), e);
        }

        // we could not extract a triple
        return null;
    }
}
