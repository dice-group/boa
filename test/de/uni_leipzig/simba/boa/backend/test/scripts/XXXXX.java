package de.uni_leipzig.simba.boa.backend.test.scripts;

import java.util.Iterator;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.rdf.store.Store;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;


public class XXXXX {

	public static void main(String[] args) {
		
		NLPediaSetup setup = new NLPediaSetup(true);
		
		String queryString = 
			"SELECT ?p ?domain ?range " +
			"WHERE {" +
			" ?s ?p ?o . " +
			" ?p <http://www.w3.org/2000/01/rdf-schema#domain> ?domain . " +
			" ?p <http://www.w3.org/2000/01/rdf-schema#domain> ?range . " +
			"} " +
			"ORDER BY ?p";
		
		Store store = new Store();
		
		QueryExecution qexec = QueryExecutionFactory.create(queryString, store.getModel("en_wiki").getModel());
		
		Iterator<QuerySolution> resultsIterator = qexec.execSelect() ;
	    while (resultsIterator.hasNext()) {
	        QuerySolution solution = resultsIterator.next();
	        System.out.println(solution);
	    }
	}
}
