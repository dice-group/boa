package de.uni_leipzig.simba.boa.backend.configuration.command.impl.scripts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.crawl.RelationFinder;


public class KnowledgeStatisticsCommand implements Command {

	private String knowledgePath = NLPediaSettings.getInstance().getSetting("labelOutputFile");
	
	@Override
	public void execute() {

		String path = knowledgePath.substring(0, knowledgePath.lastIndexOf("/"));
		
		List<String[]> labelsPer = new ArrayList<String[]>();
		labelsPer.addAll(RelationFinder.getRelationFromFile(path + "/person_subject.txt"));
		labelsPer.addAll(RelationFinder.getRelationFromFile(path + "/person_object.txt"));
		
		List<String[]> labelsOrg = new ArrayList<String[]>();
		labelsOrg.addAll(RelationFinder.getRelationFromFile(path + "/organisation_subject.txt"));
		labelsOrg.addAll(RelationFinder.getRelationFromFile(path + "/organisation_object.txt"));
		
		List<String[]> labelsLoc = new ArrayList<String[]>();
		labelsLoc.addAll(RelationFinder.getRelationFromFile(path + "/place_subject.txt"));
		labelsLoc.addAll(RelationFinder.getRelationFromFile(path + "/place_object.txt"));
		
		Map<String,Map<String,Integer>> distribution = new HashMap<String,Map<String,Integer>>();
		distribution.put("person", new HashMap<String,Integer>());
		distribution.put("organisation", new HashMap<String,Integer>());
		distribution.put("place", new HashMap<String,Integer>());
		
		for (String[] line : labelsPer) {
			
			if ( distribution.get("person").containsKey(line[1]) ) distribution.get("person").put(line[1], distribution.get("person").get(line[1]) + 1);
			else distribution.get("person").put(line[1], 1);
		}
		for (String[] line : labelsOrg) {
			
			if ( distribution.get("organisation").containsKey(line[1]) ) distribution.get("organisation").put(line[1], distribution.get("organisation").get(line[1]) + 1);
			else distribution.get("organisation").put(line[1], 1);
		}
		for (String[] line : labelsLoc) {
			
			if ( distribution.get("place").containsKey(line[1]) ) distribution.get("place").put(line[1], distribution.get("place").get(line[1]) + 1);
			else distribution.get("place").put(line[1], 1);
		}
		
		System.out.println("Place:");
		for (Entry<String,Integer> entry : distribution.get("place").entrySet() ) {
					
			System.out.println(entry.getValue() + ":\t" + entry.getKey());
		}
		System.out.println("\n\nPerson:");
		for (Entry<String,Integer> entry : distribution.get("person").entrySet() ) {
			
			System.out.println(entry.getValue() + ":\t" + entry.getKey());
		}
		System.out.println("\n\nOrganisation:");
		for (Entry<String,Integer> entry : distribution.get("organisation").entrySet() ) {
			
			System.out.println(entry.getValue() + ":\t" + entry.getKey());
		}
	}
}
