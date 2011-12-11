package de.uni_leipzig.simba.boa.backend.entity.pattern.feature;

import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

/**
 * 
 * @author Daniel Gerber
 */
public class PatternScoreThread extends Thread {

	private List<PatternMapping> patternMappings = null;
	private Map<String,Feature> features = FeatureFactory.getInstance().getFeatureMap();
	
	private final NLPediaLogger logger = new NLPediaLogger(PatternScoreThread.class);

	private int patternToMeasure;
	private int i = 0;

	public PatternScoreThread(List<PatternMapping> list) {

		this.patternMappings = list;
		for ( PatternMapping pm : this.patternMappings ) {
			
			this.patternToMeasure += pm.getPatterns().size();
		}
		this.patternToMeasure *= features.size();
		
		System.out.println("Pattern to measure: " + this.patternToMeasure + " with " + features.size());
	}
	
	@Override
	public void run() {
		
		// go through all features
		for ( Feature feature : features.values() ) {

			this.logger.info(feature.getClass().getSimpleName() + " started from " + this.getName() +"!");
			System.out.println(feature.getClass().getSimpleName() + " started from " + this.getName() +"!");
			long start = new Date().getTime();
			
			// do global feature scoring
			feature.score(patternMappings);
			
			// do local feature score with respect to each pattern mapping
			for (PatternMapping patternMapping : patternMappings ) {
				
				this.logger.info("Calculation of confidence for mapping: " + patternMapping.getProperty().getUri());
				feature.scoreMapping(patternMapping);
				this.i += patternMapping.getPatterns().size(); // add patterns to done list
			}
			this.logger.info(feature.getClass().getSimpleName() + " from " + this.getName() + " finished in " + (new Date().getTime() - start) + "ms!");
		}
	}
	
	public List<PatternMapping> getScoredPatternMappings(){
		
		return this.patternMappings;
	}

	public String getProgress() {

		return patternToMeasure != 0 ? NumberFormat.getPercentInstance().format((double) i / (double) patternToMeasure) : "100%";
	}
}
