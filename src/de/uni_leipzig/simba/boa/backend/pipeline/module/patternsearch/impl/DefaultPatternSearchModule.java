/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.patternsearch.impl;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import de.uni_leipzig.simba.boa.backend.concurrent.PatternSearchThreadManager;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.IterationCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.PatternFilterCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.PatternSearchCommand;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.filter.PatternFilter;
import de.uni_leipzig.simba.boa.backend.entity.pattern.filter.PatternFilterFactory;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.persistance.kryo.SerializationManager;
import de.uni_leipzig.simba.boa.backend.pipeline.module.patternsearch.AbstractPatternSearchModule;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;
import de.uni_leipzig.simba.boa.backend.search.PatternSearcher;
import de.uni_leipzig.simba.boa.backend.search.result.SearchResult;
import de.uni_leipzig.simba.boa.backend.search.result.comparator.SearchResultComparator;
import de.uni_leipzig.simba.boa.backend.util.SerializationUtil;


/**
 * @author gerb
 *
 */
public class DefaultPatternSearchModule extends AbstractPatternSearchModule {
	
	private final NLPediaLogger logger					= new NLPediaLogger(DefaultPatternSearchModule.class);
	private final int TOTAL_NUMBER_OF_SEARCH_THREADS	= NLPediaSettings.getInstance().getIntegerSetting("numberOfSearchThreads");
	private final String PATTERN_MAPPING_FOLDER			= NLPediaSettings.BOA_DATA_DIRECTORY + "patternmappings/";
	
	// caches for various objects
	private Map<Integer,PatternMapping> mappings		= new HashMap<Integer,PatternMapping>();
	private Map<Integer,Property> properties;			
	private Map<Integer,Map<Integer,Pattern>> patterns	= new HashMap<Integer,Map<Integer,Pattern>>();
	
	// for the report
	private long patternSearchTime		= 0;
	private long patternCreationTime	= 0;
	private long patternMappingCount	= 0;
	private long patternCount			= 0;

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
		this.logger.info("All threads finished in " + patternSearchTime + "ms! There are " + results.size() + " strings in the result list");
		
		// second part is to sort and save them
		this.logger.info("Starting pattern generation and saving!");
		startSearch = System.currentTimeMillis();
		this.createPatternMappings(results);
		this.patternCreationTime = (System.currentTimeMillis() - startSearch);
		this.logger.info("Pattern generation and serialization took " + patternCreationTime + "ms! There are " + this.patternMappingCount + " pattern mappings and " + this.patternCount + " patterns.");
	}
	
	private void createPatternMappings(List<SearchResult> results) {
		
		// get the cache from the interchange object
		this.properties = this.moduleInterchangeObject.getProperties();
		
		// sort the patterns first by property and then by their natural language representation
		Collections.sort(results, new SearchResultComparator());
		
		String currentProperty = "";
		PatternMapping currentMapping = null;
		
		for ( SearchResult searchResult : results) {
			
			String propertyUri		= searchResult.getProperty();
			String patternString	= searchResult.getNaturalLanguageRepresentation();
			String label1			= searchResult.getFirstLabel();
			String label2			= searchResult.getSecondLabel();
			String posTagged		= searchResult.getPosTags();
			Integer documentId		= new Integer(searchResult.getIndexId());

			// next line is for the same property
			if ( propertyUri.equals(currentProperty) ) {
				
				// add the patterns to the list with the hash-code of the natural language representation
				Pattern pattern = patterns.get(propertyUri.hashCode()).get(patternString.hashCode()); //(patternString.hashCode());
				
				// pattern was not found, create a new pattern 
				if ( pattern == null ) {
					
					pattern = new Pattern(patternString);
					pattern.setFoundInIteration(IterationCommand.CURRENT_ITERATION_NUMBER);
					pattern.setPosTaggedString(posTagged);
					pattern.addLearnedFrom(label1 + "-;-" + label2);
					pattern.addPatternMapping(currentMapping);
					pattern.addLuceneDocIds(Integer.valueOf(documentId));
					this.patternCount++;
					
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
					pattern.addLuceneDocIds(Integer.valueOf(documentId));
					pattern.addPatternMapping(currentMapping);
				}
			}
			// next line contains pattern for other property
			// so create a new pattern mapping and a new pattern
			else {
				
				// create it to use the proper hash function, the properties map has a COMPLETE list of all properties
				Property p = new Property();
				p.setUri(propertyUri);
				p = properties.get(p.hashCode());
				
				currentMapping = mappings.get(propertyUri.hashCode());
				
				if ( currentMapping == null ) {
					
					currentMapping = new PatternMapping(p);
					this.patternMappingCount++;
				}
				
				Pattern pattern = new Pattern(patternString);
				pattern.setFoundInIteration(IterationCommand.CURRENT_ITERATION_NUMBER);
				pattern.setPosTaggedString(posTagged);
				pattern.addLearnedFrom(label1 + "-;-" + label2);
				pattern.addPatternMapping(currentMapping);
				pattern.addLuceneDocIds(documentId);
				this.patternCount++;
				
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
		for (PatternMapping mapping : mappings.values()) {
			
			if ( mapping.getPatterns().size() > 0 ) {
				
				SerializationManager.getInstance().serializePatternMapping(mapping, PATTERN_MAPPING_FOLDER + mapping.getProperty().getLabel() + ".bin");
			}
		}
	}

	/**
	 * 
	 * @param patternMappings
	 */
	private void filterPatterns(Collection<PatternMapping> patternMappings) {

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

		return "The pattern search took " + patternSearchTime + "ms where the pattern creating/serialization took " + patternCreationTime + "ms. "
				+ " There are " + patternMappingCount + " mappings and " + patternCount + " patterns.";
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
