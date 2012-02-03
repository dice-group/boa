/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl;

import java.io.File;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.AbstractPreprocessingModule;
import de.uni_leipzig.simba.boa.backend.util.TimeUtil;


/**
 * @author gerb
 *
 */
public class CreateDataDirectoryStructureModule extends AbstractPreprocessingModule {

    // for the report
    private long timeCreateDirectoryStructure;

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#getName()
     */
    @Override
    public String getName() {

        return "Create Directory Structure Module";
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#run()
     */
    @Override
    public void run() {

        // data dir
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY).exists()) new File(NLPediaSettings.BOA_DATA_DIRECTORY).mkdir(); 
        
        // background knowledge
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.BACKGROUND_KNOWLEDGE_PATH).exists()) 
            new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.BACKGROUND_KNOWLEDGE_PATH).mkdir();
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.BACKGROUND_KNOWLEDGE_OBJECT_PROPERTY_PATH).exists()) 
            new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.BACKGROUND_KNOWLEDGE_OBJECT_PROPERTY_PATH).mkdir();
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.BACKGROUND_KNOWLEDGE_DATATYPE_PROPERTY_PATH).exists()) 
            new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.BACKGROUND_KNOWLEDGE_DATATYPE_PROPERTY_PATH).mkdir();
        
        // indexes
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.INDEX_PATH).exists()) 
            new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.INDEX_PATH).mkdir();
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.INDEX_CORPUS_PATH).exists()) 
            new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.INDEX_CORPUS_PATH).mkdir();
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.INDEX_PATTERN_PATH).exists()) 
            new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.INDEX_PATTERN_PATH).mkdir();
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.INDEX_DEFAULT_PATTERN_PATH).exists()) 
            new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.INDEX_DEFAULT_PATTERN_PATH).mkdir();
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.INDEX_DETAIL_PATTERN_PATH).exists()) 
            new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.INDEX_DETAIL_PATTERN_PATH).mkdir();
        
        // pattern mappings
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.PATTERN_MAPPINGS_PATH).exists()) 
            new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.PATTERN_MAPPINGS_PATH).mkdir();
        
        // raw input data
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.RAW_DATA_PATH).exists()) 
            new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.RAW_DATA_PATH).mkdir();
        
        // rdf output
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.RDF_DATA_PATH).exists()) 
            new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.RDF_DATA_PATH).mkdir();
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.RDF_DATA_BINARY_PATH).exists()) 
            new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.RDF_DATA_BINARY_PATH).mkdir();
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.RDF_DATA_NTRIPLES_PATH).exists()) 
            new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.RDF_DATA_NTRIPLES_PATH).mkdir();
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.RDF_DATA_TEXT_PATH).exists()) 
            new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.RDF_DATA_TEXT_PATH).mkdir();
        
        // sentences
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.SENTENCE_CORPUS_PATH).exists()) 
            new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.SENTENCE_CORPUS_PATH).mkdir();
        
        // wikipedia dumps
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.WIKIPEDIA_DUMP_PATH).exists()) 
            new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.WIKIPEDIA_DUMP_PATH).mkdir();
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#getReport()
     */
    @Override
    public String getReport() {

        return "Create the directory structure in " + this.timeCreateDirectoryStructure + "ms.";
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

        return false; // run it anyway
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#loadAlreadyAvailableData()
     */
    @Override
    public void loadAlreadyAvailableData() {

        // nothing to do here
    }
}
