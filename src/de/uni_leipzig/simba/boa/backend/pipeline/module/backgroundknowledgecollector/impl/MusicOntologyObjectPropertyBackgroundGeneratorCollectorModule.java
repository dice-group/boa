package de.uni_leipzig.simba.boa.backend.pipeline.module.backgroundknowledgecollector.impl;

import java.util.List;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;

import de.danielgerber.file.FileUtil;
import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

public class MusicOntologyObjectPropertyBackgroundGeneratorCollectorModule extends
		DefaultDatatypePropertyBackgroundKnowledgeCollectorModule {

	private final NLPediaLogger logger = new NLPediaLogger(
			DefaultObjectPropertyBackgroundKnowledgeCollectorModule.class);

	/**
	 * Reads the properties stored in
	 * WebContent/WEB-INF/data/backgroundknowledge
	 * /object_properties_to_query.txt and queries them at a given SPARQL
	 * endpoint. The knowledge is then written to the
	 * "backgroundKnowledgeOutputFilePath" + "/object/". The properties in the
	 * file have to be in one property per line format with no spaces.
	 */
	private void queryObjectProperties() {

		String backgroundKnowledgeFilename = NLPediaSettings.BOA_DATA_DIRECTORY
				+ Constants.BACKGROUND_KNOWLEDGE_PATH
				+ "object_properties_to_query.txt";
		List<String> objectPropertyUris = FileUtil.readFileInList(
				backgroundKnowledgeFilename, "UTF-8");

		for (String objectPropertyUri : objectPropertyUris) {
			String[] properties=objectPropertyUri.split(",");
			this.logger.info("Processing property: " + objectPropertyUri);
			String query = createObjectPropertyQuery(properties[0],properties[1],properties[2]);

			super.getKnowledge(
					query,
					objectPropertyUri,
					NLPediaSettings.BOA_DATA_DIRECTORY
							+ Constants.BACKGROUND_KNOWLEDGE_OBJECT_PROPERTY_PATH
							+ objectPropertyUri.substring(objectPropertyUri
									.lastIndexOf("/") + 1) + "-"
							+ objectPropertyUri.hashCode() + ".txt");
		}
	}

	/**
	 * Creates a SPARQL query for the background knowledge collection of object
	 * properties.
	 * 
	 * @param propertyUri
	 *            - the object property uri to query
	 * @return a SPARQL query
	 */
	private String createObjectPropertyQuery(String label1, String property,
			String label2) {

		return "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  "
				+ "PREFIX foaf:    <http://xmlns.com/foaf/0.1/>  "
				+ "SELECT ?s ?sl <" + property + "> ?o ?ol " + "WHERE {"
				+ "	?s <" + label1 + "> ?sl . " + "  ?s <" + property
				+ "> ?o . " + "  ?o <" + label2 + "> ?ol . " + "} " + "LIMIT "
				+ SPARQL_QUERY_LIMIT + " " + "OFFSET &OFFSET";
	}
}
