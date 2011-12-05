package de.uni_leipzig.simba.boa.evaluation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;

public class Evaluation {

	public static void main(String[] args) {

		new Evaluation();
	}
	
	public Evaluation() {
		
		// get the annotated triples out of the files
		EvaluationFileLoader evaluationFileLoader = new EvaluationFileLoader();
		Map<Integer, List<Triple>> annotatorOneFile = evaluationFileLoader.loadAnnotatorOneFile();
		Map<Integer, List<Triple>> annotatorTwoFile = evaluationFileLoader.loadAnnotatorTwoFile();
		System.out.println("----");
		
		// calculate the scores between multiple annotators
		AnnotatorScorer scorer = new AnnotatorScorer();
		scorer.calculateScores(annotatorOneFile, annotatorTwoFile);
		
		List<Triple> goldStandard	= evaluationFileLoader.loadGoldStandard();
		List<Triple> testData		= evaluationFileLoader.loadTestStandard();
		
		PrecisionRecallFMeasure precisionRecallFMeasure = new PrecisionRecallFMeasure(goldStandard, testData);
		DecimalFormat decimalFormat = new DecimalFormat("#.##");
		System.out.println("Precision:\t" + decimalFormat.format(precisionRecallFMeasure.getPrecision()) + "%");
		System.out.println("Recall:\t" + decimalFormat.format(precisionRecallFMeasure.getRecall()) + "%");
		System.out.println("F-Measure:\t" + decimalFormat.format(precisionRecallFMeasure.getFMeasure()) + "%");
	}
}
