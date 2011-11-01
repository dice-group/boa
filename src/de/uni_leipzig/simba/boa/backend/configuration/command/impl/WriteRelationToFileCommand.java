package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.ListUtils;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import de.danielgerber.file.FileUtil;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

/**
 * 
 * @author Daniel Gerber
 */
public class WriteRelationToFileCommand implements Command {

	private static final NLPediaSetup setup 			= new NLPediaSetup(true);
	private static final String SPARQL_ENDPOINT_URI		= NLPediaSettings.getInstance().getSetting("dbpediaSparqlEndpoint");
	private static final String DBPEDIA_DEFAULT_GRAPH	= NLPediaSettings.getInstance().getSetting("dbpediaDefaultGraph");
	private static final int LIMIT						= 100000;
	
	private static final NLPediaLogger logger = new NLPediaLogger(WriteRelationToFileCommand.class);
	
	private static final String language = "en";
	
	public static void main(String[] args) {

		WriteRelationToFileCommand c = new WriteRelationToFileCommand();
		c.execute();
	}
	
	/**
	 * 
	 */
	public void execute() {
		
		try {
			
//			queryObjectProperties();
			queryDatatypeProperties();
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void queryDatatypeProperties() throws UnsupportedEncodingException, FileNotFoundException, IOException {

		List<String> datatypePropertyUris = FileUtil.readFileInList(NLPediaSettings.getInstance().getSetting("crawlDirectory") + "properties_to_query.txt", "UTF-8");
		for ( String datatypePropertyUri : datatypePropertyUris ) {
			
			String personQuery = createDatatypePropertyQueryObject("http://dbpedia.org/ontology/Person", datatypePropertyUri);
			String organisationQuery = createDatatypePropertyQueryObject("http://dbpedia.org/ontology/Organisation", datatypePropertyUri);
			String placeQuery = createDatatypePropertyQueryObject("http://dbpedia.org/ontology/Place", datatypePropertyUri);
			
			getKnowledge(personQuery, 0,		"/Users/gerb/"+datatypePropertyUri.substring(datatypePropertyUri.lastIndexOf("/"), datatypePropertyUri.length())+".txt");
			getKnowledge(organisationQuery, 0,	"/Users/gerb/"+datatypePropertyUri.substring(datatypePropertyUri.lastIndexOf("/"), datatypePropertyUri.length())+".txt");
			getKnowledge(placeQuery, 0,			"/Users/gerb/"+datatypePropertyUri.substring(datatypePropertyUri.lastIndexOf("/"), datatypePropertyUri.length())+".txt");
		}
	}

	private void queryObjectProperties() {

		new Thread(new Runnable() { public void run() {
			
			String queryPersonSubject		= createObjectPropertyQuerySubject("http://dbpedia.org/ontology/Person");
			getKnowledge(queryPersonSubject, 0,"/Users/gerb/de_person_s.txt");
			
			System.out.println("person subject done");
			
		}}).start();
		
		new Thread(new Runnable() { public void run() {
			
			String queryPersonObject		= createObjectPropertyQueryObject("http://dbpedia.org/ontology/Person");
			getKnowledge(queryPersonObject, 0, "/Users/gerb/de_person_o.txt");
			
			System.out.println("person object done");
			
		}}).start();
		
		new Thread(new Runnable() { public void run() {
			
			String queryPlaceSubject		= createObjectPropertyQuerySubject("http://dbpedia.org/ontology/Place");
			getKnowledge(queryPlaceSubject, 0,"/Users/gerb/de_place_s.txt");
			
			System.out.println("place subject done");
			
		}}).start();
		
		new Thread(new Runnable() { public void run() {
			
			String queryPlaceObject			= createObjectPropertyQueryObject("http://dbpedia.org/ontology/Place");
			getKnowledge(queryPlaceObject, 0, "/Users/gerb/de_place_o.txt");

			System.out.println("place object done");
			
		}}).start();
		
		new Thread(new Runnable() { public void run() {
			
			String queryOrganisationSubject	= createObjectPropertyQuerySubject("http://dbpedia.org/ontology/Organisation");
			getKnowledge(queryOrganisationSubject, 0,"/Users/gerb/de_organisation_s.txt");
			
			System.out.println("organisation subject done");
			
		}}).start();
		
		new Thread(new Runnable() { public void run() {
			
			String queryOrganisationObject	= createObjectPropertyQueryObject("http://dbpedia.org/ontology/Organisation");
			getKnowledge(queryOrganisationObject, 0,"/Users/gerb/de_organisation_o.txt");
			
			System.out.println("organisation object done");
			
		}}).start();
	}
	
	private static String createDatatypePropertyQueryObject(String entityTypeUri, String propertyUri) {

		return 
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  " +
			"SELECT ?s ?sl ?p ?o ?range ?domain " +
			"WHERE {" +
			 "	?s rdfs:label ?sl . " +
			 "  ?s rdf:type <"+ entityTypeUri + "> . " + 
			 "  ?s <" + propertyUri + "> ?o . " +
			 "	FILTER (   lang(?sl)= \""+language+"\" && str(?p) = \""+propertyUri+"\" ) . " +
			 "	?p rdfs:range  ?range . " +
			 "	?p rdfs:domain ?domain . " +
			 "} " +
			 "LIMIT " + LIMIT +  " " +
			 "OFFSET &OFFSET";
	}
	

	private static String createObjectPropertyQueryObject(String typeUri) {

		return 
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  " +
			"SELECT ?s ?sl ?p ?o ?ol ?range ?domain " +
			"WHERE {" +
			 "	?s rdfs:label ?sl . " + 
			 "  ?s ?p ?o . " +
			 "	?o rdf:type <"+typeUri+"> . " +
			 "  ?o rdfs:label ?ol . " +
//			 "	FILTER (  langMatches( lang(?sl), \""+language+"\" )  && langMatches( lang(?ol), \""+language+"\" ) ) " +
			 "	FILTER (   lang(?sl)= \""+language+"\"  &&  lang(?ol)= \""+language+"\"  ) " +
			 "	?p rdfs:range  ?range . " +
			 "	?p rdfs:domain ?domain . " +
			 "} " +
			 "LIMIT " + LIMIT +  " " +
			 "OFFSET &OFFSET";
	}

	private static String createObjectPropertyQuerySubject(String typeUri) {

		return 
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  " +
			"SELECT ?s ?sl ?p ?o ?ol ?range ?domain " +
			"WHERE {" +
			 "	?s rdf:type <"+typeUri+"> . " +
			 "  ?s rdfs:label ?sl . " +
			 "	?s ?p ?o . " +
			 "	?o rdfs:label ?ol ." +
			 //"	FILTER (  langMatches( lang(?sl), \""+language+"\" )  && langMatches( lang(?ol), \""+language+"\" ) ) " +
			 "	FILTER (   lang(?sl)= \""+language+"\"  &&  lang(?ol)= \""+language+"\"  ) " +
			 "	?p  rdfs:range  ?range . " +
			 "	?p  rdfs:domain ?domain . " +
			 "} " +
			 "LIMIT " + LIMIT +  " " +
			 "OFFSET &OFFSET";
	}
	
	private static void handleDatatypePropertyQuery(String fileName, List<QuerySolution> resultSets) {
		
		Writer writer = null;
		
		try {
			
			writer =  new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
			
			for (QuerySolution solution : resultSets) {
				
				// ?s ?sl ?p ?o ?range ?domain
				String subject = solution.get("s").toString();
				String subjectLabel = solution.get("sl").toString().substring(0, solution.get("sl").toString().lastIndexOf("@"));;
				String property = solution.get("p").toString();
				String object = solution.get("o").toString();
				if ( object.contains("@"+language) ) object.substring(0, object.lastIndexOf("@"));
				if ( object.contains("^^") ) object = object.substring(0, solution.get("o").toString().indexOf("^"));
				String range = solution.get("range").toString();
				String domain = solution.get("domain").toString();
				
				writer.write(subject + " ||| " + subjectLabel + " ||| " + property + " ||| " + object + " ||| " + object + " ||| " + range + " ||| " + domain);
				writer.write(System.getProperty("line.separator"));
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

	private static void handleObjectPropertyQuery(String fileName, List<QuerySolution> resultSets) {

		Writer writer = null;
		
		try {
			
			writer =  new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
			
			for (QuerySolution solution : resultSets) {
				
				if (solution.get("s") != null && solution.get("p") != null && solution.get("o") != null) {
	
					if (solution.get("ol") != null) {
	
						String sl = solution.get("sl").toString();
						String ol = solution.get("ol").toString();
	
						if (ol.contains("@")) {
	
							ol = ol.substring(0, ol.lastIndexOf("@"));
						}
						if (sl.contains("@")) {
	
							sl = sl.substring(0, sl.lastIndexOf("@"));
						}
						
						String range = solution.get("range") == null ? "null" : solution.get("range").toString();
						String domain = solution.get("domain") == null ? "null" : solution.get("domain").toString();
						
						writer.write(solution.get("s").toString() + " ||| " + sl + " ||| " + solution.get("p").toString() + " ||| " + solution.get("o").toString() + " ||| " + ol + " ||| " + range + " ||| " + domain);
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
	
	private static void getKnowledge(String query, int offset1, String fileName) {
		
		logger.info("Querying started for query: " + query);
		long start = System.currentTimeMillis();
		
		int offset = offset1;
		while (true) {
			
			System.out.println(query.replaceAll("&OFFSET", String.valueOf(offset)));
			
			QueryEngineHTTP qexec = new QueryEngineHTTP(SPARQL_ENDPOINT_URI, query.replaceAll("&OFFSET", String.valueOf(offset)));
			qexec.addDefaultGraph(DBPEDIA_DEFAULT_GRAPH);

			List<QuerySolution> resultSetList = new ArrayList<QuerySolution>();
			
			ResultSet rs  = qexec.execSelect();
			while (rs.hasNext()) resultSetList.add(rs.next());
			offset = offset + LIMIT;
			
			if ( !resultSetList.isEmpty() ) {
				
				// this is an object property 
				if ( query.contains("?o rdfs:label ?ol") ) {

					handleObjectPropertyQuery(fileName, resultSetList);
				}
				else {
					
					handleDatatypePropertyQuery(fileName, resultSetList);
				}
			}
			else break;
		}
		logger.info("Querying ended for query in " + (System.currentTimeMillis() - start) + "ms: " + query);
	}
}
