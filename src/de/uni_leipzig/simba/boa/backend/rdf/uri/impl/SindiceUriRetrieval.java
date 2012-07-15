package de.uni_leipzig.simba.boa.backend.rdf.uri.impl;

import com.sindice.Sindice;
import com.sindice.SindiceException;
import com.sindice.result.SearchResult;
import com.sindice.result.SearchResults;

import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.uri.UriRetrieval;


public class SindiceUriRetrieval implements UriRetrieval {
	
	private final Sindice sindice;
	private final NLPediaLogger logger = new NLPediaLogger(SindiceUriRetrieval.class);
	
	public SindiceUriRetrieval() {
		
		this.sindice = new Sindice();
	}

	public String getUri(String entityLabel) {

		SearchResults results = null;
		
		try {
			
			results = this.sindice.termSearch(entityLabel);
		}
		catch (SindiceException e) {
		
			this.logger.fatal("Something went wrong querying sindice for entityLabel: " + entityLabel, e);
			e.printStackTrace();
		}
		
		for (SearchResult result : results) {
			
			System.out.println(result.getLink());
		}
		return results.get(0) != null ?  results.get(0).getLink() : null;
	}

}
