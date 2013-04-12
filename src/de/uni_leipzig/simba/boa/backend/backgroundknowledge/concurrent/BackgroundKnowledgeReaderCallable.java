/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.backgroundknowledge.concurrent;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import com.github.gerbsen.file.BufferedFileReader;
import com.github.gerbsen.file.FileUtil;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.BackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.BackgroundKnowledgeManager;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.impl.DatatypePropertyBackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.impl.ObjectPropertyBackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.search.result.SearchResultReaderCallable;
import de.uni_leipzig.simba.boa.backend.wordnet.query.WordnetQuery;

/**
 * @author gerb
 *
 */
public class BackgroundKnowledgeReaderCallable implements Callable<BackgroundKnowledgeReaderCallable> {

	private List<BackgroundKnowledge> backgroundKnowledge;
	private File file;
	private boolean isObjectProperty;
	private final NLPediaLogger logger				= new NLPediaLogger(BackgroundKnowledgeReaderCallable.class);

	public BackgroundKnowledgeReaderCallable(
			List<BackgroundKnowledge> backgroundKnowledge, File file, boolean isObjectProperty) {
		
		this.backgroundKnowledge = backgroundKnowledge;
		this.file = file;
		this.isObjectProperty = isObjectProperty;
	}

	@Override
	public BackgroundKnowledgeReaderCallable call() throws Exception {
		
		this.backgroundKnowledge.addAll(this.getBackgroundKnowledge(file.getAbsolutePath(), isObjectProperty));
		return this;
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
		if ( !BackgroundKnowledgeManager.urisToSurfaceForms.containsKey(subjectUri) ) {
		    
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
    		
    		BackgroundKnowledgeManager.urisToSurfaceForms.put(subjectUri, subjectLabels);
		}
		else {
		    
		    subjectLabels = BackgroundKnowledgeManager.urisToSurfaceForms.get(subjectUri);
		}
		
		// ################ OBJECT ############################
		
		if ( !BackgroundKnowledgeManager.urisToSurfaceForms.containsKey(objectUri) ) {
		
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
	        
	        BackgroundKnowledgeManager.urisToSurfaceForms.put(objectUri, objectLabels);
		}
		else {
		    
		    objectLabels = BackgroundKnowledgeManager.urisToSurfaceForms.get(objectUri);
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
            
            Property p = BackgroundKnowledgeManager.properties.get(predicate.hashCode());
            if ( p == null ) {
                
                p = new Property(predicate, domain, range);
                p.setSynsets(WordnetQuery.getSynsetsForAllSynsetTypes(predicate));
                BackgroundKnowledgeManager.properties.put(predicate.hashCode(), p);
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
            
		    Property p = BackgroundKnowledgeManager.properties.get(predicate);
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
