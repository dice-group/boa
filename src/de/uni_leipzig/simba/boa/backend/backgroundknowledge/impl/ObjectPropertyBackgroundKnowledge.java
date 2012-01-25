package de.uni_leipzig.simba.boa.backend.backgroundknowledge.impl;

import java.util.HashSet;
import java.util.Set;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.AbstractBackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Resource;
import edu.stanford.nlp.util.StringUtils;


public final class ObjectPropertyBackgroundKnowledge extends AbstractBackgroundKnowledge {

	private Set<String> subjectSurfaceForms;
	private Set<String> objectSurfaceForms;
	
	public ObjectPropertyBackgroundKnowledge(Resource subject, Property property, Resource object) {
		super();
		
		this.subject	= subject;
		this.property	= property;
		this.object		= object;
		
		this.subjectSurfaceForms = new HashSet<String>();
		this.objectSurfaceForms = new HashSet<String>();
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		// 0_URI1 ||| 1_LABEL1 ||| 2_LABELS1 ||| 3_PROP ||| 4_URI2 ||| 5_LABEL2 ||| 6_LABELS2 ||| 7_RANGE ||| 8_DOMAIN
		
		StringBuilder builder = new StringBuilder();
		builder.append(subject.getUri());
		builder.append(Constants.BACKGROUND_KNOWLEDGE_VALUE_SEPARATOR);
		
		builder.append(subject.getLabel());
		builder.append(Constants.BACKGROUND_KNOWLEDGE_VALUE_SEPARATOR);
		
		builder.append(StringUtils.join(this.subjectSurfaceForms, Constants.BACKGROUND_KNOWLEDGE_SURFACE_FORM_SEPARATOR));
		builder.append(Constants.BACKGROUND_KNOWLEDGE_VALUE_SEPARATOR);
		
		builder.append(property.getUri());
		builder.append(Constants.BACKGROUND_KNOWLEDGE_VALUE_SEPARATOR);
		
		builder.append(object.getUri());
		builder.append(Constants.BACKGROUND_KNOWLEDGE_VALUE_SEPARATOR);
		
		builder.append(object.getLabel());
		builder.append(Constants.BACKGROUND_KNOWLEDGE_VALUE_SEPARATOR);
		
		builder.append(StringUtils.join(this.objectSurfaceForms, Constants.BACKGROUND_KNOWLEDGE_SURFACE_FORM_SEPARATOR));
		builder.append(Constants.BACKGROUND_KNOWLEDGE_VALUE_SEPARATOR);
		
		builder.append(this.property.getRdfsRange());
		builder.append(Constants.BACKGROUND_KNOWLEDGE_VALUE_SEPARATOR);
		
		builder.append(this.property.getRdfsDomain());
		
		return builder.toString();
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
