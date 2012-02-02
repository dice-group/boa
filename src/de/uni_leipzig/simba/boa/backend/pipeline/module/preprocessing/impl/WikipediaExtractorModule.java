/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl;

import java.io.File;
import java.io.IOException;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.AbstractPreprocessingModule;
import de.uni_leipzig.simba.boa.backend.util.TimeUtil;


/**
 * @author gerb
 *
 */
public class WikipediaExtractorModule extends AbstractPreprocessingModule {

    private final NLPediaLogger logger = new NLPediaLogger(WikipediaExtractorModule.class);
    
    // for the report
    private long wikipediaExtractionTime;

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#getName()
     */
    @Override
    public String getName() {

        return "Wikipedia Dump Extraction Module";
    }

    /**
     * This method extract the wikipedia dump file into the BOA index
     * format xml document. Please keep in mind that you will need bunzip2 
     * and python installed on your system. Also chmod needs to be present, 
     * this means that someone needs to make this also available for windows 
     * machines in future work.
     * Please also note that this method will take some time according to your
     * machine and the wikipedia dump you are processing. For a MacBook Pro 
     * with Intel Core i7 (2Ghz) and a wikipedia dump file (Korean language) 
     * of zipped 240MB and unzipped 1,11GB this takes about 20 minutes.    
     */
    @Override
    public void run() {

        long startWikipediaExtractionTime = System.currentTimeMillis();
        this.logger.info("Starting to extract wikidepdia dump");
        
        String wikipediaDumpPath    = NLPediaSettings.BOA_DATA_DIRECTORY + "wikipedia/" + NLPediaSettings.BOA_LANGUAGE + "wiki-latest-pages-articles.xml.bz2";
        String extractorInputPath   = NLPediaSettings.BOA_DATA_DIRECTORY + "wikipedia/" + NLPediaSettings.BOA_LANGUAGE + "wiki-latest-pages-articles.xml";
        String extractorScriptPath  = NLPediaSettings.BOA_BASE_DIRECTORY + "scripts/WikiExtractor.py";
        String outputPath           = NLPediaSettings.BOA_DATA_DIRECTORY + "raw/";
        
        try {
            
            Process chmod = Runtime.getRuntime().exec("chmod +x " + extractorScriptPath);
            if ( chmod.waitFor() != 0 ) throw new RuntimeException("Chmod of extractor script failed!");
            this.logger.info("Chmod of Wikipedia Extractor successful!");
            
            // only unzip if not already unzipped
            if ( !new File(extractorInputPath).exists() ) {

                Process bunzip2 = Runtime.getRuntime().exec("bunzip2 " + wikipediaDumpPath);
                if ( bunzip2.waitFor() != 0 ) throw new RuntimeException("bunzip2 of wikipedia dump failed!");
                this.logger.info("Unzipping of wikipedia dump finished!");
            }
            
            Process wikiextract = Runtime.getRuntime().exec(extractorScriptPath + " -i " + extractorInputPath + " -l ko -o " + outputPath);
            if ( wikiextract.waitFor() != 0 ) throw new RuntimeException("Wikiextractor failed!");
            this.logger.info("Extracting of wikipedia article text from xml format done.");
        }
        catch (IOException e) {
            
            e.printStackTrace();
            String error = "Could not find required files!";
            this.logger.error(error, e);
            throw new RuntimeException(error, e);
        }
        catch (InterruptedException e) {
            
            e.printStackTrace();
            String error = "Extracting process was interrupted!";
            this.logger.error(error, e);
            throw new RuntimeException(error, e);
        }
        this.wikipediaExtractionTime = System.currentTimeMillis() - startWikipediaExtractionTime;
        this.logger.info("Finished extracting wikipedia dump in " + TimeUtil.convertMilliSeconds(this.wikipediaExtractionTime) + ".");
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#getReport()
     */
    @Override
    public String getReport() {

        return "Finished extracting wikipedia dump in " + TimeUtil.convertMilliSeconds(this.wikipediaExtractionTime) + ".";
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

        return new File(NLPediaSettings.BOA_DATA_DIRECTORY + "raw/" + NLPediaSettings.BOA_LANGUAGE + "wiki.xml").exists();
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#loadAlreadyAvailableData()
     */
    @Override
    public void loadAlreadyAvailableData() {

        // nothing to do here
    }

}
