/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.knowledgecreation.impl;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.knowledgecreation.KnowledgeCreationManager;
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

    private static final String KNOWLEDGE_CREATION_BINARY_OUTPUT_PATH       = NLPediaSettings.BOA_DATA_DIRECTORY + Constants.RDF_DATA_BINARY_PATH;
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
        
        KnowledgeCreationManager knowledgeManager = new KnowledgeCreationManager();

        this.logger.info("Starting to find new triples!");
        long startNewTripleSearch = System.currentTimeMillis();
        Map<String, List<Triple>> newKnowledge = knowledgeManager.findNewTriples(null, this.moduleInterchangeObject.getPatternMappings());
        this.newTripleSearchTime = System.currentTimeMillis() - startNewTripleSearch;
        this.logger.info("Finding new triples took " + TimeUtil.convertMilliSeconds(newTripleSearchTime) + ".");
        
        this.logger.info("Starting to merge and score new triples");
        long startMergeTriple = System.currentTimeMillis();
        Map<String, Set<Triple>> mergedTriples = knowledgeManager.mergeAndScoreTriples(newKnowledge);
        this.mergeAndScoreTripleTime = System.currentTimeMillis() - startMergeTriple;
        this.logger.info("Merging and scoring of new triples took: " + TimeUtil.convertMilliSeconds(mergeAndScoreTripleTime) + ".");
        
        long startSavingTriples = System.currentTimeMillis();
        for (Map.Entry<String, Set<Triple>> entry : mergedTriples.entrySet()) {
            
            String filename = KNOWLEDGE_CREATION_BINARY_OUTPUT_PATH + entry.getKey().substring(entry.getKey().lastIndexOf("/") + 1) + ".bin";
            SerializationManager.getInstance().serializeTriples(entry.getValue(), filename);
            this.tripleCount += entry.getValue().size();
        }
        this.moduleInterchangeObject.setNewKnowledge(mergedTriples);
        this.savingTriplesTime = System.currentTimeMillis() - startSavingTriples;
        this.logger.info("Saving of " + this.tripleCount + " new triples took " + TimeUtil.convertMilliSeconds(savingTriplesTime) + ".");
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
            this.moduleInterchangeObject.getNewKnowledge().put(file.getName().replace(".bin", ""), triples);
        }
    }
}
