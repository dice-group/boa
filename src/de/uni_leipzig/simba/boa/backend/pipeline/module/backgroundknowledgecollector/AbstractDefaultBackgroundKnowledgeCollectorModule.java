/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.backgroundknowledgecollector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import de.danielgerber.Constants;
import de.danielgerber.file.BufferedFileWriter;
import de.danielgerber.file.BufferedFileWriter.WRITER_WRITE_MODE;
import de.danielgerber.file.FileUtil;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.BackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.impl.DatatypePropertyBackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.impl.ObjectPropertyBackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.scripts.SurfaceFormGenerator;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Resource;


/**
 * This module is used to query a SPARQL endpoint with certain properties and to write 
 * the results in the background knowledge files located in $DATA/backgroundknowledge/[datatype|object].
 * Please not that the properties have to gathered beforehand and stored in the 
 * WebContent/WEB-INF/data/backgroundknowledge/datatype_properties_to_query.txt or
 * WebContent/WEB-INF/data/backgroundknowledge/object_properties_to_query.txt file.
 * 
 *@author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public abstract class AbstractDefaultBackgroundKnowledgeCollectorModule extends AbstractBackgroundKnowledgeCollectorModule {
	
	private final NLPediaLogger logger 			= new NLPediaLogger(AbstractDefaultBackgroundKnowledgeCollectorModule.class);
	
	private final String SPARQL_ENDPOINT_URI				= NLPediaSettings.getInstance().getSetting("dbpediaSparqlEndpoint");
	private final String DBPEDIA_DEFAULT_GRAPH				= NLPediaSettings.getInstance().getSetting("dbpediaDefaultGraph");
	private final int SPARQL_QUERY_LIMIT					= new Integer(NLPediaSettings.getInstance().getSetting("sparqlQueryLimit"));
	protected final String BACKGROUND_KNOWLEDGE_OUTPUT_PATH	= NLPediaSettings.getInstance().getSetting("backgroundKnowledgeOutputFilePath");
	protected final String BOA_LANGUAGE						= NLPediaSettings.BOA_LANGUAGE;
	
	private Set<BackgroundKnowledge> backgroundKnowledge = new HashSet<BackgroundKnowledge>(); 

	@Override
	public String getName() {

		return "Default Background Knowledge Collector Module (de/en)";
	}
	
	@Override
	public String getReport() {

		return "A total of " + backgroundKnowledge.size() + " triples has been added to the background knowledge repository!";
	}
	
	@Override
	public void updateModuleInterchangeObject() {

		this.moduleInterchangeObject.setBackgroundKnowledge(this.backgroundKnowledge);
	}
	
	/**
	 * This method takes a given sparql query with limit and offset and retrieves
	 * all triples found in the knowledge base for the current property/query. 
	 * The results will be written to the specified file(name). Please note the this
	 * method also generates surface forms for the background knowledge. Also the triples
	 * like rdfs:range and rdfs:domain are queried for the given property.
	 * 
	 * @param query - the query to retrieve the background knowledge 
	 * @param propertyUri - the property uri, used to query its information
	 * @param fileName - the file to where to write the knowledge
	 */
	protected void getKnowledge(String query, String propertyUri, String fileName) {
		
		logger.info("Querying started for property: " + propertyUri);
		long start 			= System.currentTimeMillis();
	
		Property property	= this.queryPropertyData(propertyUri);
		int offset			= 0;
		
		// query as long as we get resultsets back
		while (true) {
			
			QueryEngineHTTP qexec = new QueryEngineHTTP(SPARQL_ENDPOINT_URI, query.replaceAll("&OFFSET", String.valueOf(offset)));
			qexec.addDefaultGraph(DBPEDIA_DEFAULT_GRAPH);
			
			this.logger.info("Starting to query for : " + query.replaceAll("&OFFSET", String.valueOf(offset)));

			List<QuerySolution> resultSetList = new ArrayList<QuerySolution>();
			
			// query current query, collect results and increment the offset afterwards
			ResultSet rs  = getResults(qexec, query);
			while (rs.hasNext()) resultSetList.add(rs.next());
			offset = offset + SPARQL_QUERY_LIMIT;
			
			// SPARQL query returned results
			if ( !resultSetList.isEmpty() ) {
				
				// this is an object property, only object properties can have labels
				if ( query.contains("?o rdfs:label ?ol") ) {

					property.setType("http://www.w3.org/2002/07/owl#ObjectProperty");
					handleObjectPropertyQuery(property, fileName, resultSetList);
				}
				else {
					
					property.setType("http://www.w3.org/2002/07/owl#DatatypeProperty");
					handleDatatypePropertyQuery(property, fileName, resultSetList);
				}
			}
			else { // end of query for current property
				
				qexec.close();
				break;
			}
		}
		logger.info("Querying ended in " + (System.currentTimeMillis() - start) + "ms for query: " + query);
	}
	
	/**
	 * This method handles the processing of the resultset. It creates new background
	 * knowledge and also creates the surface forms for the resources. It finally 
	 * writes the data to the fileName-file.
	 * 
	 * @param property - the property for this propertyUri
	 * @param fileName - the name of the file to write to
	 * @param resultSets - the resultset returned from the SPARQL endpoint
	 */
	private void handleDatatypePropertyQuery(Property property, String fileName, List<QuerySolution> resultSets) {
		
		BufferedFileWriter writer = FileUtil.openWriter(fileName, Constants.UTF_8_ENCODING, WRITER_WRITE_MODE.APPEND);
			
		for (QuerySolution solution : resultSets) {
			
			// get the subject and its label with the language tag
			String subjectUri = solution.get("s").toString();
			String subjectLabel = solution.get("sl").toString().substring(0, solution.get("sl").toString().lastIndexOf("@"));
			
			// get the object, which is a literal so remove either datatype or language tag
			String obj = solution.get("o").toString();
			if ( obj.contains("@"+BOA_LANGUAGE) ) obj = obj.substring(0, obj.lastIndexOf("@"));
			if ( obj.contains("^^") ) obj = obj.substring(0, solution.get("o").toString().indexOf("^"));
			
			// create new background knowledge and generate the surface forms 
			Resource subject	= new Resource(subjectUri, subjectLabel);
			Resource object		= new Resource(obj, obj);
			BackgroundKnowledge backgroundKnowledge = 
					SurfaceFormGenerator.getInstance().createSurfaceFormsForBackgroundKnowledge(
						new DatatypePropertyBackgroundKnowledge(subject, property, object));

			writer.write(backgroundKnowledge.toString());
			this.backgroundKnowledge.add(backgroundKnowledge);
		}
		writer.flush();
		writer.close();
	}
	
	/**
	 * This method handles the processing of the resultset. It creates new background
	 * knowledge and also creates the surface forms for the resources. It finally 
	 * writes the data to the fileName-file.
	 * 
	 * @param property - the property for this propertyUri
	 * @param fileName - the name of the file to write to
	 * @param resultSets - the resultset returned from the SPARQL endpoint
	 */
	private void handleObjectPropertyQuery(Property property, String fileName, List<QuerySolution> resultSets) {
	
		BufferedFileWriter writer = FileUtil.openWriter(fileName, Constants.UTF_8_ENCODING, WRITER_WRITE_MODE.APPEND);
		
		for (QuerySolution solution : resultSets) {
			
			// make sure the resultset contains the wanted fields
			if ( solution.get("s") != null && solution.get("callret-2") != null 
					&& solution.get("o") != null && solution.get("ol") != null ) {

				String subjectLabel = solution.get("sl").toString();
				String objectLabel = solution.get("ol").toString();

				// cut of language tags
				if (objectLabel.contains("@")) objectLabel = objectLabel.substring(0, objectLabel.lastIndexOf("@"));
				if (subjectLabel.contains("@")) subjectLabel = subjectLabel.substring(0, subjectLabel.lastIndexOf("@"));
				
				// create new background knowledge and generate the surface forms 
				Resource subject	= new Resource(solution.get("s").toString(), subjectLabel);
				Resource object		= new Resource(solution.get("o").toString(), objectLabel);
				BackgroundKnowledge backgroundKnowledge = 
						SurfaceFormGenerator.getInstance().createSurfaceFormsForBackgroundKnowledge(
							new ObjectPropertyBackgroundKnowledge(subject, property, object));

				writer.write(backgroundKnowledge.toString());
				this.backgroundKnowledge.add(backgroundKnowledge);
			}
		}
		writer.flush();
		writer.close();
	}
	
	/**
	 * Creates a new property with defined range and domain. It queries
	 * the SPARQL endpoint for the information.
	 * 
	 * @param propertyUri - the property Uri to query
	 * @return a new Property
	 */
	private Property queryPropertyData(String propertyUri) {

		String propertyQuery = 
				"SELECT distinct ?domain ?range " + 
				"WHERE { " +	
				"  <"+propertyUri+">  rdfs:domain ?domain . " +
				"  <"+propertyUri+">  rdfs:range ?range . " +
				"}";
		
		this.logger.info("Querying: " + propertyQuery);
		
		QueryEngineHTTP qexecProperty = new QueryEngineHTTP(SPARQL_ENDPOINT_URI, propertyQuery);
		qexecProperty.addDefaultGraph(DBPEDIA_DEFAULT_GRAPH);
		
		ResultSet results  = qexecProperty.execSelect();
		QuerySolution qs = results.next();
		return new Property(propertyUri, "", qs.get("range").toString(), qs.get("domain").toString());
	}
	
	/**
	 * This method is only used to catch the 503 HttpExceptions
	 * thrown by the SPARQL endpoint. It queries the endpoint as 
	 * long as it takes with a given query to get the correct result.
	 * 
	 * @param qexec - QueryEngineHTTP the sparql endpoint
	 * @param query - the query only for logging purposes
	 * @return a resultset for the given query
	 */
	private ResultSet getResults(QueryEngineHTTP qexec, String query) {
		
		ResultSet results = null;
		
		try {
	
			results  = qexec.execSelect();
		}
		catch (Exception e){
			
			results = getResults(qexec, query);
			System.out.println("Retrying query: " + query);
			logger.warn("Need to retry query: " + query, e);
		}
		
		return results;
	}
}
