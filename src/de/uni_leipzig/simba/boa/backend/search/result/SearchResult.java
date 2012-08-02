package de.uni_leipzig.simba.boa.backend.search.result;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;

import edu.stanford.nlp.util.StringUtils;



public class SearchResult {

	private Property property;
	private String naturalLanguageRepresentation;
	private String firstLabel;
	private String secondLabel;
	private Integer sentenceId;
	
	private String toStringSplitCharacters = "][";
	
	public SearchResult() {}
	
    public SearchResult(String stringFromToString) {
	    
	    String[] searchResult               = stringFromToString.split(Pattern.quote(toStringSplitCharacters));
	    this.property                       = new Property(searchResult[0], searchResult[1], searchResult[2]);
        this.naturalLanguageRepresentation  = searchResult[3];
        this.firstLabel                     = searchResult[4];
        this.secondLabel                    = searchResult[5];
        this.sentenceId                     = Integer.valueOf(searchResult[6]);
	}
	
	/**
	 * @return the property
	 */
	public Property getProperty() {
	
		return property;
	}
	
	/**
	 * @param property the property to set
	 */
	public void setProperty(Property property) {
	
		this.property = property;
	}
	
	/**
	 * @return the naturalLanguageRepresentation
	 */
	public String getNaturalLanguageRepresentation() {
	
		return naturalLanguageRepresentation;
	}
	
	/**
	 * @param naturalLanguageRepresentation the naturalLanguageRepresentation to set
	 */
	public void setNaturalLanguageRepresentation(String naturalLanguageRepresentation) {
	
		this.naturalLanguageRepresentation = naturalLanguageRepresentation;
	}
	
	public String getNaturalLanguageRepresentationWithoutVariables() {
        
        return this.naturalLanguageRepresentation.substring(0, this.naturalLanguageRepresentation.length() - 3).substring(3).trim();
    }
	
	/**
	 * @return the firstLabel
	 */
	public String getFirstLabel() {
	
		return firstLabel;
	}
	
	/**
	 * @param firstLabel the firstLabel to set
	 */
	public void setFirstLabel(String firstLabel) {
	
		this.firstLabel = firstLabel;
	}
	
	/**
	 * @return the secondLabel
	 */
	public String getSecondLabel() {
	
		return secondLabel;
	}
	
	/**
	 * @param secondLabel the secondLabel to set
	 */
	public void setSecondLabel(String secondLabel) {
	
		this.secondLabel = secondLabel;
	}

    /**
     * @return the sentenceId
     */
    public Integer getSentence() {

        return sentenceId;
    }

    /**
     * @param sentenceId the sentenceId to set
     */
    public void setSentence(Integer sentenceId) {

        this.sentenceId = sentenceId;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        
        List<String> toString = new ArrayList<String>(); 
        toString.add(this.property.toString());
        toString.add(this.naturalLanguageRepresentation);
        toString.add(this.firstLabel);
        toString.add(this.secondLabel);
        toString.add(this.sentenceId.toString());
                
        return StringUtils.join(toString, toStringSplitCharacters);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + ((firstLabel == null) ? 0 : firstLabel.hashCode());
        result = prime * result + ((naturalLanguageRepresentation == null) ? 0 : naturalLanguageRepresentation.hashCode());
        result = prime * result + ((property == null) ? 0 : property.hashCode());
        result = prime * result + ((secondLabel == null) ? 0 : secondLabel.hashCode());
        result = prime * result + ((sentenceId == null) ? 0 : sentenceId.hashCode());
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
        SearchResult other = (SearchResult) obj;
        if (firstLabel == null) {
            if (other.firstLabel != null)
                return false;
        }
        else
            if (!firstLabel.equals(other.firstLabel))
                return false;
        if (naturalLanguageRepresentation == null) {
            if (other.naturalLanguageRepresentation != null)
                return false;
        }
        else
            if (!naturalLanguageRepresentation.equals(other.naturalLanguageRepresentation))
                return false;
        if (property == null) {
            if (other.property != null)
                return false;
        }
        else
            if (!property.equals(other.property))
                return false;
        if (secondLabel == null) {
            if (other.secondLabel != null)
                return false;
        }
        else
            if (!secondLabel.equals(other.secondLabel))
                return false;
        if (sentenceId == null) {
            if (other.sentenceId != null)
                return false;
        }
        else
            if (!sentenceId.equals(other.sentenceId))
                return false;
        return true;
    }
}
