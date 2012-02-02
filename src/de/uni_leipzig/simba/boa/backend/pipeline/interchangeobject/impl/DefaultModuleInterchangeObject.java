/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.interchangeobject.impl;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.store.Directory;

import de.uni_leipzig.simba.boa.backend.backgroundknowledge.BackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.machinelearning.MachineLearningTool;
import de.uni_leipzig.simba.boa.backend.pipeline.interchangeobject.ModuleInterchangeObject;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;


/**
 * @author gerb
 *
 */
public class DefaultModuleInterchangeObject implements ModuleInterchangeObject {

	private Set<BackgroundKnowledge> backgroundKnowledge;
	private Set<PatternMapping> patternMappings;
	private Map<Integer,Property> properties;
    private Directory index;
    private MachineLearningTool machineLearningTool;
    private Map<String,Set<Triple>> triples;
	
	public DefaultModuleInterchangeObject(){
		
		this.backgroundKnowledge  = new HashSet<BackgroundKnowledge>();
		this.patternMappings      = new HashSet<PatternMapping>();
		this.properties           = new HashMap<Integer, Property>();
		this.triples              = new HashMap<String,Set<Triple>>();
	}
	
	@Override
	public Set<BackgroundKnowledge> getBackgroundKnowledge() {

		return this.backgroundKnowledge;
	}

	@Override
	public void setBackgroundKnowledge(Set<BackgroundKnowledge> backgroundKnowledge) {

		this.backgroundKnowledge = backgroundKnowledge;
	}

	@Override
	public Set<PatternMapping> getPatternMappings() {

		return this.patternMappings;
	}

	@Override
	public void setPatternMappings(Set<PatternMapping> patternMappings) {

		this.patternMappings = patternMappings;
	}

	@Override
	public Map<Integer,Property> getProperties() {

		return this.properties;
	}

	@Override
	public void setProperties(Map<Integer,Property> properties) {

		this.properties = properties;		
	}

    @Override
    public Directory getIndex() {

        return this.index;
    }

    @Override
    public void setIndex(Directory index) {

        this.index = index;
    }

    @Override
    public MachineLearningTool getMachineLearningTool() {

        return this.machineLearningTool;
    }

    @Override
    public void setMachineLearningTool(MachineLearningTool machineLearningTool) {

        this.machineLearningTool = machineLearningTool;
    }

    @Override
    public Map<String, List<Triple>> getGeneratedData() {

        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setNewKnowledge(Map<String, Set<Triple>> mergedTriples) {

        this.triples = mergedTriples;
    }

    @Override
    public Map<String, Set<Triple>> getNewKnowledge() {

        return this.triples;
    }
}
