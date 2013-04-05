package de.uni_leipzig.simba.boa.backend.backgroundknowledge;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;

import com.github.gerbsen.file.BufferedFileReader;
import com.github.gerbsen.file.FileUtil;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.impl.DatatypePropertyBackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.impl.ObjectPropertyBackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.wordnet.query.WordnetQuery;

public class BackgroundKnowledgeManager {
	
	private final NLPediaLogger logger				= new NLPediaLogger(BackgroundKnowledgeManager.class); 

	// both used for singleton pattern
	private static BackgroundKnowledgeManager INSTANCE = null;
	private BackgroundKnowledgeManager(){}
	
	private Map<String,Set<String>> urisToSurfaceForms = new HashMap<String,Set<String>>();
	private Map<Integer,Property> properties = new HashMap<Integer,Property>();
	
	/**
	 * @return BackgroundKnowledgeManager singleton
	 */
	public static BackgroundKnowledgeManager getInstance(){
		
		if (BackgroundKnowledgeManager.INSTANCE == null){
			
			BackgroundKnowledgeManager.INSTANCE = new BackgroundKnowledgeManager();
		}
		
		return BackgroundKnowledgeManager.INSTANCE;
	}
	
	/**
	 * This message returns the values stored in the file "labelOutputFile".
	 * The values in this file should be like this:
	 * 		<label of subject> <property> <label of object>
	 * 
	 * @return list of triples
	 */
	public List<BackgroundKnowledge> getBackgroundKnowledgeInDirectory(String directory, boolean isObjectProperty) {
		
		List<BackgroundKnowledge> backgroundKnowledge = new ArrayList<BackgroundKnowledge>();

		for ( File file : FileUtils.listFiles(new File(directory), HiddenFileFilter.VISIBLE, null) ) {
			
			backgroundKnowledge.addAll(this.getBackgroundKnowledge(file.getAbsolutePath(), isObjectProperty));
		}
		return backgroundKnowledge;
	}

	/**
	 * 
	 * @param filename
	 * @return
	 */
	public List<BackgroundKnowledge> getBackgroundKnowledge(String filename, boolean isObjectProperty) {
        
        List<BackgroundKnowledge> backgroundKnowledge = new ArrayList<BackgroundKnowledge>();
        this.logger.info(String.format("Reading background knowledge from file %s", filename));

        BufferedFileReader br = FileUtil.openReader(filename, "UTF-8");

        String line;
        while ((line = br.readLine()) != null) {

            try {
                
                backgroundKnowledge.add(this.createBackgroundKnowledge(line, isObjectProperty));
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e ) {
                
                System.out.println(line);
            }
        }
        br.close();
        return backgroundKnowledge;
    }
	
	/**
	 * Reads a line of a background knowledge file to a java POJO
	 * 
	 * @param line
	 * @return
	 */
	public BackgroundKnowledge createBackgroundKnowledge(String line, boolean isObjectProperty) {
		
		String[] parts = line.split(Constants.BACKGROUND_KNOWLEDGE_VALUE_SEPARATOR_REGEX);
		
		// all subject information
		final String subjectUri   = parts[0];
		final String subjectLabel = parts[1].replaceAll("\\(.+?\\)", "").trim();
		Set<String> subjectLabels = null;
//		String subjectContext     = "";
		
		// all object information
		final String objectUri    = parts[4];
		final String objectLabel  = parts[5].replaceAll("\\(.+?\\)", "").trim();
		Set<String> objectLabels  = null;
//		String objectContext	  = "";
		
		// all predicate information
		final String predicate        = parts[3];
		final String predicateType    = isObjectProperty ? Constants.OWL_OBJECT_PROPERTY : Constants.OWL_DATATYPE_PROPERTY;
		final String range            = parts[7].equals("null") ? null : parts[7];
		final String domain           = parts[8].equals("null") ? null : parts[8];
		
		// ################ SUBJECT ############################
		
		// labels from wikipedia surface forms
		if ( !this.urisToSurfaceForms.containsKey(subjectUri) ) {
		    
		    // since we dont want to create new sets for every uri
		    subjectLabels = new HashSet<String>();
		
    		for (String part : parts[2].toLowerCase().split(Constants.BACKGROUND_KNOWLEDGE_SURFACE_FORM_SEPARATOR))  {
    		    
    		    final String surfaceForm = part.trim();
    		    if ( surfaceForm.length() >= NLPediaSettings.getIntegerSetting("surfaceFormMinimumLength")) {
                    
    		        subjectLabels.add(" " + surfaceForm + " ");
                }
    		}
    		subjectLabels.add(" " +  subjectLabel + " ");
    		subjectLabels.removeAll(Arrays.asList("", null));
    		
    		this.urisToSurfaceForms.put(subjectUri, subjectLabels);
		}
		else {
		    
		    subjectLabels = this.urisToSurfaceForms.get(subjectUri);
		}
		
		// ################ OBJECT ############################
		
		if ( !this.urisToSurfaceForms.containsKey(objectUri) ) {
		
		    // since we dont want to create new sets for every uri
            objectLabels = new HashSet<String>();
		    
		    // labels from wikipedia surface forms
	        for (String part : parts[6].toLowerCase().split(Constants.BACKGROUND_KNOWLEDGE_SURFACE_FORM_SEPARATOR))  {
	            
	            final String surfaceForm = part.trim();
	            if ( surfaceForm.length() >= NLPediaSettings.getIntegerSetting("surfaceFormMinimumLength")) {
	                
	                objectLabels.add(" " + surfaceForm + " ");
	            }
	        }
	        objectLabels.add(" " + objectLabel + " ");
	        objectLabels.removeAll(Arrays.asList("", null));
	        
	        this.urisToSurfaceForms.put(objectUri, objectLabels);
		}
		else {
		    
		    objectLabels = this.urisToSurfaceForms.get(objectUri);
		}
		
		
		// ################ resources: subject, property, object ############################
		
		// object properties have there own labels
		if ( predicateType.equals(Constants.OWL_OBJECT_PROPERTY) ) {
			
			ObjectPropertyBackgroundKnowledge objectBackgroundKnowledge = new ObjectPropertyBackgroundKnowledge();
            objectBackgroundKnowledge.setSubjectPrefixAndLocalname(subjectUri);
            objectBackgroundKnowledge.setSubjectLabel(subjectLabel);
            objectBackgroundKnowledge.setSubjectSurfaceForms(subjectLabels);
            
            objectBackgroundKnowledge.setObjectPrefixAndLocalname(objectUri);
            objectBackgroundKnowledge.setObjectLabel(objectLabel);
            objectBackgroundKnowledge.setObjectSurfaceForms(objectLabels);
            
            Property p = this.properties.get(predicate.hashCode());
            if ( p == null ) {
                
                p = new Property(predicate, domain, range);
                p.setSynsets(WordnetQuery.getSynsetsForAllSynsetTypes(predicate));
                this.properties.put(predicate.hashCode(), p);
            }
            objectBackgroundKnowledge.setProperty(p);
			
			return objectBackgroundKnowledge;
		}
		else {
			
		    DatatypePropertyBackgroundKnowledge datattypeBackgroundKnowledge = new DatatypePropertyBackgroundKnowledge();
		    datattypeBackgroundKnowledge.setSubjectPrefixAndLocalname(subjectUri);
		    datattypeBackgroundKnowledge.setSubjectLabel(subjectLabel);
		    datattypeBackgroundKnowledge.setSubjectSurfaceForms(subjectLabels);
            
		    datattypeBackgroundKnowledge.setObjectPrefixAndLocalname(objectUri);
		    datattypeBackgroundKnowledge.setObjectLabel(objectLabel);
		    datattypeBackgroundKnowledge.setObjectSurfaceForms(objectLabels);
            
		    Property p = this.properties.get(predicate);
            if ( p == null ) {
                
                p = new Property(predicate, domain, range);
                p.setSynsets(WordnetQuery.getSynsetsForAllSynsetTypes(predicate));
            }
            datattypeBackgroundKnowledge.setProperty(p);
		    
		    if ( parts.length == 10 ) datattypeBackgroundKnowledge.setObjectDatatype(parts[9]);
            
            return datattypeBackgroundKnowledge;
		}
	}
}
