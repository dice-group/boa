package de.uni_leipzig.simba.boa.backend;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This class contains common constants used in the NLPedia project.
 * These constants include:
 * 
 * 	- the 10 most common languages spoken on the Internet
 * 
 * 
 * @author Daniel Gerber
 */
public class Constants {

	/**
	 * Use this for patterns in English. 
	 */
	public static final String ENGLISH_LANGUAGE = "ENGLISH_LANGUAGE";
	
	/**
	 * Use this for patterns in Chinese.
	 */
	public static final String MANDARIN_CHINESE_LANGUAGE = "MANDARIN_CHINESE_LANGUAGE";
	
	/**
	 * Use this for patterns in Spanish.
	 */
	public static final String SPANISH_LANGUAGE = "SPANISH_LANGUAGE";
	
	/**
	 * Use this for patterns in Japanese.
	 */
	public static final String JAPANESE_LANGUAGE = "JAPANESE_LANGUAGE";
	
	/**
	 * Use this for patterns in Portuguese.
	 */
	public static final String PORTUGESE_LANGUAGE = "PORTUGESE_LANGUAGE";
	
	/**
	 * Use this for patterns in German.
	 */
	public static final String GERMAN_LANGUAGE = "GERMAN_LANGUAGE";
	
	/**
	 * Use this for patterns in Arabic.
	 */
	public static final String ARABIC_LANGUAGE = "ARABIC_LANGUAGE";	
	
	/**
	 * Use this for patterns in French.
	 */
	public static final String FRENCH_LANGUAGE = "FRENCH_LANGUAGE";
	
	/**
	 * Use this for patterns in Russian.
	 */
	public static final String RUSSIAN_LANGUAGE = "RUSSIAN_LANGUAGE";
	
	/**
	 * Use this for patterns in Korean.
	 */
	public static final String KOREAN_LANGUAGE = "KOREAN_LANGUAGE";
	
	/**
	 * Used by language detection if language could not be determined.
	 */
	public static final String NOT_SUPPORTED_LANGUAGE = "NOT_SUPPORTED_LANGUAGE";
	
	
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
