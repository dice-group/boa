package de.uni_leipzig.simba.boa.backend.limes;

/**
 * This class represents a single line of the output of limes.
 * 
 * @author Daniel Gerber
 */
public class SimilarityStatement {

	private String subject;
	private String predicate;
	private String object;
	private Double similarity;
	
	public SimilarityStatement(String subject, String predicate, String object, Double similarity) {

		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
		this.similarity = similarity;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
	
		return subject;
	}
	
	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
	
		this.subject = subject;
	}
	
	/**
	 * @return the predicate
	 */
	public String getPredicate() {
	
		return predicate;
	}
	
	/**
	 * @param predicate the predicate to set
	 */
	public void setPredicate(String predicate) {
	
		this.predicate = predicate;
	}
	
	/**
	 * @return the object
	 */
	public String getObject() {
	
		return object;
	}
	
	/**
	 * @param object the object to set
	 */
	public void setObject(String object) {
	
		this.object = object;
	}
	
	/**
	 * @return the similarity
	 */
	public Double getSimilarity() {
	
		return similarity;
	}
	
	/**
	 * @param similarity the similarity to set
	 */
	public void setSimilarity(Double similarity) {
	
		this.similarity = similarity;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

//		return "SimilarityStatement [subject=" + subject + ", predicate=" + predicate + ", object=" + object + ", similarity=" + similarity + "]";
		return subject + "," + similarity  + "," + object;
	}
}
