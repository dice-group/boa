package de.uni_leipzig.simba.boa.backend.backgroundknowledge;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;

/**
 * 
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public abstract class AbstractBackgroundKnowledge implements BackgroundKnowledge {

    protected String subjectLabel;
    protected String subjectPrefix;
    protected String subjectLocalname;
    protected Set<String> subjectSurfaceForms = new HashSet<String>();

    protected Property property;
    
    protected String objectLabel;
    protected String objectPrefix;
    protected String objectLocalname;
    protected Set<String> objectSurfaceForms = new HashSet<String>();

    public AbstractBackgroundKnowledge() {

    }

    public String getPropertyLabel() {

        return StringUtils.join((property.getUri()).split("(?=\\p{Upper})"), " ").toLowerCase();
    }

    /**
     * @return the subjectSurfaceForms
     */
    public Set<String> getSubjectSurfaceForms() {

        return subjectSurfaceForms;
    }

    /**
     * @param subjectSurfaceForms
     *            the subjectSurfaceForms to set
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
     * @param objectSurfaceForms
     *            the objectSurfaceForms to set
     */
    public void setObjectSurfaceForms(Set<String> objectSurfaceForms) {

        this.objectSurfaceForms = objectSurfaceForms;
    }

    /**
     * @return the subjectUri
     */
    public String getSubjectUri() {

        return this.subjectPrefix + this.subjectLocalname;
    }

    /**
     * @param subjectUri
     *            the subjectUri to set
     */
    public void setSubjectPrefixAndLocalname(String subjectUri) {

        int lastIndexOfSlash = subjectUri.lastIndexOf("/");
        int lastIndexOfSharp = subjectUri.lastIndexOf("#");

        this.subjectPrefix = subjectUri.substring(0, Math.max(lastIndexOfSlash, lastIndexOfSharp) + 1);
        this.subjectLocalname = subjectUri.substring(Math.max(lastIndexOfSlash, lastIndexOfSharp) + 1);
    }

    /**
     * @return the subjectLabel
     */
    public String getSubjectLabel() {

        return subjectLabel;
    }

    /**
     * @param subjectLabel
     *            the subjectLabel to set
     */
    public void setSubjectLabel(String subjectLabel) {

        this.subjectLabel = subjectLabel;
    }

    /**
     * @return the objectUri
     */
    public String getObjectUri() {

        return this.objectPrefix + this.objectLocalname;
    }

    /**
     * 
     * @param objectUri
     */
    public void setObjectPrefixAndLocalname(String objectUri) {

        int lastIndexOfSlash = objectUri.lastIndexOf("/");
        int lastIndexOfSharp = objectUri.lastIndexOf("#");

        this.objectPrefix      = objectUri.substring(0, Math.max(lastIndexOfSlash, lastIndexOfSharp) + 1);
        this.objectLocalname   = objectUri.substring(Math.max(lastIndexOfSlash, lastIndexOfSharp) + 1);
    }

    /**
     * @return the objectLabel
     */
    public String getObjectLabel() {

        return objectLabel;
    }

    /**
     * @param objectLabel
     *            the objectLabel to set
     */
    public void setObjectLabel(String objectLabel) {

        this.objectLabel = objectLabel;
    }

    /**
     * @return the subjectPrefix
     */
    public String getSubjectPrefix() {

        return subjectPrefix;
    }

    /**
     * @param subjectPrefix
     *            the subjectPrefix to set
     */
    public void setSubjectPrefix(String subjectPrefix) {

        this.subjectPrefix = subjectPrefix;
    }

    /**
     * @return the subjectLocalname
     */
    public String getSubjectLocalname() {

        return subjectLocalname;
    }

    /**
     * @param subjectLocalname
     *            the subjectLocalname to set
     */
    public void setSubjectLocalname(String subjectLocalname) {

        this.subjectLocalname = subjectLocalname;
    }

    /**
     * @return the objectPrefix
     */
    public String getObjectPrefix() {

        return objectPrefix;
    }

    /**
     * @param objectPrefix
     *            the objectPrefix to set
     */
    public void setObjectPrefix(String objectPrefix) {

        this.objectPrefix = objectPrefix;
    }

    /**
     * @return the objectLocalname
     */
    public String getObjectLocalname() {

        return objectLocalname;
    }

    /**
     * @param objectLocalname
     *            the objectLocalname to set
     */
    public void setObjectLocalname(String objectLocalname) {

        this.objectLocalname = objectLocalname;
    }
    
    @Override
    public Property getProperty() {

        return this.property;
    }

    @Override
    public void setProperty(Property property) {

        this.property = property;
    }
}