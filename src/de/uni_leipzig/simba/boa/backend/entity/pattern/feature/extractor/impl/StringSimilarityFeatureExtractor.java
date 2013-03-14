package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.concurrent.PatternMappingPatternPair;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.AbstractFeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;


public class StringSimilarityFeatureExtractor extends AbstractFeatureExtractor {

	private Map<String,String> uriToLabel = new HashMap<String,String>();
	
	@Override
	public void score(PatternMappingPatternPair pair) {

		// get the NLR and remove all stopwords
		String naturalLanguageRepresentation = pair.getPattern().getNaturalLanguageRepresentationWithoutVariables();
		List<String> tokens = new ArrayList<String>(Arrays.asList(naturalLanguageRepresentation.split(" ")));
		
		// get the uri from cache so that we don't need to query every time 
		String uri = pair.getMapping().getProperty().getUri();
		if ( !this.uriToLabel.containsKey(uri) ) 
			this.uriToLabel.put(uri, this.getLabelForUri(uri));
		
		double similarity = -1D;
		
		AbstractStringMetric metric = new Levenshtein();
		
		for ( String part : this.uriToLabel.get(uri).split(" ")) {
			if ( Constants.STOP_WORDS.contains(part) ) continue;
			
			for ( String token : tokens ) {
				if ( Constants.STOP_WORDS.contains(token) ) continue;
				
				similarity = Math.max(similarity, metric.getSimilarity(part, token));
			}
		}
		
		pair.getPattern().getFeatures().put(FeatureFactory.getInstance().getFeature("LEVENSHTEIN"), similarity >= 0 ? similarity : 0);
	}

	private String getLabelForUri(String uri) {
		
		String query = String.format("SELECT ?label " +
				"{ <%s> rdfs:label ?label . FILTER(lang(?label) = '%s' }", uri, NLPediaSettings.BOA_LANGUAGE); 
		
		QueryEngineHTTP qexecProperty = new QueryEngineHTTP("http://live.dbpedia.org/sparql", query);
        qexecProperty.addDefaultGraph("http://dbpedia.org");

        ResultSet results = qexecProperty.execSelect();
        while ( results.hasNext() ) {
        	
        	QuerySolution solution = results.nextSolution();
        	return solution.getLiteral("label").getLexicalForm();
        }
		
		return "";
	}
	
	public static void main(String[] args) {
		
//		getLabelForUri("http://dbpedia.org/ontology/capital");
	}
}
