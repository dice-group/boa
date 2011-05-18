package de.uni_leipzig.simba.boa.backend.search.concurrent;

import java.io.IOException;

import java.text.NumberFormat;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.nlp.NamedEntityRecognizer;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.search.PatternSearcher;

/**
 * 
 * @author Daniel Gerber
 */
public class PatternSearchThread extends Thread {

	private List<String[]> labels;
	private PatternSearcher patternSearcher;
	private NLPediaLogger logger;
	
	// the 
	private int i = 0;
	
	public PatternSearchThread(List<String[]> labels) {
		
		this.labels = labels;
		this.logger = new NLPediaLogger(PatternSearchThread.class);
		
		try {
			
			this.patternSearcher = new PatternSearcher(NLPediaSettings.getInstance().getSetting("sentenceIndexDirectory"));
		}
		catch (IOException e) {
			
			this.logger.fatal("Index directory not found.", e);
			e.printStackTrace();
		}
		catch (ParseException e) {
			
			this.logger.error("Could not parse query.", e);
			e.printStackTrace();
		}
	}
	
	public String getProgress() {
		
		return NumberFormat.getPercentInstance().format((double) i / (double)this.labels.size());
	}
	
	public void run() {
		
		try {
			
			for (i = 0; i < labels.size() ; i++) {
				
				if ( !labels.get(i)[0].equals(labels.get(i)[2]) && !labels.get(i)[0].contains("?") && !labels.get(i)[2].contains("?")) {
					
					patternSearcher.queryPattern(labels.get(i)[0], labels.get(i)[2], labels.get(i)[1], labels.get(i)[3], labels.get(i)[4]);
				}
			}
			System.out.println(this.getName() + ": 100%!");
			this.patternSearcher.close();
		}
		catch (IOException e) {
			
			this.logger.fatal("Index directory not found.", e);
			e.printStackTrace();
		}
		catch (ParseException e) {
			
			this.logger.error("Could not parse query.", e);
			e.printStackTrace();
		}
	}
	
	public List<String> getResults() {
		
		return this.patternSearcher.getResults();
	}
}
