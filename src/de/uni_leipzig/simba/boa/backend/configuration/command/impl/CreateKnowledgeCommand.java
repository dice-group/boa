package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.dao.rdf.TripleDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;
import de.uni_leipzig.simba.boa.backend.search.concurrent.CreateKnowledgeThread;
import de.uni_leipzig.simba.boa.backend.util.ListUtil;

/**
 * 
 * @author Daniel Gerber
 */
public class CreateKnowledgeCommand implements Command {

	private final NLPediaLogger logger = new NLPediaLogger(CreateKnowledgeCommand.class);
	
	private final PatternMappingDao patternMappingDao			= (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
	private final TripleDao tripleDao							= (TripleDao) DaoFactory.getInstance().createDAO(TripleDao.class);
	
	private final Integer NUMBER_OF_CREATE_KNOWLEDGE_THREADS	= Integer.valueOf(NLPediaSettings.getInstance().getSetting("number.of.create.knowledge.threads"));
	private final String NEW_TRIPLES_FILE							=	NLPediaSettings.getInstance().getSetting("ntriples.file.path") +  
																	"new_triples_score" + 
																	NLPediaSettings.getInstance().getSetting("score.threshold.create.knowledge") +
																	"_topn" + 
																	NLPediaSettings.getInstance().getSetting("top.n.pattern") +
																	".nt";
																	// something like: /path/to/file/triples_score0.7_topn20.nt
	private final String KNOWN_TRIPLES_FILE							=	NLPediaSettings.getInstance().getSetting("ntriples.file.path") +  
																	"known_triples_score" + 
																	NLPediaSettings.getInstance().getSetting("score.threshold.create.knowledge") +
																	"_topn" + 
																	NLPediaSettings.getInstance().getSetting("top.n.pattern") +
																	".nt";
																	// something like: /path/to/file/triples_score0.7_topn20.nt
	
	private static final String BACKGROUND_KNOWLEDGE = NLPediaSettings.getInstance().getSetting("bk.out.file");
	private final List<PatternMapping> patternMappingList;
	public static Map<Integer,Triple> tripleMap = null;

	public CreateKnowledgeCommand(List<PatternMapping> mappings) {

		if ( mappings != null ) this.patternMappingList = mappings;
		else this.patternMappingList = patternMappingDao.findAllPatternMappings();
		this.buildTripleMap();
	}
	
	public void execute(){
		
		// split the NAMED_ENTITY_TAG_MAPPINGS into numberOfThreads lists for numberOfThreads threads
		List<List<PatternMapping>> patternMappingSubLists	= ListUtil.split(patternMappingList, (patternMappingList.size() / NUMBER_OF_CREATE_KNOWLEDGE_THREADS) + 1 );
		List<Thread> threadList = new ArrayList<Thread>();
		
		// start all threads
//		for (int i = 0 ; i < NUMBER_OF_CREATE_KNOWLEDGE_THREADS ; i++ ) {
//			
		CreateKnowledgeThread t = new CreateKnowledgeThread(patternMappingList);//patternMappingSubLists.get(i));
		t.run();
//			t.setName("PatternFeatureExtractionCallable-" + (i + 1) + "-" + patternMappingSubLists.get(i).size());
//			threadList.add(i, t);
//			t.start();
//			System.out.println(t.getName() + " started!");
//			this.logger.info(t.getName() + " started!");
//		}
//		
//		// print the progress
//		Timer timer = new Timer();
//		timer.schedule(new PatternSearchPrintProgressTask(threadList), 0, 30000);
//		
//		// wait for all to finish
//		for ( Thread t : threadList ) {
//			
//			try {
//				t.join();	
//			}
//			catch (InterruptedException e) {
//				
//				this.logger.error("Interrupted exception for thread: " + t.getName() + " " +t);
//				e.printStackTrace();
//			}
//		}
//		timer.cancel();
		
		List<Triple> newTriples = new ArrayList<Triple>();
		List<Triple> knownTriples = new ArrayList<Triple>();
		
//		for ( Thread t: threadList ) {
			
			newTriples.addAll(((CreateKnowledgeThread)t).getNewTripleMap().values());
			knownTriples.addAll(((CreateKnowledgeThread)t).getKnownTripleMap().values());
//		}
		
		Comparator<Triple> comparator = new Comparator<Triple>(){

			@Override
			public int compare(Triple triple1, Triple triple2) {

				double x = (triple2.getConfidence() - triple1.getConfidence());
				if ( x < 0 ) return -1;
				if ( x == 0 ) return 0;
				return 1;
			}
		};
		
		Collections.sort(newTriples, comparator);
		Collections.sort(knownTriples, comparator);
		
		writeNewTriplesFile(newTriples);
		writeKnownTriplesFile(knownTriples);
	}

	/**
	 * @param filename 
	 * 
	 */
//	@Override
//	public void execute() {
//
//		// one thread per pattern mapping but only n threads get executed at the same time
//		for (PatternMapping mapping : this.patternMappingList ) {
//			
//			this.writeNTriplesFile(new CreateKnowledgeCallable(mapping).call());
//			this.logger.info("Added worker for mapping: " + mapping.getProperty().getUri());
//		}
//		
//		try {
//			
//			// create a thread pool and service for n threads/callable
//			ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_CREATE_KNOWLEDGE_THREADS);
//			this.logger.info("Created executorservice for knowledge creation of " + NUMBER_OF_CREATE_KNOWLEDGE_THREADS + " threads.");
//			
//			List<Callable<Collection<Triple>>> todo = new ArrayList<Callable<Collection<Triple>>>(this.patternMappingList.size());
//
//			// one thread per pattern mapping but only n threads get executed at the same time
//			for (PatternMapping mapping : this.patternMappingList ) {
//				
//				todo.add(new CreateKnowledgeCallable(mapping));
//				this.logger.info("Added worker for mapping: " + mapping.getProperty().getUri());
//			}
//			
//			// invoke all waits until all threads are finished
//			List<Future<Collection<Triple>>> answers = executorService.invokeAll(todo);
//			
//			for (Future<Collection<Triple>> future : answers) {
//			
//				Collection<Triple> triples = future.get();
//				this.logger.info("Calling write to file method with " + triples.size() + " triples.");
//				this.writeNTriplesFile(triples);
//			}
//			
//			// shut down the service and all threads
//			executorService.shutdown();
//		}
//		catch (ExecutionException e) {
//			
//			this.logger.error("Execption", e);
//			e.printStackTrace();
//		}
//		catch (InterruptedException e) {
//			
//			this.logger.error("Execption", e);
//			e.printStackTrace();
//		}
//		catch (Exception e) {
//			
//			this.logger.error("Execption", e);
//			e.printStackTrace();
//		}
//	}

	private void writeFile(String filename, Collection<Triple> resultList, boolean newTriples) {

		try {
			
			BufferedWriter tripleWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename, false), "UTF-8"));
			BufferedWriter metaWriter	= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename + ".meta", false), "UTF-8"));

			List<Triple> triples = new ArrayList<Triple>(resultList);
			
			for (int i = 0; i < triples.size(); i++) {
				
				Triple t = triples.get(i);
				tripleWriter.write(i + "\t" + t.getSubject().getUri() + "\t" + t.getProperty().getUri() + "\t" + t.getObject().getUri() + Constants.NEW_LINE_SEPARATOR);
				metaWriter.write(i + "\t" + t.getSubject().getLabel() + "\t" + t.getObject().getLabel() + "\t" + t.getConfidence() + "\t" + Constants.NEW_LINE_SEPARATOR);
				for ( String sentence : t.getLearnedFromSentences() ) {
					
					metaWriter.write("\t" + sentence + Constants.NEW_LINE_SEPARATOR);
				}
				metaWriter.write(Constants.NEW_LINE_SEPARATOR);
			}
			if ( newTriples ) tripleDao.batchSaveOrUpdate(triples);
			tripleWriter.close();
			metaWriter.close();
		}
		catch (UnsupportedEncodingException e1) {
			
			this.logger.error("UnsupportedEncodingException", e1);
		}
		catch (FileNotFoundException e1) {
			
			this.logger.error("UnsupportedEncodingException", e1);
		}
		catch (IOException e) {
			
			this.logger.error("UnsupportedEncodingException", e);
		}
	}
	
	private void writeKnownTriplesFile(Collection<Triple> resultList) {

		this.writeFile(KNOWN_TRIPLES_FILE, resultList, false);
	}
	
	private void writeNewTriplesFile(Collection<Triple> resultList) {

		this.writeFile(NEW_TRIPLES_FILE, resultList, true);
	}

	private void buildTripleMap() {

		if ( tripleMap == null ) {
			
			if ( !(new File(BACKGROUND_KNOWLEDGE)).exists() ) {
				
				tripleMap = new HashMap<Integer,Triple>();
				for (Triple t : tripleDao.findAllTriples()) {
					
					tripleMap.put(t.hashCode(), t);
				}
				try {
					
					ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(BACKGROUND_KNOWLEDGE)));
					oos.writeObject(tripleMap);
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
					tripleMap = (HashMap<Integer,Triple>) ois.readObject();
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
