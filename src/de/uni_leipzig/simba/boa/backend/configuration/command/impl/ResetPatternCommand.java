package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

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
		
		for (Pattern p: patternDao.findAllPatterns()) {
			
			p.setUseForPatternEvaluation(true);
			p.setConfidence(-1D);
			p.setSpecificity(-1D);
			p.setSupport(-1D);
			p.setTypicity(-1D);
			patternDao.updatePattern(p);
		}
	}
}
