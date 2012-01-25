package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.util.HashMap;
import java.util.List;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature;


public class ResetPatternCommand implements Command {

	@Override
	public void execute() {

		NLPediaSetup setup = new NLPediaSetup(false);
		
		PatternMappingDao patternMappingDao = (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
		
		List<PatternMapping> patternMappingList = patternMappingDao.findAllPatternMappings();
		
		for (PatternMapping mapping: patternMappingList ) {
			
			for ( Pattern p : mapping.getPatterns() ) {
				
				p.setUseForPatternEvaluation(true);
				p.setConfidence(0D);
				p.setGeneralizedPattern("");
				p.setFeatures(new HashMap<Feature,Double>());
			}
			patternMappingDao.updatePatternMapping(mapping);
		}
	}
}
