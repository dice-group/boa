package de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.namedentityrecognition;

import java.util.HashMap;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.Constants;

public class NamedEntityTagNormalizer {

	public final static Map<String,String> NAMED_ENTITY_TAG_MAPPINGS = new HashMap<String,String>();
	
	static {
		
		// persons
		NAMED_ENTITY_TAG_MAPPINGS.put("B-PER",	Constants.NAMED_ENTITY_TAG_PERSON);
		NAMED_ENTITY_TAG_MAPPINGS.put("I-PER",	Constants.NAMED_ENTITY_TAG_PERSON);
		
		// organizations
		NAMED_ENTITY_TAG_MAPPINGS.put("B-ORG",	Constants.NAMED_ENTITY_TAG_ORGANIZATION);
		NAMED_ENTITY_TAG_MAPPINGS.put("I-ORG",	Constants.NAMED_ENTITY_TAG_ORGANIZATION);
		
		// places
		NAMED_ENTITY_TAG_MAPPINGS.put("B-LOC",	Constants.NAMED_ENTITY_TAG_PLACE);
		NAMED_ENTITY_TAG_MAPPINGS.put("I-LOC",	Constants.NAMED_ENTITY_TAG_PLACE);
		
		// other
		NAMED_ENTITY_TAG_MAPPINGS.put("O", 		Constants.NAMED_ENTITY_TAG_OTHER);
	}
}
