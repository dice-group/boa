package de.uni_leipzig.simba.boa.backend.rdf.entity;

import javax.persistence.Basic;
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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Resource other = (Resource) obj;
		if (uri == null) {
			if (other.uri != null)
				return false;
		}
		else
			if (!uri.equals(other.uri))
				return false;
		return true;
	}
}
