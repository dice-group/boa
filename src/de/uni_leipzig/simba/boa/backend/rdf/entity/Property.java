package de.uni_leipzig.simba.boa.backend.rdf.entity;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;

@Entity
public class Property extends Resource {

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("Property [rdfsRange=");
		builder.append(rdfsRange);
		builder.append(", rdfsDomain=");
		builder.append(rdfsDomain);
		builder.append(", uri=");
		builder.append(uri);
		builder.append(", label=");
		builder.append(label);
		builder.append(", type=");
		builder.append(type);
		builder.append(", id=");
		builder.append(id);
		builder.append("]");
		return builder.toString();
	}

	private String rdfsRange;
	private String rdfsDomain;
	private PatternMapping patternMapping;

	public Property(String uri, String label, String rdfsRange, String rdfsDomain) {

		super(uri, label);
		this.rdfsRange = rdfsRange;
		this.rdfsDomain = rdfsDomain;
	}

	
	public Property() {
		super();
	}


	/**
	 * @return the rdfsRange
	 */
	@Basic
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
	@Basic
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
	 * @param patternmapping the patternmapping to set
	 */
	public void setPatternMapping(PatternMapping patternMapping) {

		this.patternMapping = patternMapping;
	}

	/**
	 * @return the patternmapping
	 */
	@OneToOne(mappedBy="property")
	public PatternMapping getPatternMapping() {

		return patternMapping;
	}
}