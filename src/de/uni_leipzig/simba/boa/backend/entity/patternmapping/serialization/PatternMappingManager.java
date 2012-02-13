/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.entity.patternmapping.serialization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.comparator.PatternMappingUriComparator;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;
import de.uni_leipzig.simba.boa.backend.persistance.serialization.SerializationManager;


/**
 * @author gerb
 *
 */
public class PatternMappingManager {

    private static List<PatternMapping> mappings;
    
    private final String PATTERN_MAPPING_FOLDER         = NLPediaSettings.BOA_DATA_DIRECTORY + "patternmappings/";
    
    public PatternMappingManager() {
        
        mappings = new ArrayList<PatternMapping>(SerializationManager.getInstance().deserializePatternMappings(PATTERN_MAPPING_FOLDER));
    }
    
    /**
     * 
     * @param filepath
     * @return
     */
    public List<PatternMapping> getPatternMappings() {
        
        return mappings;
    }

    /**
     * 
     * @param uri
     * @param database
     * @return
     */
    public PatternMapping getPatternMapping(String uri, String database) {

        for  (PatternMapping mapping : mappings ) {
            
            if ( mapping.getProperty().getUri().equals(uri) ) return mapping; 
        }
        return null;
    }

    /**
     * 
     * @param database
     * @param naturalLanguageRepresentation
     * @return
     */
    public int findPatternMappingsWithSamePattern(String naturalLanguageRepresentation) {

        int numberOfSamePatterns = 0;
        
        for ( PatternMapping mapping : mappings )
            for (Pattern pattern : mapping.getPatterns())
                if ( pattern.getNaturalLanguageRepresentation().equalsIgnoreCase(naturalLanguageRepresentation) ) {
                    
                    numberOfSamePatterns++;
                    break; // there will be only one pattern per pattern mapping with the same natural language representation, so go to next pattern mapping
                }
                
        return numberOfSamePatterns;
    }        
}
