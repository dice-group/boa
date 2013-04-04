/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.entity.patternmapping.serialization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;

import org.apache.commons.collections.list.TreeList;
import org.apache.commons.lang3.StringUtils;

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
    
    private final String PATTERN_MAPPING_FOLDER = NLPediaSettings.BOA_DATA_DIRECTORY + "patternmappings/";
    private final String DEFAULT_DATABASE       = NLPediaSettings.BOA_DATA_DIRECTORY.replaceAll("/$", "");
    
    
    public static void main(String[] args) {
		
    	Map<String,List<String>> patternsList = new TreeMap<String,List<String>>();
    	Map<String,Set<String>> patternsSet = new TreeMap<String,Set<String>>();
    	Set<PatternMapping> mappings = SerializationManager.getInstance().deserializePatternMappings("/Users/gerb/Development/workspaces/experimental/boa/qa/en/patternmappings/");
    	
		for ( PatternMapping mapping : mappings ) {
    		
    		for ( Pattern p : mapping.getPatterns()) {
    			
				if ( !patternsList.containsKey(mapping.getProperty().getUri()) ) patternsList.put(mapping.getProperty().getUri(), new ArrayList<String>());
				if ( !patternsSet.containsKey(mapping.getProperty().getUri()) ) patternsSet.put(mapping.getProperty().getUri(), new HashSet<String>());
				
				String nlr = p.getNaturalLanguageRepresentationWithoutVariables();
//				nlr  = PatternMapping.generalize(nlr);
				
				patternsList.get(mapping.getProperty().getUri()).add(p.getNaturalLanguageRepresentation());
				patternsSet.get(mapping.getProperty().getUri()).add(nlr);
    		}
    	}

		int sizeList = 0;
		int sizeSet = 0;
		
    	for ( Map.Entry<String, List<String>> pattern : patternsList.entrySet() )
    		sizeList += pattern.getValue().size();

    	List<String> nlrs = new ArrayList<String>();
    	
    	for ( Map.Entry<String, Set<String>> pattern : patternsSet.entrySet() ) {
    		
//    		System.out.println(pattern.getKey());
    		nlrs.addAll(pattern.getValue());
    		Collections.sort(nlrs);
//    		for( String p : pattern.getValue() ) 
//    			System.out.println("\t"+p);
    		
//    			System.out.println(pattern.getValue().size());
    		sizeSet += pattern.getValue().size();
    	}
    	for (String s : new TreeSet<String>(nlrs)) System.out.println(s); 
    	
    	System.out.println(sizeSet);
    	System.out.println(sizeList);
	}
    
    
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
