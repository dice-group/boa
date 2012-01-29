package de.uni_leipzig.simba.boa.backend.search.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.uni_leipzig.simba.boa.backend.backgroundknowledge.BackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.concurrent.BoaCallable;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.search.impl.DefaultPatternSearcher;
import de.uni_leipzig.simba.boa.backend.search.result.SearchResult;

/**
 * 
 * @author Daniel Gerber
 * @param <A>
 */
public class PatternSearchCallable extends BoaCallable<SearchResult>{

	private final NLPediaLogger logger = new NLPediaLogger(PatternSearchCallable.class);
	
	private List<BackgroundKnowledge> backgroundKnowledgeList;
	private DefaultPatternSearcher patternSearcher;
	
	public PatternSearchCallable(List<BackgroundKnowledge> backgroundKnowledge) {
		
		this.backgroundKnowledgeList = backgroundKnowledge;
		this.patternSearcher = new DefaultPatternSearcher();
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
	
	@Override
    public double getProgress() {
        
        return (double) progress / this.backgroundKnowledgeList.size();
    }

    @Override
    public int getNumberTotal() {

        // TODO Auto-generated method stub
        return this.backgroundKnowledgeList.size();
    }
}
