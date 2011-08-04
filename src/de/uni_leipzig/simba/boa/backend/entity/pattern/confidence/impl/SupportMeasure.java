package de.uni_leipzig.simba.boa.backend.entity.pattern.confidence.impl;

import java.util.Date;

import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.confidence.ConfidenceMeasure;

/**
 * 
 * @author Daniel Gerber
 */
public class SupportMeasure implements ConfidenceMeasure {

	@Override
	public void measureConfidence(PatternMapping mapping) {

		long start = new Date().getTime();
		
		for (Pattern pattern : mapping.getPatterns()) {
			
			if ( !pattern.isUseForPatternEvaluation() ) continue;
			
			double support = 
				(double) (Math.log((pattern.retrieveMaxLearnedFrom() + 1)) / Math.log(2)) * 
				(double) (Math.log((pattern.retrieveCountLearnedFrom() + 1)) / Math.log(2));
			
			pattern.setSupport(support);
		}
		System.out.println("Support measuring for pattern_mapping: " + mapping.getProperty().getUri() + " finished in " + (new Date().getTime() - start) + "ms.");
	}
}
