/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.dao.serialization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.comparator.PatternMappingUriComparator;
import de.uni_leipzig.simba.boa.backend.persistance.serialization.SerializationManager;


/**
 * @author gerb
 *
 */
public class PatternMappingManager {

    private Map<String,List<PatternMapping>> mappings;
    
    private final String PATTERN_MAPPING_FOLDER         = NLPediaSettings.BOA_DATA_DIRECTORY + "patternmappings/";
    
    public PatternMappingManager() {
        
        this.mappings = new HashMap<String,List<PatternMapping>>();
    }
    
    /**
     * 
     * @param filepath
     * @return
     */
    public List<PatternMapping> getPatternMappings(String filepath) {
        
        if ( this.mappings.containsKey(filepath) ) {
            
            return this.mappings.get(filepath);
        }
        else {
            
            List<PatternMapping> mappings = new ArrayList<PatternMapping>(SerializationManager.getInstance().deserializePatternMappings(PATTERN_MAPPING_FOLDER));
            Collections.sort(mappings, new PatternMappingUriComparator());
            this.mappings.put(filepath, mappings);
            
            return mappings;
        }
    }

    /**
     * 
     * @param uri
     * @param database
     * @return
     */
    public PatternMapping getPatternMapping(String uri, String database) {

        for  (PatternMapping mapping : this.mappings.get(database) ) {
            
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
    public int findPatternMappingsWithSamePattern(String database, String naturalLanguageRepresentation) {

        int numberOfSamePatterns = 0;
        
        for ( PatternMapping mapping : this.mappings.get(database) )
            for (Pattern pattern : mapping.getPatterns())
                if ( pattern.getNaturalLanguageRepresentation().equalsIgnoreCase(naturalLanguageRepresentation) ) {
                    
                    numberOfSamePatterns++;
                    break; // there will be only one pattern per pattern mapping with the same natural language representation, so go to next pattern mapping
                }
                
        return numberOfSamePatterns;
    }        
}
