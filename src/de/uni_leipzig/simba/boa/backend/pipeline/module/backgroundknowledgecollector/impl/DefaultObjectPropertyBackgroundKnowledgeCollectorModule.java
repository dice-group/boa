/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.backgroundknowledgecollector.impl;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import de.danielgerber.file.FileUtil;
import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.BackgroundKnowledgeManager;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.pipeline.module.backgroundknowledgecollector.AbstractDefaultBackgroundKnowledgeCollectorModule;
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
public class DefaultObjectPropertyBackgroundKnowledgeCollectorModule extends AbstractDefaultBackgroundKnowledgeCollectorModule {
	
	private final NLPediaLogger logger		= new NLPediaLogger(DefaultObjectPropertyBackgroundKnowledgeCollectorModule.class);
	
	protected final int SPARQL_QUERY_LIMIT	= new Integer(NLPediaSettings.getSetting("sparqlQueryLimit"));
	private final String BOA_LANGUAGE		= NLPediaSettings.BOA_LANGUAGE;

	// for the report
    private long loadKnowledgeTime;
	
	@Override
	public String getName() {

		return "Object Property Background Knowledge Collector Module (de/en)";
	}
	
    @Override
    public String getReport() {

        return "A total of " + this.backgroundKnowledge.size() + " object property triples has been added to the background knowledge repository!";
    }
	
	@Override
	public void run() {
		
		this.logger.info("Starting pattern search!");
        long startLoadingBackgroundKnowledge = System.currentTimeMillis();
        this.queryObjectProperties();
        this.loadKnowledgeTime = (System.currentTimeMillis() - startLoadingBackgroundKnowledge);
        this.logger.info("Loading object background knowledge finished in " + TimeUtil.convertMilliSeconds(loadKnowledgeTime));
	}
	
	@Override
	public boolean isDataAlreadyAvailable() {

		// lists all files in the directory which end with .txt and does not go into subdirectories
		return // true of more than one file is found
			FileUtils.listFiles(new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.BACKGROUND_KNOWLEDGE_OBJECT_PROPERTY_PATH), FileFilterUtils.suffixFileFilter(".txt"), null).size() > 0;
	}
	
	@Override
	public void loadAlreadyAvailableData() {

		this.backgroundKnowledge.addAll(
				BackgroundKnowledgeManager.getInstance().getBackgroundKnowledgeInDirectory(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.BACKGROUND_KNOWLEDGE_OBJECT_PROPERTY_PATH, true));
	}
	
	/**
	 * Reads the properties stored in WebContent/WEB-INF/data/backgroundknowledge/object_properties_to_query.txt
	 * and queries them at a given SPARQL endpoint. The knowledge is then written to the 
	 * "backgroundKnowledgeOutputFilePath" + "/object/". The properties in the file have to be in one property
	 * per line format with no spaces.
	 */
	protected void queryObjectProperties() {
		
		String backgroundKnowledgeFilename = NLPediaSettings.BOA_DATA_DIRECTORY + Constants.BACKGROUND_KNOWLEDGE_PATH + "object_properties_to_query.txt";
		List<String> objectPropertyUris = FileUtil.readFileInList(backgroundKnowledgeFilename, "UTF-8");
		
		for ( String objectPropertyUri : objectPropertyUris ) {
			
			this.logger.info("Processing property: " + objectPropertyUri);
			String query	= createObjectPropertyQuery(objectPropertyUri);
			
			super.getKnowledge(query, objectPropertyUri, NLPediaSettings.BOA_DATA_DIRECTORY + Constants.BACKGROUND_KNOWLEDGE_OBJECT_PROPERTY_PATH 
			        + objectPropertyUri.substring(objectPropertyUri.lastIndexOf("/") + 1) + "-"+ objectPropertyUri.hashCode() + ".txt");
		}
	}
	
	/**
	 * Creates a SPARQL query for the background knowledge collection of
	 * object properties. 
	 * 
	 * @param propertyUri - the object property uri to query
	 * @return a SPARQL query
	 */
	protected String createObjectPropertyQuery(String property) {

		return 
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  " +
			"SELECT ?s ?sl <"+property+"> ?o ?ol " +
			"WHERE {" +
			 "	?s rdfs:label ?sl . " + 
			 "  ?s "+property+" ?o . " +
			 "  ?o rdfs:label ?ol . " +
			 "	FILTER (   lang(?sl)= \""+BOA_LANGUAGE+"\"  &&  lang(?ol)= \""+BOA_LANGUAGE+"\"  ) " +
			 "} " +
			 "LIMIT " + SPARQL_QUERY_LIMIT + " " +
			 "OFFSET &OFFSET";
	}
}
