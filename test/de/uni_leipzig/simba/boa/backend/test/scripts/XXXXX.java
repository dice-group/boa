package de.uni_leipzig.simba.boa.backend.test.scripts;

import java.util.Iterator;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.rdf.store.Store;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;


public class XXXXX {

	public static void main(String[] args) {

		NLPediaSetup setup = new NLPediaSetup(true);
		
		final String SPARQL_ENDPOINT_URI	= "http://de.dbpedia.org/sparql";
		final String DBPEDIA_DEFAULT_GRAPH	= NLPediaSettings.getInstance().getSetting("dbpediaDefaultGraph");
		
		int limit = 100;
		int offset = 0;
		
		QueryEngineHTTP qexec = new QueryEngineHTTP(SPARQL_ENDPOINT_URI, createQuery("de", limit, offset));
		qexec.addDefaultGraph(DBPEDIA_DEFAULT_GRAPH);
		
		ResultSet results;
		
		while ( (results = qexec.execSelect()) != null ) {
			
			while (results.hasNext()) System.out.println(results.next());
			
			offset = offset + limit;
			qexec = new QueryEngineHTTP(SPARQL_ENDPOINT_URI, createQuery("de", limit, offset));
		}
		qexec.execSelect();
	}
	
	private static String createQuery(String language, int limit, int offset) {
		
		String query = 
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  " +
				"SELECT ?s2 ?s2l "+ //?p2 ?o2 ?o2l ?rangep2 ?domainp2 " +
				"WHERE {" +
				 "	?s2 rdf:type <http://dbpedia.org/ontology/Organisation> ." +
				 "  ?s2 rdfs:label ?s2l . " +
//				 "  FILTER (lang(?s2l) = 'de') " +
//				 "  ?o2 ?p2 ?s2 . " +
//				 "	?o2 rdfs:label ?o2l " +
//				 "	FILTER (  lang(?s2l) = \""+language+"\" && lang(?o2l) = \""+language+"\" ) " +
//				 "	?p2  rdfs:range  ?rangep2 . " +
//				 "	?p2  rdfs:domain ?domainp2 . " +
				"} " +
				"LIMIT " + limit + " " +
				"OFFSET " + offset;
		System.out.println(query);
		return query;
	}
}
