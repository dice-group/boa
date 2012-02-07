package de.uni_leipzig.simba.boa.backend.evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;


public class PrecisionRecallFMeasure {

	private double precision	= 0D;
	private double recall		= 0D;
	
	private Set<Triple> goldStandard;
	private Set<Triple> testData;
	
	public PrecisionRecallFMeasure(Set<Triple> goldStandard, Set<Triple> testData) {

		this.goldStandard	= goldStandard;
		this.testData		= testData;
	}
	
	public double getPrecision() {

		List<Triple> tempGoldStandard = new ArrayList<Triple>(this.goldStandard);
		List<Triple> tempTestData = new ArrayList<Triple>(this.testData);
		
		tempTestData.removeAll(tempGoldStandard);
		
		this.precision = (double) (this.testData.size() - tempTestData.size()) / (double) this.testData.size();
		return !Double.isNaN(this.precision) && !Double.isInfinite(this.precision) ? this.precision : 0D;
	}

	public double getRecall() {

		List<Triple> tempGoldStandard = new ArrayList<Triple>(this.goldStandard);
		List<Triple> tempTestData = new ArrayList<Triple>(this.testData);
		
		tempTestData.removeAll(tempGoldStandard);
		
		this.recall = (double) (this.testData.size() - tempTestData.size()) / (double) this.goldStandard.size();
		return !Double.isNaN(this.recall) && !Double.isInfinite(this.recall) ? this.recall : 0D;
	}

	public double getFMeasure() {

		double fMeasure = 2 * ( (this.precision * this.recall) / (this.precision + this.recall)); 
		return !Double.isNaN(fMeasure) && !Double.isInfinite(fMeasure) ? fMeasure : 0D;
	}
	
}
