package de.uni_leipzig.simba.boa.frontend.data;

import java.io.Serializable;
import java.util.List;

import com.vaadin.data.util.BeanItemContainer;

import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;


@SuppressWarnings("serial")
public class TripleContainer extends BeanItemContainer<Triple> implements Serializable {

	public TripleContainer(List<Triple> triples) {
		super(Triple.class);

		for ( Triple triple : triples) {
			
			this.addItem(triple);
		}
	}
}
