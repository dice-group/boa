/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.persistance.kryo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

import org.apache.commons.lang.SerializationUtils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.ObjectBuffer;
import com.esotericsoftware.kryo.serialize.CollectionSerializer;
import com.esotericsoftware.kryo.serialize.MapSerializer;
import com.esotericsoftware.kryo.serialize.ReferenceFieldSerializer;

import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;


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
			
			return (PatternMapping) SerializationUtils.deserialize(new FileInputStream(new File(filepath)));
		}
		catch (FileNotFoundException e) {
			
			e.printStackTrace();
			String error = "Could not deserialize mapping from file " + filepath;
			logger.error(error, e);
			throw new RuntimeException(error, e);
		}
	}
}
