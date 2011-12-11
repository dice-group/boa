package de.uni_leipzig.simba.boa.backend.search.concurrent;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;
import de.uni_leipzig.simba.boa.backend.search.PatternSearcher;
import de.uni_leipzig.simba.boa.backend.search.SearchResult;

/**
 * 
 * @author Daniel Gerber
 */
public class PatternSearchThread extends Thread {

	private List<Triple> triples;
	private PatternSearcher patternSearcher;
	private NLPediaLogger logger;
	
	// the 
	private int i = 0;
	
	public PatternSearchThread(List<Triple> triples) {
		
		this.triples = triples;
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
		
		return NumberFormat.getPercentInstance().format((double) i / (double) this.triples.size());
	}
	
	public void run() {
		
		try {
			
			for (i = 0; i < triples.size() ; i++) {
				
				// filter subject and objects with the same label and resources which have ? in their surfaceForms
				if ( !triples.get(i).getSubject().getLabel().equals(triples.get(i).getObject().getLabel()) && !triples.get(i).getSubject().getLabel().contains("?") && !triples.get(i).getObject().getLabel().contains("?")) {
					
					patternSearcher.queryPattern(triples.get(i));
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
	
	public List<SearchResult> getResults() {
		
		return this.patternSearcher.getResults();
	}

	public int getNumberOfDoneSearches() {

		return this.i;
	}
	
	public int getNumberOfSearches() {
		
		return this.triples.size();
	}
}
