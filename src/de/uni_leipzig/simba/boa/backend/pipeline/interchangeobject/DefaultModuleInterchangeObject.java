/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.interchangeobject;

import java.util.Set;

import de.uni_leipzig.simba.boa.backend.backgroundknowledge.BackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;


/**
 * @author gerb
 *
 */
public class DefaultModuleInterchangeObject implements ModuleInterchangeObject {

	private Set<BackgroundKnowledge> backgroundKnowledge;
	private Set<PatternMapping> patternMappings;
	
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
