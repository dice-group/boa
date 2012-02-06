package de.uni_leipzig.simba.boa.backend;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;

/**
 * This class contains common constants used in the NLPedia project.
 * 
 * @author Daniel Gerber
 */
public class Constants {
    
    /**
     * 
     */
    public static final String DBPEDIA_RESOURCE_PREFIX = "http://dbpedia.org/resource/";
    
    /**
     * 
     */
    public static final String DBPEDIA_ONTOLOGY_PREFIX = "http://dbpedia.org/ontology/";
    
    /* ##################################################################################### */ 
    
    
    /**
     * 
     */
    public static final String EVALUATION_PATH = "evaluation/";
    
    /**
     * 
     */
    public static final String NEURAL_NETWORK_PATH = "neuralnetwork/";
    
    /**
     * 
     */
    public static final String BACKGROUND_KNOWLEDGE_PATH = "backgroundknowledge/";
    
    /**
     * 
     */
    public static final String BACKGROUND_KNOWLEDGE_OBJECT_PROPERTY_PATH = "backgroundknowledge/object/";
    
    /**
     * 
     */
    public static final String BACKGROUND_KNOWLEDGE_DATATYPE_PROPERTY_PATH = "backgroundknowledge/datatype/";
    
    /**
     * 
     */
    public static final String INDEX_PATH = "index/";
    
    /**
     * 
     */
    public static final String INDEX_CORPUS_PATH = "index/corpus/";
    
    /**
     * 
     */
    public static final String INDEX_PATTERN_PATH = "index/pattern/";
    
    /**
     * 
     */
    public static final String INDEX_DEFAULT_PATTERN_PATH = "index/pattern/default/";
    
    /**
     * 
     */
    public static final String INDEX_DETAIL_PATTERN_PATH = "index/pattern/detail/";
    
    /**
     * 
     */
    public static final String PATTERN_MAPPINGS_PATH = "patternmappings/";
    
    /**
     * 
     */
    public static final String RAW_DATA_PATH = "raw/";
    
    /**
     * 
     */
    public static final String RDF_DATA_PATH = "rdf/";
    
    /**
     * 
     */
    public static final String RDF_DATA_BINARY_PATH = "rdf/binary/";
    
    /**
     * 
     */
    public static final String RDF_DATA_NTRIPLES_PATH = "rdf/nt/";
    
    /**
     * 
     */
    public static final String RDF_DATA_TEXT_PATH = "rdf/text/";
    
    /**
     * 
     */
    public static final String SENTENCE_CORPUS_PATH = "sentences/";
    
    /**
     * 
     */
    public static final String WIKIPEDIA_DUMP_PATH = "wikipedia/";

    /**
     * 
     */
    public static final String DBPEDIA_DUMP_PATH = "dbpedia/";

    // ##############################################################################
    
	/**
	 * 
	 */
	public static final String NAMED_ENTITY_TAG_OTHER = "OTHER";
	
	/**
	 * 
	 */
	public static final String NAMED_ENTITY_TAG_MISCELLANEOUS = "MISC";
	
	/**
	 * 
	 */
	public static final String NAMED_ENTITY_TAG_PLACE = "PLACE";
	
	/**
	 * 
	 */
	public static final String NAMED_ENTITY_TAG_ORGANIZATION = "ORGANIZATION"; 
	
	/**
	 * 
	 */
	public static final String NAMED_ENTITY_TAG_PERSON = "PERSON";
	
	// ##############################################################################

	/**
	 * Use this option to detect sentences with the opennlp framework
	 */
	public static final String SENTENCE_BOUNDARY_DISAMBIGUATION_OPEN_NLP = "opennlp";
	
	/**
	 * Use this option to detect sentences with the stanford nlp core
	 */
	public static final String SENTENCE_BOUNDARY_DISAMBIGUATION_STANFORD_NLP = "stanfordnlp";
	
	/**
	 * Use this option to detect sentences within korean text
	 */
	public static final Object SENTENCE_BOUNDARY_DISAMBIGUATION_KOREAN = "korean";
	
	// ##############################################################################
	
	/**
	 * stop words in english and german
	 */
	public static final Set<String> STOP_WORDS = new HashSet<String>();
	static {
		Collections.addAll(STOP_WORDS, ":", " ", ",", "-", "i", "a", "about", "an", "and", "are", "as", "at", "be", "by", "com", "for", "from", "how", "in", "is", "it", "of", "on", "or", "that", "the",
				"this", "to", "what", "when", "where", "who", "will", "with", "the", "www", "before", ",", "after", ";", "like", "and", "such", "-LRB-", "-RRB-", "-lrb-", "-rrb-", "aber", "als",
				"am", "an", "auch", "auf", "aus", "bei", "bin", "bis", "bist", "da", "dadurch", "daher", "darum", "das", "daß", "dass", "dein", "deine", "dem", "den", "der", "des", "dessen",
				"deshalb", "die", "dies", "dieser", "dieses", "doch", "dort", "du", "durch", "ein", "eine", "einem", "einen", "einer", "eines", "er", "es", "euer", "eure", "für", "hatte", "hatten",
				"hattest", "hattet", "hier", "hinter", "ich", "ihr", "ihre", "im", "in", "ist", "ja", "jede", "jedem", "jeden", "jeder", "jedes", "jener", "jenes", "jetzt", "kann", "kannst",
				"können", "könnt", "machen", "mein", "meine", "mit", "muß", "mußt", "musst", "müssen", "müßt", "nach", "nachdem", "nein", "nicht", "nun", "oder", "seid", "sein", "seine", "sich",
				"sie", "sind", "soll", "sollen", "sollst", "sollt", "sonst", "soweit", "sowie", "und", "unser unsere", "unter", "vom", "von", "vor", "wann", "warum", "weiter", "weitere", "wenn",
				"wer", "werde", "werden", "werdet", "weshalb", "wie", "wieder", "wieso", "wir", "wird", "wirst", "wo", "woher", "wohin", "zu", "zum", "zur", "über");
	}

	
	// ##############################################################################
	
	/**
	 * Use this property to write new lines in files or stdouts
	 */
	public static final String NEW_LINE_SEPARATOR = System.getProperty("line.separator");
	
	/**
	 * Delimiter used to separate a token from its named entity tag
	 */
	public static final String PART_OF_SPEECH_TAG_DELIMITER = "_";
	
	/**
	 * Delimiter used to separate a token from its named entity tag
	 */
	public static final String NAMED_ENTITY_TAG_DELIMITER = "_";
	
	/**
	 * used to seperate the values in the background knowledge file
	 */
	public static final String BACKGROUND_KNOWLEDGE_VALUE_SEPARATOR = " ||| ";
	
	/**
	 * used to seperate the values in the background knowledge file
	 */
	public static final String BACKGROUND_KNOWLEDGE_VALUE_SEPARATOR_REGEX = " \\|\\|\\| ";
	
	/**
	 * used to seperate the values in the background knowledge file
	 */
	public static final String BACKGROUND_KNOWLEDGE_SURFACE_FORM_SEPARATOR = "_&_";
	
	/**
     * 
     */
    public static final String FEATURE_FILE_COLUMN_SEPARATOR = "\t";
	
	// ##############################################################################
	
	/**
	 * 
	 */
	public static final String RDF_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	
	/**
	 * 
	 */
	public static final String RDFS_LABEL = "http://www.w3.org/2000/01/rdf-schema#label";
	
	/**
	 * 
	 */
	public static final String RDFS_DOMAIN = "http://www.w3.org/2000/01/rdf-schema#domain";
	
	/**
	 * 
	 */
	public static final String RDFS_RANGE = "http://www.w3.org/2000/01/rdf-schema#range";
	
	/**
	 * 
	 */
	public static final String OWL_DATATYPE_PROPERTY = "http://www.w3.org/2002/07/owl#DatatypeProperty";
	
	/**
	 * 
	 */
	public static final String OWL_OBJECT_PROPERTY = "http://www.w3.org/2002/07/owl#ObjectProperty";

	/**
	 * 
	 */
	public static final String N_TRIPLE = "N-TRIPLE";
	
	/**
	 * 
	 */
	public static final String TURTLE = "TURTLE";
}
