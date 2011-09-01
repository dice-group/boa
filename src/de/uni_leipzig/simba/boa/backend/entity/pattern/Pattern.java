package de.uni_leipzig.simba.boa.backend.entity.pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
	private Double globalConfidence = -1D;
	
	/**
	 * 
	 */
	private Double confidence = -1D;
	
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
	private Double reverb = -1D;
	
	/**
	 * 
	 */
	private Double specificity = -1D;
	
	/**
	 * 
	 */
	private Double similarity = -1D;
	
	/**
	 * 
	 */
	private Map<Integer,Double> confidences = new HashMap<Integer,Double>();
	
	/**
	 * 
	 */
	private Map<Integer,Double> supports = new HashMap<Integer,Double>();
	
	/**
	 * 
	 */
	private Map<Integer,Double> typicities = new HashMap<Integer,Double>();
	
	/**
	 * 
	 */
	private Map<Integer,Double> specificities = new HashMap<Integer,Double>();
	
	/**
	 * 
	 */
	private Map<Integer,Double> reverbs = new HashMap<Integer,Double>();

	/**
	 * @param naturalLanguageRepresentation the naturalLanguageRepresentation to set
	 */
	public void setNaturalLanguageRepresentation(String naturalLanguageRepresentation) {
	
		this.naturalLanguageRepresentation = naturalLanguageRepresentation;
	}

	public Pattern(){
		
		this.learnedFrom = new HashMap<String,Integer>();
		this.patternMappings = new ArrayList<PatternMapping>();
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
	public String retrieveNaturalLanguageRepresentationWithoutVariables() {
		
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
		builder.append(", confidence=");
		builder.append(confidences);
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

	/**
	 * @param iteration number of the iteration
	 * @return the confidence for the specified iteration
	 */
	public Double getConfidenceForIteration(Integer iteration){
		
		Double confidence = -1D;
		if ( this.confidences.containsKey(iteration) ) return this.confidences.get(iteration);
		return confidence;
	}
	
	/**
	 * @param iteration the iteration in which the support was calculated
	 * @param support the support value
	 */
	public void setConfidenceForIteration(Integer iteration, Double confidence) {
		
		this.confidences.put(iteration, confidence);
	}

	@ElementCollection(fetch = FetchType.EAGER)
	@JoinTable(name="pattern_confidences")
	public Map<Integer,Double> getConfidences() {

		return this.confidences;
	}

	public void setConfidences(Map<Integer,Double> confidences) {

		this.confidences = confidences;
	}

	/**
	 * @return the support
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	@JoinTable(name="pattern_supports")
	public Map<Integer,Double> getSupports() {
	
		return this.supports;
	}
	
	/**
	 * @param support the support to set
	 */
	public void setSupports(Map<Integer,Double> supports) {
	
		this.supports = supports;
	}
	
	/**
	 * @param iteration number of the iteration
	 * @return the support for the specified iteration
	 */
	public Double getSupportForIteration(Integer iteration){
		
		Double confidence = -1D;
		if ( this.supports.containsKey(iteration) ) return this.supports.get(iteration);
		return confidence;
	}
	
	/**
	 * @param iteration the iteration in which the support was calculated
	 * @param support the support value
	 */
	public void setSupportForIteration(Integer iteration, Double support) {
		
		this.supports.put(iteration, support);
	}
	
	/**
	 * @return the typicity
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	@JoinTable(name="pattern_typicities")
	public Map<Integer,Double> getTypicities() {
	
		return this.typicities;
	}

	/**
	 * @param typicity the typicity to set
	 */
	public void setTypicities(Map<Integer,Double> typicities) {
	
		this.typicities = typicities;
	}
	
	/**
	 * @param iteration number of the iteration
	 * @return the typicity for the specified iteration
	 */
	public Double getTypicityForIteration(Integer iteration){
		
		Double confidence = -1D;
		if ( this.typicities.containsKey(iteration) ) return this.typicities.get(iteration);
		return confidence;
	}
	
	/**
	 * @param iteration the iteration in which the typicity was calculated
	 * @param typicity the typicity value
	 */
	public void setTypicityForIteration(Integer iteration, Double typicity) {
		
		this.typicities.put(iteration, typicity);
	}

	/**
	 * @return the specificity
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	@JoinTable(name="pattern_specificities")
	public Map<Integer,Double> getSpecificities() {
	
		return this.specificities;
	}

	/**
	 * @param specificity the specificity to set
	 */
	public void setSpecificities(Map<Integer,Double> specificities) {
	
		this.specificities = specificities;
	}
	
	/**
	 * @param iteration number of the iteration
	 * @return the specificity for the specified iteration
	 */
	public Double getSpecificityForIteration(Integer iteration){
		
		Double confidence = -1D;
		if ( this.specificities.containsKey(iteration) ) return this.specificities.get(iteration);
		return confidence;
	}
	
	/**
	 * @param iteration, the iteration in which the specificity was calculated
	 * @param specificity, the specificity value
	 */
	public void setSpecificityForIteration(Integer iteration, Double specificity) {
		
		this.specificities.put(iteration, specificity);
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

	public Double retrieveMostRecentConfidence(){
		
		int maxIteration = 0;
		for ( Integer i : this.confidences.keySet() ) maxIteration = Math.max(maxIteration, i);
		
		if ( maxIteration > 0 ) return this.confidences.get(maxIteration);
		else return -1D;
	}
	
	public Double retrieveMostRecentSupport(){
			
		int maxIteration = 0;
		for ( Integer i : this.supports.keySet() ) maxIteration = Math.max(maxIteration, i);
		
		if ( maxIteration > 0 ) return this.supports.get(maxIteration);
		else return -1D;
	}

	public Double retrieveMostRecentSpecificity(){
		
		int maxIteration = 0;
		for ( Integer i : this.specificities.keySet() ) maxIteration = Math.max(maxIteration, i);
		
		if ( maxIteration > 0 ) return this.specificities.get(maxIteration);
		else return -1D;
	}
	
	public Double retrieveMostRecentTypicity(){
		
		int maxIteration = 0;
		for ( Integer i : this.typicities.keySet() ) maxIteration = Math.max(maxIteration, i);
		
		if ( maxIteration > 0 ) return this.typicities.get(maxIteration);
		else return -1D;
	}
	
	public Double retrieveMostRecentReverb(){
		
		int maxIteration = 0;
		for ( Integer i : this.reverbs.keySet() ) maxIteration = Math.max(maxIteration, i);
		
		if ( maxIteration > 0 ) return this.reverbs.get(maxIteration);
		else return -1D;
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

	/**
	 * @param similarity the similarity to set
	 */
	public void setSimilarity(Double similarity) {

		this.similarity = similarity;
	}

	/**
	 * @return the similarity
	 */
	@Basic
	public Double getSimilarity() {

		return similarity;
	}

	/**
	 * @return the reverbs
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	@JoinTable(name="pattern_reverbs")
	public Map<Integer,Double> getReverbs() {

		return reverbs;
	}

	/**
	 * @param reverbs the reverbs to set
	 */
	public void setReverbs(Map<Integer,Double> reverbs) {

		this.reverbs = reverbs;
	}

	/**
	 * @param iteration number of the iteration
	 * @return the reverb score for the specified iteration
	 */
	public Double getReverbForIteration(Integer iteration){
		
		Double reverbs = -1D;
		if ( this.reverbs.containsKey(iteration) ) return this.reverbs.get(iteration);
		return reverbs;
	}

	/**
	 * @return the reverb
	 */
	@Basic
	public Double getReverb() {

		return reverb;
	}

	/**
	 * @param reverb the reverb to set
	 */
	public void setReverb(Double reverb) {

		this.reverb = reverb;
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
	 * @return the learnedTriple
	 */
//	@OneToMany(/*fetch = FetchType.EAGER*/, mappedBy="id")
//	public Set<Triple> getLearnedFromTriples() {
//
//		return learnedFromTriples;
//	}
}

//@CollectionOfElements(/*fetch = FetchType.EAGER*/)
//@JoinTable(name = "Pattern_naturalLanguageRepresentationByLanguage", joinColumns = @JoinColumn(name = "id"))
//@org.hibernate.annotations.MapKey(columns = {@Column(name = "naturalLanguageRepresentationByLanguage_KEY")})
//@Column(name = "naturalLanguageRepresentation", length=1024)
