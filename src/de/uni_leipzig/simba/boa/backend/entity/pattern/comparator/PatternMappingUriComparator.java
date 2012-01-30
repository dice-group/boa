package de.uni_leipzig.simba.boa.backend.entity.pattern.comparator;

import java.util.Comparator;

import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;


public class PatternMappingUriComparator implements Comparator<PatternMapping> {

	@Override
	public int compare(PatternMapping mapping1, PatternMapping mapping2) {

		return mapping1.getProperty().getUri().compareTo(mapping2.getProperty().getUri());
	}
}
