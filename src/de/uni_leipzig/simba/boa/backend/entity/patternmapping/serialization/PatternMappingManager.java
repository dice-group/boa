/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.entity.patternmapping.serialization;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;
import de.uni_leipzig.simba.boa.backend.persistance.serialization.SerializationManager;


/**
 * @author gerb
 *
 */
public class PatternMappingManager {

    private static List<PatternMapping> mappings;
    private static PatternMappingManager INSTANCE;
    
    private final String PATTERN_MAPPING_FOLDER         = NLPediaSettings.BOA_DATA_DIRECTORY + "patternmappings/";
    
    private PatternMappingManager() {
        
        mappings = new ArrayList<PatternMapping>(SerializationManager.getInstance().deserializePatternMappings(PATTERN_MAPPING_FOLDER));
    }
    
    public static PatternMappingManager getInstance(){
        
        if (PatternMappingManager.INSTANCE == null) {
            
            PatternMappingManager.INSTANCE = new PatternMappingManager();
        }
        
        return PatternMappingManager.INSTANCE;
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

    /**
     * 
     * @return
     */
    public Map<String, List<PatternMapping>> getPatternMappingsInDatabases() {

        Map<String, List<PatternMapping>> mappingsInDatabases = new LinkedHashMap<String, List<PatternMapping>>();
        
        for ( String database : NLPediaSettings.getSetting("patternMappingDatabases").split(";")) {
            
            System.out.println(database);
            
            String path = database.endsWith("/") ? database + Constants.PATTERN_MAPPINGS_PATH : database + "/" + Constants.PATTERN_MAPPINGS_PATH; 
            
            for (PatternMapping mapping : SerializationManager.getInstance().deserializePatternMappings(path) ) {
                
                // only add pattern mappings with more than 0 patterns to the view
                if (mapping.getPatterns().size() > 0 ) {
                    
                    if ( mappingsInDatabases.containsKey(database) ) {
                        
                        mappingsInDatabases.get(database).add(mapping);
                    }
                    // the first mapping in a new folder 
                    else {
                        
                        List<PatternMapping> mappings = new ArrayList<PatternMapping>();
                        mappings.add(mapping);
                        mappingsInDatabases.put(database, mappings);
                    }
                }
            }
        }
        return mappingsInDatabases;
    }        
}
