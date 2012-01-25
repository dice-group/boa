package de.uni_leipzig.simba.boa.backend.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
	
	public static void main(String[] args) {

		SearchResultComparator comp = new SearchResultComparator();
		List<SearchResult> res = new ArrayList<SearchResult>();
		
		SearchResult s1 = new SearchResult();
		s1.setProperty("http://dbpedia.org/ontology/birthPlace");
		s1.setNaturalLanguageRepresentation("?D? was born in the ?R?");
		res.add(s1);
		
		SearchResult s2 = new SearchResult();
		s2.setProperty("http://dbpedia.org/ontology/airthPlace");
		s2.setNaturalLanguageRepresentation("?D? vas born in the ?R?");
		res.add(s2);
		
		SearchResult s3 = new SearchResult();
		s3.setProperty("http://dbpedia.org/ontology/airthPlace");
		s3.setNaturalLanguageRepresentation("?D? vas born in the ?R?");
		res.add(s3);
		
		SearchResult s4 = new SearchResult();
		s4.setProperty("http://dbpedia.org/ontology/cirthPlace");
		s4.setNaturalLanguageRepresentation("?D? was born in the ?R?");
		res.add(s4);
		
		Collections.sort(res, comp);
		
		for (SearchResult ress : res) {
			
			System.out.println(ress);
		}
	}
}