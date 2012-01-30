package de.uni_leipzig.simba.boa.evaluation.corpus;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryParser.ParseException;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import de.danielgerber.file.BufferedFileWriter;
import de.danielgerber.file.BufferedFileWriter.WRITER_WRITE_MODE;
import de.danielgerber.file.FileUtil;
import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.search.impl.DefaultPatternSearcher;

public class CreateAnnotationCorpus {

	public static NLPediaSetup setup = new NLPediaSetup(true);
	private Integer a1Counter	= 1;
	private Integer a2Counter	= 1;
	private Integer i 			= 1;
	
	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, IOException, ParseException {

		CreateAnnotationCorpus cac = new CreateAnnotationCorpus();
		cac.execute();
	}
	
	private void execute() throws UnsupportedEncodingException, FileNotFoundException, IOException, ParseException {

		String propertiesFilename 		= NLPediaSettings.BOA_BASE_DIRECTORY + "evaluation/object_properties_evaluation.txt";
		
		String annotatorOneFile	= NLPediaSettings.BOA_BASE_DIRECTORY + "evaluation/eval_a1_v0.2.txt";
		String annotatorTwoFile	= NLPediaSettings.BOA_BASE_DIRECTORY + "evaluation/eval_a2_v0.2.txt";
		
		List<String> propertiesToQuery = FileUtil.readFileInList(propertiesFilename, "UTF-8");
		
		DefaultPatternSearcher patternSearcher = new DefaultPatternSearcher();
		
//		createKappaAnnotationFile(propertiesToQuery.subList(0, 1));
		
		for (String property : propertiesToQuery) {
			
			// query the endpoint, shuffle the results and take only the first 1000 triples
			List<QueryResult> queryResults = queryEndpoint(property);
			Collections.shuffle(queryResults);
			queryResults = queryResults.size() >= 1000  ? queryResults.subList(0, 1000) : queryResults;

			Map<QueryResult, String> sentences = new HashMap<QueryResult, String>();
			// query the corpus-index for each of the 1000 triples
			for (QueryResult qResult : queryResults) {
				
				List<String> sentence = new ArrayList<String>(patternSearcher.getExactMatchSentencesForLabels(qResult.subjectLabel.toLowerCase(), qResult.objectLabel.toLowerCase(), 1));
				if ( sentence.size() > 0 ) {
					
					sentences.put(qResult, sentence.get(0));
				}
			}
			
			if (i % 2 == 0) writeQueryResultsToFile(annotatorOneFile, property, sentences, a1Counter);
			else writeQueryResultsToFile(annotatorTwoFile, property, sentences, a2Counter);
			
			i++;
		}
	}

	private static void writeQueryResultsToFile(String annotationFilename, String property, Map<QueryResult, String> sentences, Integer lineCounter) throws IOException {

		BufferedFileWriter writer = FileUtil.openWriter(annotationFilename, "UTF-8", WRITER_WRITE_MODE.APPEND);
		writer.write("################################ "+property+" #################################"+ Constants.NEW_LINE_SEPARATOR + Constants.NEW_LINE_SEPARATOR  + Constants.NEW_LINE_SEPARATOR );
		int j = 0;
		
		if ( sentences.size() < 30 ) System.out.println("Less then 30 sentences for property " + property);
		
		for (Map.Entry<QueryResult, String> entry : sentences.entrySet()) {
			
			if (j++ >= 30) break;
			writer.write((lineCounter++) + "." + entry.getKey().toString());
			writer.write("[o] "+ entry.getValue().toString());
			writer.write(Constants.NEW_LINE_SEPARATOR);
		}
		writer.write(Constants.NEW_LINE_SEPARATOR + Constants.NEW_LINE_SEPARATOR);
		writer.close();
	}

	private static List<QueryResult> queryEndpoint(String property) {

		// query the test set
		String query = 
				"SELECT ?s ?o ?sLabel ?oLabel " +
				"WHERE { " +
				" ?s <" + property + "> ?o . " +
				" ?s rdfs:label ?sLabel . " + 
				" ?o rdfs:label ?oLabel . " +
				" FILTER (lang(?oLabel) = 'en' && lang(?sLabel) = 'en') ." +
				"} LIMIT 1000";
			
		QueryEngineHTTP	qexec = new QueryEngineHTTP("http://dbpedia.org/sparql", query);
		qexec.addDefaultGraph("http://dbpedia.org");
		qexec.addParam("timeout","5000");
		
		List<QueryResult> queryResults = new ArrayList<QueryResult>(2000);
		System.out.println("Querying started!");
		System.out.println(query);
		ResultSet results = qexec.execSelect();
		System.out.println("Querying ended");
		int i = 0;
		while (results.hasNext()) {
			System.out.println(i++);
			QuerySolution solution = results.next();
			QueryResult res = new QueryResult();
			res.subjectUri		= solution.get("s").toString().replace(">", "").replace("<", "");
			res.subjectLabel	= solution.get("sLabel").toString().replace("@en", "").replaceAll("\\(.+?\\)", "").trim();
			res.objectUri		= solution.get("o").toString().replace(">", "").replace("<", "");
			res.objectLabel		= solution.get("oLabel").toString().replace("@en", "").replaceAll("\\(.+?\\)", "").trim();
			queryResults.add(res);
		}
		qexec.close();
		System.out.println("Found " + queryResults.size() + " pairs!");
		
		return queryResults;
	}

	private static void createKappaAnnotationFile(List<String> propertyList) throws IOException, ParseException {

		DefaultPatternSearcher patternSearcher = new DefaultPatternSearcher();
		String kappaFilename			= NLPediaSettings.BOA_BASE_DIRECTORY + "evaluation/eval_kappa_v0.2.1.txt";
		Integer i 						= 1;
		
		for ( String property : propertyList ) {
			
			List<QueryResult> queryResults = queryEndpoint(property);
			Collections.shuffle(queryResults);
			queryResults = queryResults.size() >= 1000  ? queryResults.subList(0, 1000) : queryResults;

			Map<QueryResult, String> sentences = new HashMap<QueryResult, String>();
			// query the corpus-index for each of the 1000 triples
			for (QueryResult qResult : queryResults) {
				
				// we dont need to go through all triples
				if ( sentences.size() > 20 ) break;
				
				List<String> sentence = new ArrayList<String>(patternSearcher.getExactMatchSentencesForLabels(qResult.subjectLabel.toLowerCase(), qResult.objectLabel.toLowerCase(), 1));
				if ( sentence.size() > 0 ) {
					
					sentences.put(qResult, sentence.get(0));
				}
			}
			
			writeQueryResultsToFile(kappaFilename, property, sentences, i);
		}
	}

	private static class QueryResult {
	
		String subjectUri;
		String subjectLabel;
		String objectUri;
		String objectLabel;
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {

			StringBuilder builder = new StringBuilder();
			builder.append("[" + subjectLabel + "] ");
			builder.append("[" + objectLabel + "]\t\t");			
			builder.append("["+subjectUri+","+objectUri+"]");
			return builder.toString();
		}
	}
}
