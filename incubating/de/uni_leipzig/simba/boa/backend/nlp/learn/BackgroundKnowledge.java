package de.uni_leipzig.simba.boa.backend.nlp.learn;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


public class BackgroundKnowledge implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private Map<String, String> labels = null;
	
	/**
	 * 
	 */
	private Map<String, Set<String>> types = null;
	
	public BackgroundKnowledge(){}
	
	public BackgroundKnowledge(String pathToTypesFile, String pathToLabelsFile) {
		
		long start = new Date().getTime();
		System.out.print("Reading surfaceForms ... ");
		this.readLabels(pathToLabelsFile);
		System.out.print("DONE in " + (new Date().getTime() - start) + "ms!\n");
		
		start = new Date().getTime();
		System.out.print("Reading types ... ");
		this.readRdfTypes(pathToTypesFile);
		System.out.print("DONE in " + (new Date().getTime() - start) + "ms!\n");
	}
	
	/**
	 * returns a map mapping the uri to the various rdf:type definitions of it
	 * 
	 * @return
	 */
	private void readRdfTypes(String pathToTypesFile) {

		types = new TreeMap<String, Set<String>>();

//		 Set<String> typSet1 = new HashSet<String>();
//		 typSet1.add("http://dbpedia.org/ontology/Organisation");
//		 typSet1.add("http://dbpedia.org/ontology/SportsTeam");
//		 typSet1.add("http://dbpedia.org/ontology/SoccerClub");
//		 types.put("http://dbpedia.org/resource/APR_FC", typSet1);
//		
//		 Set<String> typSet2 = new HashSet<String>();
//		 typSet2.add("http://dbpedia.org/ontology/School");
//		 typSet2.add("http://dbpedia.org/ontology/EducationalInstitution");
//		 typSet2.add("http://dbpedia.org/ontology/Organisation");
//		 types.put("http://dbpedia.org/resource/McKinley_Senior_High_School",
//		 typSet2);
//		
//		 Set<String> typSet3 = new HashSet<String>();
//		 typSet3.add("http://dbpedia.org/ontology/Organisation");
//		 typSet3.add("http://dbpedia.org/ontology/Company");
//		 types.put("http://dbpedia.org/resource/Continental_Illinois",
//		 typSet3);

		try {

			BufferedReader in = new BufferedReader(new FileReader(pathToTypesFile));

			String line = "";

			while ((line = in.readLine()) != null) {

				String[] lineParts = line.split(" ");

				String uri = lineParts[0].replaceAll("<", "").replaceAll(">", "");
				String type = lineParts[2].replaceAll("<", "").replaceAll(">", "");

				if (!type.equals("http://www.w3.org/2002/07/owl#Thing")) {

					if (types.get(uri) == null) {

						Set<String> set = new HashSet<String>();
						set.add(type);

						types.put(uri, set);
					}
					else {

						Set<String> set = types.get(uri);
						set.add(type);

						types.put(uri, set);
					}
				}
			}
		}
		catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void readLabels(String pathToLabelsFile) {

		this.labels = new HashMap<String, String>();

//		 this.labels.put("http://dbpedia.org/resource/Continental_Illinois",
//		 "Continental Illinois bank");
//		 this.labels.put("http://dbpedia.org/resource/McKinley_Senior_High_School",
//		 "McKinley Senior High School");
//		 this.labels.put("http://dbpedia.org/resource/APR_FC", "APR FC");

		try {

			BufferedReader in = new BufferedReader(new FileReader(pathToLabelsFile));

			String line = "";

			while ((line = in.readLine()) != null) {

				String[] lineParts = line.split(">");

				String uri = lineParts[0].replaceAll("<", "").replaceAll(">", "").trim();
				String label = lineParts[2].replaceAll("\"@en", "");
				label = label.substring(0, label.length() - 1).replaceAll("\"", "");

				labels.put(uri, label);
			}
		}
		catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param surfaceForms the surfaceForms to set
	 */
	public void setLabels(Map<String, String> labels) {

		this.labels = labels;
	}
	
	/**
	 * @return the surfaceForms
	 */
	public Map<String, String> getLabels() {

		return labels;
	}
	
	/**
	 * @param types the types to set
	 */
	public void setTypes(Map<String, Set<String>> types) {

		this.types = types;
	}
	
	/**
	 * @return the types
	 */
	public Map<String, Set<String>> getTypes() {

		return types;
	}
}
