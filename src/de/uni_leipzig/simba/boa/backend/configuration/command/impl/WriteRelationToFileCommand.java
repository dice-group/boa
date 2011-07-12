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

	private final String SPARQL_ENDPOINT_URI	= NLPediaSettings.getInstance().getSetting("dbpediaSparqlEndpoint");
	private final String DBPEDIA_DEFAULT_GRAPH	= NLPediaSettings.getInstance().getSetting("dbpediaDefaultGraph");
	private final String LABEL_OUTPUT_FILE		= NLPediaSettings.getInstance().getSetting("labelOutputFile");
	
	private final NLPediaLogger logger = new NLPediaLogger(WriteRelationToFileCommand.class);
	
	private final String language = "EN";
	
	/**
	 * 
	 */
	public void execute() {
		
		String queryPersonSubject		= this.createQueryPersonSubject();
		String queryPersonObject		= this.createQueryPersonObject();
		this.getPersonSubjectKnowledge(queryPersonSubject);
		this.getPersonObjectKnowledge(queryPersonObject);
		
		String queryPlaceSubject		= this.createQueryPlaceSubject();
		String queryPlaceObject			= this.createQueryPlaceObject();
		this.getPlaceSubjectKnowledge(queryPlaceSubject);
		this.getPlaceObjectKnowledge(queryPlaceObject);
		
		String queryOrganisationSubject	= this.createQueryOrganisationSubject();
		String queryOrganisationObject	= this.createQueryOrganisationObject();
		this.getOrganisationSubjectKnowledge(queryOrganisationSubject);
		this.getOrganisationObjectKnowledge(queryOrganisationObject);
	}

	private String createQueryOrganisationObject() {

		return 
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  " +
			"SELECT ?s2 ?s2l ?p2 ?o2 ?o2l ?rangep2 ?domainp2 " +
			"WHERE {" +
			 "	?s2 rdf:type <http://dbpedia.org/ontology/Organisation> ." +
			 "  ?s2 rdfs:label ?s2l . " +
			 "  ?o2 ?p2 ?s2 . " +
			 "	?o2 rdfs:label ?o2l " +
			 "	FILTER (  langMatches( lang(?s2l), \""+language+"\" )  && langMatches( lang(?o2l), \""+language+"\" ) ) " +
			 "	?p2  rdfs:range  ?rangep2 . " +
			 "	?p2  rdfs:domain ?domainp2 . " +
			"}";
	}

	private String createQueryOrganisationSubject() {

		return 
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  " +
			"SELECT ?s1 ?s1l ?p1 ?o1 ?o1l ?rangep1 ?domainp1 " +
			"WHERE {" +
			 "	?s1 rdf:type <http://dbpedia.org/ontology/Organisation> . " +
			 "  ?s1 rdfs:label ?s1l . " +
			 "	?s1 ?p1 ?o1 . " +
			 "	?o1 rdfs:label ?o1l ." +
			 "	FILTER (  langMatches( lang(?s1l), \""+language+"\" )  && langMatches( lang(?o1l), \""+language+"\" ) ) " +
			 "	?p1  rdfs:range  ?rangep1 . " +
			 "	?p1  rdfs:domain ?domainp1 . " +
			"}";
	}

	private String createQueryPlaceObject() {

		return
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  " +
			"SELECT ?s2 ?s2l ?p2 ?o2 ?o2l ?rangep2 ?domainp2 " +
			"WHERE {" +
			 "	?s2 rdf:type <http://dbpedia.org/ontology/Place> ." +
			 "  ?s2 rdfs:label ?s2l . " +
			 "  ?o2 ?p2 ?s2 . " +
			 "	?o2 rdfs:label ?o2l " +
			 "	FILTER (  langMatches( lang(?s2l), \""+language+"\" )  && langMatches( lang(?o2l), \""+language+"\" ) ) " +
			 "	?p2  rdfs:range  ?rangep2 . " +
			 "	?p2  rdfs:domain ?domainp2 . " +
			"}";
	}

	private String createQueryPlaceSubject() {

		return 
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  " +
			"SELECT ?s1 ?s1l ?p1 ?o1 ?o1l ?rangep1 ?domainp1 " +
			"WHERE {" +
			 "	?s1 rdf:type <http://dbpedia.org/ontology/Place> . " +
			 "  ?s1 rdfs:label ?s1l . " +
			 "	?s1 ?p1 ?o1 . " +
			 "	?o1 rdfs:label ?o1l ." +
			 "	FILTER (  langMatches( lang(?s1l), \""+language+"\" )  && langMatches( lang(?o1l), \""+language+"\" ) ) " +
			 "	?p1  rdfs:range  ?rangep1 . " +
			 "	?p1  rdfs:domain ?domainp1 . " +
			"}";
	}

	private String createQueryPersonObject() {

		return 
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  " +
			"SELECT ?s2 ?s2l ?p2 ?o2 ?o2l ?rangep2 ?domainp2 " +
			"WHERE {" +
			 "	?s2 rdf:type <http://dbpedia.org/ontology/Person> ." +
			 "  ?s2 rdfs:label ?s2l . " +
			 "  ?o2 ?p2 ?s2 . " +
			 "	?o2 rdfs:label ?o2l " +
			 "	FILTER (  langMatches( lang(?s2l), \""+language+"\" )  && langMatches( lang(?o2l), \""+language+"\" ) ) " +
			 "	?p2  rdfs:range  ?rangep2 . " +
			 "	?p2  rdfs:domain ?domainp2 . " +
			"}";
		
		// s2l + " ||| " + solution.get("p2").toString() + " ||| " + o2l + " ||| " + range + " ||| " + domain + " ||| isObject";
	}

	private String createQueryPersonSubject() {

		return 
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  " +
			"SELECT ?s1 ?s1l ?p1 ?o1 ?o1l ?rangep1 ?domainp1 " +
			"WHERE {" +
			 "	?s1 rdf:type <http://dbpedia.org/ontology/Person> . " +
			 "  ?s1 rdfs:label ?s1l . " +
			 "	?s1 ?p1 ?o1 . " +
			 "	?o1 rdfs:label ?o1l ." +
			 "	FILTER (  langMatches( lang(?s1l), \""+language+"\" )  && langMatches( lang(?o1l), \""+language+"\" ) ) " +
			 "	?p1  rdfs:range  ?rangep1 . " +
			 "	?p1  rdfs:domain ?domainp1 . " +
			"}";
		
		// s1l + " ||| " + solution.get("p1").toString() + " ||| " + o1l + " ||| " + range + " ||| " + domain + " ||| isSubject";
	}

	private void getPlaceObjectKnowledge(String queryPlaceObject) {

		ResultSet resultSet = this.getResultSet(queryPlaceObject);
		this.handleObjectQuery("/Users/gerb/de_place_object.txt", resultSet);
	}

	private void getPlaceSubjectKnowledge(String queryPlaceSubject) {

		ResultSet resultSet = this.getResultSet(queryPlaceSubject);
		this.handleSubjectQuery("/Users/gerb/de_place_subject.txt", resultSet);
	}

	private void getOrganisationObjectKnowledge(String queryOrganisationObject) {

		ResultSet resultSet = this.getResultSet(queryOrganisationObject);
		this.handleObjectQuery("/Users/gerb/de_organisation_object.txt", resultSet);
	}

	private void getOrganisationSubjectKnowledge(String queryOrganisationSubject) {

		ResultSet resultSet = this.getResultSet(queryOrganisationSubject);
		this.handleSubjectQuery("/Users/gerb/de_organisation_subject.txt", resultSet);
	}

	private void getPersonObjectKnowledge(String queryPersonObject) {

		ResultSet resultSet = this.getResultSet(queryPersonObject);
		this.handleObjectQuery("/Users/gerb/de_person_object.txt", resultSet);
	}

	private void getPersonSubjectKnowledge(String queryPersonSubject) {

		ResultSet resultSet = this.getResultSet(queryPersonSubject);
		this.handleSubjectQuery("/Users/gerb/de_person_subject.txt", resultSet);
	}
		
	private void handleSubjectQuery(String fileName, ResultSet resultSet) {

		Writer writer = null;
		
		try {
			
			writer =  new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
			
			while (resultSet.hasNext()) {
				
				QuerySolution solution = resultSet.next();

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
						
						writer.write(s1l + " ||| " + solution.get("p1").toString() + " ||| " + o1l + " ||| " + range + " ||| " + domain + " ||| isSubject");
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
	
	private void handleObjectQuery(String fileName, ResultSet resultSet) {

		
		Writer writer = null;
		
		try {
			
			writer =  new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
			
			while (resultSet.hasNext()) {
				
				QuerySolution solution = resultSet.next();

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
						
						writer.write(s2l + " ||| " + solution.get("p2").toString() + " ||| " + o2l + " ||| " + range + " ||| " + domain + " ||| isObject");
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

	private ResultSet getResultSet(String query) {
		
		QueryEngineHTTP qexec = new QueryEngineHTTP(SPARQL_ENDPOINT_URI, query);
		qexec.addDefaultGraph(DBPEDIA_DEFAULT_GRAPH);
		logger.info("Querying started for query: " + query);
		long start = System.currentTimeMillis();
		ResultSet rs =  qexec.execSelect();
		logger.info("Querying ended for query in " + (System.currentTimeMillis() - start) + "ms: " + query);
		return rs;
	}
}
