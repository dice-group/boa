package de.uni_leipzig.simba.boa.backend.entity;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="evaluation_result")
public class EvaluationResult extends de.uni_leipzig.simba.boa.backend.entity.Entity {

	private String evaluationId;
	private String graph;
	private String subject;
	private String predicate;
	private String object;
	private String reason;
	private boolean correct;

	public EvaluationResult(String evaluationId) {

		this.setEvaluationId(evaluationId);
	}

	/**
	 * 
	 * @param graph
	 */
	public void setGraph(String graph) {

		this.graph = graph;
	}

	/**
	 * 
	 * @param subject
	 */
	public void setSubject(String subject) {

		this.subject = subject;
	}

	/**
	 * 
	 * @param predicate
	 */
	public void setPredicate(String predicate) {

		this.predicate = predicate;
	}

	/**
	 * 
	 * @param object
	 */
	public void setObject(String object) {

		this.object = object;
	}

	/**
	 * 
	 * @param correct
	 */
	public void setCorrect(boolean correct) {

		this.correct = correct;
	}

	
	/**
	 * @return the graph
	 */
	@Basic
	public String getGraph() {
	
		return graph;
	}

	
	/**
	 * @return the subject
	 */
	@Basic
	public String getSubject() {
	
		return subject;
	}

	
	/**
	 * @return the predicate
	 */
	@Basic
	public String getPredicate() {
	
		return predicate;
	}

	
	/**
	 * @return the object
	 */
	@Basic
	public String getObject() {
	
		return object;
	}

	
	/**
	 * @return the correct
	 */
	@Basic
	public boolean isCorrect() {
	
		return correct;
	}

	/**
	 * @param evaluationId the evaluationId to set
	 */
	public void setEvaluationId(String evaluationId) {

		this.evaluationId = evaluationId;
	}

	/**
	 * @return the evaluationId
	 */
	@Basic
	public String getEvaluationId() {

		return evaluationId;
	}

	
	/**
	 * @return the reason
	 */
	@Basic
	public String getReason() {
	
		return reason;
	}

	
	/**
	 * @param reason the reason to set
	 */
	public void setReason(String reason) {
	
		this.reason = reason;
	}
}
