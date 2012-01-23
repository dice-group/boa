package de.uni_leipzig.simba.boa.backend.pipeline.module;

import java.util.Set;

import de.uni_leipzig.simba.boa.backend.pipeline.interchangeobject.ModuleInterchangeObject;

/**
 * 
 * @author gerb
 */
public interface PipelineModule {

	/**
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * 
	 * @param moduleInterchangeObject
	 */
	public void run();

	/**
	 * 
	 */
	public void updateModuleInterchangeObject();
	
	/**
	 * 
	 * @return
	 */
	public Set<Class<PipelineModule>> getModuleDependencies();
	
	/**
	 * 
	 * @param moduleDependencies
	 */
	public void setModuleDependencies(Set<Class<PipelineModule>> moduleDependencies);	
	
	/**
	 * 
	 * @return
	 */
	public Set<String> getModuleSettings();
	
	/**
	 * 
	 * @param moduleSettings
	 */
	public void setModuleSettings(Set<String> moduleSettings);
	
	/**
	 * 
	 * @return
	 */
	public ModuleInterchangeObject getModuleInterchangeObject();
	
	/**
	 * 
	 * @param moduleInterchangeObject
	 */
	public void setModuleInterchangeObject(ModuleInterchangeObject moduleInterchangeObject);
}
