package de.uni_leipzig.simba.boa.backend.search.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.uni_leipzig.simba.boa.backend.backgroundknowledge.BackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.concurrent.BoaCallable;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.search.PatternSearcher;
import de.uni_leipzig.simba.boa.backend.search.PatternSearcherFactory;
import de.uni_leipzig.simba.boa.backend.search.result.SearchResult;

/**
 * 
 * @author Daniel Gerber
 * @param <A>
 */
public class PatternSearchCallable extends BoaCallable<SearchResult>{

	private final NLPediaLogger logger = new NLPediaLogger(PatternSearchCallable.class);
	
	private List<BackgroundKnowledge> backgroundKnowledgeList;
	private PatternSearcher patternSearcher;
	private List<SearchResult> results;
	
	public PatternSearchCallable(List<BackgroundKnowledge> backgroundKnowledge) {
		
		this.backgroundKnowledgeList = backgroundKnowledge;
		this.results                 = new ArrayList<SearchResult>();
	}
	
	@Override
	public Collection<SearchResult> call() throws Exception {

	    // initialize the pattern searcher at thread execution
	    // otherwise we would have X non running threads with an opened index
	    this.patternSearcher = PatternSearcherFactory.getInstance().createDefaultPatternSearcher(null);
		
		for ( BackgroundKnowledge backgroundKnowledge : this.backgroundKnowledgeList ) {
			
			if ( !backgroundKnowledge.getSubjectLabel().equals(backgroundKnowledge.getObjectLabel()) ) {
				
				results.addAll(patternSearcher.queryBackgroundKnowledge(backgroundKnowledge));
				progress++;
			}
		}
		this.patternSearcher.close();
		return results;
	}
	
	@Override
    public double getProgress() {
        
        return (double) progress / this.backgroundKnowledgeList.size();
    }

    @Override
    public int getNumberTotal() {

        // TODO Auto-generated method stub
        return this.backgroundKnowledgeList.size();
    }

    @Override
    public int getNumberOfResultsSoFar() {

        return this.results.size();
    }
}
