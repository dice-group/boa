/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.interchangeobject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.store.Directory;

import de.uni_leipzig.simba.boa.backend.backgroundknowledge.BackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;
import de.uni_leipzig.simba.boa.backend.machinelearning.MachineLearningTool;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;


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

    /**
     * 
     * @return
     */
    public abstract MachineLearningTool getMachineLearningTool();
    
    /**
     * 
     * @return
     */
    public abstract void setMachineLearningTool(MachineLearningTool machineLearningTool);

    /**
     * 
     * @return
     */
    public abstract Map<String,List<Triple>> getGeneratedData();

    /**
     * 
     * @param mergedTriples
     */
    public abstract void setNewKnowledge(Map<String, Set<Triple>> mergedTriples);
    
    /**
     * 
     * @return
     */
    public abstract Map<String, Set<Triple>> getNewKnowledge();

	/**
	 * @author Maciej Janicki <macjan@o2.pl>
	 */
	public abstract void setClassesSurfaceForms(HashMap<String, ArrayList<String>> surfaceForms);

	/**
	 * @author Maciej Janicki <macjan@o2.pl>
	 */
	public abstract HashMap<String, ArrayList<String>> getClassesSurfaceForms();
}
