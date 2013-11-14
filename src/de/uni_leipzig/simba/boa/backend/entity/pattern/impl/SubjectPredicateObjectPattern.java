/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.entity.pattern.impl;

import java.util.Map;

import de.uni_leipzig.simba.boa.backend.entity.pattern.AbstractPattern;


/**
 * @author gerb
 *
 */
public class SubjectPredicateObjectPattern extends AbstractPattern implements Comparable<SubjectPredicateObjectPattern> {

    /**
     * 
     */
    private static final long serialVersionUID = -3762777405223269108L;
    
    /**
     * 
     * @param patternString
     */
    public SubjectPredicateObjectPattern(String patternString) {
        super(patternString);
    }

    public SubjectPredicateObjectPattern() {
        super();
    }

    /**
     * @return the NLR with ?D? and ?R?
     */
    public String getNaturalLanguageRepresentationWithoutVariables() {
        
        return this.naturalLanguageRepresentation.substring(0, this.naturalLanguageRepresentation.length() - 3).substring(3).trim();
    }

	@Override
	public int compareTo(SubjectPredicateObjectPattern o) {

		return this.naturalLanguageRepresentation.compareTo(o.naturalLanguageRepresentation);
	}
}
