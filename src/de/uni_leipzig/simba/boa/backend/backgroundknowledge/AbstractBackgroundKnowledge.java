package de.uni_leipzig.simba.boa.backend.backgroundknowledge;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Resource;


public abstract class AbstractBackgroundKnowledge implements BackgroundKnowledge {

	protected String subjectUri;
	protected String subjectLabel;
	protected Set<String> subjectSurfaceForms  = new HashSet<String>();
	
	protected String propertyPrefix;
	protected String propertyUri;
	protected String rdfsDomain;
    protected String rdfsRange;
    protected String propertyWordnetSynsets;
	
	protected String objectUri;
	protected String objectLabel;
	protected Set<String> objectSurfaceForms   = new HashSet<String>();
	
	public AbstractBackgroundKnowledge() {}
	
	
	public String getPropertyLabel() {
	    
	    return StringUtils.join(propertyUri.split("(?=\\p{Upper})"), " ").toLowerCase();
	}
	
	/**
     * @return the subjectSurfaceForms
     */
    public Set<String> getSubjectSurfaceForms() {
    
        return subjectSurfaceForms;
    }

    
    /**
     * @param subjectSurfaceForms the subjectSurfaceForms to set
     */
    public void setSubjectSurfaceForms(Set<String> subjectSurfaceForms) {
    
        this.subjectSurfaceForms = subjectSurfaceForms;
    }

    
    /**
     * @return the objectSurfaceForms
     */
    public Set<String> getObjectSurfaceForms() {
    
        return objectSurfaceForms;
    }

    
    /**
     * @param objectSurfaceForms the objectSurfaceForms to set
     */
    public void setObjectSurfaceForms(Set<String> objectSurfaceForms) {
    
        this.objectSurfaceForms = objectSurfaceForms;
    }


    
    /**
     * @return the subjectUri
     */
    public String getSubjectUri() {
    
        return subjectUri;
    }


    
    /**
     * @param subjectUri the subjectUri to set
     */
    public void setSubjectUri(String subjectUri) {
    
        this.subjectUri = subjectUri;
    }


    
    /**
     * @return the subjectLabel
     */
    public String getSubjectLabel() {
    
        return subjectLabel;
    }


    
    /**
     * @param subjectLabel the subjectLabel to set
     */
    public void setSubjectLabel(String subjectLabel) {
    
        this.subjectLabel = subjectLabel;
    }


    
    /**
     * @return the propertyUri
     */
    public String getPropertyUri() {
    
        return propertyUri;
    }


    
    /**
     * @param propertyUri the propertyUri to set
     */
    public void setPropertyUri(String propertyUri) {
    
        this.propertyUri = propertyUri;
    }


    
    /**
     * @return the objectUri
     */
    public String getObjectUri() {
    
        return objectUri;
    }


    
    /**
     * @param objectUri the objectUri to set
     */
    public void setObjectUri(String objectUri) {
    
        this.objectUri = objectUri;
    }


    
    /**
     * @return the objectLabel
     */
    public String getObjectLabel() {
    
        return objectLabel;
    }


    
    /**
     * @param objectLabel the objectLabel to set
     */
    public void setObjectLabel(String objectLabel) {
    
        this.objectLabel = objectLabel;
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
     * @return the propertyWordnetSynsets
     */
    public String getPropertyWordnetSynsets() {
    
        return propertyWordnetSynsets;
    }


    
    /**
     * @param propertyWordnetSynsets the propertyWordnetSynsets to set
     */
    public void setPropertyWordnetSynsets(String propertyWordnetSynsets) {
    
        this.propertyWordnetSynsets = propertyWordnetSynsets;
    }


    
    /**
     * @return the propertyPrefix
     */
    public String getPropertyPrefix() {
    
        return propertyPrefix;
    }


    
    /**
     * @param propertyPrefix the propertyPrefix to set
     */
    public void setPropertyPrefix(String propertyPrefix) {
    
        this.propertyPrefix = propertyPrefix;
    }
}