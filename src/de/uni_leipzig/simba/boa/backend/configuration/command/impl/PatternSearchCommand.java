package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import org.apache.commons.lang.StringUtils;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.crawl.RelationFinder;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
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
	private final List<PatternMapping> mappings			= new ArrayList<PatternMapping>();
	private Integer foundInIteration 					= null;
	
	@Override
	public void execute() {

		Date startSearchDate = new Date();
		
		List<String[]> labels =  RelationFinder.getRelationFromFile("");
		Collections.shuffle(labels); // some threads are always faster then other, shuffle them to be more even on execution time
		
		System.out.println("Number of search operations: " + labels.size() + " for input file " + NLPediaSettings.getInstance().getSetting("labelOutputFile"));
		System.out.println("Number of search threads: " + NLPediaSettings.getInstance().getSetting("numberOfSearchThreads"));
		System.out.println("Number of allowed documents: " + NLPediaSettings.getInstance().getSetting("maxNumberOfDocuments"));
		System.out.println("Index directory: " + NLPediaSettings.getInstance().getSetting("sentenceIndexDirectory"));
		System.out.println("Hibernate connection: " + NLPediaSettings.getInstance().getSetting("hibernateConnectionUrl"));
		
		this.logger.info("Number of search operations: " + labels.size() + " for input file " + NLPediaSettings.getInstance().getSetting("labelOutputFile"));
		this.logger.info("Number of search threads: " + NLPediaSettings.getInstance().getSetting("numberOfSearchThreads"));
		this.logger.info("Number of allowed documents: " + NLPediaSettings.getInstance().getSetting("maxNumberOfDocuments"));
		this.logger.info("Index directory: " + NLPediaSettings.getInstance().getSetting("sentenceIndexDirectory"));
		this.logger.info("Hibernate connection: " + NLPediaSettings.getInstance().getSetting("hibernateConnectionUrl"));
		
		int numberOfSearchThreads = new Integer(NLPediaSettings.getInstance().getSetting("numberOfSearchThreads")).intValue();
		
		List<SearchResult> results = new ArrayList<SearchResult>(labels.size() * 10);//Collections.synchronizedList(new ArrayList<String>(labels.size()*10));
		
		List<List<String[]>> labelSubLists = ListUtil.split(labels, labels.size() / numberOfSearchThreads);
		
		List<Thread> threadList = new ArrayList<Thread>();
		
		for (int i = 0 ; i < numberOfSearchThreads ; i++ ) {
			
				Thread t = new PatternSearchThread(labelSubLists.get(i));
				t.setName("PatternSearchThread-" + (i + 1) + "-" + labelSubLists.get(i).size());
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
		labelSubLists = null;
		labels = null;
		Runtime.getRuntime().gc();
		
		System.out.println("All threads finished in " + (System.currentTimeMillis() - startSearchDate.getTime()) + "ms!");
		this.logger.info("All threads finished in " + (System.currentTimeMillis() - startSearchDate.getTime()) + "ms!");
		
		long startPatternReading = System.currentTimeMillis();
		
		try {

			Collections.sort(results, new SearchResultComparator());
		
			int size = results.size();
			
			System.out.println("There are " + size + " strings in the result list");
			this.logger.info("There are " + size + " strings in the result list");
			
			String currentProperty = "";
			String currentPattern = "";
			
			PatternMapping currentMapping = null;
			
			for ( SearchResult searchResult : results) {
				
				String propertyUri		= searchResult.getProperty();
				String patternString	= searchResult.getNaturalLanguageRepresentation();
				String range			= searchResult.getRdfsRange();
				String domain			= searchResult.getRdfsDomain();
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
						p.setFoundInIteration(this.foundInIteration);
						p.addLearnedFrom(label1 + "-;-" + label2);
						p.setPatternMapping(currentMapping);
						p.addLuceneDocIds(Integer.valueOf(documentId));
						
						currentMapping.addPattern(p);
					}
				}
				// next line contains pattern for other property
				// so create a new pattern mapping
				else {
					
					String label = StringUtils.join(propertyUri.replace("http://dbpedia.org/ontology/", "").split("(?=\\p{Upper})"), " ");
					Property p = new Property(propertyUri, label , range, domain);
					
					currentMapping = new PatternMapping(p);
					
					Pattern pattern = new Pattern(patternString, "");
					pattern.setFoundInIteration(this.foundInIteration);
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
	
	
	/**
	 * @param foundInIteration the foundInIteration to set
	 */
	public void setFoundInIteration(Integer foundInIteration) {
	
		this.foundInIteration = foundInIteration;
	}
}
