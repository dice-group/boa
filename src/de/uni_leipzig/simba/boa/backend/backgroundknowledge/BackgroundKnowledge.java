package de.uni_leipzig.simba.boa.backend.backgroundknowledge;

import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Resource;

/**
 * 
 * @author gerb
 */
public interface BackgroundKnowledge {

	/**
	 * @return the subject
	 */
	public Resource getSubject();
	
	/**
	 * @param subject the subject to set
	 */
	public void setSubject(Resource subject);
	
	/**
	 * @return the property
	 */
	public Property getProperty();
	
	/**
	 * @param property the property to set
	 */
	public void setProperty(Property property);
	
	/**
	 * @return the object
	 */
	public Resource getObject();
	
	/**
	 * @param object the object to set
	 */
	public void setObject(Resource object);
}
