package de.uni_leipzig.simba.boa.backend.backgroundknowledge.impl;

import de.uni_leipzig.simba.boa.backend.backgroundknowledge.AbstractBackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Resource;


public final class DatatypePropertyBackgroundKnowledge extends AbstractBackgroundKnowledge {

	public DatatypePropertyBackgroundKnowledge(Resource sub, Property p, Resource obj) {
		super(sub,obj);
	}

}
