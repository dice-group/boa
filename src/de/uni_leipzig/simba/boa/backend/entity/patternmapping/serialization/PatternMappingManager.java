/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.entity.patternmapping.serialization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;

import org.apache.commons.collections.list.TreeList;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.comparator.PatternNlrComparator;
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
    
    private final String PATTERN_MAPPING_FOLDER = null;//NLPediaSettings.BOA_DATA_DIRECTORY + "patternmappings/";
    private final String DEFAULT_DATABASE       = null;//NLPediaSettings.BOA_DATA_DIRECTORY.replaceAll("/$", "");
    
    
    public static void main(String[] args) {
		
    	Map<String,List<String>> patterns = new TreeMap<String,List<String>>();
    	Set<PatternMapping> mappings = SerializationManager.getInstance().
    			deserializePatternMappings("/Users/gerb/Development/workspaces/experimental/boa/qa/en/patternmappings/");
		for ( PatternMapping mapping : mappings ) {
    		
    		for ( Pattern p : mapping.getPatterns()) {
    			
    			Matcher matcher = java.util.regex.Pattern.compile("\\d").matcher(p.getNaturalLanguageRepresentation());
    			Matcher year = java.util.regex.Pattern.compile("\\d{4}").matcher(p.getNaturalLanguageRepresentation());
    			Matcher rest = java.util.regex.Pattern.compile("\\d{1,2}").matcher(p.getNaturalLanguageRepresentation());
    			
				if ( matcher.find() ) {
					
					if ( !patterns.containsKey(mapping.getProperty().getUri()) ) patterns.put(mapping.getProperty().getUri(), new ArrayList<String>());
					
					String nlr = p.getNaturalLanguageRepresentation();
//					nlr = year.replaceAll("YEAR");
					nlr = nlr.replaceAll("\\d{4}", "YEAR");
					nlr = nlr.replaceAll("(-)?\\d+(\\.\\d*)?", "NUMBER");
					
					patterns.get(mapping.getProperty().getUri()).add(nlr);
				}
    		}
    	}
    	
    	for ( Map.Entry<String, List<String>> pattern : patterns.entrySet() ) {
    		
    		System.out.println(pattern.getKey());
    		Collections.sort(pattern.getValue());
    		for( String p : pattern.getValue() ) 
    			System.out.println("\t"+p);
    		
    		System.out.println(pattern.getValue().size());
    	}
	}
    
    
    private PatternMappingManager() {
        
//        mappingsInDatabases.put(
//                DEFAULT_DATABASE, 
//                SerializationManager.getInstance().deserializePatternMappings(PATTERN_MAPPING_FOLDER));
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
