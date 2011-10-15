package de.uni_leipzig.simba.boa.backend.entity.pattern.confidence;

import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

/**
 * 
 * @author Daniel Gerber
 */
public class PatternConfidenceMeasureThread extends Thread {

	private List<PatternMapping> patternMappings = null;
	private Map<String,ConfidenceMeasure> confidenceMeasures = ConfidenceMeasureFactory.getInstance().getConfidenceMeasureMap();
	
	private final NLPediaLogger logger = new NLPediaLogger(PatternConfidenceMeasureThread.class);

	private int patternToMeasure;
	private int i = 0;

	public PatternConfidenceMeasureThread(List<PatternMapping> list) {

		this.patternMappings = list;
		
		for ( PatternMapping pm : this.patternMappings ) {
			
			this.patternToMeasure += pm.getPatterns().size();
		}
		this.patternToMeasure *= confidenceMeasures.size();
		
		System.out.println("Pattern to measure: " + this.patternToMeasure);
	}
	
	@Override
	public void run() {
		
		// go through all confidenceMeasure
		for ( ConfidenceMeasure confidenceMeasure : confidenceMeasures.values() ) {

			this.logger.info(confidenceMeasure.getClass().getSimpleName() + " started from " + this.getName() +"!");
			long start = new Date().getTime();
			
			// and check each pattern mapping
			for (PatternMapping patternMapping : patternMappings ) {
				
				if ( patternMapping.getProperty().getUri().equals("http://dbpedia.org/ontology/almaMater") ) {
					
					System.out.println("THis is this fucking mapping....");
				}
				
				this.logger.info("Calculation of confidence for mapping: " + patternMapping.getProperty().getUri());
				confidenceMeasure.measureConfidence(patternMapping);
				this.i += patternMapping.getPatterns().size(); 
			}
			this.logger.info(confidenceMeasure.getClass().getSimpleName() + " from " + this.getName() + " finished in " + (new Date().getTime() - start) + "ms!");
		}
	}
	
	public List<PatternMapping> getConfidenceMeasuredPatternMappings(){
		
		return this.patternMappings;
	}

	public String getProgress() {

		return NumberFormat.getPercentInstance().format((double) i / (double) patternToMeasure);
	}
}
