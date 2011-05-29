package de.uni_leipzig.simba.boa.backend.util;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;


public class DbpediaUtil {

	private static DbpediaUtil INSTANCE = null;
	private final String SPARQL_ENDPOINT_URI	= NLPediaSettings.getInstance().getSetting("dbpediaSparqlEndpoint");
	private final String DBPEDIA_DEFAULT_GRAPH	= NLPediaSettings.getInstance().getSetting("dbpediaDefaultGraph");
	
	
	private DbpediaUtil() {}
	
	/**
	 * @return the DbpediaUtil singleton
	 */
	public static DbpediaUtil getInstance() {
		
		if ( DbpediaUtil.INSTANCE == null ) {
			
			DbpediaUtil.INSTANCE = new DbpediaUtil();
		}
		return DbpediaUtil.INSTANCE;
	}
	
	/**
	 * Checks if a triple is in dbpedia already.
	 * 
	 * @param st the statement to check
	 * @return true if it's in dbpedia
	 */
	public boolean askDbpediaForTriple(Statement st) {
		
		String askQuery = 
			"ASK { <" + st.getSubject().getURI() + "> <" + st.getPredicate().getURI() + "> <" + ((Resource)st.getObject()).getURI() + "> } ";
		
		QueryEngineHTTP qexec = new QueryEngineHTTP(SPARQL_ENDPOINT_URI, askQuery);
		qexec.addDefaultGraph(DBPEDIA_DEFAULT_GRAPH);
		return qexec.execAsk();
	}

	public boolean askIsResourceOfType(String leftResourceUri, String type) {

		String askQuery = 
			"ASK { <" + leftResourceUri + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <" + type + "> } ";
		
		QueryEngineHTTP qexec = new QueryEngineHTTP(SPARQL_ENDPOINT_URI, askQuery);
		qexec.addDefaultGraph(DBPEDIA_DEFAULT_GRAPH);
		return qexec.execAsk();
	}
}
