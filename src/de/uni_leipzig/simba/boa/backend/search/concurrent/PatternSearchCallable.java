package de.uni_leipzig.simba.boa.backend.search.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.github.gerbsen.encoding.Encoder.Encoding;
import com.github.gerbsen.file.BufferedFileWriter;
import com.github.gerbsen.file.BufferedFileWriter.WRITER_WRITE_MODE;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.BackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.concurrent.BoaCallable;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
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
	private int foundSoFar = 0;
	
	public PatternSearchCallable(List<BackgroundKnowledge> backgroundKnowledge) {
		
		this.backgroundKnowledgeList = backgroundKnowledge;
		this.results                 = new ArrayList<SearchResult>();
	}
	
	@Override
	public Collection<SearchResult> call() throws Exception {

	    // initialize the pattern searcher at thread execution
	    // otherwise we would have X non running threads with an opened index
	    this.patternSearcher = PatternSearcherFactory.getInstance().createDefaultPatternSearcher(null);
	    BufferedFileWriter writer = 
	            new BufferedFileWriter(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.SEARCH_RESULT_PATH + this.name + ".sr", Encoding.UTF_8, WRITER_WRITE_MODE.OVERRIDE);
		
		for ( BackgroundKnowledge backgroundKnowledge : this.backgroundKnowledgeList ) {
			
			if ( !backgroundKnowledge.getSubjectLabel().equals(backgroundKnowledge.getObjectLabel()) ) {
				
				for (SearchResult result : patternSearcher.queryBackgroundKnowledge(backgroundKnowledge) ) {
				    
				    writer.write(result.toString());
				    writer.flush();
                    foundSoFar++;
				}
				progress++;
			}
		}
		writer.close();
		this.patternSearcher.close();
		return results;
	}
	
	@Override
    public double getProgress() {
        
        return (double) progress / this.backgroundKnowledgeList.size();
    }

    @Override
    public int getNumberTotal() {

        return this.backgroundKnowledgeList.size();
    }

    @Override
    public int getNumberOfResultsSoFar() {

        return this.foundSoFar;
    }
}
