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
		super(subject, object);
		
		this.subject	= subject;
		this.property	= property;
		this.object		= object;
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
}
