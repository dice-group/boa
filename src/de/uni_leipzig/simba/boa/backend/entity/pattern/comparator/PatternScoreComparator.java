package de.uni_leipzig.simba.boa.backend.entity.pattern.comparator;

import java.util.Comparator;

import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;


public class PatternScoreComparator implements Comparator<Pattern> {

	@Override
	public int compare(Pattern triple1, Pattern triple2) {

		double x = (triple2.getScore() - triple1.getScore());
		if ( x < 0 ) return -1;
		if ( x == 0 ) return 0;
		return 1;
	}
}
