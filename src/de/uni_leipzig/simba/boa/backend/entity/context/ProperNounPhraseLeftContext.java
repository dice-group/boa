/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.entity.context;

import java.util.List;

import org.apache.commons.lang3.StringUtils;


/**
 * @author gerb
 *
 */
public class ProperNounPhraseLeftContext extends LeftContext {

    private List<String> nounPhrases;
    private String nearestNounPhrase = "";
    private int entityDistance = 1000;
    
    public ProperNounPhraseLeftContext(String taggedString, String sentence, String patternWithOutVariables, List<String> nounPhrases) throws IllegalArgumentException, StringIndexOutOfBoundsException {
        super(taggedString, sentence, patternWithOutVariables);
        
        this.nounPhrases = nounPhrases;
    }
    
    public boolean containsSuitableEntity(String entityType) {

        // there can't be a suitable entity if there are no entites
        if ( nounPhrases.size() == 0 ) return false;
        int lastIndexOfPatternInSentence = this.sentence.indexOf(this.pattern) + this.pattern.length();
        
        for (String nounPhrase : this.nounPhrases ) {
            
            // the last character of the noun phrase is after the last character of the pattern
            if ( this.sentence.indexOf(nounPhrase) + nounPhrase.length() > lastIndexOfPatternInSentence ) {
                
                break; // all other noun phrases will be even further right to the pattern
            }
            else {
               
                // we found one entity left of the pattern, so that's it
                this.nearestNounPhrase = nounPhrase;
                String betweenSubstrings = StringUtils.substringBetween(sentence, nearestNounPhrase, pattern);
                this.entityDistance = betweenSubstrings != null ? StringUtils.countMatches(betweenSubstrings, " ") : 0;
            }
        }
        if ( !this.nearestNounPhrase.isEmpty() )            
            return true;

        // no nnp(s) left of the pattern
        return false;
    }
    
    @Override
    public int getSuitableEntityDistance(String entityType) {
        
        return this.entityDistance;
    }
    
    @Override
    public String getSuitableEntity(String entityType) {
        
        return this.nearestNounPhrase;
    }
}
