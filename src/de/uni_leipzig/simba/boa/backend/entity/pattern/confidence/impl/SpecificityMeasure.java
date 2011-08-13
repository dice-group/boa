package de.uni_leipzig.simba.boa.backend.entity.pattern.confidence.impl;

import java.util.Date;

import de.uni_leipzig.simba.boa.backend.configuration.command.impl.IterationCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.PatternConfidenceMeasureCommand;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.confidence.ConfidenceMeasure;

/**
 * 
 * @author Daniel Gerber
 */
public class SpecificityMeasure implements ConfidenceMeasure {
	
	private PatternDao patternDao = (PatternDao) DaoFactory.getInstance().createDAO(PatternDao.class);

	@Override
	public void measureConfidence(PatternMapping mapping) {

		long start = new Date().getTime();
		
		for (Pattern pattern : mapping.getPatterns()) {
			
			if ( !pattern.isUseForPatternEvaluation() ) continue;
			
			double specificity = PatternConfidenceMeasureCommand.NUMBER_OF_PATTERN_MAPPINGS / 
					(double) pattern.getPatternMappings().size(); 
				
			System.out.println(String.format("Number of mappings: %s and %s for the pattern: %s" ,
					PatternConfidenceMeasureCommand.NUMBER_OF_PATTERN_MAPPINGS,
					pattern.getPatternMappings().size(),
					pattern.getId() + ": " + pattern.getNaturalLanguageRepresentation() ));
			
			specificity = Math.log(specificity) / Math.log(2);
			
			pattern.setSpecificityForIteration(IterationCommand.CURRENT_ITERATION_NUMBER, specificity);
			pattern.setSpecificity(specificity);
		}
		System.out.println("Specificity measuring for pattern_mapping: " + mapping.getProperty().getUri() + " finished in " + (new Date().getTime() - start) + "ms.");
	}
}
