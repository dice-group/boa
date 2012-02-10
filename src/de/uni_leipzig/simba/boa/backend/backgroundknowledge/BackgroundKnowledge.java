package de.uni_leipzig.simba.boa.backend.backgroundknowledge;

import java.util.Set;

import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Resource;

/**
 * 
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public interface BackgroundKnowledge {
	
	/**
	 * 
	 * @return
	 */
	public Set<String> getSubjectSurfaceForms();
	
	/**
	 * 
	 * @param subjectSurfaceForms
	 */
	public void setSubjectSurfaceForms(Set<String> subjectSurfaceForms);
	
	/**
	 *@return 
	 */
	public Set<String> getObjectSurfaceForms();
	
	/**
	 * 
	 * @param subjectSurfaceForms
	 */
	public void setObjectSurfaceForms(Set<String> subjectSurfaceForms);
	
    /**
     * @return the subjectUri
     */
    public String getSubjectUri();
    
    /**
     * @param subjectUri the subjectUri to set
     */
    public void setSubjectUri(String subjectUri);
    
    /**
     * @return the subjectLabel
     */
    public String getSubjectLabel();
    
    /**
     * @param subjectLabel the subjectLabel to set
     */
    public void setSubjectLabel(String subjectLabel);
    
    /**
     * @return the propertyUri
     */
    public String getPropertyUri();
    
    /**
     * @param propertyUri the propertyUri to set
     */
    public void setPropertyUri(String propertyUri);
    
    /**
     * @return the objectUri
     */
    public String getObjectUri();

    /**
     * @param objectUri the objectUri to set
     */
    public void setObjectUri(String objectUri);
    
    /**
     * @return the objectLabel
     */
    public String getObjectLabel();

    /**
     * @param objectLabel the objectLabel to set
     */
    public void setObjectLabel(String objectLabel);
    
    /**
     * @return the rdfsDomain
     */
    public String getRdfsDomain();
    
    /**
     * @param rdfsDomain the rdfsDomain to set
     */
    public void setRdfsDomain(String rdfsDomain);
    
    /**
     * @return the rdfsRange
     */
    public String getRdfsRange() ;
    
    /**
     * @param rdfsRange the rdfsRange to set
     */
    public void setRdfsRange(String rdfsRange) ;
    
    /**
     * @return the propertyWordnetSynsets
     */
    public String getPropertyWordnetSynsets();
    
    /**
     * @param propertyWordnetSynsets the propertyWordnetSynsets to set
     */
    public void setPropertyWordnetSynsets(String propertyWordnetSynsets);

    /**
     * 
     * @return
     */
    public String getPropertyLabel();
}
