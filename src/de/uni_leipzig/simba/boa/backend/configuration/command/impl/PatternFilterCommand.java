package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.util.Map;

import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.filter.PatternFilter;
import de.uni_leipzig.simba.boa.backend.entity.pattern.filter.PatternFilterFactory;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

/**
 * 
 * @author Daniel Gerber
 */
public class PatternFilterCommand implements Command {

	private final NLPediaLogger logger = new NLPediaLogger(PatternFilterCommand.class);
	
	private PatternMappingDao patternMappingDao = (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
	private Map<Integer,PatternMapping> patternMappings = null;

	/**
	 * Creates a new filter command for pattern mappings
	 * 
	 * @param patternMappings a list of pattern mappings, if patternMappings == null
	 * a database call will retrieve them
	 */
	public PatternFilterCommand(Map<Integer,PatternMapping> patternMappings) {
		
		if (patternMappings != null) this.patternMappings = patternMappings;
		else {
			
			for ( PatternMapping pm : this.patternMappingDao.findAllPatternMappings()) {
				
				this.patternMappings.put(pm.getProperty().getUri().hashCode(), pm);
			}
		}
	}
	
	@Override
	public void execute() {
		
		Map<String,PatternFilter> patternEvaluators = PatternFilterFactory.getInstance().getPatternFilterMap();
		
		// go through all filter
		for ( PatternFilter patternEvaluator : patternEvaluators.values() ) {

			this.logger.info(patternEvaluator.getClass().getSimpleName() + " started!");
			
			// and check each pattern mapping
			for ( PatternMapping patternMapping : this.patternMappings.values() ) {
			
				this.logger.debug("Evaluating pattern: " + patternMapping);
				System.out.println("Starting to filter pattern mapping: " + patternMapping.getProperty().getUri() + " with filter: " + patternEvaluator.getClass().getSimpleName());
				patternEvaluator.filterPattern(patternMapping);
				this.patternMappingDao.updatePatternMapping(patternMapping);
			}
			this.logger.info(patternEvaluator.getClass().getSimpleName() + " finished!");
		}
		System.out.println("All filters are finished.");
	}
	
	/**
	 * @return the patternMappingList
	 */
	public Map<Integer,PatternMapping> getPatternMappingList() {
	
		return this.patternMappings;
	}

	/**
	 * @param patternMappingList the patternMappingList to set
	 */
	public void setPatternMappingList(Map<Integer,PatternMapping> patternMappings) {
	
		this.patternMappings = patternMappings;
	}
}