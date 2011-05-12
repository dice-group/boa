package de.uni_leipzig.simba.boa.backend.entity.cluster;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.JoinColumn; 

import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.exception.NLPediaRuntimeException;

@Entity
@Table(name="cluster")
public class Cluster extends de.uni_leipzig.simba.boa.backend.persistance.Entity {

	
	private String name;
	
	private boolean accepted;
	
	private Set<Pattern> pattern;
	
	private Map<String,Double> uriAffiliationPropabilities;
	
	private int numberOfOccurrencesForAllPattern = 0;
	
	/**
	 * Creates a new cluster with an empty list and a random name.
	 */
	public Cluster() {
		
		this.setName("Cluster" + Math.random());
		this.setPattern(new HashSet<Pattern>());
		this.setUriAffiliationPropabilities(new HashMap<String,Double>());
	}
	
	/**
	 * Create a pattern cluster with an empty list of patterns 
	 * and the given name.
	 */
	public Cluster(String name){
		
		this.setUriAffiliationPropabilities(new HashMap<String,Double>());
		this.setPattern(new HashSet<Pattern>());
		this.setName(name);
	}
	
	/**
	 * Create a pattern cluster with the list of given patterns
	 * and the name.
	 */
	public Cluster(Set<Pattern> patternList, String name){
		
		this.setUriAffiliationPropabilities(new HashMap<String,Double>());
		this.setPattern(patternList);
		this.getNumberOfOccurrencesForAllUris();
		this.initAffiliationProbabilities();
		this.setName(name);
	}
	
	/**
	 * @param pattern add a pattern to this cluster
	 */
	public Cluster addPattern(Pattern pattern) {
		
		this.pattern.add(pattern);
		this.getNumberOfOccurrencesForAllUris();
		
		return this;
	}

	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(Set<Pattern> patternList) {

		this.pattern = patternList;
		this.initAffiliationProbabilities();
		this.getNumberOfOccurrencesForAllUris();
	}

	/**
	 * @return the pattern
	 */
	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "cluster_pattern",
        joinColumns = {
            @JoinColumn(name = "cluster_id")
        },
        inverseJoinColumns = {
            @JoinColumn(name = "pattern_id")
        }
    )
	public Set<Pattern> getPattern() {

		return pattern;
	}
	
	/**
	 * 
	 */
	public void calculateUriAffiliationPropabilities() {
		
		// try to initialize it
		if ( this.uriAffiliationPropabilities == null || this.uriAffiliationPropabilities.isEmpty() ) this.initAffiliationProbabilities();
		
		if ( !this.uriAffiliationPropabilities.isEmpty() ) {

			for ( String distinctUri : this.getUriAffiliationPropabilities().keySet() ) {
				
				Double probability = Double.valueOf(this.getNumberOfPatternsForUri(distinctUri)) / Double.valueOf(this.size());

				probability = (probability * ((double) this.getPatternOccurrencesForUri(distinctUri) / (double) this.numberOfOccurrencesForAllPattern));
				probability = probability * ( this.size() * this.numberOfOccurrencesForAllPattern ) / this.calculateLengthOfAllPatterns();
				
				this.uriAffiliationPropabilities.put(distinctUri, probability);
			}
		}
		else {
			
			throw new NLPediaRuntimeException("There are no patterns in this cluster! It's not possible to calculate any probabilties!");
		}
	}
	
	/**
	 * This method returns the pattern mapping uri with the highest 
	 * probability. Be aware of the fact, that this method does not 
	 * deal with uri's that have the same probability.
	 * 
	 * @return the name of the most probable uri
	 */
	public String calculateMostLikelyUri() {
		
		Double maxProbability = -1d;
		String maxUri = "";
		
		for (Entry<String,Double> entry : this.uriAffiliationPropabilities.entrySet()) {
			
			if ( entry.getValue() > maxProbability ) {
				
				maxProbability = entry.getValue();
				maxUri = entry.getKey();
			}
		}
		return maxUri;
	}
	
	/**
	 * @return the amount of entries in this cluster
	 */
	public int size(){
		
		return this.getPattern().size();
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {

		this.name = name;
	}

	/**
	 * @return the name
	 */
	@Basic
	public String getName() {

		return name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return "Cluster [id=" + id + ",\n name=" + name + ",\n accepted=" + accepted + ",\n numberOfOccurrencesForAllPattern=" + numberOfOccurrencesForAllPattern + ", uriAffiliationPropabilities="
				+ uriAffiliationPropabilities + ",\n pattern=" + pattern + "]";
	}

	/**
	 * @param uriAffiliationPropabilities the uriAffiliationPropabilities to set
	 */
	public void setUriAffiliationPropabilities(Map<String,Double> uriAffiliationPropabilities) {

		this.uriAffiliationPropabilities = uriAffiliationPropabilities;
	}

	/**
	 * @return the uriAffiliationPropabilities
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	public Map<String,Double> getUriAffiliationPropabilities() {

		return uriAffiliationPropabilities;
	}
	
	/**
	 * Go through all patterns and check if the pattern mapping has this uri
	 */
	private int getNumberOfPatternsForUri(String uri) {
		
		int i = 0;
		for ( Pattern p : this.getPattern() ) {
			
			if ( p.getPatternMapping().getUri().equals(uri) ) i++;
		}
		return i;
	}
	
	/**
	 * @param accepted the accepted to set
	 */
	public void setAccepted(boolean accepted) {

		this.accepted = accepted;
	}

	/**
	 * @return the accepted
	 */
	public boolean isAccepted() {

		return accepted;
	}
	
	/**
	 * 
	 */
	private void initAffiliationProbabilities() {
		
		if ( this.uriAffiliationPropabilities == null)  this.uriAffiliationPropabilities = new HashMap<String,Double>();			

		for ( Pattern p : this.getPattern() ) {
			
			// no uri yet in the list -> so add it with 0 as probability
			if ( !this.getUriAffiliationPropabilities().keySet().contains(p.getPatternMapping().getUri()) ) {
				
				this.uriAffiliationPropabilities.put(p.getPatternMapping().getUri(), 0d);  
			}
		}
	}
	
	private double calculateLengthOfAllPatterns(){
		
		double length = 0;
		
		for ( String uri : this.getUriAffiliationPropabilities().keySet() ) {
			
			List<Pattern> list = this.getPatternForUri(uri);
			
			double occ = 0;
			
			for ( Pattern p : list ) {
				
				occ += p.getNumberOfOccurrences();
			}
			
			length += list.size() * occ;
		}
		return length;
	}
	
	private List<Pattern> getPatternForUri(String uri) {
		
		List<Pattern> patternList = new ArrayList<Pattern>();
		for ( Pattern p : this.pattern ) {
			
			if ( p.getPatternMapping().getUri().equals(uri) ) patternList.add(p);
		}
		return patternList;
	}
	
	private void getNumberOfOccurrencesForAllUris() {
		
		this.numberOfOccurrencesForAllPattern = 0;
		for (Pattern p : this.pattern ) {
			
			this.numberOfOccurrencesForAllPattern += p.getNumberOfOccurrences();
		}
	}
	
	private int getPatternOccurrencesForUri(String uri) {
		
		int occurrences = 0;
		for ( Pattern p : this.pattern ) {
			
			if ( p.getPatternMapping().getUri().equals(uri) ) occurrences += p.getNumberOfOccurrences();
		}
		return occurrences;
	}
}
