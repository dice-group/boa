package de.uni_leipzig.simba.boa.backend.search.concurrent;

import java.util.HashMap;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.rdf.entity.Resource;


public class ResourceManager {

	private static ResourceManager INSTANCE;
	private Map<Integer,Resource> resourceMap;
	
	private ResourceManager(){
		
		this.resourceMap = new HashMap<Integer,Resource>();
	}
	
	/**
	 * 
	 * @return
	 */
	public static ResourceManager getInstance(){
		
		if ( ResourceManager.INSTANCE == null ) {
			
			ResourceManager.INSTANCE = new ResourceManager();
		}
		
		return ResourceManager.INSTANCE;
	}
	
	/**
	 * returns the resource if in the "cache", creates a new one and adds it to the cache otherwise
	 * 
	 * @param uri
	 * @return
	 */
	public Resource getResource(String uri, String subjectLabel, String type) {
		
		// the resource is already in the map
		if ( this.resourceMap.containsKey(uri.hashCode()) ) {
			
			return this.resourceMap.get(uri.hashCode());
		}
		else {
			
			Resource resource = new Resource();
			resource.setUri(uri);
			resource.setLabel(subjectLabel);
			resource.setType(type);
			
			return this.resourceMap.put(uri.hashCode(), resource);
		}
	}
}
