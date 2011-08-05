package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

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
	
	private List<PatternMapping> mappings				= new ArrayList<PatternMapping>();
	private Map<Integer,Triple> triples 				= new HashMap<Integer,Triple>();
	private Map<Integer,Property> properties			= new HashMap<Integer,Property>();
	
	private int iteration;
	
	private TripleDao tripleDao		= (TripleDao) DaoFactory.getInstance().createDAO(TripleDao.class);
	private PropertyDao propertyDao	= (PropertyDao) DaoFactory.getInstance().createDAO(PropertyDao.class);
	
	public PatternSearchCommand(List<Triple> triples) {

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
		
		List<SearchResult> results = new ArrayList<SearchResult>(triples.size() * 10);//Collections.synchronizedList(new ArrayList<String>(labels.size()*10));
		List<List<Triple>> triplesSubLists = ListUtil.split(new ArrayList<Triple>(triples.values()), triples.size() / numberOfSearchThreads);
		
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
			String currentPattern = "";
			
			PatternMapping currentMapping = null;
			
			for ( SearchResult searchResult : results) {
				
				String propertyUri		= searchResult.getProperty();
				String patternString	= searchResult.getNaturalLanguageRepresentation();
				String label1			= searchResult.getFirstLabel();
				String label2			= searchResult.getSecondLabel();
				Integer documentId		= new Integer(searchResult.getIndexId());
				
				// next line is for the same property
				if ( propertyUri.equals(currentProperty) ) {
					
					// previous line had the same pattern
					if ( patternString.equals(currentPattern) ) {
						
						Pattern p = currentMapping.getPatternByNaturalLanguageRepresentation(currentPattern.hashCode());
						p.increaseNumberOfOccurrences();
						p.addLearnedFrom(label1 + "-;-" + label2);
						p.addLuceneDocIds(Integer.valueOf(documentId));
					}
					// new pattern found
					else {
						
						Pattern p = new Pattern(patternString, "");
						p.setFoundInIteration(this.iteration);
						p.addLearnedFrom(label1 + "-;-" + label2);
						p.setPatternMapping(currentMapping);
						p.addLuceneDocIds(Integer.valueOf(documentId));
						
						currentMapping.addPattern(p);
					}
				}
				// next line contains pattern for other property
				// so create a new pattern mapping
				else {
					
					// create it to use the proper hash function
					Property p = new Property();
					p.setUri(propertyUri);
					p = this.properties.get(p.hashCode());
					
					currentMapping = new PatternMapping(p);
					
					Pattern pattern = new Pattern(patternString, "");
					pattern.setFoundInIteration(this.iteration);
					pattern.addLearnedFrom(label1 + "-;-" + label2);
					pattern.setPatternMapping(currentMapping);
					pattern.addLuceneDocIds(documentId);
					
					currentMapping.addPattern(pattern); // do we need this?
					
					this.mappings.add(currentMapping);
				}
				
				currentProperty = propertyUri;
				currentPattern = patternString;
			}
			System.out.println("All pattern mappings read in " + (System.currentTimeMillis() - startPatternReading) + "ms!");
			this.logger.info("All pattern mappings read in " + (System.currentTimeMillis() - startPatternReading) + "ms!");
			
			long startSaveDB = System.currentTimeMillis();
			
			PatternMappingDao pmd = (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
			
			// delete all because we generate them once again
			pmd.deleteAllPatternMappings();
			
			for (PatternMapping mapping : this.mappings) {

				pmd.createAndSavePatternMapping(mapping);
			}
			System.out.println("All pattern mappings saved to database! " + (System.currentTimeMillis() - startSaveDB) + "ms!");
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
	 * @param iteration the iteration to set
	 */
	public void setIteration(Integer iteration) {
	
		this.iteration = iteration;
	}
	
	/**
	 * @return
	 */
	public List<PatternMapping> getPatternMappings(){
		
		return this.mappings;
	}


	
	/**
	 * @return the triples
	 */
	public Map<Integer, Triple> getTriples() {
	
		return triples;
	}
}
