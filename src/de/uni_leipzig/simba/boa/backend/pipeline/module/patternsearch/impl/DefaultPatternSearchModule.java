/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.patternsearch.impl;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

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
    protected final String PATTERN_MAPPING_FOLDER         = NLPediaSettings.BOA_DATA_DIRECTORY + Constants.PATTERN_MAPPINGS_PATH;
    
    // caches for various objects
    protected Map<Integer,PatternMapping> mappings        = new HashMap<Integer,PatternMapping>();
    protected Map<Integer,Property> properties;           
    protected Map<Integer,Map<Integer,Pattern>> patterns  = new HashMap<Integer,Map<Integer,Pattern>>();
    
    // for the report
    private long patternSearchTime      = 0;
    private long patternCreationTime    = 0;
    protected long patternMappingCount    = 0;
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
        
        // sort the patterns first by property and then by their natural language representation
        Collections.sort(results, new SearchResultComparator());
        
        String currentProperty = "";
        PatternMapping currentMapping = null;
        
        for ( SearchResult searchResult : results) {
        	
            
            String propertyUri      = searchResult.getProperty();
            String patternString    = searchResult.getNaturalLanguageRepresentation();
            String label1           = searchResult.getFirstLabel();
            String label2           = searchResult.getSecondLabel();
            String posTagged        = searchResult.getPosTags();
            Integer sentence        = searchResult.getSentence();
      
            // next line is for the same property
            if ( propertyUri.equals(currentProperty) ) {
                
                // add the patterns to the list with the hash-code of the natural language representation
                Pattern pattern = patterns.get(propertyUri.hashCode()).get(patternString.hashCode()); //(patternString.hashCode());
                
                // pattern was not found, create a new pattern 
                if ( pattern == null ) {
                    
                    pattern = new SubjectPredicateObjectPattern(patternString);
                    pattern.setPosTaggedString(posTagged);
                    pattern.addLearnedFrom(label1 + "-;-" + label2); 
//                    pattern.addLearnedFrom(pattern.isDomainFirst() ? label1 + "-;-" + label2 : label2 + "-;-" + label1); 
                    pattern.addPatternMapping(currentMapping);
                    pattern.getFoundInSentences().add(sentence);
                    
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
                    
                    pattern.increaseNumberOfOccurrences();
                    pattern.addLearnedFrom(label1 + "-;-" + label2);
                    pattern.getFoundInSentences().add(sentence);
                    pattern.addPatternMapping(currentMapping);
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
                pattern.setPosTaggedString(posTagged);
                pattern.addLearnedFrom(label1 + "-;-" + label2);
                pattern.addPatternMapping(currentMapping);
                pattern.getFoundInSentences().add(sentence);
                
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
        }
        
        // filter the patterns which do not abide certain thresholds, mostly occurrence thresholds
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
