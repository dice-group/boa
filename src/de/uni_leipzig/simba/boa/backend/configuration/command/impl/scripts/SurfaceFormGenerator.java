package de.uni_leipzig.simba.boa.backend.configuration.command.impl.scripts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;

import weka.core.tokenizers.NGramTokenizer;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import de.danielgerber.file.FileUtil;
import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.BackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.impl.DatatypePropertyBackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.impl.ObjectPropertyBackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Resource;
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
	private Map<String,String> classUrisToLabels;
	
	private static SurfaceFormGenerator INSTANCE = null;
	private ClassIndexer classIndexer = new ClassIndexer();
	
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
		List<String> classLabels	= FileUtil.readFileInList(NLPediaSettings.BOA_BASE_DIRECTORY + "backgroundknowledge/class_labels_" + NLPediaSettings.BOA_LANGUAGE + ".tsv", "UTF-8");
		
		this.urisToLabels = new HashMap<String,Set<String>>(); 
		this.classUrisToLabels = new HashMap<String,String>();
		
		// initialize the surface forms from dbpedia spotlight 
		for ( String line : surfaceForms ) {
			
			String[] lineParts = line.split("\t");
			this.urisToLabels.put(lineParts[0], new HashSet<String>(Arrays.asList(Arrays.copyOfRange(lineParts, 1, lineParts.length))));
		}
		// initialize the class labels which can also be used a sort of coreference resolution mechanism
		for ( String classLabel : classLabels ){
			
			String[] lineParts = classLabel.split("\t");
			this.classUrisToLabels.put(lineParts[0], lineParts[1]);
		}
		SurfaceFormGenerator.logger.info("Finished intializing surface forms! Found " + urisToLabels.size() + 
				" dbpedia spotlight surfaceforms and " + classUrisToLabels.size() + " class labels.");
		
		this.classIndexer.index((OntModel) ModelFactory.createOntologyModel().read(("file://"+NLPediaSettings.BOA_BASE_DIRECTORY + "backgroundknowledge/dbpedia_3.7.owl")));
		SurfaceFormGenerator.logger.info("Indexing of ontology successful!");
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

		String subjectUri	= objectPropertyBackgroundKnowledge.getSubject().getUri();
		String objectUri	= objectPropertyBackgroundKnowledge.getObject().getUri();
		String domain		= objectPropertyBackgroundKnowledge.getProperty().getRdfsDomain();
		String range		= objectPropertyBackgroundKnowledge.getProperty().getRdfsRange();
		
		// we found labels for the subject in the surface form file
		if ( this.urisToLabels.containsKey(subjectUri) ) {
			
			Set<String> surfaceForms = urisToLabels.get(subjectUri);
			System.out.println("Found " + surfaceForms.size() + " for subject in spotlight");
			for (String classUri : classIndexer.getSuperClassUrisForClassUri(domain, NLPediaSettings.BOA_LANGUAGE)) {
				System.out.println(classUri + "  " + this.classUrisToLabels.get(classUri));
				surfaceForms.add(this.classUrisToLabels.get(classUri));
			}
			System.out.println("Found " + surfaceForms.size() + " at all");
			objectPropertyBackgroundKnowledge.setSubjectSurfaceForms(surfaceForms);
		}
		// we found labels for the object in the surface form file
		if ( this.urisToLabels.containsKey(objectUri) ) {
			
			Set<String> surfaceForms = urisToLabels.get(objectUri);
			System.out.println("Found " + surfaceForms.size() + " for subject in spotlight");
			for (String classUri : classIndexer.getSuperClassUrisForClassUri(range, NLPediaSettings.BOA_LANGUAGE)) {
				
				surfaceForms.add(this.classUrisToLabels.get(classUri));
			}
			System.out.println("Found " + surfaceForms.size() + " at all");
			objectPropertyBackgroundKnowledge.setObjectSurfaceForms(surfaceForms);
		}
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