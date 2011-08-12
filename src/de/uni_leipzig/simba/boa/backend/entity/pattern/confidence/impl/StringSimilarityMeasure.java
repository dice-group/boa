package de.uni_leipzig.simba.boa.backend.entity.pattern.confidence.impl;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.QGramsDistance;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.confidence.ConfidenceMeasure;


public class StringSimilarityMeasure implements ConfidenceMeasure {

	@Override
	public void measureConfidence(PatternMapping mapping) {

		// we calculate the qgram distance between the NLR and the label of the property
		for ( Pattern pattern : mapping.getPatterns() ) {
		
			AbstractStringMetric metric = new QGramsDistance();
			pattern.setSimilarity(
					Double.valueOf(
							metric.getSimilarity(
									pattern.retrieveNaturalLanguageRepresentationWithoutVariables(), 
									mapping.getProperty().getLabel())));
		}
	}
}
