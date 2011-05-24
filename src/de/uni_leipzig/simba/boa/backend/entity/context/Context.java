package de.uni_leipzig.simba.boa.backend.entity.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.nlp.NamedEntityRecognizer;


public abstract class Context {

	protected List<String> words;
	protected String pattern;
	
	protected String buffer;
	
	protected NamedEntityRecognizer ner;
	
	public static final Map<String,String> namedEntityRecognitionMappings = new HashMap<String,String>();
	static {
	
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Airline",					"ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Company",					"ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/EducationalInstitution",	"ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Legislature",				"ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SoccerClub",				"ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SportsTeam",				"ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Newspaper",					"ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Organisation",				"ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MilitaryUnit",				"ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Broadcast",					"ORG");

		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/CollegeCoach",				"PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Wrestler",					"PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Saint",						"PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Person",					"PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Monarch",					"PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/EthnicGroup",				"PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PersonFunction",			"PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Architect",					"PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Artist",					"PER");

		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Airport",					"LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Place",						"LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Country",					"LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PopulatedPlace",			"LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/River",						"LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Island",					"LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/LaunchPad",					"LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Road",						"LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BodyOfWater",				"LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Bridge",					"LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Building",					"LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Canal",						"LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/City",						"LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Settlement",				"LOC");

		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Weapon",					"MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Work",						"MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Currency",					"MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MilitaryConflict",			"MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Award",						"MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MusicGenre",				"MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MusicalWork",				"MISC");
	}
	
	/**
	 * Returns true if the context contains an entity according to the specified type. For example 
	 * if the entity type is http://dbpedia.org/ontology/Person than this method returns only true
	 * if a entity by the ner tagger with the tag "B-PER" was found.
	 * 
	 * @param entityType the uri of the entity
	 * @return true if sentence contains the entity 
	 */
	public boolean containsSuitableEntity(String entityType) {
		
		for ( String word : this.words ) {
			
			if ( word.contains(NamedEntityRecognizer.DELIMITER + "B-" + Context.namedEntityRecognitionMappings.get(entityType))) 
				return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param entityType
	 * @return
	 */
	public abstract String getSuitableEntity(String entityType);

	/**
	 * @param words the words to set
	 */
	public void setWords(List<String> words) {

		this.words = words;
	}

	/**
	 * @return the words
	 */
	public List<String> getWords() {

		return this.words;
	}

	
	/**
	 * @return the ner
	 */
	public NamedEntityRecognizer getNer() {
	
		return this.ner;
	}

	
	/**
	 * @param ner the ner to set
	 */
	public void setNer(NamedEntityRecognizer ner) {
	
		this.ner = ner;
	}

	
	/**
	 * @return the pattern
	 */
	public String getPattern() {
	
		return pattern;
	}

	
	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(String pattern) {
	
		this.pattern = pattern;
	}
}
