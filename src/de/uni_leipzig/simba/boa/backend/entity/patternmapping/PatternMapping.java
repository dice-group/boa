package de.uni_leipzig.simba.boa.backend.entity.patternmapping;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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
	private Map<String,GeneralizedPattern> patterns;
	
	/**
	 * default constructor needed for hibernate
	 */
	public PatternMapping(){
		
	    this.property   = new Property();
        this.patterns   = new TreeMap<String,GeneralizedPattern>();
	}
	
	/**
	 * Creates a new pattern mapping with the specified property as uri
	 * and an empty list for patterns.
	 * 
	 * @param property
	 */
	public PatternMapping(Property property) {

		this.property = property;
		this.patterns   = new TreeMap<String,GeneralizedPattern>();
	}

	/**
	 * @return the patterns
	 */
	public Set<Pattern> getPatterns() {
	
		Set<Pattern> patterns = new TreeSet<Pattern>();
		for ( GeneralizedPattern genPat : this.patterns.values() )
			patterns.addAll(genPat.getPatterns());
		
		return patterns;
	}
	
	/**
	 * 
	 * @return
	 */
	public Set<GeneralizedPattern> getGeneralizedPatterns() {
		
		return new TreeSet<GeneralizedPattern>(this.patterns.values());
	}
	
	/**
	 * 
	 * @param correctPatterns
	 */
	public void setGeneralizedPatterns(Set<GeneralizedPattern> correctPatterns) {
		
		this.patterns = new TreeMap<String,GeneralizedPattern>();
		for ( GeneralizedPattern gp : correctPatterns ) this.patterns.put(gp.getNaturalLanguageRepresentation(), gp);
	}
	
	/**
	 * 
	 * @param pattern
	 */
	public PatternMapping addPattern(Pattern pattern) {
		
		String generalizedNlr = String.format("%s %s %s", 
				pattern.isDomainFirst() ? "?D?" : "?R?", generalize(pattern), pattern.isDomainFirst() ? "?R?" : "?D?"); 
		
		if ( !this.patterns.containsKey(generalizedNlr)) this.patterns.put(generalizedNlr, new GeneralizedPattern(generalizedNlr));
		this.patterns.get(generalizedNlr).addPattern(pattern);
		
		return this;
	}
	
	public static String generalize(Pattern pattern) {
		
		String nlr = pattern.getNaturalLanguageRepresentationWithoutVariables();
		
    	// remove trailing trash
		nlr = nlr.trim().replaceAll(" (the|,|, and|and|, the)$", "").trim();
		nlr = nlr.startsWith("''") ? nlr.substring(2) : nlr;
		nlr = nlr.endsWith("``") ? nlr.substring(0, nlr.length() - 2) : nlr;
		nlr = " " + nlr.trim() + " ";
    	
		// replace words in upppercase
		while ( nlr.matches("^.* \\p{Upper}\\S* .*$") )
			nlr = nlr.replaceAll(" \\p{Upper}\\S* ", " _NE_ ");
		
		nlr = nlr.replaceAll("(_NE_ )+", "_NE_ ").replaceAll(" +", " ");;
		
		// date stuff
		for ( String month : Arrays.asList("January", "February", "March", "April", "May", "June", 
    			"July", "August", "September", "October", "November", "December"))
    				nlr = nlr.replace(" " + month + " ", " _MONTH_ ");
		nlr = nlr.replaceAll("\\d{4}", "_YEAR_");
		nlr = nlr.replaceAll("(-)?\\d+(\\.\\d*)?", "_NUMBER_");
		
		for ( String pronoun : Arrays.asList("his", "her", "he", "she")) nlr = nlr.replace(" " + pronoun + " ", " _PP_ ");
		
		nlr = nlr.replaceAll(" an ", " a ");
		for ( String be : Arrays.asList("was", "were", "is", "are", "am")) nlr = nlr.replace(" " + be + " ", " _BE_ ");
		
		return nlr.trim();
		
//		children -
//		children --
//		children -LRB-
//		children :
//		children ;
		
//		band _NE_ , released in
//		band _NE_ , released on
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
	public Property getProperty() {

		return property;
	}
}
