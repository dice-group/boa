package de.uni_leipzig.simba.boa.evaluation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;

import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;


public class Scorer {

	public double calculateScores(Map<Integer, List<Triple>> annotatorOneFile, Map<Integer, List<Triple>> annotatorTwoFile) {

		if ( annotatorOneFile.size() != annotatorTwoFile.size() ) throw new RuntimeException("Files contain different number of sentences");
		
		int annotatorOneNotAnnotatedCount = 0, annotatorTwoNotAnnotatedCount = 0;
		int annotatorOneRdfTypeCount = 0, annotatorTwoRdfTypeCount = 0;
		int annotatedTriplePerLineCount = 0;
		int sameAnnotationCount = 0;
		int sameAnnotationWithDifferentPropertyCount = 0;
		int tripleCountOne = 0, tripleCountTwo = 0;;
		
		Map<String,Integer> propertiesToOccurrenceByAnnotatorOne = new HashMap<String,Integer>();
		Map<String,Integer> propertiesToOccurrenceByAnnotatorTwo = new HashMap<String,Integer>();
		
		List<String> differentPropertyList = new ArrayList<String>();
		List<String> differentTripleList = new ArrayList<String>();
		
		for ( int i = 1 ; i <= annotatorOneFile.size() ; i++ ) {

			List<Triple> triplesOne = annotatorOneFile.get(i);
			List<Triple> triplesTwo = annotatorTwoFile.get(i);
			
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
					else {
						
						differentTripleList.add(tripleOne.toString());
						differentTripleList.add(tripleTwo.toString());
						differentTripleList.add(" ");
					}
				}
			}
		}
		System.out.println(" * *Sentences with no annotations from annotator 1:* " + annotatorOneNotAnnotatedCount);
		System.out.println(" * *Sentences with no annotations from annotator 2:* " + annotatorTwoNotAnnotatedCount);
		System.out.println("----");
		System.out.println(" * *Sentences of rdf:type annotations from annotator 1:* " + annotatorOneRdfTypeCount);
		System.out.println(" * *Sentences of rdf:type annotations from annotator 2:* " + annotatorTwoRdfTypeCount);
		System.out.println("----");
		System.out.println(" * *Number of distinct properties by annotator 1:* " + propertiesToOccurrenceByAnnotatorOne.size());
		System.out.println(" * *Number of distinct properties by annotator 2:* " + propertiesToOccurrenceByAnnotatorTwo.size());
		System.out.println("----");
		System.out.println(" * *Number of triples per sentence (non empty) for annotator 1:* " + new DecimalFormat("#0.00").format((double)tripleCountOne / (double)(annotatorOneFile.size() - annotatorOneNotAnnotatedCount)));
		System.out.println(" * *Number of triples per sentence (non empty) for annotator 1:* " + new DecimalFormat("#0.00").format((double)tripleCountTwo / (double)(annotatorTwoFile.size() - annotatorTwoNotAnnotatedCount)));
		System.out.println("----");
		System.out.println(" * *Number of same annotations:* " + sameAnnotationCount);
		System.out.println("----");
		System.out.println(" * *Number of sentences with same triple count:* " + annotatedTriplePerLineCount);
		System.out.println("----");
		System.out.println(" * *Number of triples where subject and object are the same:* " + sameAnnotationWithDifferentPropertyCount);
		System.out.println("----");
		System.out.println(" * *Top 5 properties by annotator 1:*");
		printTopNProperties(propertiesToOccurrenceByAnnotatorOne, 5);
		System.out.println("----");
		System.out.println(" * *Top 5 properties by annotator 2:*");
		printTopNProperties(propertiesToOccurrenceByAnnotatorTwo, 5);
		System.out.println("----");
		System.out.println(" * *Triples with different properties (Subject/Object identical): ("+differentPropertyList.size()+")* ");
		for (String s : differentPropertyList) System.out.println("      * " + s.replace("[", "").replace("]", "").replace("Triple", "").replace("_", "+"));
		System.out.println("----");
		System.out.println(" * *Different triples: ("+differentTripleList.size()+")* ");
		for (String s : differentTripleList) System.out.println("      * " + s.replace("[", "").replace("]", "").replace("Triple", "").replace("_", "+"));
		
		return 0;
	}
	
	private void printTopNProperties(Map<String, Integer> map, int topN) {
		
		Comparator valueComparator = Ordering.natural().onResultOf(Functions.forMap(map)).compound(Ordering.natural()).reverse();
		map = ImmutableSortedMap.copyOf(map, valueComparator);

		for ( Map.Entry<String, Integer> entry : map.entrySet() ) {
			
			if ( topN != 0 ) {
				
				topN--;				
				System.out.println("   # " + entry.getKey() + ": " +  entry.getValue());
			}
		}
	}

	private static void addAndCountOccurrence(Map<String,Integer> map, String key){
		
		if ( map.containsKey(key)) map.put(key, map.get(key) + 1);
		else map.put(key, 1);
	}
}
