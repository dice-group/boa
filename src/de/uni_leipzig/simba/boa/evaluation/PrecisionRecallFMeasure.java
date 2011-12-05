package de.uni_leipzig.simba.boa.evaluation;

import java.util.ArrayList;
import java.util.List;

import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;


public class PrecisionRecallFMeasure {

	private double precision	= 0D;
	private double recall		= 0D;
	
	private List<Triple> goldStandard;
	private List<Triple> testData;
	
	public PrecisionRecallFMeasure(List<Triple> goldStandard, List<Triple> testData) {

		this.goldStandard	= goldStandard;
		this.testData		= testData;
	}
	
	public double getPrecision() {

		List<Triple> tempGoldStandard = new ArrayList<Triple>(this.goldStandard);
		List<Triple> tempTestData = new ArrayList<Triple>(this.testData);
		
		tempTestData.removeAll(tempGoldStandard);
		
		this.precision = (double) (this.testData.size() - tempTestData.size()) / (double) this.testData.size();

		return this.precision;
	}

	public double getRecall() {

		List<Triple> tempGoldStandard = new ArrayList<Triple>(this.goldStandard);
		List<Triple> tempTestData = new ArrayList<Triple>(this.testData);
		
		tempTestData.removeAll(tempGoldStandard);
		
		this.recall = (double) (this.testData.size() - tempTestData.size()) / (double) this.goldStandard.size();
		
		return this.recall;
	}

	public double getFMeasure() {

		return 2 * ( (this.precision * this.recall) / (this.precision + this.recall));
	}
	
}
