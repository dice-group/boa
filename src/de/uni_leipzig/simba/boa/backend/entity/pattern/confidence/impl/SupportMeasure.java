package de.uni_leipzig.simba.boa.backend.entity.pattern.confidence.impl;

import java.util.Date;

import de.uni_leipzig.simba.boa.backend.configuration.command.impl.IterationCommand;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.confidence.ConfidenceMeasure;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

/**
 * 
 * @author Daniel Gerber
 */
public class SupportMeasure implements ConfidenceMeasure {

	private NLPediaLogger logger = new NLPediaLogger(SupportMeasure.class);
	
	@Override
	public void measureConfidence(PatternMapping mapping) {

//		System.out.println("Mapping: " +mapping.getProperty().getUri());
		
		long start = new Date().getTime();
		
		for (Pattern pattern : mapping.getPatterns()) {
			
			if ( !pattern.isUseForPatternEvaluation() ) continue;
			
//			System.out.println("\tPattern: " +pattern.getNaturalLanguageRepresentation());
			
			double maxLearnedFrom = (double) (Math.log((pattern.retrieveMaxLearnedFrom() + 1)) / Math.log(2));
			double countLearnedFrom = (double) (Math.log((pattern.retrieveCountLearnedFrom() + 1)) / Math.log(2));
			
			double support = maxLearnedFrom * countLearnedFrom;
			
			pattern.setMaxLearnedFrom(maxLearnedFrom);
			pattern.setCountLearnedFrom(countLearnedFrom);
			pattern.setSupportForIteration(IterationCommand.CURRENT_ITERATION_NUMBER, support);
			pattern.setSupport(support);
		}
		this.logger.info("Support measuring for pattern_mapping: " + mapping.getProperty().getUri() + " finished in " + (new Date().getTime() - start) + "ms.");
	}
}
