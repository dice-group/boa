/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module;

import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.pipeline.interchangeobject.ModuleInterchangeObject;


/**
 * @author gerb
 *
 */
public abstract class AbstractPipelineModule implements PipelineModule {

	protected ModuleInterchangeObject moduleInterchangeObject;
	protected List<Class<PipelineModule>> moduleDependencies;
	protected Map<String, String> moduleSettings;
	
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
	public List<Class<PipelineModule>> getModuleDependencies() {

		return this.moduleDependencies;
	}

	@Override
	public void setModuleDependencies(List<Class<PipelineModule>> moduleDependencies) {

		this.moduleDependencies = moduleDependencies;
	}

	@Override
	public Map<String, String> getModuleSettings() {

		return this.moduleSettings;
	}

	@Override
	public void setModuleSettings(Map<String, String> moduleSettings) {

		this.moduleSettings = moduleSettings;
	}
}
