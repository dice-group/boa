package de.uni_leipzig.simba.boa.backend.backgroundknowledge;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;

import de.danielgerber.file.BufferedFileReader;
import de.danielgerber.file.FileUtil;
import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.impl.DatatypePropertyBackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.impl.ObjectPropertyBackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Resource;
import de.uni_leipzig.simba.boa.backend.wordnet.query.WordnetQuery;

public class BackgroundKnowledgeManager {
	
	private final NLPediaLogger logger				= new NLPediaLogger(BackgroundKnowledgeManager.class); 

	// both used for singleton pattern
	private static BackgroundKnowledgeManager INSTANCE = null;
	private BackgroundKnowledgeManager(){}
	
	/**
	 * needed to remove text in brackets from labels
	 */
	private Pattern pattern = Pattern.compile("\\(.+?\\)");
	
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
	public List<BackgroundKnowledge> getBackgroundKnowledgeInDirectory(String directory) {
		
		List<BackgroundKnowledge> backgroundKnowledge = new ArrayList<BackgroundKnowledge>();

		for ( File file : FileUtils.listFiles(new File(directory), HiddenFileFilter.VISIBLE, TrueFileFilter.INSTANCE) ) {
			
			this.logger.info(String.format("Reading background knowledge from file %s", file.getName()));
			
			BufferedFileReader br = FileUtil.openReader(file.getAbsolutePath(), "UTF-8");
			
			String line;
			while ((line = br.readLine()) != null) {
				
				backgroundKnowledge.add(this.createBackgroundKnowledge(line));
			}
			br.close();
		}
		return backgroundKnowledge;
	}
	
	/**
	 * Reads a line of a background knowledge file to a java POJO
	 * 
	 * @param line
	 * @return
	 */
	public BackgroundKnowledge createBackgroundKnowledge(String line) {
		
		String[] parts = line.split(Constants.BACKGROUND_KNOWLEDGE_VALUE_SEPARATOR_REGEX);
		
		// all subject information
		String subjectUri		= "";
		String subjectLabel		= "";
		String subjectLabels	= "";
		String subjectContext	= "";
		String subjectType		= "";
		
		// all object information
		String objectUri		= "";
		String objectLabel		= "";
		String objectLabels		= "";
		String objectContext	= "";
		String objectType		= "";
		
		// all predicate information
		String predicate		= parts[3];
		String predicateType	= parts[4].startsWith("http://") ? Constants.OWL_OBJECT_PROPERTY : Constants.OWL_DATATYPE_PROPERTY;
		String range			= parts[7].equals("null") ? null : parts[7];
		String domain			= parts[8].equals("null") ? null : parts[8];
		
		// ################ SUBJECT ############################
		
		// uri of the subject
		subjectUri 		= parts[0];
		// context like person: heist_(artist)
	    Matcher matcher = pattern.matcher(parts[1]);
	    while (matcher.find()) { subjectContext = matcher.group(); }
	    // subject label without text in brackets
		subjectLabel	= parts[1].replaceAll("\\(.+?\\)", "").trim();
		// labels from wikipedia surface forms
		subjectLabels	= parts[2];
		// rdf:type of the subject
		subjectType		= domain;
		
		// ################ OBJECT ############################
		
		// uri of the object
		objectUri		= parts[4];
		// context like person: heist_(artist)
	    matcher			= pattern.matcher(parts[5]);
	    while (matcher.find()) { objectContext = matcher.group(); }
		// object label without text in brackets
	    objectLabel		= parts[5].replaceAll("\\(.+?\\)", "").trim();
		// labels from wikipedia surface forms
		objectLabels	= parts[6];
		// rdf:type of the object
		objectType		= range;
		
		// ################ resources: subject, property, object ############################
		
		// create the subject 
		Resource sub = new Resource();
		sub.setUri(subjectUri);
		sub.setLabel(subjectLabel);
		sub.setSurfaceForms(subjectLabels);
		sub.setType(subjectType);
		if ( subjectContext.length() > 0 ) {
			sub.setContext(subjectContext.substring(1, subjectContext.length()-1));	
		}
		
		// create the property 
		Property p = new Property();
		p.setUri(predicate);
		p.setRdfsDomain(domain);
		p.setRdfsRange(range);
		p.setType(predicateType);
		p.setLabel(StringUtils.join(predicate.replace("http://dbpedia.org/ontology/", "").split("(?=\\p{Upper})"), " ").toLowerCase());
		p.setSynsets(StringUtils.join(WordnetQuery.getSynsetsForAllSynsetTypes(p.getLabel()), ","));
		
		// create the resource: object if not found
		Resource obj = new Resource();
		obj.setType(objectType);
		obj.setLabel(objectLabel);
		obj.setSurfaceForms(objectLabels);
			
		// object properties have there own labels
		if ( predicateType.equals(Constants.OWL_OBJECT_PROPERTY) ) {
			
			obj.setUri(objectUri);
			// only resources have context information
			if ( objectContext.length() > 0 ) {
				obj.setContext(objectContext.substring(1, objectContext.length()-1));
			}
			return new ObjectPropertyBackgroundKnowledge(sub, p, obj);
		}
		else {
			
			// they dont have uris so create random strings
			obj.setUri(UUID.randomUUID().toString());
			return new DatatypePropertyBackgroundKnowledge(sub, p, obj);
		}
	}
}
