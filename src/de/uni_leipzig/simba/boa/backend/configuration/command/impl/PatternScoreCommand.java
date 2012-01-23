package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.machinelearning.ConfidenceLearner;


public class PatternScoreCommand implements Command {

	// the logger
	private final NLPediaLogger logger = new NLPediaLogger(PatternScoreCommand.class);
	
	// the dao to retrieve all NAMED_ENTITY_TAG_MAPPINGS
	private PatternMappingDao patternMappingDao = (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
	
	// the list of NAMED_ENTITY_TAG_MAPPINGS should be past in be constructor
	private List<PatternMapping> mappings;
	
	// the learner which uses a neuronal network
	private ConfidenceLearner learner = new ConfidenceLearner();
	
	public PatternScoreCommand(List<PatternMapping> mappings) {
		
		if ( mappings != null ){
			
			this.mappings = new ArrayList<PatternMapping>(mappings);
		}
		else {
			
			this.mappings = this.patternMappingDao.findAllPatternMappings();
		}
	}
	
	@Override
	public void execute() {

		// begin updating the pattern NAMED_ENTITY_TAG_MAPPINGS
		long start = new Date().getTime();
		
		// set global maxima and update pattern NAMED_ENTITY_TAG_MAPPINGS and cascade
		for ( PatternMapping mapping : mappings ) {
			
			// score each pattern
			for ( Pattern pattern : mapping.getPatterns() ) {
				
				Double score = this.learner.getConfidence(mapping, pattern);
				pattern.setConfidence(
						score == Double.NaN || 
						score == Double.NEGATIVE_INFINITY || 
						score == Double.POSITIVE_INFINITY
						? 0D : score);
				
				this.logger.info(pattern.getNaturalLanguageRepresentation() + ": " +score);
			}
			this.logger.info("Updating pattern mapping " + mapping.getProperty().getUri());
			this.patternMappingDao.updatePatternMapping(mapping);
		}
		System.out.println("Updating PatternMappings took " + (new Date().getTime() - start) + "ms.");
	}
	
	public List<PatternMapping> getPatternMappingList() {

		return this.mappings;
	}
}

//for (PatternMapping pm : results ) {
//
//// calculate local and global maxima
//double maxConfidenceForPatternMapping = 0;
//
//for (Pattern pattern : pm.getPatterns() ) {
//	
//	if ( !pattern.isUseForPatternEvaluation() ) continue;
//	
//	double specificity	= pattern.getSpecificity() >= 0	? pattern.getSpecificity()	: 0;
//	double typicity		= pattern.getTypicity() >= 0	? pattern.getTypicity()		: 0;
//	double support		= pattern.getSupport() >= 0		? pattern.getSupport()		: 0;
//	double similarity	= pattern.getSimilarity() >= 0	? pattern.getSimilarity()	: 0;
//	double reverb		= pattern.getReverb() >= 0		? pattern.getReverb()		: 0;
//	
//	double confidence = 10 * typicity + 2 * support + 1 * specificity + 10 * similarity + 4 * reverb;
//	pattern.setConfidence(confidence);
//	
//	maxConfidenceForPatternMapping		= Math.max(maxConfidenceForPatternMapping, confidence);
//	maxConfidenceForAllPatternMappings	= Math.max(maxConfidenceForAllPatternMappings, confidence);
//}
//
//// set local maximums
//for ( Pattern pattern : pm.getPatterns() ) {
//	
//	if ( !Double.isNaN(maxConfidenceForPatternMapping) && !Double.isInfinite(maxConfidenceForPatternMapping) && pattern.isUseForPatternEvaluation() ) {
//	
//		pattern.setConfidence(pattern.getConfidence() / maxConfidenceForPatternMapping);
//	}
//	else {
//		
//		pattern.setConfidence(0D);
//	}
//}
//}
