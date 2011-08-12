package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.util.HashMap;
import java.util.List;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;


public class ResetPatternCommand implements Command {

	@Override
	public void execute() {

		NLPediaSetup setup = new NLPediaSetup(false);
		
		PatternDao patternDao = (PatternDao) DaoFactory.getInstance().createDAO(PatternDao.class);
		
		List<Pattern> patternList = patternDao.findAllPatterns();
		
		for (Pattern p: patternList ) {
			
			p.setUseForPatternEvaluation(true);
			p.setConfidences(new HashMap<Integer,Double>());
			p.setSpecificities(new HashMap<Integer,Double>());
			p.setSupports(new HashMap<Integer,Double>());
			p.setTypicities(new HashMap<Integer,Double>());
		}
		patternDao.batchSaveOrUpdatePattern(patternList);
	}
}
