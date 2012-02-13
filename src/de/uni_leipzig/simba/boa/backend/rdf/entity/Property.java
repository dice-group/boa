package de.uni_leipzig.simba.boa.backend.rdf.entity;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;


@Entity
public class Property extends Resource {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1821596778644210513L;

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
	private String synsets;
	private String prefix;
	private PatternMapping patternMapping;
    private String rangePrefix;
    private String domainPrefix;

	public Property(String uri) {

		super(uri);
		this.rdfsRange = "";
        this.rdfsDomain = "";
        this.synsets = "";
        this.prefix = "";
        this.domainPrefix = "";
        this.rangePrefix = "";
	}
	
	public Property(String uri, String label, String rdfsRange, String rdfsDomain, String synsets) {

		super(uri, label);
		this.rdfsRange = rdfsRange;
		this.rdfsDomain = rdfsDomain;
		this.domainPrefix = "";
		this.rangePrefix = "";
		this.synsets = synsets;
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


	/**
	 * @return the synsets
	 */
	@Column(length=5012)
	public String getSynsets() {

		return synsets;
	}
	
	/**
	 * @return the synsets
	 */
	public List<String> retrieveSynsetsForLabel(){
		
		return Arrays.asList(this.synsets.split(","));
	}


	/**
	 * @param synsets the synsets to set
	 */
	public void setSynsets(String synsets) {

		this.synsets = synsets;
	}

    /**
     * @return the prefix
     */
    public String getPrefix() {

        return prefix;
    }

    /**
     * @param prefix the prefix to set
     */
    public void setPrefix(String prefix) {

        this.prefix = prefix;
    }

    public String getDomainPrefix() {

        return this.domainPrefix;
    }
    
    public void setDomainPrefix(String domainPrefix) {

        this.domainPrefix = domainPrefix;
    }

    public String getRangePrefix() {

        return this.rangePrefix; 
    }

    public void setRangePrefix(String rangePrefix) {

        this.rangePrefix = rangePrefix;
    }
}
