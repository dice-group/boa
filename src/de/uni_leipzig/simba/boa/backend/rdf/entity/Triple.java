package de.uni_leipzig.simba.boa.backend.rdf.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;

@Entity
@Table(name="triple")
public class Triple extends de.uni_leipzig.simba.boa.backend.entity.Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3148247276053130633L;
	
	private Resource subject;
	private Property property;
	private Resource object;
	
	private int learnedInIteration;
	private boolean isCorrect;
	private double confidence;
	private Set<Pattern> learnedFromPatterns;
	private Set<String> learnedFromSentences;
	
	public Triple(Resource subject, Property property, Resource object) {
		super();
		
		this.subject = subject;
		this.property = property;
		this.object = object;
		this.confidence = 0d;
		this.learnedInIteration = -1;
		this.learnedFromPatterns = new HashSet<Pattern>();
		this.learnedFromSentences = new HashSet<String>();
	}

	public Triple() {
		
		this.confidence = -1d;
		this.learnedInIteration = -1;
		this.learnedFromPatterns = new HashSet<Pattern>();
		this.learnedFromSentences = new HashSet<String>();
	}

	/**
	 * @return the subject
	 */
	@ManyToOne(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
	public Resource getSubject() {
	
		return subject;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return "[" + subject.uri + "\t" + property.uri + "\t" + object.uri + "]";//, " + OutputFormatter.format(confidence, "#.##") + " ]";
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(Resource subject) {
	
		this.subject = subject;
	}
	
	/**
	 * @return the property
	 */
	@ManyToOne(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
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
	 * @return the object
	 */
	@ManyToOne(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
	public Resource getObject() {
	
		return object;
	}
	
	/**
	 * @param object the object to set
	 */
	public void setObject(Resource object) {
	
		this.object = object;
	}
	
	/**
	 * @return the learnedInIteration
	 */
	@Basic
	public int getLearnedInIteration() {
	
		return learnedInIteration;
	}
	
	/**
	 * @param learnedInIteration the learnedInIteration to set
	 */
	public void setLearnedInIteration(int learnedInIteration) {
	
		this.learnedInIteration = learnedInIteration;
	}
	
	/**
	 * @return the confidence
	 */
	@Basic
	public double getConfidence() {
	
		return confidence;
	}
	
	/**
	 * @param confidence the confidence to set
	 */
	public void setConfidence(double confidence) {
	
		this.confidence = confidence;
	}
	
	/**
	 * @return the learnedFromPatterns
	 */
	@ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
	@JoinTable(name="triple_pattern", joinColumns =  {
        @JoinColumn(name = "triple_id", referencedColumnName = "id")
    }, inverseJoinColumns =  {
        @JoinColumn(name = "pattern_id", referencedColumnName = "id")
    })
	public Set<Pattern> getLearnedFromPatterns() {
	
		return learnedFromPatterns;
	}
	
	/**
	 * @param LearnedFromSentences the LearnedFromSentences to set
	 */
	public void setLearnedFromSentences(Set<String> learnedFromSentences) {
	
		this.learnedFromSentences = learnedFromSentences;
	}
	
	/**
	 * @return the LearnedFromSentences
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name="triple_sentences")
	@Column(length=1000)
	public Set<String> getLearnedFromSentences() {
	
		return this.learnedFromSentences;
	}
	
	/**
	 * @param learnedFromPatterns the learnedFromPatterns to set
	 */
	public void setLearnedFromPatterns(Set<Pattern> learnedFromPatterns) {
	
		this.learnedFromPatterns = learnedFromPatterns;
	}
	
	/**
	 * 
	 * @param pattern
	 */
	public void addLearnedFromPattern(Pattern pattern) {

		this.learnedFromPatterns.add(pattern);
	}

	/**
	 * @param isCorrect the isCorrect to set
	 */
	public void setCorrect(boolean isCorrect) {

		this.isCorrect = isCorrect;
	}

	/**
	 * @return the isCorrect
	 */
	@Basic
	public boolean isCorrect() {

		return isCorrect;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((object == null) ? 0 : object.hashCode());
		result = prime * result + ((property == null) ? 0 : property.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
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
		Triple other = (Triple) obj;
		if (object == null) {
			if (other.object != null)
				return false;
		}
		else
			if (!object.equals(other.object))
				return false;
		if (property == null) {
			if (other.property != null)
				return false;
		}
		else
			if (!property.equals(other.property))
				return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		}
		else
			if (!subject.equals(other.subject))
				return false;
		return true;
	}

	public void addLearnedFromSentences(String sentence) {

		this.learnedFromSentences.add(sentence);
	}
}
