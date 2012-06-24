package de.uni_leipzig.simba.boa.backend.evaluation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;


public class AnnotatorScorer {

	public void calculateScores(Map<Integer, List<Triple>> annotatorOneFile, Map<Integer, List<Triple>> annotatorTwoFile) {

		if ( annotatorOneFile.size() != annotatorTwoFile.size() ) throw new RuntimeException("Files contain different number of sentences");
		
		int annotatorOneNotAnnotatedCount = 0, annotatorTwoNotAnnotatedCount = 0;
		int annotatorOneRdfTypeCount = 0, annotatorTwoRdfTypeCount = 0;
		int annotatedTriplePerLineCount = 0;
		int sameAnnotationCount = 0;
		int sameAnnotationWithDifferentPropertyCount = 0;
		int annotationsWithTwoEqualParts = 0;
		int tripleCountOne = 0, tripleCountTwo = 0;;
		
		Map<String,Integer> propertiesToOccurrenceByAnnotatorOne = new HashMap<String,Integer>();
		Map<String,Integer> propertiesToOccurrenceByAnnotatorTwo = new HashMap<String,Integer>();
		
		List<String> oneDifferentPartList = new ArrayList<String>();
		List<String> differentPropertyList = new ArrayList<String>();
		List<String> notRdfTypeTripleList = new ArrayList<String>();
		
		for ( int i = 1 ; i <= annotatorOneFile.size() ; i++ ) {

			List<Triple> triplesOne = annotatorOneFile.get(i);
			List<Triple> triplesTwo = annotatorTwoFile.get(i);
			if  (triplesOne == null) {
				System.out.println(i);
			}
			if  (triplesTwo == null) {
				System.out.println(i);
			}
			
			tripleCountOne += triplesOne.size();
			tripleCountTwo += triplesTwo.size();

			// one or both of the raters as no triples annotated 
			if ( triplesOne.isEmpty() ) {
				
				annotatorOneNotAnnotatedCount++;
			}
			if ( triplesTwo.isEmpty() ) {
				
				annotatorTwoNotAnnotatedCount++;
			}
			
			if ( triplesOne.size() == triplesTwo.size() && triplesOne.size() > 0 ) annotatedTriplePerLineCount++;
			
			for (Triple tripleOne : triplesOne){
				
				if ( tripleOne.getProperty().getUri().equals("rdf:type") ) annotatorOneRdfTypeCount++;
				addAndCountOccurrence(propertiesToOccurrenceByAnnotatorOne, tripleOne.getProperty().getUri());
				
				for (Triple tripleTwo : triplesTwo){
					
					if ( !tripleOne.getSubject().getUri().startsWith("wiki:") ) throw new RuntimeException("Wiki prefix not correct in line: " + i + " in file one." + " in file two: " + tripleOne.getSubject().getUri());
					if ( !tripleOne.getObject().getUri().startsWith("wiki:") && !tripleOne.getObject().getUri().startsWith("dbpedia-owl:") ) throw new RuntimeException("Wiki prefix not correct in line: " + i + " in file one: " + tripleOne.getObject().getUri());
					if ( !tripleTwo.getSubject().getUri().startsWith("wiki:") ) throw new RuntimeException("Wiki prefix not correct in line: " + i + " in file two: " + tripleTwo.getSubject().getUri());
					if ( !tripleTwo.getObject().getUri().startsWith("wiki:") && !tripleTwo.getObject().getUri().startsWith("dbpedia-owl:") ) throw new RuntimeException("Wiki prefix not correct in line: " + i + " in file two: " + tripleTwo.getObject().getUri());
					if ( !tripleOne.getProperty().getUri().startsWith("dbpedia-owl:") && !tripleOne.getProperty().getUri().startsWith("rdf:type") ) throw new RuntimeException("Wiki prefix not correct in line: " + i + " in file one: " + tripleOne.getProperty().getUri());
					if ( !tripleTwo.getProperty().getUri().startsWith("dbpedia-owl:") && !tripleTwo.getProperty().getUri().startsWith("rdf:type") ) throw new RuntimeException("Wiki prefix not correct in line: " + i + " in file two: " + tripleTwo.getProperty().getUri());
					
					if ( tripleTwo.getProperty().getUri().equals("rdf:type") ) annotatorTwoRdfTypeCount++;
					addAndCountOccurrence(propertiesToOccurrenceByAnnotatorTwo, tripleTwo.getProperty().getUri());
					
					if ( tripleOne.getSubject().equals(tripleTwo.getSubject()) && 
						 tripleOne.getProperty().equals(tripleTwo.getProperty()) &&
						 tripleOne.getObject().equals(tripleTwo.getObject())) {
						
						sameAnnotationCount++;
					}
					else if ( 
						tripleOne.getSubject().equals(tripleTwo.getSubject()) && 
						tripleOne.getObject().equals(tripleTwo.getObject()) &&
						 
						(tripleOne.getProperty().getUri().startsWith("dbpedia") || tripleTwo.getProperty().getUri().startsWith("dbpedia")) ) {
							
						sameAnnotationWithDifferentPropertyCount++;
						differentPropertyList.add(tripleOne.toString());
						differentPropertyList.add(tripleTwo.toString());
						differentPropertyList.add(" ");
					}
					else if ( 	(tripleOne.getSubject().equals(tripleTwo.getSubject()) &&
								tripleOne.getProperty().equals(tripleTwo.getProperty()))
								|| 
								(tripleOne.getObject().equals(tripleTwo.getObject()) &&
								tripleOne.getProperty().equals(tripleTwo.getProperty()))) {
						
						annotationsWithTwoEqualParts++;
						oneDifferentPartList.add(tripleOne.toString());
						oneDifferentPartList.add(tripleTwo.toString());
						oneDifferentPartList.add("");
					}
					else if ( !(tripleOne.getProperty().getUri().equals("rdf:type") || tripleTwo.getProperty().getUri().equals("rdf:type")) ) {	
						
						notRdfTypeTripleList.add(tripleOne.toString());
						notRdfTypeTripleList.add(tripleTwo.toString());
						notRdfTypeTripleList.add(" ");
					}
				}
			}
		}
		EvaluationManager.OUTPUT.append(" * *Sentences with no annotations from annotator 1:* " + annotatorOneNotAnnotatedCount).append(Constants.NEW_LINE_SEPARATOR);
		EvaluationManager.OUTPUT.append(" * *Sentences with no annotations from annotator 2:* " + annotatorTwoNotAnnotatedCount).append(Constants.NEW_LINE_SEPARATOR);
		EvaluationManager.OUTPUT.append("----").append(Constants.NEW_LINE_SEPARATOR);
		
		
		EvaluationManager.OUTPUT.append(" * *Sentences of rdf:type annotations from annotator 1:* " + annotatorOneRdfTypeCount).append(Constants.NEW_LINE_SEPARATOR);
		EvaluationManager.OUTPUT.append(" * *Sentences of rdf:type annotations from annotator 2:* " + annotatorTwoRdfTypeCount).append(Constants.NEW_LINE_SEPARATOR);
		EvaluationManager.OUTPUT.append("----").append(Constants.NEW_LINE_SEPARATOR);
		
		
		EvaluationManager.OUTPUT.append(" * *Number of distinct properties by annotator 1:* " + propertiesToOccurrenceByAnnotatorOne.size()).append(Constants.NEW_LINE_SEPARATOR);
		EvaluationManager.OUTPUT.append(" * *Number of distinct properties by annotator 2:* " + propertiesToOccurrenceByAnnotatorTwo.size()).append(Constants.NEW_LINE_SEPARATOR);
		EvaluationManager.OUTPUT.append("----").append(Constants.NEW_LINE_SEPARATOR);
		
		
		EvaluationManager.OUTPUT.append(" * *Number of triples per sentence (non empty) for annotator 1:* " + new DecimalFormat("#0.00").format((double)tripleCountOne / (double)(annotatorOneFile.size() - annotatorOneNotAnnotatedCount))).append(Constants.NEW_LINE_SEPARATOR);
		EvaluationManager.OUTPUT.append(" * *Number of triples per sentence (non empty) for annotator 2:* " + new DecimalFormat("#0.00").format((double)tripleCountTwo / (double)(annotatorTwoFile.size() - annotatorTwoNotAnnotatedCount))).append(Constants.NEW_LINE_SEPARATOR);
		EvaluationManager.OUTPUT.append("----").append(Constants.NEW_LINE_SEPARATOR);
		
		
		EvaluationManager.OUTPUT.append(" * *Number of same triples:* " + sameAnnotationCount).append(Constants.NEW_LINE_SEPARATOR);
		EvaluationManager.OUTPUT.append("----").append(Constants.NEW_LINE_SEPARATOR);
		
		
		EvaluationManager.OUTPUT.append(" * *Number of sentences with same triple count:* " + annotatedTriplePerLineCount).append(Constants.NEW_LINE_SEPARATOR);
		EvaluationManager.OUTPUT.append("----").append(Constants.NEW_LINE_SEPARATOR);
		
		
		EvaluationManager.OUTPUT.append(" * *Number of triples where subject and object are the same:* " + sameAnnotationWithDifferentPropertyCount).append(Constants.NEW_LINE_SEPARATOR);
		EvaluationManager.OUTPUT.append("----").append(Constants.NEW_LINE_SEPARATOR);
		
		
		EvaluationManager.OUTPUT.append(" * *Top 5 properties by annotator 1:*").append(Constants.NEW_LINE_SEPARATOR);
		printTopNProperties(propertiesToOccurrenceByAnnotatorOne, 5);
		EvaluationManager.OUTPUT.append("----").append(Constants.NEW_LINE_SEPARATOR);
		
		
		EvaluationManager.OUTPUT.append(" * *Top 5 properties by annotator 2:*").append(Constants.NEW_LINE_SEPARATOR);
		printTopNProperties(propertiesToOccurrenceByAnnotatorTwo, 5);
		EvaluationManager.OUTPUT.append("----").append(Constants.NEW_LINE_SEPARATOR);
		
		
		EvaluationManager.OUTPUT.append(" * *Triples with different properties (Subject/Object identical): ("+differentPropertyList.size()/3+" pairs)* ").append(Constants.NEW_LINE_SEPARATOR);
		for (String s : differentPropertyList) EvaluationManager.OUTPUT.append("      * " + s.replace("[", "").replace("]", "").replace("Triple", "").replace("_", "+")).append(Constants.NEW_LINE_SEPARATOR);
		EvaluationManager.OUTPUT.append("----").append(Constants.NEW_LINE_SEPARATOR);
				
				
		EvaluationManager.OUTPUT.append(" * *Triples with one resource difference (Subject/Predicate or Predicate/Object identical): ("+oneDifferentPartList.size()/3+" pairs)* ").append(Constants.NEW_LINE_SEPARATOR);
		for (String s : oneDifferentPartList) EvaluationManager.OUTPUT.append("      * " + s.replace("[", "").replace("]", "").replace("Triple", "").replace("_", "+")).append(Constants.NEW_LINE_SEPARATOR);
		EvaluationManager.OUTPUT.append("----").append(Constants.NEW_LINE_SEPARATOR);
				
				
		EvaluationManager.OUTPUT.append(" * *Different triples: ("+notRdfTypeTripleList.size()/3+" pairs)* ").append(Constants.NEW_LINE_SEPARATOR);
		for (String s : notRdfTypeTripleList) EvaluationManager.OUTPUT.append("      * " + s.replace("[", "").replace("]", "").replace("Triple", "").replace("_", "+")).append(Constants.NEW_LINE_SEPARATOR);
	}
	
	private void printTopNProperties(Map<String, Integer> map, int topN) {
		
		Comparator valueComparator = Ordering.natural().onResultOf(Functions.forMap(map)).compound(Ordering.natural()).reverse();
		map = ImmutableSortedMap.copyOf(map, valueComparator);

		for ( Map.Entry<String, Integer> entry : map.entrySet() ) {
			
			if ( topN != 0 ) {
				
				topN--;				
				EvaluationManager.OUTPUT.append("   # " + entry.getKey() + ": " +  entry.getValue()).append(Constants.NEW_LINE_SEPARATOR);
			}
		}
	}

	private static void addAndCountOccurrence(Map<String,Integer> map, String key){
		
		if ( map.containsKey(key)) map.put(key, map.get(key) + 1);
		else map.put(key, 1);
	}
}
