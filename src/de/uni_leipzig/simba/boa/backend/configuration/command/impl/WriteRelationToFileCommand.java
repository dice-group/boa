package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

/**
 * 
 * @author Daniel Gerber
 */
public class WriteRelationToFileCommand implements Command {

	/**
	 * 
	 */
	public void execute() {
		
		final String SPARQL_ENDPOINT_URI	= NLPediaSettings.getInstance().getSetting("dbpediaSparqlEndpoint");
		final String DBPEDIA_DEFAULT_GRAPH	= NLPediaSettings.getInstance().getSetting("dbpediaDefaultGraph");
		final String LABEL_OUTPUT_FILE		= NLPediaSettings.getInstance().getSetting("labelOutputFile");
		final String RESOURCE_TYPE_URL		= NLPediaSettings.getInstance().getSetting("resourceTypeUrl");
		
		final NLPediaLogger logger = new NLPediaLogger(WriteRelationToFileCommand.class);
		
//		String constructQuery = 
//				"CONSTRUCT { " +
//				"	?s1 rdf:type <" + RESOURCE_TYPE_URL + "> . " +
//				"   ?s1 rdfs:label ?s1l . " +
//				"	?s1 ?p1 ?o1 . " + 
//				"	?o1 ?oo ?ooo . " +
//				"	?p1 ?pp ?ppp . " +
//				"}" +
//				"WHERE {" +
//				"	?s1 rdf:type <" + RESOURCE_TYPE_URL + "> . " +
//				"   ?s1 rdfs:label ?s1l . " +
//				"	?s1 ?p1 ?o1 . " + 
//				"	?o1 rdfs:label ?o1l . " +
//				"	?o1 ?oo ?ooo . " +
//				"	?p1 ?pp ?ppp . " +
//				"	FILTER (lang(?s1l) = \"en\") . " +
//				 "	FILTER (lang(?o1l) = \"en\") . " +
//				 "	FILTER (?p1 != <http://dbpedia.org/property/redirect>) . " +
//				"}";
		
		String queryString = 
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  " +
			"SELECT ?s1 ?s1l ?p1 ?o1 ?o1l ?rangep1 ?domainp1 ?s2 ?s2l ?p2 ?o2 ?o2l ?rangep2 ?domainp2 " +
			"WHERE {" +
			 "{ " +
			 "	?s1 rdf:type <" + RESOURCE_TYPE_URL + "> . " +
			 "  ?s1 rdfs:label ?s1l . " +
			 "	?s1 ?p1 ?o1 . " +
			 "	?o1 rdfs:label ?o1l ." +
			 "	FILTER (lang(?s1l) = \"en\") . " +
			 "	FILTER (lang(?o1l) = \"en\") . " +
			 "	?p1  rdfs:range  ?rangep1 . " +
			 "	?p1  rdfs:domain ?domainp1 . " +
			 "} " +
			 " UNION " +
			 "{ " +
			 "	?s2 rdf:type <" + RESOURCE_TYPE_URL + "> ." +
			 "  ?s2 rdfs:label ?s2l . " +
			 "  ?o2 ?p2 ?s2 . " +
			 "	?o2 rdfs:label ?o2l " +
			 "	FILTER (lang(?s2l) = \"en\") " +
			 "	FILTER (lang(?o2l) = \"en\") " +
			 "	?p2  rdfs:range  ?rangep2 . " +
			 "	?p2  rdfs:domain ?domainp2 . " +
			 "} " +
			"} ";

		System.out.println(queryString);
		
		QueryEngineHTTP qexec = new QueryEngineHTTP(SPARQL_ENDPOINT_URI, queryString);
		qexec.addDefaultGraph(DBPEDIA_DEFAULT_GRAPH);
	
		logger.info("Querying started for query: " + queryString);
	
//		Model model = qexec.execConstruct();
		
//		Store store = new Store();
//		de.uni_leipzig.simba.boa.backend.rdf.Model model1 = store.createModelIfNotExists("en_wiki");
//		model1.addStatements(model.listStatements().toList());
//		System.exit(0);
		
		ResultSet results = qexec.execSelect();
	
		logger.info("Querying ended.");
	
		Writer writer = null;
		
		try {
			
			writer =  new PrintWriter(new BufferedWriter(new FileWriter(LABEL_OUTPUT_FILE)));
	
			while (results.hasNext()) {
	
				QuerySolution solution = results.next();
	
				if (solution.get("s1") != null && solution.get("p1") != null && solution.get("o1") != null) {
	
					if (solution.get("o1l") != null) {
	
						String s1l = solution.get("s1l").toString(), o1l = solution.get("o1l").toString();
	
						if (o1l.contains("@")) {
	
							o1l = o1l.substring(0, o1l.lastIndexOf("@"));
						}
						if (s1l.contains("@")) {
	
							s1l = s1l.substring(0, s1l.lastIndexOf("@"));
						}
						
						String range = solution.get("rangep1") == null ? "null" : solution.get("rangep1").toString();
						String domain = solution.get("domainp1") == null ? "null" : solution.get("domainp1").toString();
						
						writer.write(s1l + " ||| " + solution.get("p1").toString() + " ||| " + o1l + " ||| " + range + " ||| " + domain);
						writer.write(System.getProperty("line.separator"));
					}
				}
	
				if (solution.get("s2") != null && solution.get("p2") != null && solution.get("o2") != null) {
	
					if (solution.get("o2l") != null) {
	
						String s2l = solution.get("s2l").toString(), o2l = solution.get("o2l").toString();
	
						if (o2l.contains("@")) {
	
							o2l = o2l.substring(0, o2l.lastIndexOf("@"));
						}
						if (s2l.contains("@")) {
	
							s2l = s2l.substring(0, s2l.lastIndexOf("@"));
						}
						
						String range = solution.get("rangep2") == null ? "null" : solution.get("rangep2").toString();
						String domain = solution.get("domainp2") == null ? "null" : solution.get("domainp2").toString();
						
						writer.write(s2l + " ||| " + solution.get("p2").toString() + " ||| " + o2l + " ||| " + domain + " ||| " + range);
						writer.write(System.getProperty("line.separator"));
					}
				}
			}
		}
		catch (Exception e) {
	
			e.printStackTrace();
			logger.error("Could not write to label relation file.", e);
		}
		finally {
	
			try {
				
				writer.close();
			}
			catch (IOException ioe) {
				
				logger.error("Could not close file writer.", ioe);
				ioe.printStackTrace();
			}
		}
	}
}
