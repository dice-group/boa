/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.AbstractPreprocessingModule;


/**
 * @author gerb
 *
 */
public class CreateDataDirectoryStructureModule extends AbstractPreprocessingModule {

    private final NLPediaLogger logger = new NLPediaLogger(CreateDataDirectoryStructureModule.class);
    
    // for the report
    private long timeCreateDirectoryStructure;

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#getName()
     */
    @Override
    public String getName() {

        return "Create Directory Structure Module";
    }
    
    /**
     * This main is going to be executed in the ant configuration step. If we do this 
     * we don't need to start the framework twice! 
     * 
     * @param args
     */
    public static void main(String[] args) {

        // get the bao configuration
        NLPediaSetup setup = new NLPediaSetup(true);
        
        // run the module
        System.out.println("Creating BOA directory structure!");
        CreateDataDirectoryStructureModule module = new CreateDataDirectoryStructureModule();
        module.run();
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
        
        this.createFile(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.BACKGROUND_KNOWLEDGE_PATH + "object_properties_to_query.txt");
        this.createFile(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.BACKGROUND_KNOWLEDGE_PATH + "datatype_properties_to_query.txt");
        
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
        
        // dbpedia dumps like redirect, dismabiguations, labels
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.DBPEDIA_DUMP_PATH).exists()) 
            new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.DBPEDIA_DUMP_PATH).mkdir();
        
        // the training file for the network and the network and the evaluation of the network
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.MACHINE_LEARNING_PATH).exists()) 
            new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.MACHINE_LEARNING_PATH).mkdir();
        
        // this is the place to store the models generated by weka
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.MACHINE_LEARNING_CLASSIFIER_PATH).exists()) 
            new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.MACHINE_LEARNING_CLASSIFIER_PATH).mkdir();
        
        // this is the place to store the annotated training files
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.MACHINE_LEARNING_TRAINING_PATH).exists()) 
            new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.MACHINE_LEARNING_TRAINING_PATH).mkdir();
        
        try {
            // copy the boa to arff script to the directory where the boa training file is generated
            FileUtils.copyFile(
                    new File(NLPediaSettings.BOA_BASE_DIRECTORY + "scripts/boa2arff.jar"),
                    new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.MACHINE_LEARNING_TRAINING_PATH + "boa2arff.jar"));
        }
        catch (IOException e) {
            
            throw new RuntimeException("Could not copy boa2arff.jar to ML directory!", e);
        }
        
        // the output/input folder of the evaluation
        if ( !new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.EVALUATION_PATH).exists()) 
            new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.EVALUATION_PATH).mkdir();
    }

    private void createFile(String filename) {

        try {
             
            File file = new File(filename);
            if ( !file.exists() ) {
                
                file.createNewFile();
            }
        }
        catch (IOException e) {
            
            e.printStackTrace();
            String error = "Could not create file for filename: " + filename;
            this.logger.error(error, e);
            throw new RuntimeException(error, e);
        }
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
