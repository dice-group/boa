package de.uni_leipzig.simba.boa.backend.entity.pattern.comparator;

import java.util.Comparator;

import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;

/**
 * 
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 *
 */
public class PatternNlrComparator implements Comparator<Pattern> {

	@Override
	public int compare(Pattern pattern1, Pattern pattern2) {
		return pattern1.getNaturalLanguageRepresentationWithoutVariables().compareTo(pattern2.getNaturalLanguageRepresentationWithoutVariables());
	}

}
