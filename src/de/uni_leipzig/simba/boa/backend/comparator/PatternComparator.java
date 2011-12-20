package de.uni_leipzig.simba.boa.backend.comparator;

import java.util.Comparator;

import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;


public class PatternComparator implements Comparator<Pattern> {

	@Override
	public int compare(Pattern triple1, Pattern triple2) {

		double x = (triple2.getConfidence() - triple1.getConfidence());
		if ( x < 0 ) return -1;
		if ( x == 0 ) return 0;
		return 1;
	}
}
