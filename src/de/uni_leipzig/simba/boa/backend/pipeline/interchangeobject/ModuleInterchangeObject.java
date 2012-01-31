/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.interchangeobject;

import java.util.Map;
import java.util.Set;

import org.apache.lucene.store.Directory;

import de.uni_leipzig.simba.boa.backend.backgroundknowledge.BackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;


/**
 * @author gerb
 *
 */
public interface ModuleInterchangeObject {

    
    
	/**
	 * 
	 * @return
	 */
	public abstract Set<BackgroundKnowledge> getBackgroundKnowledge();
	
	/**
	 * 
	 */
	public abstract void setBackgroundKnowledge(Set<BackgroundKnowledge> backgroundKnowledge);
	
	/**
	 * @return
	 */
	public abstract Set<PatternMapping> getPatternMappings();
	
	/**
	 * @return
	 */
	public abstract void setPatternMappings(Set<PatternMapping> patternMappings);

	/**
	 * 
	 * @return
	 */
	public abstract Map<Integer, Property> getProperties();
	
	/**
	 * 
	 * @return
	 */
	public abstract void setProperties(Map<Integer,Property> properties);
	
	/**
     * 
     * @return
     */
    public abstract Directory getIndex();
    
    /**
     * 
     * @return
     */
    public abstract void setIndex(Directory directory);
}
