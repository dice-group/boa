package de.uni_leipzig.simba.boa.backend.rdf.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;

@Entity
@Table(name="triple")
public class Triple extends de.uni_leipzig.simba.boa.backend.persistance.Entity {

	private Resource subject;
	private Property property;
	private Resource object;
	
	private int learnedInIteration;
	private boolean isCorrect;
	private double confidence;
	private Set<Pattern> learnedFromPatterns;
	
	public Triple(Resource subject, Property property, Resource object) {
		super();
		
		this.subject = subject;
		this.property = property;
		this.object = object;
		this.confidence = -1d;
		this.learnedInIteration = -1;
		this.learnedFromPatterns = new HashSet<Pattern>();
	}

	public Triple() {
		
		this.confidence = -1d;
		this.learnedInIteration = -1;
		this.learnedFromPatterns = new HashSet<Pattern>();
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

		StringBuilder builder = new StringBuilder();
		builder.append("Triple [subject=");
		builder.append(subject.uri);
		builder.append(", property=");
		builder.append(property.uri);
		builder.append(", object=");
		builder.append(object.uri);
		builder.append(", learnedInIteration=");
		builder.append(learnedInIteration);
		builder.append(", isCorrect=");
		builder.append(isCorrect);
		builder.append(", confidence=");
		builder.append(confidence);
		builder.append(", learnedFromPatterns=");
		builder.append(learnedFromPatterns.size());
		builder.append(", id=");
		builder.append(id);
		builder.append("]");
		return builder.toString();
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
	 * @param learnedFromPatterns the learnedFromPatterns to set
	 */
	public void setLearnedFromPatterns(Set<Pattern> learnedFromPatterns) {
	
		this.learnedFromPatterns = learnedFromPatterns;
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
}