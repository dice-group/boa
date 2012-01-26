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
	 * @return
	 */
	public String getReport();
	
	/**
	 * @return the overrideData
	 */
	public boolean isOverrideData();

	/**
	 * @param overrideData the overrideData to set
	 */
	public void setOverrideData(boolean overrideData);
	
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

	/**
	 * 
	 * @return
	 */
	public boolean isDataAlreadyAvailable();
	
	/**
	 * 
	 */
	public void loadAlreadyAvailableData();
}
