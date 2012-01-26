package de.uni_leipzig.simba.boa.backend.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.uni_leipzig.simba.boa.backend.backgroundknowledge.BackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;
import de.uni_leipzig.simba.boa.backend.search.concurrent.PatternSearchThread;
import de.uni_leipzig.simba.boa.backend.search.result.SearchResult;
import de.uni_leipzig.simba.boa.backend.util.ListUtil;


public class ThreadManager {

	private int NUMBER_OF_CREATE_KNOWLEDGE_THREADS = 1;
	private List<PatternMapping> patternMappingList;
	private List<BackgroundKnowledge> backgroundKnowledge;

	public void test(Class<? extends Callable> clazz){

		try {
			
			List<List<BackgroundKnowledge>> backgroundKnowledgeSubLists = 
					ListUtil.split(new ArrayList<BackgroundKnowledge>(backgroundKnowledge), (patternMappingList.size() / NUMBER_OF_CREATE_KNOWLEDGE_THREADS) + 1 );
			
			// create a thread pool and service for n threads/callable
			ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_CREATE_KNOWLEDGE_THREADS );
//			this.logger.info("Created executorservice for knowledge creation of " + NUMBER_OF_CREATE_KNOWLEDGE_THREADS + " threads.");
			
			List<Callable<Collection<SearchResult>>> todo = new ArrayList<Callable<Collection<SearchResult>>>();

			// one thread per pattern mapping but only n threads get executed at the same time
			for (List<BackgroundKnowledge> backgroundKnowledge : backgroundKnowledgeSubLists ) {
				
				todo.add(new PatternSearchThread(backgroundKnowledge));
//				this.logger.info("Added worker for mapping: " + mapping.getProperty().getUri());
			}
			
			// invoke all waits until all threads are finished
			List<Future<Collection<Triple>>> answers = executorService.invokeAll(todo);
			
			for (Future<Collection<Triple>> future : answers) {
			
				Collection<Triple> triples = future.get();
				this.logger.info("Calling write to file method with " + triples.size() + " triples.");
				this.writeNTriplesFile(triples);
			}
			
			// shut down the service and all threads
			executorService.shutdown();
		}
		catch (ExecutionException e) {
			
			this.logger.error("Execption", e);
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			
			this.logger.error("Execption", e);
			e.printStackTrace();
		}
		catch (Exception e) {
			
			this.logger.error("Execption", e);
			e.printStackTrace();
		}
	}
}
