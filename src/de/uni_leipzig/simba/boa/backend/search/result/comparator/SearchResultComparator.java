package de.uni_leipzig.simba.boa.backend.search.result.comparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.search.result.SearchResult;

/**
 * 
 */
public class SearchResultComparator implements Comparator<SearchResult> {

	@Override
	public int compare(SearchResult arg0, SearchResult arg1) {

		int i = arg0.getProperty().getUri().compareTo(arg1.getProperty().getUri());

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
		s1.setProperty(new Property("http://dbpedia.org/ontology/birthPlace", "range1", "domain1"));
		s1.setNaturalLanguageRepresentation("?D? was born in the ?R?");
		s1.setSentence(12);
		res.add(s1);
		
		SearchResult s2 = new SearchResult();
		s2.setProperty(new Property("http://dbpedia.org/ontology/airthPlace", "range1", "domain1"));
		s2.setNaturalLanguageRepresentation("?D? vas born in the ?R?");
		s2.setSentence(123);
		res.add(s2);
		
		SearchResult s3 = new SearchResult();
		s3.setProperty(new Property("http://dbpedia.org/ontology/airthPlace", "range1", "domain1"));
		s3.setNaturalLanguageRepresentation("?D? vas born in the ?R?");
		s3.setSentence(124);
		res.add(s3);
		
		SearchResult s4 = new SearchResult();
		s4.setProperty(new Property("http://dbpedia.org/ontology/cirthPlace", "range1", "domain1"));
		s4.setNaturalLanguageRepresentation("?D? was born in the ?R?");
		s4.setSentence(125);
		res.add(s4);
		
		Collections.sort(res, comp);
		
		for (SearchResult ress : res) {
			
			System.out.println(ress);
		}
	}
}