/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl;

import java.io.File;

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
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + "backgroundknowledge/").exists()) new File(NLPediaSettings.BOA_DATA_DIRECTORY + "backgroundknowledge/").mkdir();
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + "backgroundknowledge/object/").exists()) new File(NLPediaSettings.BOA_DATA_DIRECTORY + "backgroundknowledge/object/").mkdir();
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + "backgroundknowledge/datatype/").exists()) new File(NLPediaSettings.BOA_DATA_DIRECTORY + "backgroundknowledge/datatype/").mkdir();
        
        // indexes
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + "index/").exists()) new File(NLPediaSettings.BOA_DATA_DIRECTORY + "index/").mkdir();
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + "index/corpus/").exists()) new File(NLPediaSettings.BOA_DATA_DIRECTORY + "index/corpus/").mkdir();
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + "index/pattern/").exists()) new File(NLPediaSettings.BOA_DATA_DIRECTORY + "index/pattern/").mkdir();
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + "index/pattern/default").exists()) new File(NLPediaSettings.BOA_DATA_DIRECTORY + "index/pattern/default").mkdir();
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + "index/pattern/detail").exists()) new File(NLPediaSettings.BOA_DATA_DIRECTORY + "index/pattern/detail").mkdir();
        
        // pattern mappings
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + "patternmappings/").exists()) new File(NLPediaSettings.BOA_DATA_DIRECTORY + "patternmappings/").mkdir();
        
        // raw input data
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + "raw/").exists()) new File(NLPediaSettings.BOA_DATA_DIRECTORY + "raw/").mkdir();
        
        // rdf output
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + "rdf/").exists()) new File(NLPediaSettings.BOA_DATA_DIRECTORY + "rdf/").mkdir();
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + "rdf/binary/").exists()) new File(NLPediaSettings.BOA_DATA_DIRECTORY + "rdf/binary/").mkdir();
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + "rdf/nt/").exists()) new File(NLPediaSettings.BOA_DATA_DIRECTORY + "rdf/nt/").mkdir();
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + "rdf/text/").exists()) new File(NLPediaSettings.BOA_DATA_DIRECTORY + "rdf/text/").mkdir();
        
        // sentences
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + "sentences/").exists()) new File(NLPediaSettings.BOA_DATA_DIRECTORY + "sentences/").mkdir();
        
        // wikipedia dumps
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + "wikipedia/").exists()) new File(NLPediaSettings.BOA_DATA_DIRECTORY + "wikipedia/").mkdir();
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#getReport()
     */
    @Override
    public String getReport() {

        return "Create the directory structure in " + TimeUtil.convertMilliSeconds(this.timeCreateDirectoryStructure) + ".";
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
