package de.uni_leipzig.simba.boa.backend.backgroundknowledge;

import java.util.Set;

import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Resource;

/**
 * 
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
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
	
	/**
	 * 
	 * @return
	 */
	public Set<String> getSubjectSurfaceForms();
	
	/**
	 * 
	 * @param subjectSurfaceForms
	 */
	public void setSubjectSurfaceForms(Set<String> subjectSurfaceForms);
	
	/**
	 *@return 
	 */
	public Set<String> getObjectSurfaceForms();
	
	/**
	 * 
	 * @param subjectSurfaceForms
	 */
	public void setObjectSurfaceForms(Set<String> subjectSurfaceForms);
}
