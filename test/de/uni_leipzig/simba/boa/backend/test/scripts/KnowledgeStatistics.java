package de.uni_leipzig.simba.boa.backend.test.scripts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.crawl.RelationFinder;


public class KnowledgeStatistics {

	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		NLPediaSetup setup = new NLPediaSetup(true);

		List<String[]> personSubject	= RelationFinder.getRelationFromFile("/Users/gerb/person_subject.txt");
		List<String[]> personObject		= RelationFinder.getRelationFromFile("/Users/gerb/person_object.txt");
		
		System.out.println("Size of personSubject: " + personSubject.size());
		System.out.println("Size of personObject: " + personObject.size());
		
		List<String[]> placeSubject		= RelationFinder.getRelationFromFile("/Users/gerb/place_subject.txt");
		List<String[]> placeObject		= RelationFinder.getRelationFromFile("/Users/gerb/place_object.txt");
		
		System.out.println("Size of placeSubject: " + placeSubject.size());
		System.out.println("Size of placeObject: " + placeObject.size());
		
		List<String[]> orgSubject		= RelationFinder.getRelationFromFile("/Users/gerb/organisation_subject.txt");
		List<String[]> orgObject		= RelationFinder.getRelationFromFile("/Users/gerb/organisation_object.txt");
		
		System.out.println("Size of orgSubject: " + orgSubject.size());
		System.out.println("Size of orgObject: " + orgObject.size());
		
		
		List<String[]> all = new ArrayList<String[]>();
		all.addAll(placeSubject);
		all.addAll(placeObject);
		all.addAll(personSubject);
		all.addAll(personObject);
		all.addAll(orgSubject);
		all.addAll(orgObject);
		
		Map<String,Integer> distribution = new HashMap<String,Integer>();
		
		for (String[] line : all) {
			
			if ( distribution.containsKey(line[1]) ) distribution.put(line[1], distribution.get(line[1]) + 1);
			else distribution.put(line[1], 1);
		}
		
		for (Entry<String, Integer> entry : distribution.entrySet()) {
			
			System.out.println(entry.getValue() + "\t" + entry.getKey());
		}
	}
}
