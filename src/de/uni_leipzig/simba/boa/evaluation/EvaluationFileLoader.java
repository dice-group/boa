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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javatools.parsers.Char;

import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Resource;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;


public class EvaluationFileLoader {
	
	private int numberOfSentencesToCompare = 200;
	
	private static final String ANNOTATOR_ONE_FILE = "/Users/gerb/Desktop/EVAL/EVAL_Haack_500.txt";
	private static final String ANNOTATOR_TWO_FILE = "/Users/gerb/Desktop/EVAL/EVAL_Upmeier_500.txt";
	
	public static void main(String[] args) {

		EvaluationFileLoader evaluationFileLoader = new EvaluationFileLoader();
		evaluationFileLoader.loadAnnotatorOneFile();
		evaluationFileLoader.loadAnnotatorTwoFile();
	}
	
	/**
	 * The files do contain different commenting symbols % and # 
	 * Sometimes there is the [] missing, so replace the x with [] 
	 */
	public Map<Integer, List<Triple>> loadAnnotatorOneFile() {

		try {
			
			return parseEvaluationFile(EvaluationFileLoader.ANNOTATOR_ONE_FILE);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Map<Integer, List<Triple>> loadAnnotatorTwoFile() {

		try {
			
			return parseEvaluationFile(EvaluationFileLoader.ANNOTATOR_TWO_FILE);
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
		
		int openingBracketCounter = 0;
		int openingAndClosingBracketCounter = 0;
		
		String line = "";
		while ((line = br.readLine()) != null) {
			
			if ( line.startsWith("% ") ) continue;
			if ( line.startsWith("[") ) openingBracketCounter++;
			if ( line.startsWith("[]") ) openingAndClosingBracketCounter++;
			
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
							
							this.addTripleToCollection(sentenceNumberToTriples, sentenceNumber, triple);
						}
						// nothing inside of []
						else {
							
							if ( line.trim().startsWith("[]")) {
								
								sentenceNumberToTriples.put(sentenceNumber, new ArrayList<Triple>());
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
		
		int tripleCount = 0;
		Integer i = 1;
		for ( Map.Entry<Integer, List<Triple>> entry : sentenceNumberToTriples.entrySet() ) {
			
			tripleCount += entry.getValue().size();
//			System.out.println(new Boolean(entry.getKey() == i) + " " + entry.getKey() + " "+ i++);
		}

		System.out.println("*Annotator: \t" + filename + "*");
		System.out.println(" * Number of Sentences: \t" + sentenceNumberToTriples.size());
		System.out.println(" * Empty Sentences: \t" + openingAndClosingBracketCounter);
		System.out.println(" * Parsed Triples: \t" + tripleCount);
		System.out.println(" * Annotated Triples: \t" + (openingBracketCounter - openingAndClosingBracketCounter)); 
		System.out.println(" * Parsed Triples == Annotated Triples? -> " + new Boolean((openingBracketCounter - openingAndClosingBracketCounter) == tripleCount));
		System.out.println();
		
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
			
			if ( line.contains(".") ) {
				
				sentenceNumber = Integer.valueOf(line.substring(0, line.indexOf(".")));
			}
		}
		catch (NumberFormatException  nfe) {
			
			// we did not found a sentence number so dont do anything
//			System.out.println("No number: " + line);
		}
//		System.out.println("Number: " + sentenceNumber);
		
		return sentenceNumber;
	}

	public List<Triple> loadGoldStandard() {

		// TODO Auto-generated method stub
		return null;
	}

	public List<Triple> loadTestStandard() {

		// TODO Auto-generated method stub
		return null;
	}
}
