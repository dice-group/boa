package de.uni_leipzig.simba.boa.backend.pipeline.module.backgroundknowledgecollector.impl;

import java.util.ArrayList;
import java.util.List;

import cern.colt.Arrays;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
import com.hp.hpl.jena.sparql.resultset.ResultSetException;

import de.danielgerber.file.BufferedFileWriter;
import de.danielgerber.file.BufferedFileWriter.WRITER_WRITE_MODE;
import de.danielgerber.file.FileUtil;
import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.impl.ObjectPropertyBackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;

public class DrugBankObjectProperty extends
		DefaultObjectPropertyBackgroundKnowledgeCollectorModule {

	private final NLPediaLogger logger = new NLPediaLogger(
			DrugBankObjectProperty.class);

	protected String createObjectPropertyQuery(String property) {
		return "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/> "
				+ "SELECT ?s ?sl ?o ?ol WHERE { " + "?s rdfs:label ?sl . "
				+ "?di drugbank:interactionDrug1 ?s . "
				+ "?di drugbank:interactionDrug2 ?o . "
				+ "?o rdfs:label ?ol . } ";
	}

	/**
	 * This method takes a given sparql query with limit and offset and
	 * retrieves all triples found in the knowledge base for the current
	 * property/query. The results will be written to the specified file(name).
	 * Please note the this method also generates surface forms for the
	 * background knowledge. Also the triples like rdfs:range and rdfs:domain
	 * are queried for the given property.
	 * 
	 * @param query
	 *            - the query to retrieve the background knowledge
	 * @param propertyUri
	 *            - the property uri, used to query its information
	 * @param fileName
	 *            - the file to where to write the knowledge
	 */
	@Override
	protected void getKnowledge(String query, String propertyUri,
			String fileName) {

		logger.info("Querying started for property: " + propertyUri);
		long start = System.currentTimeMillis();

		// Property property = this.queryPropertyData(propertyUri);
		Property property = new Property("http://example.org/interact");
		int offset = 0;

		// query as long as we get resultsets back
		while (true) {

			QueryEngineHTTP qexec = new QueryEngineHTTP(SPARQL_ENDPOINT_URI,
					query.replaceAll("&OFFSET", String.valueOf(offset)));
			// qexec.addDefaultGraph(DBPEDIA_DEFAULT_GRAPH);

			this.logger.info("Starting to query for : "
					+ query.replaceAll("&OFFSET", String.valueOf(offset)));

			List<QuerySolution> resultSetList = new ArrayList<QuerySolution>();

			// query current query, collect results and increment the offset
			// afterwards
			ResultSet rs = getResults(qexec, query);
			try {
				while (rs.hasNext())
					resultSetList.add(rs.next());
				offset = offset + SPARQL_QUERY_LIMIT;

				// SPARQL query returned results
				if (!resultSetList.isEmpty()) {

					// this is an object property, only object properties can
					// have
					// labels

					property.setType("http://www.w3.org/2002/07/owl#ObjectProperty");
					handleObjectPropertyQuery(property, fileName, resultSetList);

				} else { // end of query for current property

					qexec.close();
					break;
				}
			} catch (ResultSetException e) {
				logger.warn("ResultSetException "+Arrays.toString(e.getStackTrace()));
				qexec.close();
				break;
			}
		}
		logger.info("Querying ended in " + (System.currentTimeMillis() - start)
				+ "ms for query: " + query);
	}

	/**
	 * This method handles the processing of the resultset. It creates new
	 * background knowledge and also creates the surface forms for the
	 * resources. It finally writes the data to the fileName-file.
	 * 
	 * @param property
	 *            - the property for this propertyUri
	 * @param fileName
	 *            - the name of the file to write to
	 * @param resultSets
	 *            - the resultset returned from the SPARQL endpoint
	 */
	@Override
	protected void handleObjectPropertyQuery(Property property,
			String fileName, List<QuerySolution> resultSets) {

		BufferedFileWriter writer = FileUtil.openWriter(fileName,
				de.danielgerber.Constants.UTF_8_ENCODING,
				WRITER_WRITE_MODE.APPEND);

		for (QuerySolution solution : resultSets) {

			// make sure the resultset contains the wanted fields
			if (solution.get("s") != null && solution.get("callret-2") != null
					&& solution.get("o") != null && solution.get("ol") != null) {

				String subjectLabel = solution.get("sl").toString();
				String objectLabel = solution.get("ol").toString();

				// cut of language tags
				if (objectLabel.contains("@"))
					objectLabel = objectLabel.substring(0,
							objectLabel.lastIndexOf("@"));
				if (subjectLabel.contains("@"))
					subjectLabel = subjectLabel.substring(0,
							subjectLabel.lastIndexOf("@"));

				// create new background knowledge and generate the surface
				// forms
				ObjectPropertyBackgroundKnowledge objectBackgroundKnowledge = new ObjectPropertyBackgroundKnowledge();
				objectBackgroundKnowledge.setSubjectPrefixAndLocalname(solution
						.get("s").toString());
				objectBackgroundKnowledge.setSubjectLabel(subjectLabel
						.replaceAll("\\(.+?\\)", "").trim());

				objectBackgroundKnowledge.setObjectPrefixAndLocalname(solution
						.get("o").toString());
				objectBackgroundKnowledge.setObjectLabel(objectLabel
						.replaceAll("\\(.+?\\)", "").trim());

				objectBackgroundKnowledge.setProperty(property);

				// BackgroundKnowledge backgroundKnowledge =
				// SurfaceFormGenerator.getInstance().createSurfaceFormsForBackgroundKnowledge(objectBackgroundKnowledge);

				writer.write(objectBackgroundKnowledge.toString());
				this.backgroundKnowledge.add(objectBackgroundKnowledge);
			}
		}
		writer.flush();
		writer.close();
	}

	/**
	 * Reads the properties stored in
	 * WebContent/WEB-INF/data/backgroundknowledge
	 * /object_properties_to_query.txt and queries them at a given SPARQL
	 * endpoint. The knowledge is then written to the
	 * "backgroundKnowledgeOutputFilePath" + "/object/". The properties in the
	 * file have to be in one property per line format with no spaces.
	 */
	@Override
	protected void queryObjectProperties() {

		String objectPropertyUri = "http://example.org/interact";

		this.logger.info("Processing property: " + objectPropertyUri);
		String query = createObjectPropertyQuery(objectPropertyUri);

		this.getKnowledge(
				query,
				objectPropertyUri,
				NLPediaSettings.BOA_DATA_DIRECTORY
						+ Constants.BACKGROUND_KNOWLEDGE_OBJECT_PROPERTY_PATH
						+ objectPropertyUri.substring(objectPropertyUri
								.lastIndexOf("/") + 1) + "-"
						+ objectPropertyUri.hashCode() + ".txt");

	}

}
