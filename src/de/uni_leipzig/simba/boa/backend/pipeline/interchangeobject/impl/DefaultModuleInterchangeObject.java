/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.interchangeobject.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.uni_leipzig.simba.boa.backend.backgroundknowledge.BackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.pipeline.interchangeobject.ModuleInterchangeObject;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;


/**
 * @author gerb
 *
 */
public class DefaultModuleInterchangeObject implements ModuleInterchangeObject {

	private Set<BackgroundKnowledge> backgroundKnowledge;
	private Set<PatternMapping> patternMappings;
	private Map<Integer,Property> properties;
	
	public DefaultModuleInterchangeObject(){
		
		this.backgroundKnowledge	= new HashSet<BackgroundKnowledge>();
		this.patternMappings		= new HashSet<PatternMapping>();
		this.properties				= new HashMap<Integer, Property>();
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
}
