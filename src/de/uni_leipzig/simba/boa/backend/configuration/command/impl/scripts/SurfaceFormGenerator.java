package de.uni_leipzig.simba.boa.backend.configuration.command.impl.scripts;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import de.danielgerber.file.FileUtil;
import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.BackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.impl.DatatypePropertyBackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.impl.ObjectPropertyBackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.ontology.ClassIndexer;

/**
 * This thing needs at least 4GB of RAM.
 * 
 * 
 * @author gerb
 */
public class SurfaceFormGenerator {

	private static NLPediaLogger logger = new NLPediaLogger(SurfaceFormGenerator.class);
	private Map<String,Set<String>> urisToLabels;
//	private Map<String,String> classUrisToLabels;
	
	private static SurfaceFormGenerator INSTANCE = null;
//	private ClassIndexer classIndexer = new ClassIndexer();
	
	private SurfaceFormGenerator() { 
		
		initializeSurfaceForms();
	}
	
	/**
	 * @return
	 */
	public static SurfaceFormGenerator getInstance() {
		
		if ( SurfaceFormGenerator.INSTANCE == null ) {
			
			SurfaceFormGenerator.INSTANCE = new SurfaceFormGenerator();
		}
		
		return SurfaceFormGenerator.INSTANCE;
	}
	
	

	/**
	 * 
	 */
	private void initializeSurfaceForms() {
		
		SurfaceFormGenerator.logger.info("Intializing surface forms...");
		
		List<String> surfaceForms	= FileUtil.readFileInList(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.BACKGROUND_KNOWLEDGE_PATH + NLPediaSettings.BOA_LANGUAGE + "_uri_surface_form.tsv", "UTF-8");
//		List<String> classLabels	= FileUtil.readFileInList(NLPediaSettings.BOA_BASE_DIRECTORY + "backgroundknowledge/class_labels_" + NLPediaSettings.BOA_LANGUAGE + ".tsv", "UTF-8");
		
		this.urisToLabels = new HashMap<String,Set<String>>(); 
//		this.classUrisToLabels = new HashMap<String,String>();
		
		// initialize the surface forms from dbpedia spotlight 
		for ( String line : surfaceForms ) {
			
			String[] lineParts = line.split("\t");
			String[] surfaceFormsPart = Arrays.copyOfRange(lineParts, 1, lineParts.length);
			Set<String> filteredSurfaceForms = new HashSet<String>();
			
			for ( String surfaceForm : surfaceFormsPart) {
			    
			    if ( surfaceForm.length() >= NLPediaSettings.getIntegerSetting("surfaceFormMinimumLength") ) filteredSurfaceForms.add(surfaceForm);
			}
			this.urisToLabels.put(lineParts[0], filteredSurfaceForms);
		}
//		// initialize the class labels which can also be used a sort of coreference resolution mechanism
//		for ( String classLabel : classLabels ){
//			
//			String[] lineParts = classLabel.split("\t");
//			this.classUrisToLabels.put(lineParts[0], lineParts[1]);
//		}
		SurfaceFormGenerator.logger.info("Finished intializing surface forms! Found " + urisToLabels.size() + 
				" dbpedia spotlight surfaceforms");// and " + classUrisToLabels.size() + " class labels.");
		
//		this.classIndexer.index((OntModel) ModelFactory.createOntologyModel().read(("file://"+NLPediaSettings.BOA_BASE_DIRECTORY + "backgroundknowledge/dbpedia_3.7.owl")));
//		SurfaceFormGenerator.logger.info("Indexing of ontology successful!");
	}
	
	/**
	 * 
	 * @param backgroundKnowledge
	 * @return
	 */
	public BackgroundKnowledge createSurfaceFormsForBackgroundKnowledge(BackgroundKnowledge backgroundKnowledge) {
		
		if ( backgroundKnowledge instanceof ObjectPropertyBackgroundKnowledge ) {
			
			return this.createSurfaceFormsForObjectProperty((ObjectPropertyBackgroundKnowledge) backgroundKnowledge);
		} 
		if ( backgroundKnowledge instanceof DatatypePropertyBackgroundKnowledge ) {
			
			return this.createSurfaceFormsForDatatypeProperty((DatatypePropertyBackgroundKnowledge) backgroundKnowledge);
		}
		throw new RuntimeException("background knowledge of wrong type found: "  + backgroundKnowledge.getClass()); 
	}
	
	/**
	 * 
	 * @param backgroundKnowledge
	 * @return
	 */
	private BackgroundKnowledge createSurfaceFormsForObjectProperty(ObjectPropertyBackgroundKnowledge objectPropertyBackgroundKnowledge) {

		String subjectUri	= objectPropertyBackgroundKnowledge.getSubjectUri();
		String objectUri	= objectPropertyBackgroundKnowledge.getObjectUri();
//		String domain		= objectPropertyBackgroundKnowledge.getRdfsDomain();
//		String range		= objectPropertyBackgroundKnowledge.getRdfsRange();
		
		Set<String> subjectSurfaceForms = new HashSet<String>();
		subjectSurfaceForms.add(objectPropertyBackgroundKnowledge.getSubjectLabel());
		
		// we found labels for the subject in the surface form file
		if ( this.urisToLabels.containsKey(subjectUri) ) {
			
		    subjectSurfaceForms = urisToLabels.get(subjectUri);
			logger.debug("Found " + subjectSurfaceForms.size() + " for subject in spotlight");
		}
//		// add the class labels and special ons for person
//		for (String classUri : classIndexer.getSuperClassUrisForClassUri(Constants.DBPEDIA_ONTOLOGY_PREFIX + domain, NLPediaSettings.BOA_LANGUAGE)) {
//
//            if ( classUri.equalsIgnoreCase("Person") && NLPediaSettings.BOA_LANGUAGE.equals("en") ) {
//                subjectSurfaceForms.add("he");
//                subjectSurfaceForms.add("she");
//            }
//            if ( classUri.equalsIgnoreCase("Person") && NLPediaSettings.BOA_LANGUAGE.equals("de") ) {
//                subjectSurfaceForms.add("er");
//                subjectSurfaceForms.add("sie");
//            }
//            subjectSurfaceForms.add(this.classUrisToLabels.get(classUri));
//        }
        logger.debug("Found " + subjectSurfaceForms.size() + " at all!");
		subjectSurfaceForms.removeAll(Arrays.asList("", null));
		objectPropertyBackgroundKnowledge.setSubjectSurfaceForms(subjectSurfaceForms);
		
		// ################################################################################
		// ################################################################################
		// ################################################################################
		
		Set<String> objectSurfaceForms = new HashSet<String>();
		objectSurfaceForms.add(objectPropertyBackgroundKnowledge.getObjectLabel());
		
		// we found labels for the object in the surface form file
		if ( this.urisToLabels.containsKey(objectUri) ) {
			
		    objectSurfaceForms = urisToLabels.get(objectUri);
			logger.debug("Found " + objectSurfaceForms.size() + " for subject in spotlight");
		}
//	      // add the class labels and special ons for person
//		for (String classUri : classIndexer.getSuperClassUrisForClassUri(Constants.DBPEDIA_ONTOLOGY_PREFIX + range, NLPediaSettings.BOA_LANGUAGE)) {
//            
//            if ( classUri.equalsIgnoreCase("Person") && NLPediaSettings.BOA_LANGUAGE.equals("en") ) {
//                objectSurfaceForms.add("he");
//                objectSurfaceForms.add("she");
//            }
//            if ( classUri.equalsIgnoreCase("Person") && NLPediaSettings.BOA_LANGUAGE.equals("de") ) {
//                objectSurfaceForms.add("er");
//                objectSurfaceForms.add("sie");
//            }
//            objectSurfaceForms.add(this.classUrisToLabels.get(classUri));
//        }
        logger.debug("Found " + objectSurfaceForms.size() + " at all");
		
		objectSurfaceForms.removeAll(Arrays.asList("", null));
        objectPropertyBackgroundKnowledge.setObjectSurfaceForms(objectSurfaceForms);
        
		return objectPropertyBackgroundKnowledge;
	}
	
	/** 
	 * @param backgroundKnowledge
	 * @return
	 */
	private BackgroundKnowledge createSurfaceFormsForDatatypeProperty(DatatypePropertyBackgroundKnowledge backgroundKnowledge) {

		// TODO implement this code
		// dummy code
		return backgroundKnowledge;
	}
}