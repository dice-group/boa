/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.knowledgecreation;

import java.util.ArrayList;
import java.util.List;

import de.uni_leipzig.simba.boa.backend.Constants;
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
import de.uni_leipzig.simba.boa.backend.rdf.uri.impl.ClassesUriRetrieval;
import de.uni_leipzig.simba.boa.backend.rdf.uri.impl.FeatureBasedDisambiguation;


/**
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 *
 */
public class TripleGenerator {
    
    private final NLPediaLogger logger = new NLPediaLogger(TripleGenerator.class);
    
    private NamedEntityRecognition nerTagger;
    private PartOfSpeechTagger posTagger;
	private ClassesUriRetrieval classesUriRetrieval;
	FeatureBasedDisambiguation disambiguation = new FeatureBasedDisambiguation();
    
    public TripleGenerator() {
        
        if ( NLPediaSettings.getBooleanSetting("useProperNounPhraseExtraction") )
            this.nerTagger  = NaturalLanguageProcessingToolFactory.getInstance().createDefaultNamedEntityRecognition();
        else 
            this.posTagger  = NaturalLanguageProcessingToolFactory.getInstance().createDefaultPartOfSpeechTagger();

		// @author Maciej Janicki
		if (NLPediaSettings.getBooleanSetting("rdfTypeKnowledgeGeneration"))
			this.classesUriRetrieval = new ClassesUriRetrieval();
    }
    
    public void close(){
    	
    	this.disambiguation.close();
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

            if (beginsWithDomain) {
    
                if (leftContext.containsSuitableEntity(domainUri) && rightContext.containsSuitableEntity(rangeUri)) {
    
                    if ( leftContext.getSuitableEntityDistance(domainUri) <= NLPediaSettings.getIntegerSetting("contextLookAhead") 
                            	&& rightContext.getSuitableEntityDistance(rangeUri) <= NLPediaSettings.getIntegerSetting("contextLookAhead") ) {
                        
                        String subjectLabel = leftContext.getSuitableEntity(domainUri);
                        String objectLabel = rightContext.getSuitableEntity(rangeUri);
                        
                        List<String> entities = getEntities(mergeTagsInSentences(taggedSentence));
                        
                        String subjectUri   = this.disambiguation.getUri(subjectLabel, objectLabel, entities);
                        String objectUri    = this.disambiguation.getUri(objectLabel, subjectLabel, entities);
                        
                        if ( subjectUri.equals(Constants.NON_GOOD_URL_FOUND) 
                        		|| objectUri.equals(Constants.NON_GOOD_URL_FOUND) ) return null;
                        
                        Resource subject    = new Resource(subjectUri, subjectLabel, domainUri);
                        Resource object     = new Resource(objectUri, objectLabel, rangeUri);
                        
                        Triple triple = new Triple(subject, mapping.getProperty(), object);
                        triple.addLearnedFromPattern(pattern);
                        triple.addLearnedFromSentences(sentence);
                        
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

                        String subjectUri   = this.disambiguation.getUri(subjectLabel, objectLabel);
                        String objectUri    = this.disambiguation.getUri(objectLabel, subjectLabel);
                        
                        if ( subjectUri.equals(Constants.NON_GOOD_URL_FOUND) 
                        		|| objectUri.equals(Constants.NON_GOOD_URL_FOUND) ) return null;
                        
                        // this is necessary, because we would generate triples with the same object & subject
                        if ( subjectUri.equals(objectUri) ) return null;
                        
                        Resource subject    = new Resource(subjectUri, subjectLabel, domainUri);
                        Resource object     = new Resource(objectUri, objectLabel, rangeUri);
    
                        Triple triple = new Triple(subject, mapping.getProperty(), object);
                        triple.addLearnedFromPattern(pattern);
                        triple.addLearnedFromSentences(sentence);
                        
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
    
    /**
     * 
     * @param mergedTaggedSentence
     * @return
     */
    private List<String> getEntities(List<String> mergedTaggedSentence){
        
        List<String> entities = new ArrayList<String>();
        for (String entity :  mergedTaggedSentence) {

            if (entity.endsWith("_PERSON") ) entities.add(entity.replace("_PERSON", ""));
            if (entity.endsWith("_MISC")) entities.add(entity.replace("_MISC", ""));
            if (entity.endsWith("_PLACE")) entities.add(entity.replace("_PLACE", ""));
            if (entity.endsWith("_ORGANIZATION")) entities.add(entity.replace("_ORGANIZATION", ""));
            if (entity.endsWith("_NNP")) entities.add(entity.replace("_NNP", ""));
        }
        
        return entities;
    }
    
    /**
     * 
     */
    public List<String> mergeTagsInSentences(String nerTaggedSentence) {

        List<String> tokens = new ArrayList<String>();
        String lastToken = "";
        String lastTag = "";
        String currentTag = "";
        String newToken = "";
        
        for (String currentToken : nerTaggedSentence.split(" ")) {

            currentTag = currentToken.substring(currentToken.lastIndexOf("_") + 1);

            // we need to check for the previous token's tag
            if (!currentToken.endsWith("_OTHER")) {

                // we need to merge the cell
                if (currentTag.equals(lastTag)) {

                    newToken = lastToken.substring(0, lastToken.lastIndexOf("_")) + " " + currentToken;
                    tokens.set(tokens.size() - 1, newToken);
                }
                // different tag found so just add it
                else
                    tokens.add(currentToken);
            }
            else {

                // add the current token
                tokens.add(currentToken);
            }
            // update for next iteration
            lastToken = tokens.get(tokens.size() - 1);
            lastTag = currentTag;
        }
        return tokens;
    }
}


////String objectUri    = uriRetrieval.getUri(objectLabel);
//// @author Maciej Janicki
//String objectUri = null;
//if (NLPediaSettings.getBooleanSetting("rdfTypeKnowledgeGeneration")) {
//	objectUri	= this.classesUriRetrieval.getUri(objectLabel);
//} else {
//	objectUri    = uriRetrieval.getUri(objectLabel);
//}
//if (objectUri == null)
//	return null;

////String objectUri = uriRetrieval.getUri(objectLabel);
//// @author Maciej Janicki
//String objectUri = null;
//if (NLPediaSettings.getBooleanSetting("rdfTypeKnowledgeGeneration")) {
//	objectUri	= this.classesUriRetrieval.getUri(objectLabel);
//} else {
//	objectUri    = uriRetrieval.getUri(objectLabel);
//}
//if (objectUri == null)
//	return null;
