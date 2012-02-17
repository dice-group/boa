/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.persistance.serialization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang.SerializationUtils;

import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;


/**
 * @author gerb
 *
 */
public class SerializationManager {

	private static final NLPediaLogger logger = new NLPediaLogger(SerializationManager.class);
	private static SerializationManager INSTANCE = null;
	
	private SerializationManager() {
		
	}
	
	public static SerializationManager getInstance(){
		
		if ( SerializationManager.INSTANCE == null ) {
			
			SerializationManager.INSTANCE = new SerializationManager();
		}
		
		return SerializationManager.INSTANCE;
	}
	
	/**
	 * 
	 * @param mapping
	 * @param filepath
	 */
	public void serializePatternMapping(PatternMapping mapping, String filepath) {
		
		try {
			
			SerializationUtils.serialize(mapping, new FileOutputStream(new File(filepath)));
		}
		catch (FileNotFoundException e) {
			
			e.printStackTrace();
			String error = "Could not serialize mapping: " + mapping.getProperty().getUri() + " to " + filepath;
			logger.error(error, e);
			throw new RuntimeException(error, e);
		}
	}
	
	/**
	 * 
	 * @param filepath
	 * @return
	 */
	public PatternMapping deserializePatternMapping(String filepath) {
		
		try {
			
		    logger.info("Deserializing mapping from: " + filepath);
			return (PatternMapping) SerializationUtils.deserialize(new FileInputStream(new File(filepath)));
		}
		catch (FileNotFoundException e) {
			
			e.printStackTrace();
			String error = "Could not deserialize mapping from file " + filepath;
			logger.error(error, e);
			throw new RuntimeException(error, e);
		}
	}
	
	/**
	 * 
	 * @param folder
	 * @return
	 */
	public Set<PatternMapping> deserializePatternMappings(String folder) {
		
		Set<PatternMapping> mappings = new HashSet<PatternMapping>();
		
		for (File mapping : FileUtils.listFiles(new File(folder), FileFilterUtils.suffixFileFilter(".bin"), null) ) {
		
			mappings.add(deserializePatternMapping(mapping.getAbsolutePath()));
		}
		
		return mappings;
	}

	/**
	 * 
	 * @param patternMappings
	 * @param patternMappingFolder
	 */
    public void serializePatternMappings(Collection<PatternMapping> patternMappings, String patternMappingFolder) {

        for (PatternMapping mapping : patternMappings) {
 
            this.serializePatternMapping(mapping, patternMappingFolder + mapping.getProperty().getPropertyLocalname() + "--" + mapping.getProperty().getUri().hashCode() + ".bin");
        }
    }

    /**
     * 
     * @param absoluteFile
     * @return
     */
    public Set<Triple> deserializeTriples(String absoluteFile) {

        try {
            
            return (Set<Triple>) SerializationUtils.deserialize(new FileInputStream(new File(absoluteFile)));
        }
        catch (FileNotFoundException e) {
            
            e.printStackTrace();
            String error = "Could not deserialize triples from file " + absoluteFile;
            logger.error(error, e);
            throw new RuntimeException(error, e);
        }
    }
    
    /**
     * 
     * @param mapping
     * @param filepath
     */
    public void serializeTriples(Set<Triple> triples, String filepath) {
        
        try {
            
            SerializationUtils.serialize((Serializable) triples, new FileOutputStream(new File(filepath)));
        }
        catch (FileNotFoundException e) {
            
            e.printStackTrace();
            String error = "Could not serialize triples to " + filepath;
            logger.error(error, e);
            throw new RuntimeException(error, e);
        }
    }
}
