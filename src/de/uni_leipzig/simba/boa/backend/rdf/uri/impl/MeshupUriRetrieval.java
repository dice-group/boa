package de.uni_leipzig.simba.boa.backend.rdf.uri.impl;

import nlpbox.lookup.SparqlLookup;
import de.uni_leipzig.simba.boa.backend.rdf.uri.UriRetrieval;


public class MeshupUriRetrieval implements UriRetrieval {

	@Override
	public String getUri(String label) {

		String uri = "";
		
		try {
			SparqlLookup sl = new SparqlLookup("", "", "");
			uri = sl.lookup(label, "", "");
		}
		catch ( NullPointerException npe) {
			
			uri = "http://nlpedia.de/"+label.replaceAll(" ", "_");
		}
		return uri;
	}
	
	public static void main(String[] args) {

		MeshupUriRetrieval mur = new MeshupUriRetrieval();
		System.out.println(mur.getUri("Bosnia"));
		System.out.println(mur.getUri("Bosniaasdasdasddd"));
	}
}
