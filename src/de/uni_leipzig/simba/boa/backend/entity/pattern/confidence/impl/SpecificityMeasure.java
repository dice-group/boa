package de.uni_leipzig.simba.boa.backend.entity.pattern.confidence.impl;

import java.util.Date;

import de.uni_leipzig.simba.boa.backend.configuration.command.impl.IterationCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.PatternConfidenceMeasureCommand;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternDao;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.confidence.ConfidenceMeasure;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

/**
 * 
 * @author Daniel Gerber
 */
public class SpecificityMeasure implements ConfidenceMeasure {
	
	private PatternMappingDao patternMappingDao = (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
	private NLPediaLogger logger = new NLPediaLogger(SpecificityMeasure.class);

	@Override
	public void measureConfidence(PatternMapping mapping) {

		long start = new Date().getTime();
		
		for (Pattern pattern : mapping.getPatterns()) {
			
			if ( !pattern.isUseForPatternEvaluation() ) continue;
			
			double specificity = PatternConfidenceMeasureCommand.NUMBER_OF_PATTERN_MAPPINGS / 
					patternMappingDao.findPatternMappingsWithSamePattern(pattern.getNaturalLanguageRepresentation()); 
				
			specificity = Math.log(specificity) / Math.log(2);
			
			pattern.setSpecificityForIteration(IterationCommand.CURRENT_ITERATION_NUMBER, specificity);
			pattern.setSpecificity(specificity);
		}
		logger.info("Specificity measuring for pattern_mapping: " + mapping.getProperty().getUri() + " finished in " + (new Date().getTime() - start) + "ms.");
	}
}
