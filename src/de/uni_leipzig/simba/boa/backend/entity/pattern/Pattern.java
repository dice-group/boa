package de.uni_leipzig.simba.boa.backend.entity.pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;

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

//	private Set<Triple> learnedFromTriples;
	
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
	private Double confidence = -1D;
	
	/**
	 * 
	 */
	private Double globalConfidence = -1D;

	/**
	 * 
	 */
	private Double support = -1D;
	
	/**
	 * 
	 */
	private Double typicity = -1D;
	
	/**
	 * 
	 */
	private Double specificity = -1D;

	/**
	 * 
	 */
	private double tempConfidence = -1D;
	
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

	public Pattern(){
		
		this.confidence = -1D;
		this.learnedFrom = new HashMap<String,Integer>();
		this.patternMapping = null;
	}
	
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
		this.useForPatternEvaluation = true;
		this.luceneDocIds = "";
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

		StringBuilder builder = new StringBuilder();
		builder.append("Pattern [naturalLanguageRepresentation=");
		builder.append(naturalLanguageRepresentation);
		builder.append(", numberOfOccurrences=");
		builder.append(numberOfOccurrences);
		builder.append(", useForPatternEvaluation=");
		builder.append(useForPatternEvaluation);
		builder.append(", patternMapping=");
		builder.append(patternMapping.getId());
		builder.append(", learnedFrom=");
		builder.append(learnedFrom);
		builder.append(", luceneDocIds=");
		builder.append(luceneDocIds);
		builder.append(", confidence=");
		builder.append(confidence);
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
	@ManyToOne(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
	//@JoinColumn(name = "pattern_mapping_id")//, updatable = false, insertable = false, nullable=true)
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
		System.out.println( this.luceneDocIds);
		
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
	 * @return the support
	 */
	public Double getSupport() {
	
		return support;
	}
	
	/**
	 * @param support the support to set
	 */
	public void setSupport(Double support) {
	
		this.support = support;
	}
	
	/**
	 * @return the typicity
	 */
	public Double getTypicity() {
	
		return typicity;
	}

	/**
	 * @param typicity the typicity to set
	 */
	public void setTypicity(Double typicity) {
	
		this.typicity = typicity;
	}

	/**
	 * @return the specificity
	 */
	public Double getSpecificity() {
	
		return specificity;
	}

	/**
	 * @param specificity the specificity to set
	 */
	public void setSpecificity(Double specificity) {
	
		this.specificity = specificity;
	}


	public double retrieveTempConfidence() {

		return this.tempConfidence ;
	}

	public void updateTempConfidence(double tempConfidence) {

		this.tempConfidence = tempConfidence;
	}	


	/**
	 * @param globalConfidence the globalConfidence to set
	 */
	public void setGlobalConfidence(Double globalConfidence) {

		this.globalConfidence = globalConfidence;
	}


	/**
	 * @return the globalConfidence
	 */
	public Double getGlobalConfidence() {

		return globalConfidence;
	}


//	/**
//	 * @param learnedTriple the learnedTriple to set
//	 */
//	public void setLearnedFromTriples(Set<Triple> learnedFromTriples) {
//
//		this.learnedFromTriples = learnedFromTriples;
//	}


	/**
	 * @return the learnedTriple
	 */
//	@OneToMany(fetch = FetchType.EAGER, mappedBy="id")
//	public Set<Triple> getLearnedFromTriples() {
//
//		return learnedFromTriples;
//	}
}

//@CollectionOfElements(fetch = FetchType.EAGER)
//@JoinTable(name = "Pattern_naturalLanguageRepresentationByLanguage", joinColumns = @JoinColumn(name = "id"))
//@org.hibernate.annotations.MapKey(columns = {@Column(name = "naturalLanguageRepresentationByLanguage_KEY")})
//@Column(name = "naturalLanguageRepresentation", length=1024)
