package de.uni_leipzig.simba.boa.backend.search.result.comparator;

import java.util.Comparator;
import de.uni_leipzig.simba.boa.backend.search.result.SearchResult;

/**
 * 
 */
public class SearchResultComparator implements Comparator<SearchResult> {

	@Override
	public int compare(SearchResult arg0, SearchResult arg1) {

		int i = arg0.getProperty().compareTo(arg1.getProperty());

		if (i == 0) {

			return arg0.getNaturalLanguageRepresentation().compareTo(arg1.getNaturalLanguageRepresentation());
		}
		else {

			return i;
		}
	}
}