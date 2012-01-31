/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.featureextraction.FeatureExtractionPair;
import de.uni_leipzig.simba.boa.backend.featureextraction.concurrent.PatternFeatureExtractionCallable;
import de.uni_leipzig.simba.boa.backend.featureextraction.concurrent.PatternFeatureExtractionPrintProgressTask;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.util.ListUtil;


/**
 * @author gerb
 *
 */
public class PatternFeatureExtractionThreadManager {

    private static final NLPediaLogger logger                               = new NLPediaLogger(PatternFeatureExtractionThreadManager.class);
    private static final int PATTERN_FEATURE_EXTRACTION_THREAD_POOL_SIZE    = NLPediaSettings.getInstance().getIntegerSetting("patternFeatureExtractionThreadPoolSize");
    
    public static List<FeatureExtractionPair> startFeatureExtractionCallables(Set<PatternMapping> patternMappings, int numberOfTotalFeatureExtractionThreads) {

        List<FeatureExtractionPair> results = new ArrayList<FeatureExtractionPair>();
        
        // prepare pairs so that they can be better distributed to extraction threads 
        Set<FeatureExtractionPair> patternMappingPattern = new HashSet<FeatureExtractionPair>();
        for ( PatternMapping mapping : patternMappings ) 
            for ( Pattern pattern : mapping.getPatterns() )
                patternMappingPattern.add(new FeatureExtractionPair(mapping, pattern));
                    
        try {
            
            // devide them into numberOfTotalFeatureExtractionThreads sublists to better distribute them to threads
            List<List<FeatureExtractionPair>> featureExtractionPairsSubLists = ListUtil.split(new ArrayList<FeatureExtractionPair>(patternMappingPattern), (patternMappingPattern.size() / numberOfTotalFeatureExtractionThreads) + 1);

            // create a thread pool and service for n threads/callable 
            ExecutorService executorService = Executors.newFixedThreadPool(PATTERN_FEATURE_EXTRACTION_THREAD_POOL_SIZE);
            logger.info("Created executorservice for pattern feature extraction with " + numberOfTotalFeatureExtractionThreads + 
                    " threads and a thread pool of size " + PATTERN_FEATURE_EXTRACTION_THREAD_POOL_SIZE + ".");
            
            List<Callable<Collection<FeatureExtractionPair>>> todo = new ArrayList<Callable<Collection<FeatureExtractionPair>>>();
            
            int i = 1;
            // one thread per sublist of pattern mapping & pattern but only n threads get executed at the same time
            for (List<FeatureExtractionPair> featureExtractionPairsSubList : featureExtractionPairsSubLists ) {
                
                PatternFeatureExtractionCallable pfec = new PatternFeatureExtractionCallable(featureExtractionPairsSubList);
                pfec.setName("PatternFeatureExtractionCallable-" + i++);
                todo.add(pfec);
                logger.info("Create thread for " + featureExtractionPairsSubList.size() + " pairs of patterns & pattern mappings.");
            }
            
            // start the timer which prints every 30s the progress of the callables
            Timer timer = new Timer();
            timer.schedule(new PatternFeatureExtractionPrintProgressTask(todo), 0, 30000);
            
            // invoke all waits until all threads are finished
            List<Future<Collection<FeatureExtractionPair>>> answers = executorService.invokeAll(todo);
            
            // all threads have finished so we can shut down the progess printing
            timer.cancel();
            
            // collect all the results
            for (Future<Collection<FeatureExtractionPair>> future : answers) results.addAll(future.get());
            
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
