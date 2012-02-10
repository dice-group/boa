/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.search;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import de.uni_leipzig.simba.boa.backend.backgroundknowledge.BackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.search.result.SearchResult;


/**
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public interface PatternSearcher {

	public String getSentencesByID(Integer id);
	
	public List<String> getSentencesByIds(List<Integer> ids);
	
	public Collection<SearchResult> queryBackgroundKnowledge(BackgroundKnowledge backgroundKnowledge);
	
	public boolean isPatternSuitable(String naturalLanguageRepresentation);
	
	public Set<String> getExactMatchSentences(String keyphrase, int maxNumberOfDocuments);
	
	public Set<String> getExactMatchSentencesForLabels(String label1, String label2, int maxNumberOfDocuments);
	
	public void close();
}
