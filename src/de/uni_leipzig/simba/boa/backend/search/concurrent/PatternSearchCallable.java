package de.uni_leipzig.simba.boa.backend.search.concurrent;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.lucene.queryParser.ParseException;

import de.uni_leipzig.simba.boa.backend.backgroundknowledge.BackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;
import de.uni_leipzig.simba.boa.backend.search.impl.DefaultPatternSearcher;
import de.uni_leipzig.simba.boa.backend.search.result.SearchResult;

/**
 * 
 * @author Daniel Gerber
 * @param <A>
 */
public class PatternSearchCallable implements Callable<Collection<SearchResult>> {

	private final NLPediaLogger logger = new NLPediaLogger(PatternSearchCallable.class);
	
	private List<BackgroundKnowledge> backgroundKnowledgeList;
	private DefaultPatternSearcher patternSearcher;
	
	private final String INDEX_DIRECTORY = NLPediaSettings.BOA_DATA_DIRECTORY + NLPediaSettings.getInstance().getSetting("indexSentenceDirectory");
	
	// the 
	private int progress = 0;

	private String name;
	
	public PatternSearchCallable(List<BackgroundKnowledge> backgroundKnowledge) {
		
		this.backgroundKnowledgeList = backgroundKnowledge;
		
		try {
			
			this.patternSearcher = new DefaultPatternSearcher(INDEX_DIRECTORY);
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
	
	public String getName(){
		
		return this.name;
	}
	
	public double getProgress() {
		
		return (double) progress / this.backgroundKnowledgeList.size();
	}
	
	public int getNumberOfDoneSearches() {

		return this.progress;
	}
	
	public int getNumberOfSearches() {
		
		return this.backgroundKnowledgeList.size();
	}

	@Override
	public Collection<SearchResult> call() throws Exception {

		List<SearchResult> results = new ArrayList<SearchResult>();
		
		for ( BackgroundKnowledge backgroundKnowledge : this.backgroundKnowledgeList ) {
			
			if ( !backgroundKnowledge.getSubject().getLabel().equals(backgroundKnowledge.getObject().getLabel()) ) {
				
				results.addAll(patternSearcher.queryBackgroundKnowledge(backgroundKnowledge));
				progress++;
			}
		}
		this.patternSearcher.close();
		return results;
	}

	public void setName(String name) {

		this.name = name;
	}
}
