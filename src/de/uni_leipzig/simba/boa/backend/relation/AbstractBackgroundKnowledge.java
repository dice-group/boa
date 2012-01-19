package de.uni_leipzig.simba.boa.backend.relation;

import java.util.HashSet;
import java.util.Set;

import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Resource;


public abstract class AbstractBackgroundKnowledge implements BackgroundKnowledge {

	protected Resource subject;
	protected Property property;
	protected Resource object;
	
	public AbstractBackgroundKnowledge() {}
	
	/**
	 * @return the subject
	 */
	public Resource getSubject() {
	
		return subject;
	}
	
	/**
	 * @param subject the subject to set
	 */
	public void setSubject(Resource subject) {
	
		this.subject = subject;
	}
	
	/**
	 * @return the property
	 */
	public Property getProperty() {
	
		return property;
	}
	
	/**
	 * @param property the property to set
	 */
	public void setProperty(Property property) {
	
		this.property = property;
	}
	
	/**
	 * @return the object
	 */
	public Resource getObject() {
	
		return object;
	}
	
	/**
	 * @param object the object to set
	 */
	public void setObject(Resource object) {
	
		this.object = object;
	}
}