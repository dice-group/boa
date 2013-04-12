package de.uni_leipzig.simba.boa.backend.backgroundknowledge;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;

import com.github.gerbsen.file.BufferedFileReader;
import com.github.gerbsen.file.FileUtil;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.concurrent.BackgroundKnowledgeReaderCallable;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.impl.DatatypePropertyBackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.impl.ObjectPropertyBackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.search.result.SearchResultReaderCallable;
import de.uni_leipzig.simba.boa.backend.wordnet.query.WordnetQuery;

public class BackgroundKnowledgeManager {
	
	private final NLPediaLogger logger				= new NLPediaLogger(BackgroundKnowledgeManager.class); 

	// both used for singleton pattern
	private static BackgroundKnowledgeManager INSTANCE = null;
	private BackgroundKnowledgeManager(){}
	
	public static Map<String,Set<String>> urisToSurfaceForms = new ConcurrentHashMap<String,Set<String>>();
	public static Map<Integer,Property> properties = new ConcurrentHashMap<Integer,Property>();
	
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
		
		List<BackgroundKnowledge> backgroundKnowledge = Collections.synchronizedList(new ArrayList<BackgroundKnowledge>());
		
		try {
        	
        	List<BackgroundKnowledgeReaderCallable> backgroundKnowledgeReader = new ArrayList<BackgroundKnowledgeReaderCallable>();
        	// collect all search results from the written files
        	ExecutorService executor = Executors.newFixedThreadPool(8);
            for (final File file : FileUtils.listFiles(new File(directory), HiddenFileFilter.VISIBLE, null)) 
            	backgroundKnowledgeReader.add(new BackgroundKnowledgeReaderCallable(backgroundKnowledge, file, isObjectProperty));
        	
			executor.invokeAll(backgroundKnowledgeReader);
			executor.shutdown();
			
			logger.info("Reading of background knowledge finished!");
		}
        catch (InterruptedException e) {
			
			e.printStackTrace();
		}

		return backgroundKnowledge;
	}
}
