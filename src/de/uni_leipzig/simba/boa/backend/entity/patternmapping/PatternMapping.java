package de.uni_leipzig.simba.boa.backend.entity.patternmapping;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.uni_leipzig.simba.boa.backend.entity.pattern.GeneralizedPattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;

/**
 * 
 * @author Daniel Gerber
 */
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
	private Map<String,Set<GeneralizedPattern>> patterns;
	
	/**
	 * default constructor needed for hibernate
	 */
	public PatternMapping(){
		
	    this.property   = new Property();
        this.patterns   = new TreeMap<String,Set<GeneralizedPattern>>();
	}
	
	/**
	 * Creates a new pattern mapping with the specified property as uri
	 * and an empty list for patterns.
	 * 
	 * @param property
	 */
	public PatternMapping(Property property) {

		this.property = property;
		this.patterns   = new TreeMap<String,Set<GeneralizedPattern>>();
	}

	/**
	 * @return the patterns
	 */
	public Set<Pattern> getPatterns() {
	
		Set<Pattern> patterns = new TreeSet<Pattern>();
		for ( Set<GeneralizedPattern> set : this.patterns.values() )
			for ( GeneralizedPattern genPat : set )
				patterns.addAll(genPat.getPatterns());
		
		return patterns;
	}
	
	/**
	 * 
	 * @return
	 */
	public Set<GeneralizedPattern> getGeneralizedPatterns() {
		
		Set<GeneralizedPattern> generalizedPatterns = new TreeSet<GeneralizedPattern>();
		for ( Set<GeneralizedPattern> set : this.patterns.values() ) 
			generalizedPatterns.addAll(set);
		
		return generalizedPatterns;
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
