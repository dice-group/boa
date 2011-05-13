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
import de.uni_leipzig.simba.boa.backend.crawl.RelationFinder;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.nlp.NamedEntityRecognizer;
import de.uni_leipzig.simba.boa.backend.search.concurrent.PatternSearchThread;
import de.uni_leipzig.simba.boa.backend.search.concurrent.PrintProgressTask;
import de.uni_leipzig.simba.boa.backend.util.ListUtil;

/**
 * 
 * @author Daniel Gerber
 */
public class PatternSearchCommand implements Command {

	private final NLPediaLogger logger					= new NLPediaLogger(PatternSearchCommand.class);
	private final NamedEntityRecognizer ner				= new NamedEntityRecognizer();
	private final Map<String,PatternMapping> mappings	= new HashMap<String,PatternMapping>();
	private Integer foundInIteration 					= null;
	
	@Override
	public void execute() {

		Date startSearchDate = new Date();
		
		List<String[]> labels =  RelationFinder.getRelationFromFile(NLPediaSettings.getInstance().getSetting("labelOutputFile"));
		
		System.out.println("Number of search operations: " + labels.size());
		this.logger.info("Number of search operations: " + labels.size());
		
		int numberOfSearchThreads = new Integer(NLPediaSettings.getInstance().getSetting("numberOfSearchThreads")).intValue();
		
		List<String> results = new ArrayList<String>(labels.size() * 10);//Collections.synchronizedList(new ArrayList<String>(labels.size()*10));
		
		List<List<String[]>> labelSubLists = ListUtil.split(labels, labels.size() / numberOfSearchThreads);
		
		List<Thread> threadList = new ArrayList<Thread>();
		
		NamedEntityRecognizer ner = new NamedEntityRecognizer();
		
		for (int i = 0 ; i < numberOfSearchThreads ; i++ ) {
			
				Thread t = new PatternSearchThread(labelSubLists.get(i), results, ner);
				t.setName("PatternSearchThread-" + (i + 1));
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
		
		for ( Thread t : threadList ) t = null;
		threadList = null;
		labelSubLists = null;
		labels = null;
		Runtime.getRuntime().gc();
		
		System.out.println("All threads finished in " + (System.currentTimeMillis() - startSearchDate.getTime()) + "ms!");
		this.logger.info("All threads finished in " + (System.currentTimeMillis() - startSearchDate.getTime()) + "ms!");
		
		long startPatternReading = System.currentTimeMillis();
		
		try {

			Collections.sort(results);
			
			String currentProperty = "";
			String currentPattern = "";
			
			PatternMapping currentMapping = null;
			
			for ( String patternMappingString : results) {
				
				String[] line = patternMappingString.split("-;-");
				String property			= line[0];
				String patternString	= line[1];
				String range			= line[2];
				String domain			= line[3];
				String label1			= line[4];
				String label2			= line[5];
				
				// next line is for the same property
				if ( property.equals(currentProperty) ) {
					
					// previous line had the same pattern
					if ( patternString.equals(currentPattern) ) {
						
						Pattern p = currentMapping.getPatternByNaturalLanguageRepresentation(currentPattern);
						p.increaseNumberOfOccurrences();
						p.addLearnedFrom(label1 + "-;-" + label2);
					}
					// new pattern found
					else {
						
						Pattern p = new Pattern(patternString, "");
						p.setFoundInIteration(this.foundInIteration);
						p.addLearnedFrom(label1 + "-;-" + label2);
						p.setPatternMapping(currentMapping);
						
						currentMapping.addPattern(p);
					}
				}
				// next line contains pattern for other property
				// so create a new pattern mapping
				else {
					
					currentMapping = new PatternMapping(property);
					currentMapping.setRdfsRange(range);
					currentMapping.setRdfsDomain(domain);
					
					Pattern p = new Pattern(patternString, "");
					p.setFoundInIteration(this.foundInIteration);
					p.addLearnedFrom(label1 + "-;-" + label2);
					p.setPatternMapping(currentMapping);
					
					currentMapping.addPattern(p);
					
					this.mappings.put(property, currentMapping);
				}
				
				currentProperty = property;
				currentPattern = patternString;
			}
			System.out.println("All pattern mappings read in " + (System.currentTimeMillis() - startPatternReading) + "ms!");
			this.logger.info("All pattern mappings read in " + (System.currentTimeMillis() - startPatternReading) + "ms!");
			
			long startSaveDB = System.currentTimeMillis();
			
			PatternMappingDao pmd = (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);

			for (PatternMapping mapping : this.mappings.values()) {

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
