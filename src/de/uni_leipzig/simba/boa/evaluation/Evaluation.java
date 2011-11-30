package de.uni_leipzig.simba.boa.evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;

public class Evaluation {

	private double propertyKappa = 0D;
	private double resourceKappa = 0D;
	private double precision = 0D;
	private double recall = 0D;
	private double fMeasure = 0D;
	
	public static void main(String[] args) {

		new Evaluation();
	}
	
	public Evaluation() {
		
		// get the annotated triples out of the files
		EvaluationFileLoader evaluationFileLoader = new EvaluationFileLoader();
		Map<Integer, List<Triple>> annotatorOneFile = evaluationFileLoader.loadAnnotatorOneFile();
		Map<Integer, List<Triple>> annotatorTwoFile = evaluationFileLoader.loadAnnotatorTwoFile();
		System.out.println("----");
		
		// calculate the cohens propertyKappa between two annotators
		Scorer scorer = new Scorer();
		this.propertyKappa = scorer.calculateScores(annotatorOneFile, annotatorTwoFile);
	}
}
