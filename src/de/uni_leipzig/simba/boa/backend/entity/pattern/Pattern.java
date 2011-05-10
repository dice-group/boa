package de.uni_leipzig.simba.boa.backend.entity.pattern;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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
	private Double confidence;
	
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
	private String nerTaggedString;
	
	/**
	 * 
	 */
	private String posTaggedString;
	
	/**
	 * 
	 */
	private Integer foundInIteration;
	
	/**
	 * 
	 */
	private PatternMapping patternMapping;
	
	/**
	 * 
	 */
	private Map<String,Integer> learnedFrom;
	
	/**
	 * @param naturalLanguageRepresentation the naturalLanguageRepresentation to set
	 */
	public void setNaturalLanguageRepresentation(String naturalLanguageRepresentation) {
	
		this.naturalLanguageRepresentation = naturalLanguageRepresentation;
	}

	
	/**
	 * @param nerTaggedString the nerTaggedString to set
	 */
	public void setNerTaggedString(String nerTaggedString) {
	
		this.nerTaggedString = nerTaggedString;
	}

	public Pattern(){}
	
	/**
	 * Creates a Pattern with the specified pattern string and sets
	 * the number of occurrences to one. 
	 * 
	 * @param patternString
	 */
	public Pattern(String patternString, String generalizedPatternString) {

		this.naturalLanguageRepresentation = patternString;
		this.nerTaggedString = generalizedPatternString;
		this.learnedFrom = new HashMap<String,Integer>();
		this.numberOfOccurrences = 1;
		this.confidence = -1D;
	}

	/**
	 * @return the naturalLanguageRepresentation
	 */
	@Basic
	public String getNaturalLanguageRepresentation() {
	
		return this.naturalLanguageRepresentation;
	}
	
	/**
	 * @return the confidence
	 */
	@Basic
	public Double getConfidence() {
	
		return this.confidence;
	}
	
	/**
	 * @param confidence the confidence to set
	 */
	public void setConfidence(Double confidence) {
	
		this.confidence = confidence;
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
	 * @return the nerTaggedString
	 */
	@Basic
	@Column(length=512)
	public String getNerTaggedString() {

		return nerTaggedString;
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

		return "Pattern [id=" + id + ", naturalLanguageRepresentation=" + naturalLanguageRepresentation + ", numberOfOccurrences=" + numberOfOccurrences
				+ ", useForPatternEvaluation=" + useForPatternEvaluation + ", confidence=" + confidence + ", nerTaggedString=" + nerTaggedString + ", posTaggedString=" + posTaggedString
				+ ", foundInIteration=" + foundInIteration + "]\n";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((naturalLanguageRepresentation == null) ? 0 : naturalLanguageRepresentation.hashCode());
		result = prime * result + ((patternMapping == null) ? 0 : patternMapping.hashCode());
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
		if (patternMapping == null) {
			if (other.patternMapping != null)
				return false;
		}
		else
			if (!patternMapping.equals(other.patternMapping))
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
	 * @param patternMapping the patternMapping to set
	 */
	public void setPatternMapping(PatternMapping patternMapping) {

		this.patternMapping = patternMapping;
	}


	/**
	 * @return the patternMapping
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "pattern_mapping_id", updatable = false, insertable = false, nullable=false)
	public PatternMapping getPatternMapping() {

		return patternMapping;
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
}

//@CollectionOfElements(fetch = FetchType.EAGER)
//@JoinTable(name = "Pattern_naturalLanguageRepresentationByLanguage", joinColumns = @JoinColumn(name = "id"))
//@org.hibernate.annotations.MapKey(columns = {@Column(name = "naturalLanguageRepresentationByLanguage_KEY")})
//@Column(name = "naturalLanguageRepresentation", length=1024)
