package de.uni_leipzig.simba.boa.backend.pipeline.module;

import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.pipeline.interchangeobject.ModuleInterchangeObject;

/**
 * 
 * @author gerb
 */
public interface PipelineModule {

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
	public List<Class<PipelineModule>> getModuleDependencies();
	
	/**
	 * 
	 * @param moduleDependencies
	 */
	public void setModuleDependencies(List<Class<PipelineModule>> moduleDependencies);	
	
	/**
	 * 
	 * @return
	 */
	public Map<String,String> getModuleSettings();
	
	/**
	 * 
	 * @param moduleSettings
	 */
	public void setModuleSettings(Map<String,String> moduleSettings);
	
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
