package de.uni_leipzig.simba.boa.backend.backgroundknowledge;

import java.util.Set;

import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;

/**
 * 
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public interface BackgroundKnowledge {
	
	/**
     * @return the objectLabel
     */
    public String getObjectLabel();
	
	/**
     * @return the objectLocalname
     */
    public String getObjectLocalname();
	
	/**
     * @return the objectPrefix
     */
    public String getObjectPrefix();
	
	/**
	 *@return 
	 */
	public Set<String> getObjectSurfaceForms();
	
	/**
     * @return the objectUri
     */
    public String getObjectUri();
	
    /**
     * @return the subjectLabel
     */
    public String getSubjectLabel();

    /**
     * @return the subjectLocalname
     */
    public String getSubjectLocalname();
    
    /**
     * @return the subjectPrefix
     */
    public String getSubjectPrefix() ;
    
    /**
	 * 
	 * @return
	 */
	public Set<String> getSubjectSurfaceForms();
    
    /**
     * @return the subjectUri
     */
    public String getSubjectUri();
    
    /**
     * @param objectLabel the objectLabel to set
     */
    public void setObjectLabel(String objectLabel);
    
    /**
     * @param objectLocalname
     *            the objectLocalname to set
     */
    public void setObjectLocalname(String objectLocalname);
    
    /**
     * @param objectPrefix
     *            the objectPrefix to set
     */
    public void setObjectPrefix(String objectPrefix);

    /**
     * 
     * @param objectUri
     */
    public void setObjectPrefixAndLocalname(String objectUri);
    
    /**
	 * 
	 * @param subjectSurfaceForms
	 */
	public void setObjectSurfaceForms(Set<String> subjectSurfaceForms);

    /**
     * @param subjectLabel the subjectLabel to set
     */
    public void setSubjectLabel(String subjectLabel);

    /**
     * @param subjectLocalname
     *            the subjectLocalname to set
     */
    public void setSubjectLocalname(String subjectLocalname);

    /**
     * @param subjectPrefix
     *            the subjectPrefix to set
     */
    public void setSubjectPrefix(String subjectPrefix);
    
    /**
	 * 
	 * @param subjectUri
	 */
	public void setSubjectPrefixAndLocalname(String subjectUri);
    
    /**
	 * 
	 * @param subjectSurfaceForms
	 */
	public void setSubjectSurfaceForms(Set<String> subjectSurfaceForms);
	
	/**
	 * 
	 * @return
	 */
	public Property getProperty();
	
	public void setProperty(Property property);
}
