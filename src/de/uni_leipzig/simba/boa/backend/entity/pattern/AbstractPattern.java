package de.uni_leipzig.simba.boa.backend.entity.pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.FeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureHelper;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl.Feature;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;

/**
 * 
 * @author Daniel Gerber
 */
@Entity
@Table(name="pattern")
public abstract class AbstractPattern extends de.uni_leipzig.simba.boa.backend.entity.Entity implements Pattern {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2696752169065166358L;

	/**
	 * 
	 */
	protected String naturalLanguageRepresentation;
	
	/**
	 * 
	 */
	protected Integer numberOfOccurrences;
	
	/**
	 * 
	 */
	protected Boolean useForPatternEvaluation;

	/**
	 * 
	 */
	protected String posTaggedString;
	
	/**
	 * 
	 */
	protected String generalizedPattern;
	
	/**
	 * 
	 */
	protected Integer foundInIteration;
	
	/**
	 * 
	 */
	protected List<PatternMapping> patternMappings;

	/**
	 * 
	 */
	protected Map<String,Integer> learnedFrom;
	
	/**
	 * 
	 */
	protected Set<Integer> foundInSentences;
	
	/**
	 * 
	 */
	protected Double score = 0D;
	
	/**
	 * 
	 */
	protected Map<Feature, Double> features;
	
	/**
	 * @param naturalLanguageRepresentation the naturalLanguageRepresentation to set
	 */
	public void setNaturalLanguageRepresentation(String naturalLanguageRepresentation) {
	
		this.naturalLanguageRepresentation = naturalLanguageRepresentation;
	}
	
	public AbstractPattern(){
		
	    this.learnedFrom = new HashMap<String,Integer>();
        this.patternMappings = new ArrayList<PatternMapping>();
        this.numberOfOccurrences = 1;
        this.useForPatternEvaluation = true;
        this.setFoundInSentences(new HashSet<Integer>());
        this.features = new HashMap<Feature,Double>();
	}
	
	/**
	 * Creates a Pattern with the specified pattern string and sets
	 * the number of occurrences to one. 
	 * 
	 * @param patternString
	 */
	public AbstractPattern(String patternString) {

		this.naturalLanguageRepresentation = patternString;
		this.learnedFrom = new HashMap<String,Integer>();
		this.patternMappings = new ArrayList<PatternMapping>();
		this.numberOfOccurrences = 1;
		this.useForPatternEvaluation = true;
		this.setFoundInSentences(new HashSet<Integer>());
		this.features = new HashMap<Feature,Double>();
	}

	/**
	 * @return the naturalLanguageRepresentation
	 */
	@Basic
	public String getNaturalLanguageRepresentation() {
	
		return this.naturalLanguageRepresentation;
	}
	
	/**
	 * @return the numberOfOccurrences
	 */
	@Basic
	public Integer getNumberOfOccurrences() {
	
		return numberOfOccurrences;
	}

	/**
	 * @param numberOfOccurrences the numberOfOccurrences to set
	 */
	public void setNumberOfOccurrences(Integer numberOfOccurrences) {
	
		this.numberOfOccurrences = numberOfOccurrences;
	}
	

	/**
	 * increases the number of occurrences for this pattern by one
	 */
	public void increaseNumberOfOccurrences() {

		this.numberOfOccurrences++;
	}

	/**
	 * @param useForPatternEvaluation the useForPatternEvaluation to set
	 */
	public void setUseForPatternEvaluation(Boolean useForPatternEvaluation) {

		this.useForPatternEvaluation = useForPatternEvaluation;
	}

	/**
	 * @return the useForPatternEvaluation
	 */
	@Basic
	public Boolean isUseForPatternEvaluation() {

		return useForPatternEvaluation;
	}
	
	/**
	 * @param foundInIteration the foundInIteration to set
	 */
	public void setFoundInIteration(Integer foundInIteration) {

		this.foundInIteration = foundInIteration;
	}


	/**
	 * @return the foundInIteration
	 */
	@Basic
	public Integer getFoundInIteration() {

		return foundInIteration;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("Pattern [naturalLanguageRepresentation=");
		builder.append(naturalLanguageRepresentation);
		builder.append(", score=");
		builder.append(score);
		builder.append(", features=");
        builder.append(features);
		builder.append("]");
		return builder.toString();
	}

	/* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + ((naturalLanguageRepresentation == null) ? 0 : naturalLanguageRepresentation.hashCode());
        result = prime * result + ((patternMappings == null) ? 0 : patternMappings.hashCode());
        return result;
    }


	/* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractPattern other = (AbstractPattern) obj;
        if (naturalLanguageRepresentation == null) {
            if (other.naturalLanguageRepresentation != null)
                return false;
        }
        else
            if (!naturalLanguageRepresentation.equals(other.naturalLanguageRepresentation))
                return false;
        if (patternMappings == null) {
            if (other.patternMappings != null)
                return false;
        }
        else
            if (!patternMappings.equals(other.patternMappings))
                return false;
        return true;
    }

    /**
	 * @param posTaggedString the posTaggedString to set
	 */
	public void setPosTaggedString(String posTaggedString) {

		this.posTaggedString = posTaggedString;
	}


	/**
	 * @return the posTaggedString
	 */
	public String getPosTaggedString() {

		return posTaggedString;
	}


	/**
	 * @param patternMappings the patternMappings to set
	 */
	public void setPatternMappings(List<PatternMapping> patternMappings) {

		this.patternMappings = patternMappings;
	}


	/**
	 * @return the patternMappings
	 */
	//@ManyToMany(/*fetch = FetchType.EAGER,*/ cascade=CascadeType.ALL)
	@ManyToMany(mappedBy="patterns", fetch = FetchType.EAGER)
	public List<PatternMapping> getPatternMappings() {

		return patternMappings;
	}

	/**
	 * @param currentMapping adds a mapping for this pattern
	 */
	public void addPatternMapping(PatternMapping currentMapping) {

		// only add the pattern mapping if its not already there
		if ( !this.patternMappings.contains(currentMapping) ) this.patternMappings.add(currentMapping);
	}

	/**
	 * @param learnedFrom the learnedFrom to set
	 */
	public void setLearnedFrom(Map<String,Integer> learnedFrom) {

		this.learnedFrom = learnedFrom;
	}

	/**
	 * @return the learnedFrom
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	@JoinTable(name="pattern_learned_from")
	public Map<String,Integer> getLearnedFrom() {

		return learnedFrom;
	}
	
	/**
	 * 
	 * @param label
	 */
	public void addLearnedFrom(String label) {
		
		if ( this.learnedFrom.containsKey(label) ) this.learnedFrom.put(label, this.learnedFrom.get(label) + 1);
		else {
			this.learnedFrom.put(label, 1);
		}
	}

	public int retrieveMaxLearnedFrom() {

		int maximum = 0;
		for ( Entry<String,Integer> entry : this.learnedFrom.entrySet() ) {
			
			maximum = Math.max(entry.getValue(), maximum);
		}
		return maximum;
	}
	
	/**
	 * @return the number from how many triples this pattern has been learned from
	 */
	public int retrieveCountLearnedFrom(){
		
		return this.learnedFrom.size();
	}

	@Basic
	public Double getScore() {

		return this.score;
	}

	public void setScore(Double score) {

		this.score = score;
	}

	/**
	 * @return the generalizedPattern
	 */
	@Basic
	public String getGeneralizedPattern() {

		return generalizedPattern;
	}

	/**
	 * @param generalizedPattern the generalizedPattern to set
	 */
	public void setGeneralizedPattern(String generalizedPattern) {

		this.generalizedPattern = generalizedPattern;
	}

	/**
	 * @return the maxLearnedFrom
	 */
	@Transient
	public int getMaxLearnedFrom() {

		int max = 0;
		for ( Map.Entry<String, Integer> entry : this.learnedFrom.entrySet()) {
			
			max = Math.max(max, entry.getValue());
		}
		return max;
	}
	
	/**
	 * 
	 * @return
	 */
	@Transient
	public int getLearnedFromPairs() {
		
		return this.getLearnedFrom().size(); 
	}
	
	/**
	 * @return true if the pattern starts with ?D?
	 */
	@Transient
	public boolean isDomainFirst() {
		
		return this.naturalLanguageRepresentation.startsWith("?D?") ? true : false;
	}

	/**
	 * @return the features
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	@JoinTable(name="pattern_features")
	public Map<Feature,Double> getFeatures() {

		return features;
	}
	
	/**
	 * 
	 * @param features
	 */
	public void setFeatures(Map<Feature,Double> features) {
		
		this.features = features;
	}
	
	/**
     * @return the foundInSentences
     */
    public Set<Integer> getFoundInSentences() {

        return foundInSentences;
    }

    /**
     * @param foundInSentences the foundInSentences to set
     */
    public void setFoundInSentences(Set<Integer> foundInSentences) {

        this.foundInSentences = foundInSentences;
    }

    public List<Double> buildNormalizedFeatureVector(PatternMapping mapping){
		
		List<Double> featureValues = new ArrayList<Double>();

		// we do only want all activated features
	    for ( FeatureExtractor featureExtractor : FeatureFactory.getInstance().getFeatureExtractorMap().values() ) {
	        
	        if ( featureExtractor.isActivated() ) {
	            
	            for ( Feature feature : featureExtractor.getHandeledFeatures() ) {
	                
	                if ( feature.getSupportedLanguages().contains(NLPediaSettings.getSystemLanguage()) ) {
	                    
	                    // exclude everything which is not activated
	                    if ( feature.isUseForPatternLearning() ) {
	                        
	                        // non zero to one values have to be normalized
	                        if ( !feature.isZeroToOneValue() ) {
	                            
	                            Double maximum = 0D;
	                            // take every mapping into account to find the maximum value
	                            if ( feature.isNormalizeGlobaly() ) {

	                                maximum = FeatureHelper.getGlobalMaximum(feature);
	                            }
	                            // only use the current mapping to find the maximum
	                            else {
	                                
	                                maximum = FeatureHelper.getLocalMaximum(mapping, feature);
	                            }
	                            Double featureValue =  this.features.get(feature) / maximum;
	                            featureValue = featureValue.isNaN() || featureValue.isInfinite() ? 0D : featureValue;
	                            featureValues.add(featureValue);
	                        }
	                        // we dont need to normalize a 0-1 value
	                        else {
	                            
	                            featureValues.add(this.features.get(feature));
	                        }
	                    }
	                }
	            }
	        }
	    }
		return featureValues;
	}
}