/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module;

import java.util.Set;

import de.uni_leipzig.simba.boa.backend.pipeline.interchangeobject.ModuleInterchangeObject;


/**
 * @author gerb
 *
 */
public abstract class AbstractPipelineModule implements PipelineModule {

	protected ModuleInterchangeObject moduleInterchangeObject;
	protected Set<Class<PipelineModule>> moduleDependencies;
	protected Set<String> moduleSettings;
	
	/**
	 * @return the moduleInterchangeObject
	 */
	public ModuleInterchangeObject getModuleInterchangeObject() {
	
		return moduleInterchangeObject;
	}

	/**
	 * @param moduleInterchangeObject the moduleInterchangeObject to set
	 */
	public void setModuleInterchangeObject(ModuleInterchangeObject moduleInterchangeObject) {
	
		this.moduleInterchangeObject = moduleInterchangeObject;
	}

	@Override
	public Set<Class<PipelineModule>> getModuleDependencies() {

		return this.moduleDependencies;
	}

	@Override
	public void setModuleDependencies(Set<Class<PipelineModule>> moduleDependencies) {

		this.moduleDependencies = moduleDependencies;
	}

	@Override
	public Set<String> getModuleSettings() {

		return this.moduleSettings;
	}

	@Override
	public void setModuleSettings(Set<String> moduleSettings) {

		this.moduleSettings = moduleSettings;
	}
}
