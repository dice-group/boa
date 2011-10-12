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
import de.uni_leipzig.simba.boa.backend.ml.ConfidenceLearner;


public class PatternScoreCommand implements Command {

	// the logger
	private final NLPediaLogger logger = new NLPediaLogger(PatternScoreCommand.class);
	
	// all variables which are used in the neuronal network
	private Double reverbMax = 0D, supportMax = 0D, specificityMax = 0D, typicityMax = 0D, occMax = 0D, simMax = 0D, tfIdfMax = 0D, maxMax = 0D, pairMax = 0D;
	
	// the dao to retrieve all mappings
	private PatternMappingDao patternMappingDao = (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
	
	// the list of mappings should be past in be constructor
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

		// begin updating the pattern mappings
		long start = new Date().getTime();
		
		// set global maxima and update pattern mappings and cascade
		for ( PatternMapping mapping : mappings ) {
			
			// resets the maximums and calculates for this mapping the new values
			this.calculateMaximas(mapping);
			
			// score each pattern
			for ( Pattern pattern : mapping.getPatterns() ) {
				
				// build the output for the neuronal network
				StringBuilder builder = new StringBuilder()
					.append(mapping.getProperty().getUri() + "\t")
					.append(pattern.getNaturalLanguageRepresentation() + "\t")
					.append(pattern.getReverb() / reverbMax + "\t")
					.append(pattern.getSupport() / supportMax + "\t")
					.append(pattern.getSpecificity() / specificityMax + "\t")
					.append(pattern.getTypicity() / typicityMax + "\t")
					.append(new Double(pattern.getNumberOfOccurrences()) / occMax + "\t")
					.append(pattern.getSimilarity() / simMax + "\t")
					.append(pattern.getTfIdf() / tfIdfMax + "\t")
					.append(pattern.getLearnedFromPairs() / pairMax + "\t")
					.append(pattern.getMaxLearnedFrom() / maxMax + "\t");
				
				Double score = this.learner.getConfidence(builder.toString());
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
	
	private void calculateMaximas(PatternMapping mapping) {

		//reset the pattern maximums 
		this.reverbMax		= 0D;
		this.supportMax 	= 0D;
		this.specificityMax = 0D;
		this.typicityMax 	= 0D;
		this.occMax 		= 0D;
		this.simMax 		= 0D;
		this.tfIdfMax 		= 0D;
		this.pairMax 		= 0D;
		this.maxMax 		= 0D;
		
		// reverbMax, supportMax, specificityMax, typicityMax, occMax, simMax, tfIdfMax, maxMax, pairMax;
		for ( Pattern p: mapping.getPatterns()) {
			
			this.reverbMax		= Math.max(this.reverbMax, p.getReverb() == null ? 0D : p.getReverb());
			this.supportMax 	= Math.max(this.supportMax, p.getSupport() == null ? 0D : p.getSupport());
			this.specificityMax = Math.max(this.specificityMax, p.getSpecificity() == null ? 0D : p.getSpecificity());
			this.typicityMax 	= Math.max(this.typicityMax, p.getTypicity() == null ? 0D : p.getTypicity());
			this.occMax 		= Math.max(this.occMax, p.getNumberOfOccurrences() == null ? 0D : p.getNumberOfOccurrences());
			this.simMax 		= Math.max(this.simMax, p.getSimilarity() == null ? 0D : p.getSimilarity());
			this.tfIdfMax 		= Math.max(this.tfIdfMax, p.getTfIdf() == null ? 0D : p.getTfIdf());
			this.pairMax 		= Math.max(this.pairMax, p.getLearnedFromPairs());
			this.maxMax 		= Math.max(this.maxMax, p.getMaxLearnedFrom());
		}
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
