package de.uni_leipzig.simba.boa.backend.search.concurrent;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;

import de.uni_leipzig.simba.boa.backend.backgroundknowledge.BackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.search.impl.DefaultPatternSearcher;
import de.uni_leipzig.simba.boa.backend.search.result.SearchResult;

/**
 * 
 * @author Daniel Gerber
 */
public class PatternSearchThread extends Thread {

	private final NLPediaLogger logger = new NLPediaLogger(PatternSearchThread.class);
	
	private List<BackgroundKnowledge> backgroundKnowledgeList;
	private DefaultPatternSearcher patternSearcher;
	
	// the 
	private int i = 0;
	
	public PatternSearchThread(List<BackgroundKnowledge> backgroundKnowledge) {
		
		this.backgroundKnowledgeList = backgroundKnowledge;
		
		try {
			
			this.patternSearcher = new DefaultPatternSearcher(NLPediaSettings.getInstance().getSetting("sentenceIndexDirectory"));
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
			
			for ( BackgroundKnowledge backgroundKnowledge : this.backgroundKnowledgeList ) {
				
				if ( !backgroundKnowledge.getSubject().getLabel().equals(backgroundKnowledge.getObject().getLabel()) ) {
					
					patternSearcher.queryBackgroundKnowledge(backgroundKnowledge);
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
		
		return this.backgroundKnowledgeList.size();
	}
}
