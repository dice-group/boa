package de.uni_leipzig.simba.boa.backend.rdf.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    
    private String toStringSplitCharacters = "][";

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

	
	/* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        List<String> toString = new ArrayList<String>(); 
        toString.add(this.getUri());
        toString.add(this.getRdfsRange());
        toString.add(this.getRdfsDomain());
                
        return StringUtils.join(toString, toStringSplitCharacters);
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
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((propertyLocalname == null) ? 0 : propertyLocalname.hashCode());
        result = prime * result + ((propertyPrefix == null) ? 0 : propertyPrefix.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Property other = (Property) obj;
        if (propertyLocalname == null) {
            if (other.propertyLocalname != null)
                return false;
        }
        else
            if (!propertyLocalname.equals(other.propertyLocalname))
                return false;
        if (propertyPrefix == null) {
            if (other.propertyPrefix != null)
                return false;
        }
        else
            if (!propertyPrefix.equals(other.propertyPrefix))
                return false;
        if (rdfsDomainLocalname == null) {
            if (other.rdfsDomainLocalname != null)
                return false;
        }
        else
            if (!rdfsDomainLocalname.equals(other.rdfsDomainLocalname))
                return false;
        if (rdfsDomainPrefix == null) {
            if (other.rdfsDomainPrefix != null)
                return false;
        }
        else
            if (!rdfsDomainPrefix.equals(other.rdfsDomainPrefix))
                return false;
        if (rdfsRangeLocalname == null) {
            if (other.rdfsRangeLocalname != null)
                return false;
        }
        else
            if (!rdfsRangeLocalname.equals(other.rdfsRangeLocalname))
                return false;
        if (rdfsRangePrefix == null) {
            if (other.rdfsRangePrefix != null)
                return false;
        }
        else
            if (!rdfsRangePrefix.equals(other.rdfsRangePrefix))
                return false;
        return true;
    }
}
