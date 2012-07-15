/**
 *
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import de.danielgerber.Constants;
import de.danielgerber.file.BufferedFileReader;
import de.danielgerber.file.BufferedFileWriter;
import de.danielgerber.file.BufferedFileWriter.WRITER_WRITE_MODE;
import de.danielgerber.file.FileUtil;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.AbstractPreprocessingModule;
import de.uni_leipzig.simba.boa.backend.wordnet.query.WordnetQuery;

/**
 * @author Maciej Janicki <macjan@o2.pl>
 */
public class ClassesSurfaceFormModule extends AbstractPreprocessingModule {
	private final NLPediaLogger logger		= new NLPediaLogger(ClassesSurfaceFormModule.class);
	
    private final String SPARQL_ENDPOINT_URI = NLPediaSettings.getSetting("dbpediaSparqlEndpoint");
    private final String DBPEDIA_DEFAULT_GRAPH = "http://dbpedia.org";
	private final int SPARQL_QUERY_LIMIT	= new Integer(NLPediaSettings.getSetting("sparqlQueryLimit"));
    protected final String BACKGROUND_KNOWLEDGE_OUTPUT_PATH = NLPediaSettings.BOA_DATA_DIRECTORY + de.uni_leipzig.simba.boa.backend.Constants.BACKGROUND_KNOWLEDGE_PATH;
	private final String BOA_LANGUAGE		= NLPediaSettings.BOA_LANGUAGE;
	private final String CLASSES_SURFACE_FORMS_FILE = BACKGROUND_KNOWLEDGE_OUTPUT_PATH + "classes_surface_forms.tsv";

	private final String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  " +
		"SELECT ?class, ?label WHERE {" +
		 "  ?class rdfs:label ?label . " +
		 "  FILTER (lang(?label)=\"" + BOA_LANGUAGE + "\" " + 
		 "  && regex(?class, \"http://dbpedia.org/ontology/[A-Z][^/]*$\")) " +
		"} LIMIT " + SPARQL_QUERY_LIMIT + " " +
		"OFFSET &OFFSET";

	private HashMap<String, ArrayList<String>> surfaceForms = new HashMap<String, ArrayList<String>>();

	@Override
	public String getName() {
		return "Classes Surface Form Module";
	}

    @Override
    public String getReport() {
        //return "Create the directory structure in " + this.timeCreateDirectoryStructure + "ms.";
		return "";
    }

    @Override
    public void updateModuleInterchangeObject() {
        this.moduleInterchangeObject.setClassesSurfaceForms(this.surfaceForms);
    }

    @Override
    public boolean isDataAlreadyAvailable() {
		return new File(CLASSES_SURFACE_FORMS_FILE).exists();
    }

    @Override
    public void loadAlreadyAvailableData() {
        BufferedFileReader reader = FileUtil.openReader(CLASSES_SURFACE_FORMS_FILE, "UTF-8");
		String line;
		while ((line = reader.readLine()) != null) {
			String[] content = line.split("\t");
			if (!this.surfaceForms.containsKey(content[0]))
				this.surfaceForms.put(content[0], new ArrayList<String>());
			for (int i = 1; i < content.length; i++) {
				this.surfaceForms.get(content[0]).add(content[i]);
			}
		}
		reader.close();
    }

	@Override
	public void run() {
		getSurfaceForms();
		expandSurfaceForms();
		writeResults(CLASSES_SURFACE_FORMS_FILE);
    }

	private void getSurfaceForms() {
        long start = System.currentTimeMillis();
		int offset = 0;
        this.logger.info("Starting to query for : " + query.replaceAll("&OFFSET", String.valueOf(offset)));

        while (true) {
			//System.out.println("offset = " + offset);
            QueryEngineHTTP qexec = new QueryEngineHTTP(SPARQL_ENDPOINT_URI, 
				this.query.replaceAll("&OFFSET", String.valueOf(offset)));
            qexec.addDefaultGraph(DBPEDIA_DEFAULT_GRAPH);

            this.logger.info("Starting to query for : " + query.replaceAll("&OFFSET", String.valueOf(offset)));
            ArrayList<QuerySolution> resultSetList = new ArrayList<QuerySolution>();

            // query current query, collect results and increment the offset
            ResultSet rs = getResults(qexec, query);
            while (rs.hasNext())
                resultSetList.add(rs.next());
            offset = offset + SPARQL_QUERY_LIMIT;

            if (!resultSetList.isEmpty()) {
				for (QuerySolution solution : resultSetList) {
					String classUri = solution.get("class").toString();
					String label = solution.get("label").toString();
                	if (label.contains("@")) 
						label = label.substring(0, label.lastIndexOf("@"));
					if (!this.surfaceForms.containsKey(classUri))
						this.surfaceForms.put(classUri, new ArrayList<String>());
					this.surfaceForms.get(classUri).add(label);
				}
            }
            else { // end of query
                qexec.close();
                break;
            }
			//break; // TODO remove later
        }
        logger.info("Querying ended in " + (System.currentTimeMillis() - start) + "ms for query: " + query);
	}

	private void expandSurfaceForms() {
		for (String uri : this.surfaceForms.keySet()) {
			HashSet<String> synonymsToAdd = new HashSet<String>();
			for (String sf : this.surfaceForms.get(uri)) {
				for (String synonym : WordnetQuery.getSynsetsForAllSynsetTypes(sf)) {
					if (!this.surfaceForms.get(uri).contains(synonym))
						synonymsToAdd.add(synonym);
				}
			}
			this.surfaceForms.get(uri).addAll(synonymsToAdd);
		}
	}

	private void writeResults(String fileName) {
		BufferedFileWriter writer = FileUtil.openWriter(fileName, Constants.UTF_8_ENCODING, WRITER_WRITE_MODE.APPEND);

		for (String uri : this.surfaceForms.keySet()) {
			StringBuilder line = new StringBuilder();
			line.append(uri);
			for (String sf : this.surfaceForms.get(uri)) {
				line.append("\t" + sf);
			}
			writer.write(line.toString());
		}
		writer.flush();
		writer.close();
	}

    private ResultSet getResults(QueryEngineHTTP qexec, String query) {
        ResultSet results = null;
        try {
            results = qexec.execSelect();
        }
        catch (Exception e) {
            results = getResults(qexec, query);
            System.out.println("Retrying query: " + query);
            logger.warn("Need to retry query: " + query, e);
        }
        return results;
    }
}
