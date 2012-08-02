package de.uni_leipzig.simba.boa.backend.concurrent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.io.FileUtils;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.BackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.search.concurrent.PatternSearchCallable;
import de.uni_leipzig.simba.boa.backend.search.concurrent.PatternSearchPrintProgressTask;
import de.uni_leipzig.simba.boa.backend.search.result.SearchResult;
import de.uni_leipzig.simba.boa.backend.util.ListUtil;

/**
 * 
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public class PatternSearchThreadManager {

    private static final NLPediaLogger logger = new NLPediaLogger(PatternSearchThreadManager.class);
    private static final int PATTERN_SEARCH_THREAD_POOL_SIZE = NLPediaSettings.getIntegerSetting("patternSearchThreadPoolSize");

    /**
     * 
     * @param backgroundKnowledge
     * @param numberOfTotalSearchThreads
     * @return
     */
    public static List<SearchResult> startPatternSearchCallables(Set<BackgroundKnowledge> backgroundKnowledge, int numberOfTotalSearchThreads) {

        List<SearchResult> results = new ArrayList<SearchResult>();
        
        try {
            
            // we create as much threads as we have pattern mappings, so every mapping needs to have one list of background knowledge
            List<List<BackgroundKnowledge>> backgroundKnowledgeSubLists = ListUtil.split(new ArrayList<BackgroundKnowledge>(backgroundKnowledge), (backgroundKnowledge.size() / numberOfTotalSearchThreads) + 1);

            // create a thread pool and service for n threads/callable
            ExecutorService executorService = Executors.newFixedThreadPool(PATTERN_SEARCH_THREAD_POOL_SIZE);
            logger.info("Created executorservice for pattern search with " + numberOfTotalSearchThreads + 
                    " threads and a thread pool of size " + PATTERN_SEARCH_THREAD_POOL_SIZE + ".");
            
            List<Callable<Collection<SearchResult>>> todo = new ArrayList<Callable<Collection<SearchResult>>>();
            
            int i = 1;
            // one thread per pattern mapping but only n threads get executed at the same time
            for (List<BackgroundKnowledge> backgroundKnowledgeSubList : backgroundKnowledgeSubLists ) {
                
                PatternSearchCallable psc = new PatternSearchCallable(backgroundKnowledgeSubList);
                psc.setName("PatternSearchCallable-" + i++);
                todo.add(psc);
                logger.info("Create thread for " + backgroundKnowledgeSubList.size() + " triples of background knowledge.");
            }
            
            // start the timer which prints every 30s the progress of the callables
            Timer timer = new Timer();
            timer.schedule(new PatternSearchPrintProgressTask(todo), 0, 30000);
            
            // delete every file which is in the search result path and create the directory again
            FileUtils.deleteDirectory(new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.SEARCH_RESULT_PATH));
            new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.SEARCH_RESULT_PATH).mkdir();
            
            // invoke all waits until all threads are finished
            List<Future<Collection<SearchResult>>> answers = executorService.invokeAll(todo);
            
            // all threads have finished so we can shut down the progess printing
            timer.cancel();
            
            // collect all the results
            for (Future<Collection<SearchResult>> future : answers) results.addAll(future.get());
            
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
        catch (IOException e) {

            e.printStackTrace();
        }
        
        return results;
    }
}
