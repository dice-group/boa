package de.uni_leipzig.simba.boa.backend.rdf;


public class Property {

	private final String uri;
	private String domain;
	private String range;
	private String label;
	private String type;
	
	public Property(String uri) {
		
		this.uri = uri;
	}

	/**
	 * @param domain the domain to set
	 */
	public void setDomain(String domain) {

		this.domain = domain;
	}

	/**
	 * @return the domain
	 */
	public String getDomain() {

		return domain;
	}

	/**
	 * @param range the range to set
	 */
	public void setRange(String range) {

		this.range = range;
	}

	/**
	 * @return the range
	 */
	public String getRange() {

		return range;
	}

	/**
	 * @return the uri
	 */
	public String getUri() {

		return uri;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {

		this.label = label;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {

		return label;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {

		this.type = type;
	}

	/**
	 * @return the type
	 */
	public String getType() {

		return type;
	}
}
