package de.uni_leipzig.simba.boa.backend.rdf.entity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.apache.commons.lang3.StringUtils;

import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;


@Entity
public class Property extends Resource {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1821596778644210513L;


	private Set<String> synsets;
	private PatternMapping patternMapping;

	private String propertyPrefix;
	private String propertyLocalname;
	
	private String rdfsRangePrefix;
	private String rdfsRangeLocalname;
    private String rdfsDomainPrefix;
    private String rdfsDomainLocalname;

	public Property(String uri) {
		super(uri);
		
        this.synsets = new HashSet<String>();
        
        int lastIndexOfSlashP = uri.lastIndexOf("/");
        int lastIndexOfSharpP = uri.lastIndexOf("#");

        this.propertyLocalname  = uri.substring(Math.max(lastIndexOfSlashP, lastIndexOfSharpP) + 1);
        this.propertyPrefix     = uri.replace(propertyLocalname, "");
        this.label              = StringUtils.join(propertyLocalname.split("(?=\\p{Upper})"), " ").toLowerCase();
	}
	
    public Property(String uri, String rdfsRange, String rdfsDomain) {
        super(uri);

        int lastIndexOfSlashP = uri.lastIndexOf("/");
        int lastIndexOfSharpP = uri.lastIndexOf("#");

        this.propertyLocalname  = uri.substring(Math.max(lastIndexOfSlashP, lastIndexOfSharpP) + 1);
        this.propertyPrefix     = uri.replace(propertyLocalname, "");
        this.label              = StringUtils.join(propertyLocalname.split("(?=\\p{Upper})"), " ").toLowerCase();

        if ( !rdfsDomain.equals("NA") ) {

            int lastIndexOfSlashD = rdfsDomain.lastIndexOf("/");
            int lastIndexOfSharpD = rdfsDomain.lastIndexOf("#");
            this.rdfsDomainLocalname = rdfsDomain.substring(Math.max(lastIndexOfSlashD, lastIndexOfSharpD) + 1);
            this.rdfsDomainPrefix = rdfsDomain.replace(rdfsDomainLocalname, "");
        }
        
        if ( !rdfsRange.equals("NA") ) {

            int lastIndexOfSlashR = rdfsRange.lastIndexOf("/");
            int lastIndexOfSharpR = rdfsRange.lastIndexOf("#");
            this.rdfsRangeLocalname = rdfsRange.substring(Math.max(lastIndexOfSlashR, lastIndexOfSharpR) + 1);
            this.rdfsRangePrefix = rdfsRange.replace(rdfsRangeLocalname, "");
        }

        this.synsets = new HashSet<String>();
    }

	
	public Property() {
		super();
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
	public Set<String> getSynsets() {

		return synsets;
	}
	
    /**
     * 
     * @return
     */
    public String getPropertyUri() {
        
        return propertyPrefix + propertyLocalname;
    }

    /**
	 * @param synsets the synsets to set
	 */
	public void setSynsets(Set<String> synsets) {

		this.synsets = synsets;
	}

    /**
     * @return the rdfsRangePrefix
     */
    public String getRdfsRange() {
        
        if ( (rdfsRangePrefix + rdfsRangeLocalname).isEmpty() 
                || (rdfsRangePrefix == null || rdfsRangeLocalname == null) ) return "NA";
        return rdfsRangePrefix + rdfsRangeLocalname;
    }

    /**
     * @return the rdfsDomainPrefix
     */
    public String getRdfsDomain() {
    
        if ( (rdfsDomainPrefix + rdfsDomainLocalname).isEmpty() 
                || (rdfsDomainPrefix == null || rdfsDomainLocalname == null) ) return "NA";
        return rdfsDomainPrefix + rdfsDomainLocalname;
    }

    public String getPropertyLocalname() {

        return propertyLocalname;
    }
}
