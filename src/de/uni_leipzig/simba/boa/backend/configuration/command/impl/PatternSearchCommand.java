package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import de.uni_leipzig.simba.boa.backend.NLPedia;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.dao.rdf.PropertyDao;
import de.uni_leipzig.simba.boa.backend.dao.rdf.TripleDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;
import de.uni_leipzig.simba.boa.backend.search.SearchResult;
import de.uni_leipzig.simba.boa.backend.search.SearchResultComparator;
import de.uni_leipzig.simba.boa.backend.search.concurrent.PatternSearchThread;
import de.uni_leipzig.simba.boa.backend.search.concurrent.PrintProgressTask;
import de.uni_leipzig.simba.boa.backend.util.ListUtil;

/**
 * 
 * @author Daniel Gerber
 */
public class PatternSearchCommand implements Command {

	private final NLPediaLogger logger					= new NLPediaLogger(PatternSearchCommand.class);
	
	private static final String BACKGROUND_KNOWLEDGE = NLPediaSettings.getInstance().getSetting("bk.out.file");
	private Map<Integer,PatternMapping> mappings		= new HashMap<Integer,PatternMapping>();
	private Map<Integer,Triple> triples 				= null;
	private Map<Integer,Property> properties			= new HashMap<Integer,Property>();
	private Map<Integer,Map<Integer,Pattern>> patterns	= new HashMap<Integer,Map<Integer,Pattern>>();
	
	private TripleDao tripleDao		= (TripleDao) DaoFactory.getInstance().createDAO(TripleDao.class);
	private PropertyDao propertyDao	= (PropertyDao) DaoFactory.getInstance().createDAO(PropertyDao.class);
	
	public PatternSearchCommand(List<Triple> triples) {

//		this.buildTripleMap();
		if ( triples != null ) this.triples = this.createTripleMap(triples);
		else this.triples	= this.createTripleMap(tripleDao.findAllTriples());
	}

	@Override
	public void execute() {

		Date startSearchDate = new Date();
		
		this.properties	= this.createPropertyMap(propertyDao.findAllProperties());
		
		System.out.println("Number of search operations: " + triples.size() + " for input file " + NLPediaSettings.getInstance().getSetting("labelOutputFile"));
		System.out.println("Number of search threads: " + NLPediaSettings.getInstance().getSetting("numberOfSearchThreads"));
		System.out.println("Number of allowed documents: " + NLPediaSettings.getInstance().getSetting("maxNumberOfDocuments"));
		System.out.println("Index directory: " + NLPediaSettings.getInstance().getSetting("sentenceIndexDirectory"));
		System.out.println("Hibernate connection: " + NLPediaSettings.getInstance().getSetting("hibernateConnectionUrl"));
		
		this.logger.info("Number of search operations: " + triples.size() + " for input file " + NLPediaSettings.getInstance().getSetting("labelOutputFile"));
		this.logger.info("Number of search threads: " + NLPediaSettings.getInstance().getSetting("numberOfSearchThreads"));
		this.logger.info("Number of allowed documents: " + NLPediaSettings.getInstance().getSetting("maxNumberOfDocuments"));
		this.logger.info("Index directory: " + NLPediaSettings.getInstance().getSetting("sentenceIndexDirectory"));
		this.logger.info("Hibernate connection: " + NLPediaSettings.getInstance().getSetting("hibernateConnectionUrl"));
		
		int numberOfSearchThreads = new Integer(NLPediaSettings.getInstance().getSetting("numberOfSearchThreads")).intValue();
		
		List<SearchResult> results = new ArrayList<SearchResult>(triples.size() * 10);
		List<List<Triple>> triplesSubLists = ListUtil.split(new ArrayList<Triple>(triples.values()), (triples.size() / numberOfSearchThreads) + 1 );
		
		List<Thread> threadList = new ArrayList<Thread>();
		
		for (int i = 0 ; i < numberOfSearchThreads ; i++ ) {
			
				Thread t = new PatternSearchThread(triplesSubLists.get(i));
				t.setName("PatternSearchThread-" + (i + 1) + "-" + triplesSubLists.get(i).size());
				threadList.add(i, t);
				t.start();
				System.out.println(t.getName() + " started!");
				this.logger.info(t.getName() + " started!");
		}
		
		Timer timer = new Timer();
		timer.schedule(new PrintProgressTask(threadList), 0, 30000);
		
		for ( Thread t : threadList ) {
			
			try {
				t.join();	
			}
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		timer.cancel();
		
		for ( Thread t: threadList ) {
			
			results.addAll(((PatternSearchThread)t).getResults());
		}
		
		for ( Thread t : threadList ) t = null;
		threadList = null;
		triplesSubLists = null;
		triples = null;
		Runtime.getRuntime().gc();
		
		System.out.println("All threads finished in " + (System.currentTimeMillis() - startSearchDate.getTime()) + "ms!");
		this.logger.info("All threads finished in " + (System.currentTimeMillis() - startSearchDate.getTime()) + "ms!");
		
		long startPatternReading = System.currentTimeMillis();
		
		try {

			Collections.sort(results, new SearchResultComparator());
		
			System.out.println("There are " + results.size() + " strings in the result list");
			this.logger.info("There are " + results.size() + " strings in the result list");
			
			String currentProperty = "";
			
			PatternMapping currentMapping = null;
			
			for ( SearchResult searchResult : results) {
				
				String propertyUri		= searchResult.getProperty();
				String patternString	= searchResult.getNaturalLanguageRepresentation();
				String label1			= searchResult.getFirstLabel();
				String label2			= searchResult.getSecondLabel();
				String posTagged		= searchResult.getPosTags();
				Integer documentId		= new Integer(searchResult.getIndexId());
	
				// next line is for the same property
				if ( propertyUri.equals(currentProperty) ) {
					
					// add the patterns to the list with the hash-code of the natural language representation
					Pattern pattern = this.patterns.get(propertyUri.hashCode()).get(patternString.hashCode()); //(patternString.hashCode());
					
					// pattern was not found, create a new pattern 
					if ( pattern == null ) {
						
						pattern = new Pattern(patternString);
						pattern.setFoundInIteration(IterationCommand.CURRENT_ITERATION_NUMBER);
						pattern.setPosTaggedString(posTagged);
						pattern.addLearnedFrom(label1 + "-;-" + label2);
						pattern.addPatternMapping(currentMapping);
						pattern.addLuceneDocIds(Integer.valueOf(documentId));
						
						if ( this.patterns.get(propertyUri.hashCode()) != null ) {
							
							this.patterns.get(propertyUri.hashCode()).put(patternString.hashCode(), pattern);
						}
						else {
							
							Map<Integer,Pattern> patternMap = new HashMap<Integer,Pattern>();
							patternMap.put(patternString.hashCode(), pattern);
							this.patterns.put(propertyUri.hashCode(), patternMap);
						}
						// add the current pattern to the current mapping
						currentMapping.addPattern(pattern);
					}
					// pattern already created, just add new values
					else {
						
						pattern.increaseNumberOfOccurrences();
						pattern.addLearnedFrom(label1 + "-;-" + label2);
						pattern.addLuceneDocIds(Integer.valueOf(documentId));
						pattern.addPatternMapping(currentMapping);
					}
				}
				// next line contains pattern for other property
				// so create a new pattern mapping and a new pattern
				else {
					
					// create it to use the proper hash function, the properties map has a COMPLETE list of all properties
					Property p = new Property();
					p.setUri(propertyUri);
					p = this.properties.get(p.hashCode());
					
					currentMapping = this.mappings.get(propertyUri.hashCode());
					
					if ( currentMapping == null ) {
						
						currentMapping = new PatternMapping(p);
					}
					
					Pattern pattern = new Pattern(patternString);
					pattern.setFoundInIteration(IterationCommand.CURRENT_ITERATION_NUMBER);
					pattern.setPosTaggedString(posTagged);
					pattern.addLearnedFrom(label1 + "-;-" + label2);
					pattern.addPatternMapping(currentMapping);
					pattern.addLuceneDocIds(documentId);
					
					currentMapping.addPattern(pattern);
					
					if ( this.patterns.get(propertyUri.hashCode()) != null ) {
						
						this.patterns.get(propertyUri.hashCode()).put(patternString.hashCode(), pattern);
					}
					else {
						
						Map<Integer,Pattern> patternMap = new HashMap<Integer,Pattern>();
						patternMap.put(patternString.hashCode(), pattern);
						this.patterns.put(propertyUri.hashCode(), patternMap);
					}
					this.mappings.put(propertyUri.hashCode(), currentMapping);
				}
				currentProperty = propertyUri;
			}
			System.out.println("All pattern mappings read in " + (System.currentTimeMillis() - startPatternReading) + "ms!");
			this.logger.info("All pattern mappings read in " + (System.currentTimeMillis() - startPatternReading) + "ms!");
			
			long startSaveDB = System.currentTimeMillis();
			
			PatternMappingDao pmd = (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
			// delete all because we generate them once again
			pmd.deleteAllPatternMappings();
			
			// filter the patterns which do not abide certain thresholds
			new PatternFilterCommand(this.mappings).execute();
			
			for (PatternMapping mapping : this.mappings.values()) {
				
				if ( mapping.getPatterns().size() > 0 ) {
					
					pmd.createAndSavePatternMapping(mapping);
				}
			}
			NLPedia.getCache().put(NLPedia.CACHE_KEY_PATTERN_MAPPING_LIST, this.mappings);
			
			System.out.println("All pattern mappings ("+this.mappings.size()+") saved to database! " + (System.currentTimeMillis() - startSaveDB) + "ms!");
			this.logger.info("All pattern mappings saved to database! " + (System.currentTimeMillis() - startSaveDB) + "ms!");
		}
		catch (Exception e) {

			e.printStackTrace();
			logger.error("Could not read file: " + NLPediaSettings.getInstance().getSetting("patternStoreFile"), e);
		}
		
		System.out.println("Searching took " + (System.currentTimeMillis() - startSearchDate.getTime()) + "ms.");
		this.logger.info("Searching took " + (System.currentTimeMillis() - startSearchDate.getTime()) + "ms.");
	}
	
	
	private Map<Integer, Property> createPropertyMap(List<Property> allProperties) {

		for (Property prop : allProperties) {
			
			this.properties.put(prop.hashCode(), prop);
		}
		return this.properties;
	}


	private Map<Integer, Triple> createTripleMap(List<Triple> allTriples) {

		for (Triple t : allTriples) {
			
			this.triples.put(t.hashCode(), t);
		}
		return this.triples;
	}

	/**
	 * @return
	 */
	public Map<Integer,PatternMapping> getPatternMappings(){
		
		return this.mappings;
	}


	
	/**
	 * @return the triples
	 */
	public Map<Integer, Triple> getTriples() {
	
		return triples;
	}
	
	private void buildTripleMap() {

		if ( triples == null ) {
			
			if ( !(new File(BACKGROUND_KNOWLEDGE)).exists() ) {
				
				triples = new HashMap<Integer,Triple>();
				for (Triple t : tripleDao.findAllTriples()) {
					
					triples.put(t.hashCode(), t);
				}
				try {
					
					ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(BACKGROUND_KNOWLEDGE)));
					oos.writeObject(triples);
					oos.close();
				}
				catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				
				try {
					
					ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(BACKGROUND_KNOWLEDGE)));
					triples = (HashMap<Integer,Triple>) ois.readObject();
					ois.close();
				}
				catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
