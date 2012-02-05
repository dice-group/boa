/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.knowledgecreation.impl;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import de.uni_leipzig.simba.boa.backend.concurrent.KnowledgeCreationThreadManager;
import de.uni_leipzig.simba.boa.backend.concurrent.PatternMappingPatternPair;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.persistance.serialization.SerializationManager;
import de.uni_leipzig.simba.boa.backend.pipeline.module.knowledgecreation.AbstractKnowledgeCreationModule;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;
import de.uni_leipzig.simba.boa.backend.util.TimeUtil;


/**
 * @author gerb
 *
 */
public class DefaultKnowledgeCreationModule extends AbstractKnowledgeCreationModule {

    private static final String KNOWLEDGE_CREATION_BINARY_OUTPUT_PATH       = NLPediaSettings.BOA_DATA_DIRECTORY + "rdf/binary/";
    private static final Double KNOWLEDGE_CREATION_PATTERN_SCORE_THRESHOLD  = NLPediaSettings.getDoubleSetting("score.threshold.create.knowledge");
    private static final int KNOWLEDGE_CREATION_NUMBER_OF_THREADS           = NLPediaSettings.getIntegerSetting("number.of.create.knowledge.threads");
    private final NLPediaLogger logger                                      = new NLPediaLogger(DefaultKnowledgeCreationModule.class);
    
    // for the report
    private long newTripleSearchTime;
    private long mergeAndScoreTripleTime;
    private long savingTriplesTime;
    private long tripleCount = 0;

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#getName()
     */
    @Override
    public String getName() {

        return "Default Knowledge Creation Tool";
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#run()
     */
    @Override
    public void run() {

        this.logger.info("Starting to find new triples!");
        long startNewTripleSearch = System.currentTimeMillis();
        Map<String, List<Triple>> newKnowledge = this.findNewTriples();
        this.newTripleSearchTime = System.currentTimeMillis() - startNewTripleSearch;
        this.logger.info("Finding new triples took " + TimeUtil.convertMilliSeconds(newTripleSearchTime) + ".");
        
        this.logger.info("Starting to merge and score new triples");
        long startMergeTriple = System.currentTimeMillis();
        Map<String, Set<Triple>> mergedTriples = this.mergeAndScoreTriples(newKnowledge);
        this.mergeAndScoreTripleTime = System.currentTimeMillis() - startMergeTriple;
        this.logger.info("Merging and scoring of new triples took: " + TimeUtil.convertMilliSeconds(mergeAndScoreTripleTime) + ".");
        
        long startSavingTriples = System.currentTimeMillis();
        for (Map.Entry<String, Set<Triple>> entry : mergedTriples.entrySet()) {
            
            String filename = KNOWLEDGE_CREATION_BINARY_OUTPUT_PATH + entry.getKey().substring(entry.getKey().lastIndexOf("/") + 1) + ".bin";
            SerializationManager.getInstance().serializeTriples(entry.getValue(), filename);
            this.tripleCount += entry.getValue().size();
            
            // Debugging reasons! TODO remove
            System.out.println(entry.getKey());
            for (Triple t : entry.getValue()) {
                
                System.out.println(t.getScore() + " " + t);
                for (String sentence : t.getLearnedFromSentences()) {
                    
                    System.out.println("\t" + sentence);
                }
                for (Pattern pattern : t.getLearnedFromPatterns()) {
                    
                    System.out.println("\t" + pattern.getNaturalLanguageRepresentation());
                }
            }
        }
        this.moduleInterchangeObject.setNewKnowledge(mergedTriples);
        this.savingTriplesTime = System.currentTimeMillis() - startSavingTriples;
        this.logger.info("Saving of " + this.tripleCount + " new triples took " + TimeUtil.convertMilliSeconds(savingTriplesTime) + ".");
    }

    /**
     * 
     * @param newKnowledge
     * @return
     */
    private Map<String, Set<Triple>> mergeAndScoreTriples(Map<String, List<Triple>> newKnowledge) {

        Map<String,Set<Triple>> results =  new HashMap<String,Set<Triple>>();
        Map<Integer,Triple> mergedTriples;
        
        for ( Map.Entry<String, List<Triple>> entry : newKnowledge.entrySet()) {
            
            mergedTriples = new HashMap<Integer,Triple>();
            
            String propertyUri = entry.getKey();
            List<Triple> triples = entry.getValue();
            
            for ( Triple triple : triples ) {
                
                // we have seen this triple before, so merge it
                if ( mergedTriples.containsKey(triple.hashCode()) ) {
                    
                    // subject, predicate, object is the same, so don't change it
                    // the triple also has not a score yet
                    // the only things we need to merge are the patterns and the sentences this
                    // triple has been learned from
                    Triple knownTriple = mergedTriples.get(triple.hashCode());
                    knownTriple.getLearnedFromPatterns().addAll(triple.getLearnedFromPatterns());
                    knownTriple.getLearnedFromSentences().addAll(triple.getLearnedFromSentences());
                }
                else // we can simply put it in the list
                    mergedTriples.put(triple.hashCode(), triple);
            }
            results.put(propertyUri, this.calculateConfidence(mergedTriples.values()));
        }
        return results;
    }

    /**
     * 
     * @param unscoredTriples
     * @return
     */
    private Set<Triple> calculateConfidence(Collection<Triple> unscoredTriples) {

        Set<Triple> scoredTriples = new HashSet<Triple>();
        for ( Triple triple : unscoredTriples ) {
            
            double confidence = 0;
            for ( Pattern patternLearnedFrom : triple.getLearnedFromPatterns() ) {
                
                confidence += patternLearnedFrom.getScore();
            }
            // sigmoid function shifted to the right to boost pattern which are learned from more than one pattern
            triple.setConfidence(1D / (1D + Math.pow(Math.E, - confidence * triple.getLearnedFromPatterns().size() + 1)));
            
            scoredTriples.add(triple);
        }
        return scoredTriples;
    }

    private Map<String, List<Triple>> findNewTriples() {
        
        // create the input for the search threads  
        Set<PatternMappingPatternPair> pairs = new HashSet<PatternMappingPatternPair>();
        for ( PatternMapping mapping : this.moduleInterchangeObject.getPatternMappings()) {
            for (Pattern pattern : mapping.getPatterns()) {
                
                // but only those patterns which are higher scored then the threshold
                if ( pattern.getScore() >= KNOWLEDGE_CREATION_PATTERN_SCORE_THRESHOLD )
                    pairs.add(new PatternMappingPatternPair(mapping, pattern));
            }
        }
        return KnowledgeCreationThreadManager.startKnowledgeCreationCallables(pairs, KNOWLEDGE_CREATION_NUMBER_OF_THREADS);
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#getReport()
     */
    @Override
    public String getReport() {

        return "Knowledge Creation finished in " + TimeUtil.convertMilliSeconds(savingTriplesTime + mergeAndScoreTripleTime + newTripleSearchTime) +
                ". The search took: " + TimeUtil.convertMilliSeconds(newTripleSearchTime) + ", merging and scoring took " 
                + TimeUtil.convertMilliSeconds(mergeAndScoreTripleTime) + " and saving took " + TimeUtil.convertMilliSeconds(savingTriplesTime) +
                ".\n  Created knowledge for " + this.moduleInterchangeObject.getNewKnowledge().size() + " pattern mappings with a total of " + this.tripleCount + " triples.";
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#updateModuleInterchangeObject()
     */
    @Override
    public void updateModuleInterchangeObject() {

        // nothing to do here
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#isDataAlreadyAvailable()
     */
    @Override
    public boolean isDataAlreadyAvailable() {

        // lists all files in the directory which end with .nt and does not go into subdirectories
        return // true of more than one file is found
            FileUtils.listFiles(new File(KNOWLEDGE_CREATION_BINARY_OUTPUT_PATH), FileFilterUtils.suffixFileFilter(".bin"), null).size() > 0;
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#loadAlreadyAvailableData()
     */
    @Override
    public void loadAlreadyAvailableData() {

        for (File file : FileUtils.listFiles(new File(KNOWLEDGE_CREATION_BINARY_OUTPUT_PATH), FileFilterUtils.suffixFileFilter(".bin"), null)) {
            
            Set<Triple> triples = SerializationManager.getInstance().deserializeTriples(file.getAbsolutePath());
            this.moduleInterchangeObject.getNewKnowledge().put("http://dbpedia.org/ontology/" + file.getName().replace(".bin", ""), triples);
        }
    }

}
