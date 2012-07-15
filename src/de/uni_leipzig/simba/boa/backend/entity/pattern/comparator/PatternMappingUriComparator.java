package de.uni_leipzig.simba.boa.backend.entity.pattern.comparator;

import java.util.Comparator;

import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;

/**
 * 
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 *
 */
public class PatternMappingUriComparator implements Comparator<PatternMapping> {

	@Override
	public int compare(PatternMapping mapping1, PatternMapping mapping2) {

		return mapping1.getProperty().getUri().compareTo(mapping2.getProperty().getUri());
	}
}
