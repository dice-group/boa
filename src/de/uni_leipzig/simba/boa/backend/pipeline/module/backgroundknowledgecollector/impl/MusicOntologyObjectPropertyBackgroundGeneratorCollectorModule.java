package de.uni_leipzig.simba.boa.backend.pipeline.module.backgroundknowledgecollector.impl;

import java.util.ArrayList;
import java.util.List;

import cern.colt.Arrays;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import de.danielgerber.file.BufferedFileWriter;
import de.danielgerber.file.BufferedFileWriter.WRITER_WRITE_MODE;
import de.danielgerber.file.FileUtil;
import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.impl.ObjectPropertyBackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;

public class MusicOntologyObjectPropertyBackgroundGeneratorCollectorModule
		extends DefaultObjectPropertyBackgroundKnowledgeCollectorModule {

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
	@Override
	protected void queryObjectProperties() {

		String backgroundKnowledgeFilename = NLPediaSettings.BOA_DATA_DIRECTORY
				+ Constants.BACKGROUND_KNOWLEDGE_PATH
				+ "object_properties_to_query.txt";
		logger.info("path: " + backgroundKnowledgeFilename);
		
		List<String> objectPropertyUris = FileUtil.readFileInList(
				backgroundKnowledgeFilename, "UTF-8");

		this.logger
				.info("objectpropertyuris: " + objectPropertyUris.toString());
		for (String objectPropertyUri : objectPropertyUris) {
			String[] properties = objectPropertyUri.split(",");
			this.logger.info("Processing property: "
					+ Arrays.toString(properties));

			String query = createObjectPropertyQuery(properties[0].trim(),
					properties[1].trim(), properties[2].trim());
			this.logger.info("query: " + query);
			this.getKnowledge(
					query,
					properties[1].trim().replaceAll("<", ""),
					NLPediaSettings.BOA_DATA_DIRECTORY
							+ Constants.BACKGROUND_KNOWLEDGE_OBJECT_PROPERTY_PATH
							+ properties[1].trim().replaceAll("<", "").substring(properties[1].trim().replaceAll("<", "")
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

	protected String createObjectPropertyQuery(String label1, String property,
			String label2) {

		return "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  "
				+ "PREFIX foaf:    <http://xmlns.com/foaf/0.1/>  "
				+ "SELECT ?sl ?s " + property + " ?o ?ol " + "WHERE {"
				+ "	?s " + label1 + " ?sl . " + "  ?s " + property
				+ " ?o . " + "  ?o " + label2 + " ?ol . " + "} " + "LIMIT "
				+ SPARQL_QUERY_LIMIT + " " + "OFFSET &OFFSET";
	}

	@Override
	protected void getKnowledge(String query, String propertyUri,
			String fileName) {

		logger.info("Querying started for property: " + propertyUri);
		long start = System.currentTimeMillis();

		Property property = new Property(propertyUri);
		int offset = 0;

		// query as long as we get resultsets back
		while (true) {

			QueryEngineHTTP qexec = new QueryEngineHTTP(SPARQL_ENDPOINT_URI,
					query.replaceAll("&OFFSET", String.valueOf(offset)));
			qexec.addDefaultGraph("http://muscibrainz.org");

			this.logger.info("Starting to query for : "
					+ query.replaceAll("&OFFSET", String.valueOf(offset)));

			List<QuerySolution> resultSetList = new ArrayList<QuerySolution>();

			// query current query, collect results and increment the offset
			// afterwards
			ResultSet rs = super.getResults(qexec, query);
			while (rs.hasNext())
				resultSetList.add(rs.next());
			offset = offset + SPARQL_QUERY_LIMIT;

			// SPARQL query returned results
			if (!resultSetList.isEmpty()) {

				// this is an object property, only object properties can have
				// labels
					property.setType("http://www.w3.org/2002/07/owl#ObjectProperty");
					handleObjectPropertyQuery(property, fileName, resultSetList);
				
			} else { // end of query for current property

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
    protected void handleObjectPropertyQuery(Property property, String fileName, List<QuerySolution> resultSets) {

        BufferedFileWriter writer = FileUtil.openWriter(fileName, de.danielgerber.Constants.UTF_8_ENCODING, WRITER_WRITE_MODE.APPEND);

        for (QuerySolution solution : resultSets) {

            // make sure the resultset contains the wanted fields
            if (solution.get("s") != null && solution.get("callret-2") != null && solution.get("o") != null && solution.get("ol") != null) {

                String subjectLabel = solution.get("sl").toString();
                String objectLabel = solution.get("ol").toString();

                // cut of language tags
                if (objectLabel.contains("@")) objectLabel = objectLabel.substring(0, objectLabel.lastIndexOf("@"));
                if (subjectLabel.contains("@")) subjectLabel = subjectLabel.substring(0, subjectLabel.lastIndexOf("@"));

                // create new background knowledge and generate the surface
                // forms
                ObjectPropertyBackgroundKnowledge objectBackgroundKnowledge = new ObjectPropertyBackgroundKnowledge();
                objectBackgroundKnowledge.setSubjectPrefixAndLocalname(solution.get("s").toString());
                objectBackgroundKnowledge.setSubjectLabel(subjectLabel.replaceAll("\\(.+?\\)", "").trim());

                objectBackgroundKnowledge.setObjectPrefixAndLocalname(solution.get("o").toString());
                objectBackgroundKnowledge.setObjectLabel(objectLabel.replaceAll("\\(.+?\\)", "").trim());

                objectBackgroundKnowledge.setProperty(property);

//                BackgroundKnowledge backgroundKnowledge = SurfaceFormGenerator.getInstance().createSurfaceFormsForBackgroundKnowledge(objectBackgroundKnowledge);

                writer.write(objectBackgroundKnowledge.toString());
                this.backgroundKnowledge.add(objectBackgroundKnowledge);
            }
        }
        writer.flush();
        writer.close();
    }
}
