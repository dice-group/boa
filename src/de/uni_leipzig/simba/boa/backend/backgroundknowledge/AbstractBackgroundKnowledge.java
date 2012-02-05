package de.uni_leipzig.simba.boa.backend.backgroundknowledge;

import java.util.HashSet;
import java.util.Set;

import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Resource;


public abstract class AbstractBackgroundKnowledge implements BackgroundKnowledge {

	protected Resource subject;
	protected Property property;
	protected Resource object;
	
	protected Set<String> subjectSurfaceForms  = new HashSet<String>();
	protected Set<String> objectSurfaceForms   = new HashSet<String>();
	
	public AbstractBackgroundKnowledge(Resource subject, Resource object) {
	    
	    this.subjectSurfaceForms    = subject.retrieveLabels() != null ? subject.retrieveLabels() : new HashSet<String>();
        this.objectSurfaceForms     = object.retrieveLabels() != null ? object.retrieveLabels() : new HashSet<String>();
	}
	
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
	
	   /**
     * @return the subjectSurfaceForms
     */
    public Set<String> getSubjectSurfaceForms() {
    
        return subjectSurfaceForms;
    }

    
    /**
     * @param subjectSurfaceForms the subjectSurfaceForms to set
     */
    public void setSubjectSurfaceForms(Set<String> subjectSurfaceForms) {
    
        this.subjectSurfaceForms = subjectSurfaceForms;
    }

    
    /**
     * @return the objectSurfaceForms
     */
    public Set<String> getObjectSurfaceForms() {
    
        return objectSurfaceForms;
    }

    
    /**
     * @param objectSurfaceForms the objectSurfaceForms to set
     */
    public void setObjectSurfaceForms(Set<String> objectSurfaceForms) {
    
        this.objectSurfaceForms = objectSurfaceForms;
    }
}