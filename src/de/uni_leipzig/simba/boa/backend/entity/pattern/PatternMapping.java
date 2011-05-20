package de.uni_leipzig.simba.boa.backend.entity.pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * 
 * @author Daniel Gerber
 */
@Entity
@Table(name="pattern_mapping") // uniqueConstraints = {@UniqueConstraint(columnNames={"uri"})} 
public class PatternMapping extends de.uni_leipzig.simba.boa.backend.persistance.Entity {
	
	private Map<Integer,Pattern> patternMap;
	
	/**
	 * 
	 */
	private String uri;
	
	/**
	 * 
	 */
	private List<Pattern> patterns;
	
	/**
	 * 
	 */
	private String rdfsDomain;
	
	/**
	 * 
	 */
	private String rdfsRange;
	
	/**
	 * 
	 */
	public PatternMapping() {
		
		this.uri		= new String();
		this.patterns	= new ArrayList<Pattern>();
		this.patternMap	= new HashMap<Integer,Pattern>();
	}

	/**
	 * Creates a new pattern mapping with the specified property as uri
	 * and an empty list for patterns.
	 * 
	 * @param property
	 */
	public PatternMapping(String property) {

		this.uri = property;
		this.patterns = new ArrayList<Pattern>();
		this.patternMap	= new HashMap<Integer,Pattern>();
	}

	/**
	 * @return the uri
	 */
	@Basic
	public String getUri() {
	
		return uri;
	}
	
	/**
	 * @param uri the uri to set
	 */
	public void setUri(String uri) {
	
		this.uri = uri;
	}

	/**
	 * @return the patterns
	 */
	@OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinColumn(name = "pattern_mapping_id", nullable = false, updatable = false, insertable = false)
	public List<Pattern> getPatterns() {
	
		return patterns;
	}
	
	/**
	 * @param patterns the patterns to set
	 */
	public void setPatterns(List<Pattern> patterns) {
	
		this.patterns = patterns;
	}
	
	/**
	 * 
	 * @param pattern
	 */
	public PatternMapping addPattern(Pattern pattern) {
		
		this.patterns.add(pattern);
		this.patternMap.put(pattern.getNaturalLanguageRepresentation().hashCode(), pattern);
		return this;
	}
	

	/**
	 * 
	 * @param rdfsRange
	 */
	public void setRdfsRange(String range) {

		this.rdfsRange = range;
	}

	/**
	 * 
	 * @param rdfsDomain
	 */
	public void setRdfsDomain(String domain) {

		this.rdfsDomain = domain;
	}
	
	/**
	 * @return the rdfsDomain
	 */
	@Basic
	public String getRdfsDomain() {
	
		return rdfsDomain;
	}

	
	/**
	 * @return the rdfsRange
	 */
	@Basic
	public String getRdfsRange() {
	
		return rdfsRange;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return "PatternMapping [id=" + id + ", uri=" + uri + ", patterns=" + patterns + ", rdfsDomain=" + rdfsDomain + ", rdfsRange=" + rdfsRange + "]";
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((patterns == null) ? 0 : patterns.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
		PatternMapping other = (PatternMapping) obj;
		if (patterns == null) {
			if (other.patterns != null)
				return false;
		}
		else
			if (!patterns.equals(other.patterns))
				return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		}
		else
			if (!uri.equals(other.uri))
				return false;
		return true;
	}

	/**
	 * This method returns the pattern with the specified natural language representation
	 * if this pattern is present in the current list of patterns for this mapping, else it 
	 * will return null. 
	 * 
	 * @param naturalLanguageRepresentation - the natural language representation of the pattern to search for
	 * @return the pattern or null if no such pattern was found
	 */
	public Pattern getPatternByNaturalLanguageRepresentation(int patternHashCode) { 
		
		return this.patternMap.get(patternHashCode);
	}
}
