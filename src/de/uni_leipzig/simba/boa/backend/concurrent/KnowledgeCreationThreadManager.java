package de.uni_leipzig.simba.boa.backend.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.uni_leipzig.simba.boa.backend.backgroundknowledge.BackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.knowledgecreation.cuncurrent.KnowledgeCreationCallable;
import de.uni_leipzig.simba.boa.backend.knowledgecreation.cuncurrent.KnowledgeCreationPrintProgressTask;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;
import de.uni_leipzig.simba.boa.backend.search.concurrent.PatternSearchCallable;
import de.uni_leipzig.simba.boa.backend.search.concurrent.PatternSearchPrintProgressTask;
import de.uni_leipzig.simba.boa.backend.search.result.SearchResult;
import de.uni_leipzig.simba.boa.backend.util.ListUtil;

/**
 * 
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public class KnowledgeCreationThreadManager {

    private static final NLPediaLogger logger = new NLPediaLogger(KnowledgeCreationThreadManager.class);
    private static final int KNOWLEDGE_CREATION_THREAD_POOL_SIZE = NLPediaSettings.getInstance().getIntegerSetting("knowledgeCreationThreadPoolSize");

    /**
     * 
     * @param patterns
     * @param numberOfTotalSearchThreads
     * @return
     */
    public static Map<String,List<Triple>> startKnowledgeCreationCallables(Set<PatternMappingPatternPair> patterns, int numberOfTotalSearchThreads) {

        Map<String,List<Triple>> results = new HashMap<String,List<Triple>>();
        
        try {
            
            // we create numberOfTotalSearchThreads threads to split the patterns for the threads
            List<List<PatternMappingPatternPair>> patternMappingPatternSubLists = ListUtil.split(new ArrayList<PatternMappingPatternPair>(patterns), (patterns.size() / numberOfTotalSearchThreads) + 1);

            // create a thread pool and service for n threads/callable
            ExecutorService executorService = Executors.newFixedThreadPool(KNOWLEDGE_CREATION_THREAD_POOL_SIZE);
            logger.info("Created executorservice for knowledge creation with " + numberOfTotalSearchThreads + 
                    " threads and a thread pool of size " + KNOWLEDGE_CREATION_THREAD_POOL_SIZE + ".");
            
            List<Callable<Collection<Map<String,List<Triple>>>>> todo = new ArrayList<Callable<Collection<Map<String,List<Triple>>>>>();
            
            int i = 1;
            // distribute the pattern/pattern mapping pairs equally
            for (List<PatternMappingPatternPair> patternMappingPatternPairSubList : patternMappingPatternSubLists ) {
                
                KnowledgeCreationCallable psc = new KnowledgeCreationCallable(patternMappingPatternPairSubList);
                psc.setName("KnowledgeCreationCallable-" + i++);
                todo.add(psc);
                logger.info("Create thread for " + patternMappingPatternPairSubList.size() + " patterns.");
            }
            
            // start the timer which prints every 30s the progress of the callables
            Timer timer = new Timer();
            timer.schedule(new KnowledgeCreationPrintProgressTask(todo), 0, 30000);
            
            // invoke all waits until all threads are finished
            List<Future<Collection<Map<String,List<Triple>>>>> answers = executorService.invokeAll(todo);
            
            // all threads have finished so we can shut down the progess printing
            timer.cancel();
            
            // collect all the results
            for (Future<Collection<Map<String,List<Triple>>>> future : answers) {
                
                // there should be only one element in the future object
                Collection<Map<String, List<Triple>>> answer = future.get();
                for ( Map<String, List<Triple>> knowledge : answer ) {
                    for (Map.Entry<String, List<Triple>> mappings : knowledge.entrySet() ) {
                        
                        // the mapping is from property uri to list of triples with that property
                        if ( results.containsKey(mappings.getKey()) ) results.get(mappings.getKey()).addAll(mappings.getValue());
                        else {
                            
                            results.put(mappings.getKey(), mappings.getValue());
                        }
                    }
                }
            }
            
            // shut down the service and all threads
            executorService.shutdown();
        }
        catch (ExecutionException e) {
            
            e.printStackTrace();
            String error = "Could not execute callables!";
            logger.error(error, e);
            throw new RuntimeException(error, e);
        }
        catch (InterruptedException e) {
            
            e.printStackTrace();
            String error = "Threads got interrupted!";
            logger.error(error, e);
            throw new RuntimeException(error, e);
        }
        
        return results;
    }
}
