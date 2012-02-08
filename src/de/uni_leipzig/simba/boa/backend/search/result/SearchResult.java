package de.uni_leipzig.simba.boa.backend.search.result;



public class SearchResult {

	private String property;
	private String naturalLanguageRepresentation;
	private String rdfsRange;
	private String rdfsDomain;
	private String firstLabel;
	private String secondLabel;
	private String posTags;
	private Integer sentenceId;
	
	/**
	 * @return the property
	 */
	public String getProperty() {
	
		return property;
	}
	
	/**
	 * @param property the property to set
	 */
	public void setProperty(String property) {
	
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
	 * @return the rdfsRange
	 */
	public String getRdfsRange() {
	
		return rdfsRange;
	}
	
	/**
	 * @param rdfsRange the rdfsRange to set
	 */
	public void setRdfsRange(String rdfsRange) {
	
		this.rdfsRange = rdfsRange;
	}
	
	/**
	 * @return the rdfsDomain
	 */
	public String getRdfsDomain() {
	
		return rdfsDomain;
	}
	
	/**
	 * @param rdfsDomain the rdfsDomain to set
	 */
	public void setRdfsDomain(String rdfsDomain) {
	
		this.rdfsDomain = rdfsDomain;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("SearchResult [property=");
		builder.append(property);
		builder.append(", naturalLanguageRepresentation=");
		builder.append(naturalLanguageRepresentation);
		builder.append(", rdfsRange=");
		builder.append(rdfsRange);
		builder.append(", rdfsDomain=");
		builder.append(rdfsDomain);
		builder.append(", firstLabel=");
		builder.append(firstLabel);
		builder.append(", secondLabel=");
		builder.append(secondLabel);
		builder.append("]");
		return builder.toString();
	}
	
	/**
	 * @return
	 */
	public String getPosTags(){
		
		return this.posTags;
	}

	/**
	 * @param posTagsForSentence
	 */
	public void setPosTags(String posTagsForSentence) {

		this.posTags = posTagsForSentence;
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
}
