package de.uni_leipzig.simba.boa.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.lucene.store.Directory;

import javatools.parsers.Char;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Resource;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;
import de.uni_leipzig.simba.boa.backend.search.concurrent.CreateKnowledgeCallable;


public class EvaluationFileLoader {
	
	public static final String FIRST_BATCH_ANNOTATOR_ONE_FILE	= NLPediaSettings.getInstance().getSetting("first.batch.annotator.one.file"); //"/Users/gerb/Desktop/EVAL/EVAL_Haack_595.txt";
	public static final String FIRST_BATCH_ANNOTATOR_TWO_FILE	= NLPediaSettings.getInstance().getSetting("first.batch.annotator.two.file");//"/Users/gerb/Desktop/EVAL/EVAL_Upmeier_595.txt";
	
	public static final String SECOND_BATCH_ANNOTATOR_ONE_FILE	= NLPediaSettings.getInstance().getSetting("second.batch.annotator.one.file");//"/Users/gerb/Desktop/EVAL/Evaluation_2_Haack_303.txt";
	public static final String SECOND_BATCH_ANNOTATOR_TWO_FILE	= NLPediaSettings.getInstance().getSetting("second.batch.annotator.two.file");//"/Users/gerb/Desktop/EVAL/Evaluation_2_Upmeier_241.txt";
	
	public enum ExcludeRdfTypeStatements { YES, NO }
	
	private final PatternMappingDao patternMappingDao = (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
	
	private Set<String> sentences = new HashSet<String>();
	int i = 1;
	int number;
	public static void main(String[] args) {

		EvaluationFileLoader evaluationFileLoader = new EvaluationFileLoader();
		evaluationFileLoader.loadAnnotatorFile(FIRST_BATCH_ANNOTATOR_ONE_FILE);
		evaluationFileLoader.loadAnnotatorFile(FIRST_BATCH_ANNOTATOR_TWO_FILE);
	}
	
	/**
	 * The files do contain different commenting symbols % and # 
	 * Sometimes there is the [] missing, so replace the x with [] 
	 */
	public Map<Integer, List<Triple>> loadAnnotatorFile(String fileName) {

		try {
			
			return parseEvaluationFile(fileName);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private Map<Integer, List<Triple>> parseEvaluationFile(String filename) throws IOException {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename)), "UTF-8"));
		
		Map<Integer, List<Triple>> sentenceNumberToTriples = new TreeMap<Integer, List<Triple>>();
		Integer sentenceNumber = null;
		this.number = 0;
		
		int openingBracketCounter = 0;
		int openingAndClosingBracketCounter = 0;
		
		String line = "";
		while ((line = br.readLine()) != null) {
			
			if ( line.startsWith("% ") ) continue;
			
			if ( !line.startsWith("[one]") && !line.startsWith("[coref]")) {

				if ( line.startsWith("[") ) openingBracketCounter++;
				if ( line.startsWith("[]") ) openingAndClosingBracketCounter++;
			}
			
			// this is not between the sentences
			if ( !line.trim().isEmpty() ) {
				
				sentenceNumber = this.getSentenceNumber(sentenceNumber, line);
				
				// we found a triple
				if ( sentenceNumber == null ) {	} // we dont need anything except the line number
				else {
					
					// sometimes there are more than one whitespace character
					line = line.replace("  ", " ");
					// remove comments after the closing bracket
					if ( line.contains("]") ) line = line.substring(0, line.indexOf("]") + 1);
					// remove whitespace
					line = line.trim();
					
					if ( line.substring(0, 1).equals("[") && line.substring(line.length() - 1).equals("]") ) {

						line = line.replace("wiki: ", "wiki:");
						line = line.replace("dbpedia-owl: ", "dbpedia-owl:");
						
						String[] tripleParts = line.replace("[", "").replace("]", "").split(" ");
						if ( tripleParts.length == 3 ) {
							
							if ( tripleParts[1].contains("http") || tripleParts[2].contains("http") || tripleParts[2].contains("http")  ) throw new RuntimeException("Check that file");
							
							Triple triple = new Triple();
							triple.setSubject(new Resource(tripleParts[0].toLowerCase()));
							triple.setProperty(new Property(tripleParts[1].toLowerCase()));
							triple.setObject(new Resource(tripleParts[2].toLowerCase()));
							
							this.addTripleToCollection(sentenceNumberToTriples, number, triple);
						}
						// nothing inside of []
						else {
							
							if ( line.trim().startsWith("[]") || line.trim().startsWith("[coref]") || line.trim().startsWith("[one]")) {
								
								sentenceNumberToTriples.put(number, new ArrayList<Triple>());
							}
							else {
								
								System.out.println(line);
							}
						}
					}
					else {
						
						if ( !line.startsWith(String.valueOf(sentenceNumber)) ) {
							
							System.out.println("ALERT not [ or ] at start or end: " + sentenceNumber + ". " + line);
						}
					}
				}
			}
		}
		
//		int tripleCount = 0;
//		Integer i = 1;
//		for ( Map.Entry<Integer, List<Triple>> entry : sentenceNumberToTriples.entrySet() ) {
//			
//			tripleCount += entry.getValue().size();
//			System.out.println(new Boolean(entry.getKey() == i) + " " + entry.getKey() + " "+ i++);
//			System.out.println(entry.getKey() + " " + entry.getValue() + " " + i++);
//		}
//
//		Evaluation.OUTPUT.append("*Annotator: \t" + filename + "*").append(Constants.NEW_LINE_SEPARATOR);
//		Evaluation.OUTPUT.append(" * Number of Sentences: \t" + sentenceNumberToTriples.size()).append(Constants.NEW_LINE_SEPARATOR);
//		Evaluation.OUTPUT.append(" * Empty Sentences: \t" + openingAndClosingBracketCounter).append(Constants.NEW_LINE_SEPARATOR);
//		Evaluation.OUTPUT.append(" * Parsed Triples: \t" + tripleCount).append(Constants.NEW_LINE_SEPARATOR);
//		Evaluation.OUTPUT.append(" * Annotated Triples: \t" + (openingBracketCounter - openingAndClosingBracketCounter)).append(Constants.NEW_LINE_SEPARATOR); 
//		Evaluation.OUTPUT.append(" * Parsed Triples == Annotated Triples? -> " + new Boolean((openingBracketCounter - openingAndClosingBracketCounter) == tripleCount)).append(Constants.NEW_LINE_SEPARATOR);
//		Evaluation.OUTPUT.append(Constants.NEW_LINE_SEPARATOR);
		
		return sentenceNumberToTriples;
	}

	private void addTripleToCollection(Map<Integer, List<Triple>> sentenceNumberToTriples, Integer sentenceNumber, Triple triple) {

		// we found a new line so create a new set of 
		if ( !sentenceNumberToTriples.containsKey(sentenceNumber) ) {
			
			List<Triple> triples = new ArrayList<Triple>();
			triples.add(triple);
			sentenceNumberToTriples.put(sentenceNumber, triples);
		}
		else {
			
			sentenceNumberToTriples.get(sentenceNumber).add(triple);
		}
	}

	private Integer getSentenceNumber(Integer sentenceNumber, String line) {
		
		try {
			
			if ( line.contains(". ") ) {
				
				sentenceNumber = Integer.valueOf(line.substring(0, line.indexOf(".")));
				this.sentences.add(line.substring(line.indexOf(".") + 1).trim());
				this.number++; // we need to override the regular line number because there are lines missing in the files
			}
		}
		catch (NumberFormatException  nfe) {
			
			// we did not found a sentence number so dont do anything
//			System.out.println("No number: " + line);
		}
//		System.out.println("Number: " + sentenceNumber);
		
		return sentenceNumber;
	}
	
	public Set<String> getSentences() {
		
		return this.sentences;
	}

	public Set<Triple> loadGoldStandard(ExcludeRdfTypeStatements withRdfTypeRelations) {

		Set<Triple> allAnnotatedTriples = new HashSet<Triple>();
		for (List<Triple> triples : this.loadAnnotatorFile(EvaluationFileLoader.FIRST_BATCH_ANNOTATOR_ONE_FILE).values()) {
			
			allAnnotatedTriples.addAll(triples);
		}
		for (List<Triple> triples : this.loadAnnotatorFile(EvaluationFileLoader.FIRST_BATCH_ANNOTATOR_TWO_FILE).values()) {
			
			allAnnotatedTriples.addAll(triples);
		}
		for (List<Triple> triples : this.loadAnnotatorFile(EvaluationFileLoader.SECOND_BATCH_ANNOTATOR_ONE_FILE).values()) {
			
			allAnnotatedTriples.addAll(triples);
		}
		for (List<Triple> triples : this.loadAnnotatorFile(EvaluationFileLoader.SECOND_BATCH_ANNOTATOR_TWO_FILE).values()) {
			
			allAnnotatedTriples.addAll(triples);
		}
		// we most likely want to exlude all rdf:type statements because we cant find them at the moment 
		if ( withRdfTypeRelations.equals(EvaluationFileLoader.ExcludeRdfTypeStatements.YES) ) {
		
			Set<Triple> triplesWithoutRdfType = new HashSet<Triple>();
			for (Triple t : allAnnotatedTriples) {
				
				if ( !t.getProperty().getUri().equals("rdf:type") ) triplesWithoutRdfType.add(t);
			}
			allAnnotatedTriples = triplesWithoutRdfType;
		}
		
		return allAnnotatedTriples;
	}

	public Set<Triple> loadBoa(Directory idx, double tripleScoreThreshold) {

		Set<Triple> boaTriples = new HashSet<Triple>(); 
		for (PatternMapping mapping : patternMappingDao.findAllPatternMappings() ) {
			
			boaTriples.addAll(new CreateKnowledgeCallable(mapping, idx).call());
		}
		return boaTriples;
	}
}
