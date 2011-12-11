package de.uni_leipzig.simba.boa.backend.rdf.uri.impl;

import com.hp.hpl.jena.query.QueryParseException;

import nlpbox.lookup.SparqlLookup;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.uri.UriRetrieval;


public class MeshupUriRetrieval implements UriRetrieval {

	private NLPediaLogger logger = new NLPediaLogger(MeshupUriRetrieval.class);
	
	@Override
	public String getUri(String label) {

		String uri = "";
		
		try {
			SparqlLookup sl = new SparqlLookup("", "", "");
			uri = sl.lookup(label.replace("``", ""), "", "");
		}
		catch ( NullPointerException npe) {
			
			logger.error("A nullpointer for label: \"" + label + "\" has occured.", npe);
			uri = "http://boa.akws.org/resource/"+label.replaceAll(" ", "_");
		}
		catch ( QueryParseException qpe ) {
			
			logger.error("Remote lucene index throws parse excpetion because of illegal charactes while precessing label: \"" + label + "\" has occured.", qpe);
			uri = "http://boa.akws.org/resource/"+label.replaceAll(" ", "_");
		}
		catch (Exception e) {
			
			logger.error("Sometimes we get an httpexception for label: \"" + label + "\" has occured.", e);
			uri = "http://boa.akws.org/resource/"+label.replaceAll(" ", "_");
		}
		if ( uri.startsWith("http://scms.eu/") ) uri = uri.replace("http://scms.eu/", "http://boa.akws.org/resource/");
		return uri;
	}
	
	public static void main(String[] args) {

		MeshupUriRetrieval mur = new MeshupUriRetrieval();
		System.out.println(mur.getUri("Bosnia"));
		System.out.println(mur.getUri("Bosniaasdasdasddd"));
	}
}
