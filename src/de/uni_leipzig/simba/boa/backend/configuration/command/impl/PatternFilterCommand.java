package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.util.Collection;
import java.util.List;
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
	
	private Collection<PatternMapping> patternMappings = null;

	/**
	 * Creates a new filter command for pattern NAMED_ENTITY_TAG_MAPPINGS
	 * 
	 * @param patternMappings a list of pattern NAMED_ENTITY_TAG_MAPPINGS, if patternMappings == null
	 * a database call will retrieve them
	 */
	public PatternFilterCommand(Collection<PatternMapping> patternMappings) {
		
		this.patternMappings = patternMappings;
	}
	
	@Override
	public void execute() {
		
		
	}
}