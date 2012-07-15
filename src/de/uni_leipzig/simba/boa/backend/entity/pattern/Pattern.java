/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.entity.pattern;

import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl.Feature;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;


/**
 * @author gerb
 *
 */
public interface Pattern {

    /**
     * @return the naturalLanguageRepresentation
     */
    public String getNaturalLanguageRepresentation();
    
    /**
     * @return the NLR with ?D? and ?R?
     */
    public String getNaturalLanguageRepresentationWithoutVariables();
    
    /**
     * @return the numberOfOccurrences
     */
    public Integer getNumberOfOccurrences();

    /**
     * @param numberOfOccurrences the numberOfOccurrences to set
     */
    public void setNumberOfOccurrences(Integer numberOfOccurrences);

    /**
     * increases the number of occurrences for this pattern by one
     */
    public void increaseNumberOfOccurrences();
    
    /**
     * @param useForPatternEvaluation the useForPatternEvaluation to set
     */
    public void setUseForPatternEvaluation(Boolean useForPatternEvaluation);

    /**
     * @return the useForPatternEvaluation
     */
    public Boolean isUseForPatternEvaluation();
    
    /**
     * @param foundInIteration the foundInIteration to set
     */
    public void setFoundInIteration(Integer foundInIteration);

    /**
     * @return the foundInIteration
     */
    public Integer getFoundInIteration();
    
    /**
     * @param posTaggedString the posTaggedString to set
     */
    public void setPosTaggedString(String posTaggedString);

    /**
     * @return the posTaggedString
     */
    public String getPosTaggedString();

    /**
     * @param patternMappings the patternMappings to set
     */
    public void setPatternMappings(List<PatternMapping> patternMappings);

    /**
     * @return the patternMappings
     */
    public List<PatternMapping> getPatternMappings();

    /**
     * @param currentMapping adds a mapping for this pattern
     */
    public void addPatternMapping(PatternMapping currentMapping);
    
    /**
     * @param learnedFrom the learnedFrom to set
     */
    public void setLearnedFrom(Map<String,Integer> learnedFrom);

    /**
     * @return the learnedFrom
     */
    public Map<String,Integer> getLearnedFrom();
    
    /**
     * 
     * @param label
     */
    public void addLearnedFrom(String label);

    /**
     * 
     * @return
     */
    public int retrieveMaxLearnedFrom();
    
    /**
     * @return the number from how many triples this pattern has been learned from
     */
    public int retrieveCountLearnedFrom();

    /**
     * 
     * @return
     */
    public Double getScore();

    /**
     * 
     * @param confidence
     */
    public void setScore(Double confidence);
    
    /**
     * @return the generalizedPattern
     */
    public String getGeneralizedPattern();
    
    /**
     * @param generalizedPattern the generalizedPattern to set
     */
    public void setGeneralizedPattern(String generalizedPattern);
    
    /**
     * @return the maxLearnedFrom
     */
    public int getMaxLearnedFrom();
    
    /**
     * 
     * @return
     */
    public int getLearnedFromPairs();
    
    /**
     * 
     * @return
     */
    public boolean isDomainFirst();
    
    /**
     * 
     * @return
     */
    public Map<Feature,Double> getFeatures();
    
    /**
     * 
     * @param features
     */
    public void setFeatures(Map<Feature,Double> features);
    
    /**
     * @return the foundInSentences
     */
    public Set<Integer> getFoundInSentences() ;

    /**
     * @param foundInSentences the foundInSentences to set
     */
    public void setFoundInSentences(Set<Integer> foundInSentences);
    
    /**
     * 
     * @param mapping
     * @return
     */
    public List<Double> buildNormalizedFeatureVector(PatternMapping mapping);

    /**
     * 
     * @param string
     */
    public void setNaturalLanguageRepresentation(String string);
    
}
