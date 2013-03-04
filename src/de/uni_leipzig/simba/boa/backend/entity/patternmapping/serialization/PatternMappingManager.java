/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.entity.patternmapping.serialization;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.persistance.serialization.SerializationManager;


/**
 * @author gerb
 *
 */
public final class PatternMappingManager {

    private final NLPediaLogger logger = new NLPediaLogger(PatternMappingManager.class);
    private static Map<String, Set<PatternMapping>> mappingsInDatabases = new LinkedHashMap<String, Set<PatternMapping>>();
    private static final PatternMappingManager INSTANCE = new PatternMappingManager();
    
    private final String PATTERN_MAPPING_FOLDER = NLPediaSettings.BOA_DATA_DIRECTORY + "patternmappings/";
    private final String DEFAULT_DATABASE       = NLPediaSettings.BOA_DATA_DIRECTORY.replaceAll("/$", "");
    
    private PatternMappingManager() {
        
        mappingsInDatabases.put(
                DEFAULT_DATABASE, 
                SerializationManager.getInstance().deserializePatternMappings(PATTERN_MAPPING_FOLDER));
    }
    
    public static PatternMappingManager getInstance(){
        
        return PatternMappingManager.INSTANCE;
    }
    
    /**
     * 
     * @param filepath
     * @return
     */
    public Set<PatternMapping> getPatternMappings() {
        
        return mappingsInDatabases.get(DEFAULT_DATABASE);
    }

    /**
     * 
     * @param uri
     * @param database
     * @return
     */
    public PatternMapping getPatternMapping(String uri, String database) {

        for  (PatternMapping mapping : PatternMappingManager.mappingsInDatabases.get(database) ) {
            
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
    public List<Pattern> findPatternMappingsWithSamePattern(String naturalLanguageRepresentation) {

    	List<Pattern> patterns = new ArrayList<Pattern>();
    	
        for ( PatternMapping mapping : mappingsInDatabases.get(DEFAULT_DATABASE) )
            for (Pattern pattern : mapping.getPatterns())
                if ( pattern.getNaturalLanguageRepresentation().equalsIgnoreCase(naturalLanguageRepresentation) ) {
                    
                    patterns.add(pattern);
                    break; // there will be only one pattern per pattern mapping with the same natural language representation, so go to next pattern mapping
                }
                
        return patterns;
    }

    /**
     * 
     * @return
     */
    public Map<String, Set<PatternMapping>> getPatternMappingsInDatabases() {

        for ( String database : NLPediaSettings.getSetting("patternMappingDatabases").split(";")) {
            
            this.logger.info("Reading mappings from database: " + database);
            
            String path = database.endsWith("/") ? database + Constants.PATTERN_MAPPINGS_PATH : database + "/" + Constants.PATTERN_MAPPINGS_PATH; 
            database = database.endsWith("/") ? database : database + "/";
            
            if ( database.equals(NLPediaSettings.BOA_DATA_DIRECTORY) ) {
                
//                because of the construtor the default database is already in the map, so we dont need to do anything
//                mappingsInDatabases.put(database.replaceAll("/$", ""), new HashSet<PatternMapping>(mappings));
            }
            else {

                mappingsInDatabases.put(database.replaceAll("/$", ""), SerializationManager.getInstance().deserializePatternMappings(path));
            }
            
//            for (PatternMapping mapping : SerializationManager.getInstance().deserializePatternMappings(path) ) {
//                
//                // only add pattern mappings with more than 0 patterns to the view
//                if (mapping.getPatterns().size() > 0 ) {
//                    
//                    if ( mappingsInDatabases.containsKey(database) ) {
//                        
//                        mappingsInDatabases.get(database).add(mapping);
//                    }
//                    // the first mapping in a new folder 
//                    else {
//                        
//                        List<PatternMapping> mappings = new ArrayList<PatternMapping>();
//                        mappings.add(mapping);
//                        mappingsInDatabases.put(database, mappings);
//                    }
//                }
//            }
        }
        return mappingsInDatabases;
    }        
}
