/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.backgroundknowledgecollector.impl;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import de.danielgerber.file.FileUtil;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.BackgroundKnowledgeManager;
import de.uni_leipzig.simba.boa.backend.concurrent.PatternSearchThreadManager;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.pipeline.module.backgroundknowledgecollector.AbstractDefaultBackgroundKnowledgeCollectorModule;
import de.uni_leipzig.simba.boa.backend.search.result.SearchResult;
import de.uni_leipzig.simba.boa.backend.util.TimeUtil;


/**
 * This module is used to query a SPARQL endpoint with certain properties and to write 
 * the results in the background knowledge files located in $DATA/backgroundknowledge/[datatype|object].
 * Please not that the properties have to gathered beforehand and stored in the 
 * WebContent/WEB-INF/data/backgroundknowledge/datatype_properties_to_query.txt or
 * WebContent/WEB-INF/data/backgroundknowledge/object_properties_to_query.txt file.
 * 
 *@author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public class DefaultDatatypePropertyBackgroundKnowledgeCollectorModule extends AbstractDefaultBackgroundKnowledgeCollectorModule {
	
	private final NLPediaLogger logger 			= new NLPediaLogger(DefaultDatatypePropertyBackgroundKnowledgeCollectorModule.class);
	
	private final int SPARQL_QUERY_LIMIT					= new Integer(NLPediaSettings.getInstance().getSetting("sparqlQueryLimit"));
	private final String BOA_LANGUAGE						= NLPediaSettings.BOA_LANGUAGE;

	// for the report
    private long loadKnowledgeTime;
	
	@Override
	public String getName() {

		return "Datatype Property Background Knowledge Collector Module (de/en)";
	}
	
    @Override
    public String getReport() {

        return "A total of " + this.backgroundKnowledge.size() + " datatype property triples has been added to the background knowledge repository!";
    }
	
	@Override
	public void run() {

	    this.logger.info("Starting pattern search!");
        long startLoadingBackgroundKnowledge = System.currentTimeMillis();
        queryDatatypeProperties();
		this.loadKnowledgeTime = (System.currentTimeMillis() - startLoadingBackgroundKnowledge);
        this.logger.info("Loading datatype background knowledge finished in " + TimeUtil.convertMilliSeconds(loadKnowledgeTime));
	}
	
	@Override
	public void loadAlreadyAvailableData() {

		this.backgroundKnowledge.addAll(
				BackgroundKnowledgeManager.getInstance().getBackgroundKnowledgeInDirectory(BACKGROUND_KNOWLEDGE_OUTPUT_PATH + "/datatype/"));
	}
	
	@Override
	public boolean isDataAlreadyAvailable() {
		
		// lists all files in the directory which end with .txt and does not go into subdirectories
		return // true of more than one file is found
				FileUtils.listFiles(new File(BACKGROUND_KNOWLEDGE_OUTPUT_PATH + "/datatype/"), FileFilterUtils.suffixFileFilter(".txt"), null).size() > 0;
	}

	/**
	 * Reads the properties stored in WebContent/WEB-INF/data/backgroundknowledge/datatype_properties_to_query.txt
	 * and queries them at a given SPARQL endpoint. The knowledge is then written to the 
	 * "backgroundKnowledgeOutputFilePath" + "/datatype/". The properties in the file have to be in one property
	 * per line format with no spaces. 
	 */
	private void queryDatatypeProperties() {

		List<String> datatypePropertyUris = FileUtil.readFileInList(NLPediaSettings.BOA_BASE_DIRECTORY + "backgroundknowledge/datatype_properties_to_query.txt", "UTF-8");
		for ( String datatypePropertyUri : datatypePropertyUris ) {
			
			this.logger.info("Processing property: " + datatypePropertyUri);
			String query = createDatatypePropertyQuery(datatypePropertyUri);
			String filePath	= BACKGROUND_KNOWLEDGE_OUTPUT_PATH + "/datatype/";
			
			getKnowledge(query, datatypePropertyUri, filePath + datatypePropertyUri.substring(datatypePropertyUri.lastIndexOf("/"), datatypePropertyUri.length()) + ".txt");
		}
	}

	/**
	 * Creates a SPARQL query for the background knowledge collection of
	 * datatype properties. Since literals don't have URIs we use the label
	 * twice for the URI and the label itself.
	 * 
	 * @param propertyUri - the datatype property uri to query
	 * @return a SPARQL query
	 */
	private String createDatatypePropertyQuery(String propertyUri) {

		return 
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  " +
			"SELECT ?s ?sl <"+propertyUri+"> ?o ?o " +
			"WHERE {" +
			 "	?s rdfs:label ?sl . " +
			 "  ?s <" + propertyUri + "> ?o . " +
			 "	FILTER (   lang(?sl)= \""+BOA_LANGUAGE+"\" ) . " + 
			 "} " +
			 "LIMIT " + SPARQL_QUERY_LIMIT +  " " +
			 "OFFSET &OFFSET";
	}
}
