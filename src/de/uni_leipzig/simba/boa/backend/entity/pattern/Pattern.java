package de.uni_leipzig.simba.boa.backend.entity.pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.engine.Cascade;

import de.danielgerber.format.OutputFormatter;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Language;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureHelper;

/**
 * 
 * @author Daniel Gerber
 */
@Entity
@Table(name="pattern")
public class Pattern extends de.uni_leipzig.simba.boa.backend.persistance.Entity {

	/**
	 * 
	 */
	private String naturalLanguageRepresentation;
	
	/**
	 * 
	 */
	private Integer numberOfOccurrences;
	
	/**
	 * 
	 */
	private Boolean useForPatternEvaluation;

	/**
	 * 
	 */
	private String posTaggedString;
	
	/**
	 * 
	 */
	private String generalizedPattern;
	
	/**
	 * 
	 */
	private Integer foundInIteration;
	
	/**
	 * 
	 */
	private List<PatternMapping> patternMappings;

	/**
	 * 
	 */
	private Map<String,Integer> learnedFrom;
	
	/**
	 * 
	 */
	private String luceneDocIds;
	
	/**
	 * 
	 */
	private Double confidence = 0D;
	
	/**
	 * 
	 */
	private Map<Feature, Double> features;
	
	/**
	 * 
	 */
	private static final String FEATURE_FILE_COLUMN_SEPARATOR = "\t";
	
	/**
	 * @param naturalLanguageRepresentation the naturalLanguageRepresentation to set
	 */
	public void setNaturalLanguageRepresentation(String naturalLanguageRepresentation) {
	
		this.naturalLanguageRepresentation = naturalLanguageRepresentation;
	}
	
	public Pattern(){
		
		this.learnedFrom = new HashMap<String,Integer>();
		this.patternMappings = new ArrayList<PatternMapping>();
		this.features = new HashMap<Feature,Double>();
	}
	
	/**
	 * Creates a Pattern with the specified pattern string and sets
	 * the number of occurrences to one. 
	 * 
	 * @param patternString
	 */
	public Pattern(String patternString) {

		this.naturalLanguageRepresentation = patternString;
		this.learnedFrom = new HashMap<String,Integer>();
		this.patternMappings = new ArrayList<PatternMapping>();
		this.numberOfOccurrences = 1;
		this.useForPatternEvaluation = true;
		this.luceneDocIds = "";
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
	 * @return the NLR with ?D? and ?R?
	 */
	@Transient
	public String getNaturalLanguageRepresentationWithoutVariables() {
		
		return this.naturalLanguageRepresentation.substring(0, this.naturalLanguageRepresentation.length() - 3).substring(3).trim();
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
		builder.append(", numberOfOccurrences=");
		builder.append(numberOfOccurrences);
		builder.append(", useForPatternEvaluation=");
		builder.append(useForPatternEvaluation);
		builder.append(", patternMappings=");
		builder.append(patternMappings);
		builder.append(", learnedFrom=");
		builder.append(learnedFrom);
		builder.append(", luceneDocIds=");
		builder.append(luceneDocIds);
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
		Pattern other = (Pattern) obj;
		if (naturalLanguageRepresentation == null) {
			if (other.naturalLanguageRepresentation != null)
				return false;
		}
		else
			if (!naturalLanguageRepresentation.equals(other.naturalLanguageRepresentation))
				return false;
		if (patternMappings == null) {
			if (other.patternMappings != null) {
				
				return false;
			}
		}
		else
			if (!patternMappings.equals(other.patternMappings)) {
				
				return false;
			}
				
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
	@ManyToMany(mappedBy="patterns")
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


	/**
	 * @param luceneDocIds the luceneDocIds to set
	 */
	public void setLuceneDocIds(String luceneDocIds) {

		this.luceneDocIds = luceneDocIds;
	}

	/**
	 * @return the luceneDocIds
	 */
	@Basic
	@Column(length=5012)
	public String getLuceneDocIds() {

		return luceneDocIds;
	}
	
	public void addLuceneDocIds(int id){
		
		this.luceneDocIds += "$" + id; 
	}
	
	public List<Integer> retrieveLuceneDocIdsAsList(){
		
		List<Integer> ids = new ArrayList<Integer>();
		String[] s = this.luceneDocIds.substring(1).split("\\$");
		for (String id : s) {
			
			ids.add(Integer.valueOf(id));
		}
		return ids;
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
	public Double getConfidence() {

		return this.confidence;
	}

	public void setConfidence(Double confidence) {

		this.confidence = confidence;
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
	public double getMaxLearnedFrom() {

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

//	/**
//	 * @return the featureMap
//	 */
//	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//	@JoinColumn(name="feature_map_fk")
//	public FeatureMap getFeatureMap() {
//
//		return featureMap;
//	}
//
//	/**
//	 * @param featureMap the featureMap to set
//	 */
//	public void setFeatureMap(FeatureMap featureMap) {
//
//		this.featureMap = featureMap;
//	}
	
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
	
	public String buildFeatureString(PatternMapping mapping){
		
		StringBuffer output = new StringBuffer();

		for ( Feature feature : Feature.values() ) {
			
			if ( feature.getSupportedLanguages().contains(NLPediaSettings.getInstance().getSystemLanguage()) ) {
				
				// exclude everything which is not activated
				if ( feature.useForPatternFeatureLearning() ) {
					
					// non zero to one values have to be normalized
					if ( !feature.isZeroToOneValue() ) {
						
						Double maximum = 0D;
						// take every mapping into account to find the maximum value
						if ( feature.needsGlobalNormalization() ) {
							
							maximum = FeatureHelper.calculateGlobalMaximum(feature);
						}
						// only use the current mapping to find the maximum
						else {
							
							maximum = FeatureHelper.calculateLocalMaximum(mapping, feature);
						}
						output.append(OutputFormatter.format((this.features.get(feature) / maximum), "0.00000") + FEATURE_FILE_COLUMN_SEPARATOR);
					}
					// we dont need to normalize a 0-1 value
					else {
						
						output.append(OutputFormatter.format(this.features.get(feature), "0.00000") + FEATURE_FILE_COLUMN_SEPARATOR);
					}
				}
			}
		}
		return output.toString();
	}
}