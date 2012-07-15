package de.uni_leipzig.simba.boa.backend.evaluation;

import java.text.DecimalFormat;
import java.text.NumberFormat;


public class EvaluationResult {

    private int foundTriples;
    private double precision;
    private double recall;
    private double fMeasure;
    private double patternThreshold;
    private double tripleTreshold;
    private double contextLookAhead;
    private int availableTriples;
    
    /**
     * @return the foundTriples
     */
    public int getFoundTriples() {
    
        return foundTriples;
    }
    
    /**
     * @param foundTriples the foundTriples to set
     */
    public EvaluationResult setFoundTriples(int foundTriples) {
    
        this.foundTriples = foundTriples;
        return this;
    }
    
    /**
     * @return the precision
     */
    public double getPrecision() {
    
        return precision;
    }
    
    /**
     * @param precision the precision to set
     */
    public EvaluationResult setPrecision(double precision) {
    
        this.precision = precision;
        return this;
    }
    
    /**
     * @return the recall
     */
    public double getRecall() {
    
        return recall;
    }
    
    /**
     * @param recall the recall to set
     */
    public EvaluationResult setRecall(double recall) {
    
        this.recall = recall;
        return this;
    }
    
    /**
     * @return the fMeasure
     */
    public double getFMeasure() {
    
        return fMeasure;
    }
    
    /**
     * @param fMeasure the fMeasure to set
     */
    public EvaluationResult setFMeasure(double fMeasure) {
    
        this.fMeasure = fMeasure;
        return this;
    }
    
    /**
     * @return the patternThreshold
     */
    public double getPatternThreshold() {
    
        return patternThreshold;
    }
    
    /**
     * @param patternThreshold the patternThreshold to set
     */
    public EvaluationResult setPatternThreshold(double patternThreshold) {
    
        this.patternThreshold = patternThreshold;
        return this;
    }
    
    /**
     * @return the tripleTreshold
     */
    public double getTripleTreshold() {
    
        return tripleTreshold;
    }
    
    /**
     * @param tripleTreshold the tripleTreshold to set
     */
    public EvaluationResult setTripleTreshold(double tripleTreshold) {
    
        this.tripleTreshold = tripleTreshold;
        return this;
    }
    
    /**
     * @return the contextLookAhead
     */
    public double getContextLookAhead() {
    
        return contextLookAhead;
    }
    
    /**
     * @param contextLookAhead the contextLookAhead to set
     */
    public EvaluationResult setContextLookAhead(double contextLookAhead) {
    
        this.contextLookAhead = contextLookAhead;
        return this;
    }
    
    /**
     * @return the availableTriples
     */
    public int getAvailableTriples() {

        return availableTriples;
    }

    /**
     * @param availableTriples the availableTriples to set
     */
    public EvaluationResult setAvailableTriples(int availableTriples) {

        this.availableTriples = availableTriples;
        return this;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        DecimalFormat decimalFormat = new DecimalFormat("0.000");
        NumberFormat integerFormat = new DecimalFormat("0000");
        
        StringBuilder builder = new StringBuilder();
        builder.append("precision: ");
        builder.append(decimalFormat.format(precision));
        builder.append("\trecall: ");
        builder.append(decimalFormat.format(recall));
        builder.append("\tfMeasure: ");
        builder.append(decimalFormat.format(fMeasure));
        builder.append("\t\tpatternThreshold: ");
        builder.append(decimalFormat.format(patternThreshold));
        builder.append("\t\ttripleTreshold: ");
        builder.append(decimalFormat.format(tripleTreshold));
        builder.append("\tcontextLookAhead: ");
        builder.append(integerFormat.format(contextLookAhead));
        builder.append("\tfoundTriples: ");
        builder.append(integerFormat.format(foundTriples));
        builder.append("\tavailableTriples: ");
        builder.append(integerFormat.format(availableTriples));
        return builder.toString();
    }
}
