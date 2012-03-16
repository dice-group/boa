package de.uni_leipzig.simba.boa.backend.backgroundknowledge.impl;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.AbstractBackgroundKnowledge;
import edu.stanford.nlp.util.StringUtils;


public final class DatatypePropertyBackgroundKnowledge extends AbstractBackgroundKnowledge {

	private String objectDatatype = "NA";

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        // 0_URI1 ||| 1_LABEL1 ||| 2_LABELS1 ||| 3_PROP ||| 4_URI2 ||| 5_LABEL2 ||| 6_LABELS2 ||| 7_RANGE ||| 8_DOMAIN
        
        StringBuilder builder = new StringBuilder();
        builder.append(this.getSubjectUri());
        builder.append(Constants.BACKGROUND_KNOWLEDGE_VALUE_SEPARATOR);
        
        builder.append(subjectLabel);
        builder.append(Constants.BACKGROUND_KNOWLEDGE_VALUE_SEPARATOR);
        
        builder.append(StringUtils.join(this.subjectSurfaceForms, Constants.BACKGROUND_KNOWLEDGE_SURFACE_FORM_SEPARATOR));
        builder.append(Constants.BACKGROUND_KNOWLEDGE_VALUE_SEPARATOR);
        
        builder.append(property.getUri());
        builder.append(Constants.BACKGROUND_KNOWLEDGE_VALUE_SEPARATOR);
        
        builder.append(this.getObjectUri());
        builder.append(Constants.BACKGROUND_KNOWLEDGE_VALUE_SEPARATOR);
        
        builder.append(objectLabel);
        builder.append(Constants.BACKGROUND_KNOWLEDGE_VALUE_SEPARATOR);
        
        builder.append(StringUtils.join(this.objectSurfaceForms, Constants.BACKGROUND_KNOWLEDGE_SURFACE_FORM_SEPARATOR));
        builder.append(Constants.BACKGROUND_KNOWLEDGE_VALUE_SEPARATOR);
        
        builder.append(property.getRdfsRange());
        builder.append(Constants.BACKGROUND_KNOWLEDGE_VALUE_SEPARATOR);
        
        builder.append(property.getRdfsDomain());
        builder.append(Constants.BACKGROUND_KNOWLEDGE_VALUE_SEPARATOR);
        
        builder.append(objectDatatype);
        
        return builder.toString();
    }
    
    public void setObjectDatatype(String datatype) {
        
        this.objectDatatype = datatype;
    }
    
    public String getObjectDatatype() {
       
        return this.objectDatatype ;
    }
}
