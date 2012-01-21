/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.configuration;

import java.util.List;

import de.uni_leipzig.simba.boa.backend.pipeline.interchangeobject.DefaultModuleInterchangeObject;
import de.uni_leipzig.simba.boa.backend.pipeline.interchangeobject.ModuleInterchangeObject;
import de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule;


/**
 * @author gerb
 *
 */
public class PipelineConfiguration {

	private ModuleInterchangeObject moduleInterchangeObject;
	private List<PipelineModule> pipelineModules;
	
	public PipelineConfiguration() {}

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

	/**
	 * @return the pipelineModules
	 */
	public List<PipelineModule> getPipelineModules() {

		return pipelineModules;
	}

	/**
	 * @param pipelineModules the pipelineModules to set
	 */
	public void setPipelineModules(List<PipelineModule> pipelineModules) {

		this.pipelineModules = pipelineModules;
	}
}
