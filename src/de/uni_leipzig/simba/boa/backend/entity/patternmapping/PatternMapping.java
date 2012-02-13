package de.uni_leipzig.simba.boa.backend.entity.patternmapping;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;

/**
 * 
 * @author Daniel Gerber
 */
@Entity
@Table(name="pattern_mapping") // uniqueConstraints = {@UniqueConstraint(columnNames={"uri"})} 
public class PatternMapping extends de.uni_leipzig.simba.boa.backend.entity.Entity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6036673326860046237L;

	/**
	 * 
	 */
	private Property property;
	
	/**
	 * 
	 */
	private Set<Pattern> patterns;
	
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
		
		this.property	= new Property(uri, label, range, domain, "");
		this.patterns	= new HashSet<Pattern>();
	}

	/**
	 * Creates a new pattern mapping with the specified property as uri
	 * and an empty list for patterns.
	 * 
	 * @param property
	 */
	public PatternMapping(Property property) {

		this.property = property;
		this.patterns = new HashSet<Pattern>();
	}

	/**
	 * @return the patterns
	 */
//	@OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
//	@JoinColumn(name = "patternMapping_id", nullable = true, updatable = true, insertable = true)
	@ManyToMany(fetch = FetchType.EAGER,cascade=CascadeType.ALL)
	  @JoinTable(name = "pattern_mapping_pattern",
	    joinColumns = {
	      @JoinColumn(name="pattern_mapping_id")//, unique = true)           
	    },
	    inverseJoinColumns = {
	      @JoinColumn(name="pattern_id")
	    }
	  )
	public Set<Pattern> getPatterns() {
	
		return this.patterns;
	}
	
	/**
	 * @param patterns the patterns to set
	 */
	public void setPatterns(Set<Pattern> patterns) {
	
		this.patterns = patterns;
	}
	
	/**
	 * 
	 * @param pattern
	 */
	public PatternMapping addPattern(Pattern pattern) {
		
		this.patterns.add(pattern);
		return this;
	}
	
	public void removePattern(Pattern pattern) {
		
//		pattern = this.patternMap.remove(pattern.getNaturalLanguageRepresentation().hashCode());
//		this.patterns.remove(pattern);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
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
