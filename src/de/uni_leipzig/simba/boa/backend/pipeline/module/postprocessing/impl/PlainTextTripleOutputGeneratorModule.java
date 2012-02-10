/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.postprocessing.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import de.danielgerber.file.BufferedFileWriter;
import de.danielgerber.file.FileUtil;
import de.danielgerber.file.BufferedFileWriter.WRITER_WRITE_MODE;
import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.pipeline.module.postprocessing.AbstractPostProcessingModule;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;
import de.uni_leipzig.simba.boa.backend.rdf.entity.comparator.TripleScoreComparator;
import de.uni_leipzig.simba.boa.backend.util.TimeUtil;


/**
 * @author gerb
 *
 */
public class PlainTextTripleOutputGeneratorModule extends AbstractPostProcessingModule {

    private final NLPediaLogger logger = new NLPediaLogger(PlainTextTripleOutputGeneratorModule.class);
    
    private static final String RDF_TEXT_OUTPUT_PATH    = NLPediaSettings.BOA_DATA_DIRECTORY + Constants.RDF_DATA_TEXT_PATH;
    private static final Double TRIPLE_SCORE_THRESHOLD  = NLPediaSettings.getDoubleSetting("score.threshold.write.plaintext.knowledge");
    
    // for the report
    private long writePlainTextOutputFilesTime = 0;
    
    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#getName()
     */
    @Override
    public String getName() {

        return "Plain Text Triple Output Generator Module";
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#run()
     */
    @Override
    public void run() {
        
        this.logger.info("Starting to write plain text triple output files");
        long startWritePlainTextOutputFiles = System.currentTimeMillis();
        
        // for each mapping we write one file in the rdf/nt directory 
        for (Map.Entry<String, Set<Triple>> uriToTriplesMapping : this.moduleInterchangeObject.getNewKnowledge().entrySet() ) {
            
            if ( uriToTriplesMapping.getValue().size() == 0 ) {
                
                this.logger.info("Could not create knowledge (plain text) for mapping: " + uriToTriplesMapping.getKey() + " no data available.");
                continue;
            }
            
            String uriLabel =  uriToTriplesMapping.getKey().substring(uriToTriplesMapping.getKey().lastIndexOf("/") + 1);
            BufferedFileWriter writer = FileUtil.openWriter(RDF_TEXT_OUTPUT_PATH + uriLabel + ".txt", "UTF-8", WRITER_WRITE_MODE.OVERRIDE);
            
            // sort them so that the highest scored triples are on the top
            List<Triple> sortTriples = new ArrayList<Triple>(uriToTriplesMapping.getValue());
            Collections.sort(sortTriples, new TripleScoreComparator());
            
            for ( Triple triple : sortTriples ) {
                
                // only if triple score is higher then x
                if ( triple.getScore() > TRIPLE_SCORE_THRESHOLD ) {
                    
                    writer.write(triple.getScore() + ": <" + triple.getSubject().getUri() + "> <" + triple.getProperty().getUri() + "> <" + triple.getObject().getUri() + "> . ");
                    writer.write("---");
                    int i = 1;
                    for ( Pattern pattern : triple.getLearnedFromPatterns() ) {
                        
                        writer.write("\t" + i + ". " + pattern.getNaturalLanguageRepresentation());
                    }
                    writer.write("---");
                    i = 1;
                    for ( String sentence : triple.getLearnedFromSentences() ) {
                        
                        writer.write("\t" + i + ". " + sentence);
                    }
                    writer.write(Constants.NEW_LINE_SEPARATOR);
                }
            }
            writer.close();
        }
        
        this.writePlainTextOutputFilesTime = System.currentTimeMillis() - startWritePlainTextOutputFiles;
        this.logger.info("Writing plain text output files finished in " + TimeUtil.convertMilliSeconds(this.writePlainTextOutputFilesTime) + "." );
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#getReport()
     */
    @Override
    public String getReport() {

        return "Writing plain text output files finished in " + TimeUtil.convertMilliSeconds(this.writePlainTextOutputFilesTime) + "." ;
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
            FileUtils.listFiles(new File(RDF_TEXT_OUTPUT_PATH), FileFilterUtils.suffixFileFilter(".txt"), null).size() > 0;
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#loadAlreadyAvailableData()
     */
    @Override
    public void loadAlreadyAvailableData() {

        // nothing to do here
    }
}
