/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.patternsearch.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import com.github.gerbsen.encoding.Encoder.Encoding;
import com.github.gerbsen.file.BufferedFileReader;
import com.github.gerbsen.file.FileUtil;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.concurrent.PatternSearchThreadManager;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.GeneralizedPattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.SupportInstance;
import de.uni_leipzig.simba.boa.backend.entity.pattern.filter.PatternFilter;
import de.uni_leipzig.simba.boa.backend.entity.pattern.filter.PatternFilterFactory;
import de.uni_leipzig.simba.boa.backend.entity.pattern.impl.SubjectPredicateObjectPattern;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.NaturalLanguageProcessingToolFactory;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.partofspeechtagger.PartOfSpeechTagger;
import de.uni_leipzig.simba.boa.backend.persistance.serialization.SerializationManager;
import de.uni_leipzig.simba.boa.backend.pipeline.module.backgroundknowledgecollector.AbstractDefaultBackgroundKnowledgeCollectorModule;
import de.uni_leipzig.simba.boa.backend.pipeline.module.patternsearch.AbstractPatternSearchModule;
import de.uni_leipzig.simba.boa.backend.pipeline.module.patternsearch.concurrent.PatternPosTagCallable;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.search.impl.DefaultPatternSearcher;
import de.uni_leipzig.simba.boa.backend.search.result.SearchResult;
import de.uni_leipzig.simba.boa.backend.search.result.SearchResultReaderCallable;
import de.uni_leipzig.simba.boa.backend.search.result.comparator.SearchResultComparator;
import de.uni_leipzig.simba.boa.backend.util.TimeUtil;


/**
 * @author gerb
 *
 */
public class DefaultPatternSearchModule extends AbstractPatternSearchModule {
    
    private final NLPediaLogger logger                  = new NLPediaLogger(DefaultPatternSearchModule.class);
    private final int TOTAL_NUMBER_OF_SEARCH_THREADS    = NLPediaSettings.getIntegerSetting("numberOfSearchThreads");
    protected final String PATTERN_MAPPING_FOLDER       = NLPediaSettings.BOA_DATA_DIRECTORY + Constants.PATTERN_MAPPINGS_PATH;
    protected final String SEARCH_RESULT_FOLDER         = NLPediaSettings.BOA_DATA_DIRECTORY + Constants.SEARCH_RESULT_PATH;
    
    // caches for various objects
    protected Map<Integer,PatternMapping> mappings        = new HashMap<Integer,PatternMapping>();
    protected Map<Integer,Property> properties;           
    protected Map<Integer,Map<Integer,Pattern>> patterns  = new HashMap<Integer,Map<Integer,Pattern>>();
    
    // for the report
    private long patternSearchTime      = 0;
    private long patternCreationTime    = 0;
    protected long patternMappingCount  = 0;
    private long patternCount           = 0;

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#getName()
     */
    @Override
    public String getName() {

        return "Default Pattern Search Module (SPO languages - de/en)";
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#run()
     */
    @Override
    public void run() {
        
    	// we have found the patterns already, no need to query lucene again and just parse the seralized patterns
    	if ( !NLPediaSettings.getBooleanSetting("useSerializedPatternsForSearch") ) {

    		// first part is to find the patterns
            this.logger.info("Starting pattern search!");
            long startSearch = System.currentTimeMillis();
            PatternSearchThreadManager.startPatternSearchCallables(this.moduleInterchangeObject.getBackgroundKnowledge(), TOTAL_NUMBER_OF_SEARCH_THREADS);
            this.patternSearchTime = (System.currentTimeMillis() - startSearch);
            this.logger.info("All threads finished in " + TimeUtil.convertMilliSeconds(patternSearchTime) + "!");
    	}
        
        // second part is to sort and save them
        this.logger.info("Starting pattern generation and saving!");
        long startSearch = System.currentTimeMillis();
        this.createPatternMappings();
        this.patternCreationTime = (System.currentTimeMillis() - startSearch);
        this.logger.info("Pattern generation and serialization took " + TimeUtil.convertMilliSeconds(patternCreationTime) + "! There are " + this.patternMappingCount + " pattern mappings and " + this.patternCount + " patterns.");
    }
    
    protected void createPatternMappings() {

        // get the cache from the interchange object
        this.properties = this.moduleInterchangeObject.getProperties();
        if ( this.properties != null || this.properties.isEmpty() ) fillProperties();

        List<SearchResult> results = Collections.synchronizedList(new ArrayList<SearchResult>());
        Collection<File> files = FileUtils.listFiles(new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.SEARCH_RESULT_PATH), FileFilterUtils.suffixFileFilter(".sr"), null);
        Map<Integer,String> alreadyKnowString = new ConcurrentHashMap<Integer,String>();
        
        // reading all files in parallel is much faster
        this.readSearchResultsInParallel(results, files, alreadyKnowString);
        
        // sort the patterns first by property and then by their natural
        // language representation
        Collections.sort(results, new SearchResultComparator());
        logger.info("Sorting of " + results.size() + " search results finished!");
        
        // parse the results
        this.createMappings(results);

        // filter the patterns which do not abide certain thresholds, mostly
        // occurrence thresholds
        this.filterPatterns(mappings.values());
        
        // we need to do this after we have filtered them, otherwise it would be too much
//        this.createPartOfSpeechTagsInParallel(mappings.values());
        
        // save the mappings
        SerializationManager.getInstance().serializePatternMappings(mappings.values(), PATTERN_MAPPING_FOLDER);
        logger.info("Pattern mapping saving finished!");
    }

    private void fillProperties() {
		
    	String backgroundKnowledgeFilename = NLPediaSettings.BOA_DATA_DIRECTORY + Constants.BACKGROUND_KNOWLEDGE_PATH + "object_properties_to_query.txt";
		List<String> objectPropertyUris = FileUtil.readFileInList(backgroundKnowledgeFilename, "UTF-8", "#");
		
		for ( String objectPropertyUri : objectPropertyUris ) {
			
			this.logger.info("Processing property: " + objectPropertyUri);
			Property property	= AbstractDefaultBackgroundKnowledgeCollectorModule.queryPropertyData(objectPropertyUri);
			this.properties.put(property.hashCode(), property);
		}
	}

	private void createMappings(List<SearchResult> results) {
    	
    	String currentProperty = null;
        PatternMapping currentMapping = null;
        
        int totalCounter = 0;
        int resultSize = results.size();
        
        do {
        	
        	for ( int i = 0 ; i < results.size() && i < 1000000; i++) {
        		
        		if ( totalCounter++ % 100 == 0 ) logger.debug("SearchResult " + totalCounter + " of " + resultSize + " current: " + results.size());
                SearchResult searchResult = results.get(i);
                
                
                if ( i % 10000  == 0 ) {
                	
                	System.out.println("|Mappings| = " + mappings.size());
                	System.out.println("|Properties| = " + properties.size());
                	System.out.println("|Patterns| = " + patterns.size());
                	long sum = 0L;
                	for ( Map.Entry<Integer, Map<Integer,Pattern>> entry : patterns.entrySet()) {
                		
                		System.out.println("\t" + entry.getValue().size());
                		sum += entry.getValue().size();
                		for ( Map.Entry<Integer, Pattern> e : entry.getValue().entrySet() ) {
                			
//                			System.out.println("\t\t"+ e.getValue().getNaturalLanguageRepresentation());
                		}
                	}
                	System.out.println("Sum: " + sum);
                	System.out.println("\n------------------------------------------------------\n");
                }

                String propertyUri       = searchResult.getProperty();
                String patternString     = searchResult.getNaturalLanguageRepresentation();
                String label1            = searchResult.getFirstLabel();
                String label2            = searchResult.getSecondLabel();
                Integer sentenceID       = searchResult.getSentence();
                
                // next line is for the same property
                if ( propertyUri.equals(currentProperty) ) {
                    
                    // add the patterns to the list with the hash-code of the natural language representation
                    Pattern pattern = patterns.get(propertyUri.hashCode()).get(patternString.hashCode()); //(patternString.hashCode());
                    
                    // pattern was not found, create a new pattern 
                    if ( pattern == null ) {
                        
                        pattern = new SubjectPredicateObjectPattern(patternString);
                        pattern.addSupportInstance(new SupportInstance(label1, label2)); 
//                        pattern.addLearnedFrom(pattern.isDomainFirst() ? label1 + "-;-" + label2 : label2 + "-;-" + label1); 
                        pattern.addPatternMapping(currentMapping);
                        pattern.getFoundInSentences().add(sentenceID);
                        
                        if ( patterns.get(propertyUri.hashCode()) != null ) {
                            
                            patterns.get(propertyUri.hashCode()).put(patternString.hashCode(), pattern);
                        }
                        else {
                            
                            Map<Integer,Pattern> patternMap = new HashMap<Integer,Pattern>();
                            patternMap.put(patternString.hashCode(), pattern);
                            patterns.put(propertyUri.hashCode(), patternMap);
                        }
                        // add the current pattern to the current mapping
                        currentMapping.addPattern(pattern);
                    }
                    // pattern already created, just add new values
                    else {
                        
                        // due to the surface forms we find the same pattern multiple times in
                        // one sentence, so we need to skip this pattern if we found it in the 
                        // same sentence already
                        if ( !pattern.getFoundInSentences().contains(sentenceID) ) {

                            pattern.increaseNumberOfOccurrences();
                            pattern.addSupportInstance(new SupportInstance(label1,label2));
                            pattern.getFoundInSentences().add(sentenceID);
                            pattern.addPatternMapping(currentMapping);
                        }
                    }
                }
                // next line contains pattern for other property
                // so create a new pattern mapping and a new pattern
                else {
                    
                    // create it to use the proper hash function, the properties map has a COMPLETE list of all properties
                    Property p = properties.get(propertyUri.hashCode());
                    currentMapping = mappings.get(propertyUri.hashCode());
                    
                    if ( currentMapping == null ) {
                        
                        currentMapping = new PatternMapping(p);
                        this.patternMappingCount++;
                    }
                    
                    Pattern pattern = new SubjectPredicateObjectPattern(patternString);
                    pattern.addSupportInstance(new SupportInstance(label1,label2));
                    pattern.addPatternMapping(currentMapping);
                    pattern.getFoundInSentences().add(sentenceID);
                    
                    currentMapping.addPattern(pattern);
                    
                    if ( patterns.get(propertyUri.hashCode()) != null ) {
                        
                        patterns.get(propertyUri.hashCode()).put(patternString.hashCode(), pattern);
                    }
                    else {
                        
                        Map<Integer,Pattern> patternMap = new HashMap<Integer,Pattern>();
                        patternMap.put(patternString.hashCode(), pattern);
                        patterns.put(propertyUri.hashCode(), patternMap);
                    }
                    mappings.put(propertyUri.hashCode(), currentMapping);
                }
                
                results.set(i, null);
                currentProperty = propertyUri;
        	}
        	// make this huge list smaller
        	logger.info("Result list size: " + results.size());
        	results.removeAll(Collections.singleton(null));
        	logger.info("Result list size after cleaning: " + results.size());
        }
        while ( !results.isEmpty() );
        
        logger.info("Pattern mapping creation finished!");
	}

	/**
     * 
     * @param results
     * @param files
     * @param alreadyKnowString
     */
    private void readSearchResultsInParallel(final List<SearchResult> results, Collection<File> files, final Map<Integer, String> alreadyKnowString) {

        try {
        	
        	List<SearchResultReaderCallable> searchResultReader = new ArrayList<SearchResultReaderCallable>();
        	// collect all search results from the written files
        	ExecutorService executor = Executors.newFixedThreadPool(files.size());
            for (final File file : files) searchResultReader.add((new SearchResultReaderCallable(results, file, alreadyKnowString)));
        	
			executor.invokeAll(searchResultReader);
			executor.shutdown();
			
			logger.info("Reading of "+results.size()+" search results finished!");
		}
        catch (InterruptedException e) {
			
			e.printStackTrace();
		}
	}

	/**
     * 
     * @param mappings
     */
    private void createPartOfSpeechTagsInParallel(Collection<PatternMapping> mappings) {
    	
    	try {
        	
    		ExecutorService executor = Executors.newFixedThreadPool(mappings.size());
    		
    		logger.info("Starting parallel POS tagging of all patterns ...");
    		
    		List<PatternPosTagCallable> patternsTaggerCallables = new ArrayList<PatternPosTagCallable>();
        	for ( PatternMapping mapping : mappings ) patternsTaggerCallables.add(new PatternPosTagCallable(mapping));
        	
			executor.invokeAll(patternsTaggerCallables);
			executor.shutdown();
			
			logger.info("Parallel POS tagging of all patterns finished!");
		}
        catch (InterruptedException e) {
			
			e.printStackTrace();
		}
	}

	/**
     * 
     * @param patternMappings
     */
    protected void filterPatterns(Collection<PatternMapping> patternMappings) {

        Map<String,PatternFilter> patternEvaluators = PatternFilterFactory.getInstance().getPatternFilterMap();
        int numberOfPatterns = 0;
        
        // go through all filter
        for ( PatternFilter patternEvaluator : patternEvaluators.values() ) {

            this.logger.info(patternEvaluator.getClass().getSimpleName() + " started!");
            
            // and check each pattern mapping
            for ( PatternMapping patternMapping : patternMappings ) {
            
                patternEvaluator.filterPattern(patternMapping);
                numberOfPatterns += patternMapping.getPatterns().size();
            }
            
            this.logger.info(patternEvaluator.getClass().getSimpleName() + " finished!");
        }
        this.logger.info("All filters are finished. A total of " + numberOfPatterns + " have survived the filtering!");
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#isDataAlreadyAvailable()
     */
    @Override
    public boolean isDataAlreadyAvailable() {

        // lists all files in the directory which end with .txt and does not go into subdirectories
        return // true of more than one file is found
            FileUtils.listFiles(new File(PATTERN_MAPPING_FOLDER), FileFilterUtils.suffixFileFilter(".bin"), null).size() > 0;
    }


    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#getReport()
     */
    @Override
    public String getReport() {

        // we need to calculate this here because we have filter some out
        for (PatternMapping mapping : this.mappings.values()) 
            this.patternCount += mapping.getPatterns().size();
        
        return "The pattern search took " + TimeUtil.convertMilliSeconds(this.patternSearchTime) + " where the pattern creating/serialization took " + TimeUtil.convertMilliSeconds(this.patternCreationTime) + "ms. "
                + " There are " + this.moduleInterchangeObject.getPatternMappings().size() + " mappings and " + this.patternCount + " patterns.";
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#updateModuleInterchangeObject()
     */
    @Override
    public void updateModuleInterchangeObject() {

        this.moduleInterchangeObject.getPatternMappings().addAll(this.mappings.values());
    }

    @Override
    public void loadAlreadyAvailableData() {

        for (File mappingFile : FileUtils.listFiles(new File(PATTERN_MAPPING_FOLDER), FileFilterUtils.suffixFileFilter(".bin"), null)) {
            
            PatternMapping mapping = SerializationManager.getInstance().deserializePatternMapping(mappingFile.getAbsolutePath());
            this.mappings.put(mapping.getProperty().getUri().hashCode(), mapping);
        }
        
        // since we have the mappings already there, we don't need the background knowledge anymore
        // so we can save up the RAM
        this.moduleInterchangeObject.setBackgroundKnowledge(null);
    }
}
