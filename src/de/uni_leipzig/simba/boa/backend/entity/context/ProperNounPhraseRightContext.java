/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.entity.context;

import java.util.List;

import org.apache.commons.lang3.StringUtils;


/**
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 *
 */
public class ProperNounPhraseRightContext extends RightContext {

    private List<String> nounPhrases;
    private String nearestNounPhrase;
    private int entityDistance = 1000;
    
    public ProperNounPhraseRightContext(String nerTaggedString, String sentence, String patternWithOutVariables, List<String> nounPhrases) throws IllegalArgumentException {
        super(nerTaggedString, sentence, patternWithOutVariables);
        
        this.nounPhrases = nounPhrases;
    }
    
    @Override
    public boolean containsSuitableEntity(String entityType) {

        // there can't be a suitable entity if there are no entites
        if ( nounPhrases.size() == 0 ) return false;
        int lastIndexOfPatternInSentence = this.sentence.indexOf(this.pattern);
		int lastPatternEnding = this.sentence.indexOf(this.pattern) + this.pattern.length();
        
        for (String nounPhrase : this.nounPhrases ) {
            
            // the last character of the noun phrase is after the last character of the pattern
            if ( this.sentence.indexOf(nounPhrase) < lastIndexOfPatternInSentence ) {
                
                continue; // all other noun phrases will be even further right to the pattern
            }
			// the noun phrase lies entirely inside the pattern
			else if ( this.sentence.indexOf(nounPhrase) + nounPhrase.length() < lastPatternEnding ) {
				continue;
			}
            else {
               
                // we found one entity left of the pattern, so that's it
                this.nearestNounPhrase = nounPhrase;
                String betweenSubstrings = StringUtils.substringBetween(sentence, pattern, nearestNounPhrase);
                this.entityDistance = betweenSubstrings != null ? StringUtils.countMatches(betweenSubstrings, " ") : 0;
                return true;
            }
        }
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
