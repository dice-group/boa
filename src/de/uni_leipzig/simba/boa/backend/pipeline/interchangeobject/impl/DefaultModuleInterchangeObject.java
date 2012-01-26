/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.interchangeobject.impl;

import java.util.HashSet;
import java.util.Set;

import de.uni_leipzig.simba.boa.backend.backgroundknowledge.BackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.pipeline.interchangeobject.ModuleInterchangeObject;


/**
 * @author gerb
 *
 */
public class DefaultModuleInterchangeObject implements ModuleInterchangeObject {

	private Set<BackgroundKnowledge> backgroundKnowledge;
	private Set<PatternMapping> patternMappings;
	
	public DefaultModuleInterchangeObject(){
		
		this.backgroundKnowledge = new HashSet<BackgroundKnowledge>();
		this.patternMappings = new HashSet<PatternMapping>();
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
}
