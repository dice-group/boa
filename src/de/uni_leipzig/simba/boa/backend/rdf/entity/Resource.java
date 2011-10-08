package de.uni_leipzig.simba.boa.backend.rdf.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import de.uni_leipzig.simba.boa.backend.entity.context.Context;

@Entity
@Table(name="resource")
public class Resource extends de.uni_leipzig.simba.boa.backend.persistance.Entity {

	protected String uri;
	protected String label;
	protected String type;
	protected String context;
	protected String surfaceForms;
	
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
	@Column(length=2048)
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
	
	/**
	 * @return the context
	 */
	@Basic
	public String getContext() {
	
		return context;
	}

	/**
	 * @param context the context to set
	 */
	public void setContext(String context) {
	
		this.context = context;
	}
	
	/**
	 * @return the surfaceForms
	 */
	@Basic
	@Column(length=2048)
	public String getSurfaceForms() {
	
		return this.surfaceForms;
	}
	
	/**
	 * Since we save the list of surface forms as a single string in the database
	 * we need to split them into a list in java. if the surface forms do not contain
	 * the regular label (wiki page title) the label gets added to the list of surface
	 * forms.
	 * 
	 * @return a list of surface forms
	 */
	public Set<String> retrieveLabels(){
		
		Set<String> labels = new HashSet<String>();
		for ( String s : this.surfaceForms.toLowerCase().split("_&_")) {
			
			// avoid labels with only one character like the TV station "A" 
			if ( s.length() > 1 ) {
				
				labels.add(" " + s.trim() + " ");
			}
			// this is very experimental 
//			if ( Context.namedEntityRecognitionMappings.get(this.type).equals("PER") ) {
//				
//				labels.add("he");
//				labels.add("she");
//			}
		}
		if ( this.label.length() > 1 ) labels.add(" " + this.label.toLowerCase() + " ");
		return labels;
	}

	/**
	 * @param surfaceForms the surfaceForms to set
	 */
	public void setSurfaceForms(String labels) {
	
		this.surfaceForms = labels;
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
