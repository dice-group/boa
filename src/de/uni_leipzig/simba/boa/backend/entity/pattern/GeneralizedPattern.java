/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.entity.pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author gerb
 *
 */
public class GeneralizedPattern extends AbstractPattern {

	java.util.regex.Pattern number	= java.util.regex.Pattern.compile("\\d{1,2}");
	java.util.regex.Pattern year	= java.util.regex.Pattern.compile("\\d{4}");
	
	private List<Pattern> patterns = new ArrayList<Pattern>();
	
	/* (non-Javadoc)
	 * @see de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern#getNaturalLanguageRepresentationWithoutVariables()
	 */
	@Override
	public String getNaturalLanguageRepresentationWithoutVariables() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @return the naturalLanguageRepresentation
	 */
	@Override
	public String getNaturalLanguageRepresentation() {
	
		// TODO
		return null;
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

	public String generateGeneralizedNaturalLanguageRepresentation(String nlr) {
		
		nlr = nlr.replaceAll("\\d{4}", "YEAR");
		nlr = nlr.replaceAll("(-)?\\d+(\\.\\d*)?", "NUMBER");
		
		return nlr;
	}
	
	public String getGeneralizedNaturalLanguageRepresentation() {
		return this.naturalLanguageRepresentation;
	}
}
