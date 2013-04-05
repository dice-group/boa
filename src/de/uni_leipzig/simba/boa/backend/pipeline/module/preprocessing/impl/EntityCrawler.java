package de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class EntityCrawler {

	public static List<String> getEntityNames(String enpoint,
			List<String> classes, String relation, int limit) {
//		Query query = QueryFactory.create(buildQuery(relation, classes, limit));
		QueryExecution qexec = QueryExecutionFactory.sparqlService(enpoint,
				QueryFactory.create(buildQuery(relation, classes, limit)));
	
		ResultSet resultSet = qexec.execSelect();
		List<String> labels = new LinkedList<String>();
		while (resultSet.hasNext()) {
			QuerySolution result = resultSet.next();
			labels.add(result.getLiteral("label").getString());
		}
		return labels;
	}

	private static String buildQuery(String relation, List<String> classes,
			int limit) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("SELECT distinct ?label WHERE {\n");
		queryBuilder.append("?s a ?o ;\n <");
		queryBuilder.append(relation);
		queryBuilder.append("> ?label . \n");
		queryBuilder.append("FILTER ( ?o =( ");
		for (int i = 0; i < classes.size(); i++) {
			queryBuilder.append("<");
			queryBuilder.append(classes.get(i));
			queryBuilder.append(">");
			if (i == classes.size() - 1) {
				queryBuilder.append(" )");
			} else {
				queryBuilder.append(") || ?o =( ");
			}
		}
		queryBuilder.append(")\n");
		queryBuilder.append(" } LIMIT ");
		queryBuilder.append(limit);
		return queryBuilder.toString();
	}

	public static void main(String... args) {
		ArrayList<String> classes = new ArrayList<String>();
//		classes.add("http://purl.org/ontology/mo/Record");
		classes.add("http://purl.org/ontology/mo/MusicArtist");

		
		List<String> labels = EntityCrawler.getEntityNames(
				"http://dbtune.org/jamendo/sparql/", classes,
				"http://xmlns.com/foaf/0.1/name", 100);
		System.out.println(labels);
	}
}
