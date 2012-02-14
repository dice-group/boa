/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.entity.pattern.impl;

import de.uni_leipzig.simba.boa.backend.entity.pattern.AbstractPattern;


/**
 * @author gerb
 *
 */
public class SubjectObjectPredicatePattern extends AbstractPattern {

    /**
     * 
     */
    private static final long serialVersionUID = -204649984648292668L;

    
    public SubjectObjectPredicatePattern(String strPattern){
    	super(strPattern);
    }
    
    /**
     * @return the NLR without ?D? and ?R?
     */
    @Override
    public String getNaturalLanguageRepresentationWithoutVariables() {
          return this.naturalLanguageRepresentation.substring(7).trim();
    }
}
