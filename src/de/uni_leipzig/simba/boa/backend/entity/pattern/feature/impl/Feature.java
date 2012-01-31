/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public final class Feature implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -8037032332488472509L;
    
    private final String name;
    private final List<Language> supportedLanguages;
    private final boolean useForPatternLearning;
    private final boolean normalizeGlobaly;
    private final boolean isZeroToOneValue;
    
    /**
     * @param supportedLanguages
     * @param useForPatternLearning
     * @param normalizeGlobaly
     * @param isZeroToOneValue
     */
    public Feature(String name, List<String> supportedLanguages, boolean isZeroToOneValue, boolean normalizeGlobaly, boolean useForPatternLearning) {

        this.name                   = name;
        this.useForPatternLearning  = useForPatternLearning;
        this.normalizeGlobaly       = normalizeGlobaly;
        this.isZeroToOneValue       = isZeroToOneValue;
        this.supportedLanguages     = new ArrayList<Language>();
        
        for ( String language : supportedLanguages )
            this.supportedLanguages.add(Language.getLanguage(language));
    }
    
    /**
     * @return the supportedLanguages
     */
    public List<Language> getSupportedLanguages() {
    
        return supportedLanguages;
    }

    
    /**
     * @return the useForPatternLearning
     */
    public boolean isUseForPatternLearning() {
    
        return useForPatternLearning;
    }

    
    /**
     * @return the normalizeGlobaly
     */
    public boolean isNormalizeGlobaly() {
    
        return normalizeGlobaly;
    }

    
    /**
     * @return the isZeroToOneValue
     */
    public boolean isZeroToOneValue() {
    
        return isZeroToOneValue;
    }

    /**
     * @return the name
     */
    public String getName() {

        return name;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Feature other = (Feature) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        }
        else
            if (!name.equals(other.name))
                return false;
        return true;
    }
}
