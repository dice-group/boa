package de.uni_leipzig.simba.boa.backend.configuration.command.impl.scripts;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.rdf.Model;
import de.uni_leipzig.simba.boa.backend.rdf.store.Store;


public class AskDbpediaForTriple implements de.uni_leipzig.simba.boa.backend.configuration.command.Command {

	final String SPARQL_ENDPOINT_URI	= NLPediaSettings.getInstance().getSetting("dbpediaSparqlEndpoint");
	final String DBPEDIA_DEFAULT_GRAPH	= NLPediaSettings.getInstance().getSetting("dbpediaDefaultGraph");
	
	@Override
	public void execute() {

		Store store = new Store();
		Model model = store.createModelIfNotExists("en_wiki_rdf");
		QueryEngineHTTP qexec;
		
		int inDbpedia = 0, notInDbpedia = 0, j = 1;
		
		StmtIterator iter = model.getModel().listStatements();
		
		System.out.println("Anzahl Tripel: " + model.getNumberOfStatements());
		
		while (iter.hasNext()) {
			
			System.out.println("Iteration " + j++);
			
			Statement st = iter.next();

			String askQuery = 
				"ASK { <" + st.getSubject().getURI() + "> <" + st.getPredicate().getURI() + "> <" + ((Resource)st.getObject()).getURI() + "> } ";
			
			qexec = new QueryEngineHTTP(SPARQL_ENDPOINT_URI, askQuery);
			qexec.addDefaultGraph(DBPEDIA_DEFAULT_GRAPH);
			
			if (qexec.execAsk()) inDbpedia++;
			else {
				notInDbpedia++;
//				System.out.println(st);
			}
		}
		System.out.println("Nicht in Dbpedia: " + notInDbpedia);
		System.out.println("In Dbpedia: " + inDbpedia);
	}
}
