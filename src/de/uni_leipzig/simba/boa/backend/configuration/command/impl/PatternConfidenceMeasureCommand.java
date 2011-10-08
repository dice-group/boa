package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.confidence.ConfidenceMeasure;
import de.uni_leipzig.simba.boa.backend.entity.pattern.confidence.ConfidenceMeasureFactory;
import de.uni_leipzig.simba.boa.backend.entity.pattern.confidence.PatternConfidenceMeasureThread;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.ml.ConfidenceLearner;
import de.uni_leipzig.simba.boa.backend.search.concurrent.PrintProgressTask;
import de.uni_leipzig.simba.boa.backend.util.ListUtil;

/**
 * 
 * @author Daniel Gerber
 */
public class PatternConfidenceMeasureCommand implements Command {

	private final NLPediaLogger logger = new NLPediaLogger(PatternConfidenceMeasureCommand.class);
	
	private ConfidenceLearner learner = new ConfidenceLearner();
	private PatternMappingDao patternMappingDao = (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
	private List<PatternMapping> patternMappingList = null;
	
	private Double reverbMax = 0D, supportMax = 0D, specificityMax = 0D, typicityMax = 0D, occMax = 0D, simMax = 0D, tfIdfMax = 0D, maxMax = 0D, pairMax = 0D;
	
	public static double NUMBER_OF_PATTERN_MAPPINGS;

	public PatternConfidenceMeasureCommand(Map<Integer,PatternMapping> patternMappingList) {
		
		if ( patternMappingList != null ){
		
			this.patternMappingList = new ArrayList<PatternMapping>(patternMappingList.values());
		}
		else {
			
			this.patternMappingList = this.patternMappingDao.findAllPatternMappings();
		}
		PatternConfidenceMeasureCommand.NUMBER_OF_PATTERN_MAPPINGS = (double) this.patternMappingList.size();
	}
	
	@Override
	public void execute() {
		
		int numberOfConfidenceMeasureThreads = new Integer(NLPediaSettings.getInstance().getSetting("numberOfConfidenceMeasureThreads")).intValue();
		
		// split the mappings into several lists
		List<List<PatternMapping>> patternMappingSubLists	= ListUtil.split(patternMappingList, (patternMappingList.size() / numberOfConfidenceMeasureThreads));
		
		List<Thread> threadList = new ArrayList<Thread>();
		List<PatternMapping> results = new ArrayList<PatternMapping>();
		
		if ( numberOfConfidenceMeasureThreads != 1 ) {
			
			for (int i = 0 ; i < numberOfConfidenceMeasureThreads ; i++ ) {
				
				Thread t = new PatternConfidenceMeasureThread(patternMappingSubLists.get(i));
				t.setName("PatternConfidenceMeasureThread-" + (i + 1));
				threadList.add(i, t);
				t.start();
				System.out.println(t.getName() + " started!");
				this.logger.info(t.getName() + " started!");
			}
			
			Timer timer = new Timer();
			timer.schedule(new PrintProgressTask(threadList), 0, 30000);
			
			// wait for all to finish
			for ( Thread t : threadList ) {
				
				try {
					t.join();	
				}
				catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			timer.cancel();
			
			for ( Thread t: threadList ) {
				
				results.addAll(((PatternConfidenceMeasureThread)t).getConfidenceMeasuredPatternMappings());
			}
		}
		else {
			
			Map<String,ConfidenceMeasure> confidenceMeasures = ConfidenceMeasureFactory.getInstance().getConfidenceMeasureMap();
			
			System.out.println("Measuring confidence for " + this.patternMappingList.size() + " pattern mappings with " + confidenceMeasures.size() + " confidence measures!");
			
			// go through all confidence measurements
			for ( ConfidenceMeasure confidenceMeasure : confidenceMeasures.values() ) {

				this.logger.info(confidenceMeasure.getClass().getSimpleName() + " started from " + confidenceMeasure.getClass().getSimpleName() +"!");
				System.out.println(confidenceMeasure.getClass().getSimpleName() + " started!");
				long start = new Date().getTime();
				
				// and check each pattern mapping
				for (PatternMapping patternMapping : this.patternMappingList ) {
				
					this.logger.debug("Calculation of confidence for mapping: " + patternMapping.getProperty().getUri());
					confidenceMeasure.measureConfidence(patternMapping);
				}
				this.logger.info(confidenceMeasure.getClass().getSimpleName() + " from " + confidenceMeasure.getClass().getSimpleName() + " finished in " + (new Date().getTime() - start) + "ms!");
				System.out.println(confidenceMeasure.getClass().getSimpleName() + " finished!");
			}
			results.addAll(patternMappingList);
		}

		System.out.println("All confidence measurement threads are finished. Start to calculate normalized confidence!");
		
//		double maxConfidenceForAllPatternMappings = 0;
//		
//		for (PatternMapping pm : results ) {
//			
//			// calculate local and global maxima
//			double maxConfidenceForPatternMapping = 0;
//			
//			for (Pattern pattern : pm.getPatterns() ) {
//				
//				if ( !pattern.isUseForPatternEvaluation() ) continue;
//				
//				double specificity	= pattern.getSpecificity() >= 0	? pattern.getSpecificity()	: 0;
//				double typicity		= pattern.getTypicity() >= 0	? pattern.getTypicity()		: 0;
//				double support		= pattern.getSupport() >= 0		? pattern.getSupport()		: 0;
//				double similarity	= pattern.getSimilarity() >= 0	? pattern.getSimilarity()	: 0;
//				double reverb		= pattern.getReverb() >= 0		? pattern.getReverb()		: 0;
//				
//				double confidence = 10 * typicity + 2 * support + 1 * specificity + 10 * similarity + 4 * reverb;
//				pattern.setConfidence(confidence);
//				
//				maxConfidenceForPatternMapping		= Math.max(maxConfidenceForPatternMapping, confidence);
//				maxConfidenceForAllPatternMappings	= Math.max(maxConfidenceForAllPatternMappings, confidence);
//			}
//			
//			// set local maximums
//			for ( Pattern pattern : pm.getPatterns() ) {
//				
//				if ( !Double.isNaN(maxConfidenceForPatternMapping) && !Double.isInfinite(maxConfidenceForPatternMapping) && pattern.isUseForPatternEvaluation() ) {
//				
//					pattern.setConfidence(pattern.getConfidence() / maxConfidenceForPatternMapping);
//				}
//				else {
//					
//					pattern.setConfidence(0D);
//				}
//			}
//		}
		// begin updating the pattern mappings
		long start = new Date().getTime();
		
		// set global maxima and update pattern mappings and cascade
		for ( PatternMapping mapping : results ) {
			
			// resets the maximums and calculates for this mapping the new values
			this.calculateMaximas(mapping);
			
			// score each pattern
			for ( Pattern pattern : mapping.getPatterns() ) {
				
				// build the output for the neuronal network
				StringBuilder builder = new StringBuilder();
				builder.append(mapping.getProperty().getUri() + "\t");
				builder.append(pattern.getNaturalLanguageRepresentation() + "\t");
				builder.append(pattern.getReverb() / reverbMax + "\t");
				builder.append(pattern.getSupport() / supportMax + "\t");
				builder.append(pattern.getSpecificity() / specificityMax + "\t");
				builder.append(pattern.getTypicity() / typicityMax + "\t");
				builder.append(new Double(pattern.getNumberOfOccurrences()) / occMax + "\t");
				builder.append(pattern.getSimilarity() / simMax + "\t");
				builder.append(pattern.getTfIdf() / tfIdfMax + "\t");
				builder.append(pattern.getLearnedFromPairs() / pairMax + "\t");
				builder.append(pattern.getMaxLearnedFrom() / maxMax + "\t");
				
//				Double score = this.learner.getConfidence(builder.toString());
//				pattern.setConfidence(score);
				
//				System.out.println(pattern.getNaturalLanguageRepresentation() + ": " +score);
			}
			this.logger.info("Updating pattern mapping " + mapping.getProperty().getUri());
			this.patternMappingDao.updatePatternMapping(mapping);
		}
		System.out.println("Updating PatternMappings took " + (new Date().getTime() - start) + "ms.");
		
		// set this so that the next command does not need to query them from the database again
		this.patternMappingList = results;
	}
	
	public static void main(String[] args) {

		PatternConfidenceMeasureCommand c = new PatternConfidenceMeasureCommand(null);
		StringBuilder builder = new StringBuilder();
		builder.append("http://uri.de" + "\t");
		builder.append("?D? is a ?R?" + "\t");
		builder.append(0.1 + "\t");
		builder.append(0.1 + "\t");
		builder.append(0.1 + "\t");
		builder.append(0.1 + "\t");
		builder.append(0.1 + "\t");
		builder.append(0.1 + "\t");
		builder.append(0.1 + "\t");
		builder.append(0.1 + "\t");
		builder.append(0.1 + "\t");
		
		System.out.println(c.learner.getConfidence(builder.toString()));
	}

	
	/**
	 * @return the patternMappingList
	 */
	public List<PatternMapping> getPatternMappingList() {
	
		return patternMappingList;
	}

	
	/**
	 * @param patternMappingList the patternMappingList to set
	 */
	public void setPatternMappingList(List<PatternMapping> patternMappingList) {
	
		this.patternMappingList = patternMappingList;
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
}
