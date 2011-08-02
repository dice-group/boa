package de.uni_leipzig.simba.boa.backend.entity.pattern.confidence.impl;

import java.util.Date;

import de.uni_leipzig.simba.boa.backend.configuration.command.impl.PatternFilteringCommand;
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
		
		for (Pattern p : mapping.getPatterns()) {
			
			double specificity = PatternFilteringCommand.NUMBER_OF_PATTERN_MAPPINGS / 
					this.getNumberOfPatternMappingsWithPattern(p.getNaturalLanguageRepresentation()); 
				
			specificity = Math.log(specificity) / Math.log(2);
			
			p.setSpecificity(specificity);
		}
		System.out.println("Specificity measuring for pattern_mapping: " + mapping.getProperty().getUri() + " finished in " + (new Date().getTime() - start) + "ms.");
	}
	
	/**
	 * @param naturalLanguageRepresentation
	 * @return the number of pattern mappings which have a pattern with the same natural language representation
	 */
	private int getNumberOfPatternMappingsWithPattern(String naturalLanguageRepresentation) {

		return this.patternDao.countPatternMappingsWithSameNaturalLanguageRepresenation(naturalLanguageRepresentation);
	}
}
