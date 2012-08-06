/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.patternsearch.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import de.danielgerber.file.BufferedFileReader;
import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.concurrent.PatternSearchThreadManager;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.filter.PatternFilter;
import de.uni_leipzig.simba.boa.backend.entity.pattern.filter.PatternFilterFactory;
import de.uni_leipzig.simba.boa.backend.entity.pattern.impl.SubjectPredicateObjectPattern;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.persistance.serialization.SerializationManager;
import de.uni_leipzig.simba.boa.backend.pipeline.module.patternsearch.AbstractPatternSearchModule;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.search.result.SearchResult;
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
        
        // first part is to find the patterns
        this.logger.info("Starting pattern search!");
        long startSearch = System.currentTimeMillis();
        List<SearchResult> results = PatternSearchThreadManager.startPatternSearchCallables(this.moduleInterchangeObject.getBackgroundKnowledge(), TOTAL_NUMBER_OF_SEARCH_THREADS);
        this.patternSearchTime = (System.currentTimeMillis() - startSearch);
        this.logger.info("All threads finished in " + TimeUtil.convertMilliSeconds(patternSearchTime) + "! There are " + results.size() + " strings in the result list");
        
        // second part is to sort and save them
        this.logger.info("Starting pattern generation and saving!");
        startSearch = System.currentTimeMillis();
        this.createPatternMappings(results);
        this.patternCreationTime = (System.currentTimeMillis() - startSearch);
        this.logger.info("Pattern generation and serialization took " + TimeUtil.convertMilliSeconds(patternCreationTime) + "! There are " + this.patternMappingCount + " pattern mappings and " + this.patternCount + " patterns.");
    }
    
    protected void createPatternMappings(List<SearchResult> results) {

        // get the cache from the interchange object
        this.properties = this.moduleInterchangeObject.getProperties();

        results = new ArrayList<SearchResult>();
        Map<Integer,String> alreadyKnowString = new HashMap<Integer,String>();

        // collect all search results from the written files
        for (File file : FileUtils.listFiles(new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.SEARCH_RESULT_PATH), FileFilterUtils.suffixFileFilter(".sr"), null)) {

            logger.info("Reading search results from file: " + file.getName());
            BufferedFileReader reader = new BufferedFileReader(file.getAbsolutePath(), "UTF-8");
            String line = "";

            // every line in each file is a serialized search result
            while ((line = reader.readLine()) != null) {
                
                String[] lineParts                  = line.split(java.util.regex.Pattern.quote("]["));
                
                // we need to do this none-sense to avoid create 32mio different property uris and so on 
                // this should dramatically reduce the memory usage while processing the search results
                for (String part : lineParts )
                    if ( !alreadyKnowString.containsKey(part.hashCode()) ) alreadyKnowString.put(part.hashCode(), part);
                
                SearchResult searchResult = new SearchResult();
                searchResult.setProperty(alreadyKnowString.get(lineParts[0].hashCode()));
                searchResult.setNaturalLanguageRepresentation(alreadyKnowString.get(lineParts[1].hashCode()));
                searchResult.setFirstLabel(alreadyKnowString.get(lineParts[2].hashCode()));
                searchResult.setSecondLabel(alreadyKnowString.get(lineParts[3].hashCode()));
                searchResult.setSentence(Integer.valueOf(alreadyKnowString.get(lineParts[4].hashCode())));
                
                results.add(searchResult);
            }
            reader.close();
        }
        
        // sort the patterns first by property and then by their natural
        // language representation
        Collections.sort(results, new SearchResultComparator());

        String currentProperty = null;
        PatternMapping currentMapping = null;

        // collect all search results from the written files
        Iterator<SearchResult> iterator = results.iterator();
        while ( iterator.hasNext()) {
            
            SearchResult searchResult = iterator.next();

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
                    pattern.addLearnedFrom(label1 + "-;-" + label2); 
//                    pattern.addLearnedFrom(pattern.isDomainFirst() ? label1 + "-;-" + label2 : label2 + "-;-" + label1); 
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
                        pattern.addLearnedFrom(label1 + "-;-" + label2);
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
                pattern.addLearnedFrom(label1 + "-;-" + label2);
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
            currentProperty = propertyUri;
//            iterator.remove(); // TODO can we call this since it shifts the collection for every pattern
            searchResult = null; // probably better to just null it instead delete it from the collection
        }

        // filter the patterns which do not abide certain thresholds, mostly
        // occurrence thresholds
        this.filterPatterns(mappings.values());

        // save the mappings
        SerializationManager.getInstance().serializePatternMappings(mappings.values(), PATTERN_MAPPING_FOLDER);
    }

    /**
     * 
     * @param patternMappings
     */
    protected void filterPatterns(Collection<PatternMapping> patternMappings) {

        Map<String,PatternFilter> patternEvaluators = PatternFilterFactory.getInstance().getPatternFilterMap();
        
        // go through all filter
        for ( PatternFilter patternEvaluator : patternEvaluators.values() ) {

            this.logger.info(patternEvaluator.getClass().getSimpleName() + " started!");
            
            // and check each pattern mapping
            for ( PatternMapping patternMapping : patternMappings ) {
            
                this.logger.debug("Starting to filter pattern mapping: " + patternMapping.getProperty().getUri() + " with filter: " + patternEvaluator.getClass().getSimpleName());
                patternEvaluator.filterPattern(patternMapping);
            }
            
            this.logger.info(patternEvaluator.getClass().getSimpleName() + " finished!");
        }
        this.logger.info("All filters are finished.");
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
    }
}
