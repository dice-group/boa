package de.uni_leipzig.simba.boa.backend.evaluation.comparator;

import java.util.Comparator;

import de.uni_leipzig.simba.boa.backend.evaluation.EvaluationResult;


public class EvaluationResultComparator implements Comparator<EvaluationResult> {

    @Override
    public int compare(EvaluationResult eval1, EvaluationResult eval2) {

        double x = (eval2.getFMeasure() - eval1.getFMeasure());
        if ( x < 0 ) return -1;
        if ( x == 0 ) return 0;
        return 1;
    }

}
