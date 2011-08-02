package de.uni_leipzig.simba.boa.backend.rdf.entity;

import javax.persistence.Basic;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="resource")
public class Resource extends de.uni_leipzig.simba.boa.backend.persistance.Entity {

	protected String uri;
	protected String label;
	protected String type;
	
	/**
	 * 
	 * @param uri
	 * @param label
	 */
	public Resource(String uri, String label){
		
		this.uri = uri;
		this.label = label;
	}
	
	public Resource() {}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {

		this.label = label;
	}
	
	/**
	 * @return the label
	 */
	@Basic
	public String getLabel() {

		return label;
	}
	
	/**
	 * @param uri the uri to set
	 */
	public void setUri(String uri) {

		this.uri = uri;
	}
	
	/**
	 * @return the uri
	 */
	@Basic
	public String getUri() {

		return uri;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {

		this.type = type;
	}

	/**
	 * @return the type
	 */
	@Basic
	public String getType() {

		return type;
	}
}
