/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl;

import java.io.File;

import de.danielgerber.www.FileDownloader;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.AbstractPreprocessingModule;
import de.uni_leipzig.simba.boa.backend.util.TimeUtil;

/**
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public class WikipediaDownloadModule extends AbstractPreprocessingModule {

    private final NLPediaLogger logger      = new NLPediaLogger(WikipediaDownloadModule.class);
    
    private static final String DOWNLOAD_DIRECTORY = NLPediaSettings.BOA_DATA_DIRECTORY + "wikipedia/"; 
    
    // for the report
    private long downloadWikipediaTime = 0;
    
    @Override
    public String getName() {

        return "Wikipedia Download Module";
    }

    @Override
    public void run() {

        String dumpUrl = "http://dumps.wikimedia.org/" + NLPediaSettings.BOA_LANGUAGE + "wiki/latest/" + NLPediaSettings.BOA_LANGUAGE + "wiki-latest-pages-articles.xml.bz2";
        String filename = DOWNLOAD_DIRECTORY + NLPediaSettings.BOA_LANGUAGE + "wiki-latest-pages-articles.xml.bz2";
        
        long startWikipediaDownload = System.currentTimeMillis();
        this.logger.info("Starting to download latest wikipedia articles dump!");
        FileDownloader.downloadFile(dumpUrl, filename);
        this.downloadWikipediaTime = System.currentTimeMillis() - startWikipediaDownload;
        this.logger.info("Finished downloading of " + NLPediaSettings.BOA_LANGUAGE + "-wikipedia dump in " + TimeUtil.convertMilliSeconds(this.downloadWikipediaTime));
    }

    @Override
    public String getReport() {

        return "Finished downloading of " + NLPediaSettings.BOA_LANGUAGE + "-wikipedia dump in " + TimeUtil.convertMilliSeconds(this.downloadWikipediaTime);
    }

    @Override
    public void updateModuleInterchangeObject() {

        // nothing to do here
    }

    @Override
    public boolean isDataAlreadyAvailable() {

        return new File(DOWNLOAD_DIRECTORY + NLPediaSettings.BOA_LANGUAGE + "wiki-latest-pages-articles.xml.bz2").exists() || 
                new File(DOWNLOAD_DIRECTORY + NLPediaSettings.BOA_LANGUAGE + "wiki-latest-pages-articles.xml").exists();
    }

    @Override
    public void loadAlreadyAvailableData() {

        // nothing to do here
    }

}
