package de.uni_leipzig.simba.boa.backend.entity.pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;

/**
 * 
 * @author Daniel Gerber
 */
@Entity
@Table(name="pattern_mapping") // uniqueConstraints = {@UniqueConstraint(columnNames={"uri"})} 
public class PatternMapping extends de.uni_leipzig.simba.boa.backend.persistance.Entity {
	
	/**
	 * 
	 */
	private Map<Integer,Pattern> patternMap;
	
	/**
	 * 
	 */
	private Property property;
	
	/**
	 * 
	 */
	private List<Pattern> patterns;
	
	/**
	 * default constructor needed for hibernate
	 */
	public PatternMapping(){
		
		// needed for hibernate
	}
	
	/**
	 * 
	 */
	public PatternMapping(String uri, String label, String domain, String range) {
		
		this.property	= new Property(uri, label, range, domain);
		this.patterns	= new ArrayList<Pattern>();
		this.patternMap	= new HashMap<Integer,Pattern>();
	}

	/**
	 * Creates a new pattern mapping with the specified property as uri
	 * and an empty list for patterns.
	 * 
	 * @param property
	 */
	public PatternMapping(Property property) {

		this.property = property;
		this.patterns = new ArrayList<Pattern>();
		this.patternMap	= new HashMap<Integer,Pattern>();
	}

	/**
	 * @return the patterns
	 */
	@OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinColumn(name = "patternMapping_id", nullable = true, updatable = true, insertable = true)
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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((patterns == null) ? 0 : patterns.hashCode());
		result = prime * result + ((property == null) ? 0 : property.hashCode());
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
		if (property == null) {
			if (other.property != null)
				return false;
		}
		else
			if (!property.equals(other.property))
				return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("PatternMapping [property=");
		builder.append(property.getUri());
		builder.append(", patternsSize=");
		builder.append(patterns.size());
		builder.append("]");
		return builder.toString();
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

	/**
	 * @param property the property to set
	 */
	public void setProperty(Property property) {

		this.property = property;
	}

	/**
	 * @return the property
	 */
	@ManyToOne(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinColumn(name="property_id")
	public Property getProperty() {

		return property;
	}
}
