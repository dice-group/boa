/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.entity.pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gerb
 *
 */
public class GeneralizedPattern extends AbstractPattern implements Comparable<GeneralizedPattern> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7073266160090836276L;
	
	java.util.regex.Pattern number	= java.util.regex.Pattern.compile("\\d{1,2}");
	java.util.regex.Pattern year	= java.util.regex.Pattern.compile("\\d{4}");
	
	private List<Pattern> patterns = new ArrayList<Pattern>();
	
	public GeneralizedPattern(String generalizedNlr) {
		this.naturalLanguageRepresentation = generalizedNlr;
	}

	/**
	 * @return the naturalLanguageRepresentation
	 */
	@Override
	public String getNaturalLanguageRepresentation() {
	
		return this.naturalLanguageRepresentation;
	}
	
	/**
	 * @return the patterns
	 */
	public List<Pattern> getPatterns() {
		return patterns;
	}
	
	/**
	 * 
	 * @param p
	 */
	public void addPattern(Pattern p) {
		
		this.patterns.add(p);
	}
	
	@Override
	public Integer getNumberOfOccurrences() {
		
		Integer occ = 0;
		for ( Pattern p : this.patterns ) occ += p.getNumberOfOccurrences();
		return occ;
	}
	
	/**
	 * 
	 */
	@Override
	public Map<String,Integer> getLearnedFrom() {

		Map<String,Integer> learnedFrom = new HashMap<String,Integer>();
		for ( Pattern p : this.patterns ) learnedFrom.putAll(p.getLearnedFrom());
		return learnedFrom;
	}

	@Override
	public String getNaturalLanguageRepresentationWithoutVariables() {
        
        return this.naturalLanguageRepresentation.substring(0, this.naturalLanguageRepresentation.length() - 3).substring(3).trim();
    }

	@Override
	public int compareTo(GeneralizedPattern o) {
		
		return this.naturalLanguageRepresentation.compareTo(o.naturalLanguageRepresentation);
	}
}
