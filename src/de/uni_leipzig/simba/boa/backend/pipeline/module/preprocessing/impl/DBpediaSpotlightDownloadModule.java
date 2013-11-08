/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl;

import java.io.File;
import java.io.IOException;

import com.github.gerbsen.www.FileDownloader;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.AbstractPreprocessingModule;
import de.uni_leipzig.simba.boa.backend.util.TimeUtil;


/**
 * @author gerb
 *
 */
public class DBpediaSpotlightDownloadModule extends AbstractPreprocessingModule {

    private NLPediaLogger logger = new NLPediaLogger(DBpediaSpotlightDownloadModule.class);
    
    public static final String DUMP_PATH							= NLPediaSettings.BOA_DATA_DIRECTORY + Constants.DBPEDIA_DUMP_PATH;
    public static final String LANGUAGE								= NLPediaSettings.BOA_LANGUAGE;
    
    public static final String DBPEDIA_LABELS_FILE                  = DUMP_PATH + "labels_" + LANGUAGE + ".nt";
    public static final String DBPEDIA_LABELS_FILE_URL              = "http://downloads.dbpedia.org/3.9/" + LANGUAGE + "/labels_" + LANGUAGE + ".nt.bz2";
    public static final String DBPEDIA_LABELS_FILE_BZ2              = DUMP_PATH + "labels_" + LANGUAGE + ".nt.bz2";

    public static final String DBPEDIA_REDIRECTS_FILE               = DUMP_PATH + "redirects_" + LANGUAGE + ".nt";
    public static final String DBPEDIA_REDIRECTS_FILE_URL           = "http://downloads.dbpedia.org/3.9/" + LANGUAGE + "/redirects_" + LANGUAGE + ".nt.bz2";
    public static final String DBPEDIA_REDIRECTS_FILE_BZ2           = DUMP_PATH + "redirects_" + LANGUAGE + ".nt.bz2";
    
    public static final String DBPEDIA_DISAMBIGUATIONS_FILE         = DUMP_PATH + "disambiguations_" + LANGUAGE + ".nt";
    public static final String DBPEDIA_DISAMBIGUATIONS_FILE_URL     = "http://downloads.dbpedia.org/3.9/" + LANGUAGE + "/disambiguations_" + LANGUAGE + ".nt.bz2";
    public static final String DBPEDIA_DISAMBIGUATIONS_FILE_BZ2     = DUMP_PATH + "disambiguations_" + LANGUAGE + ".nt.bz2";
    
    public static final String DBPEDIA_INTERLANGUAGE_LINKS_FILE     = DUMP_PATH + "interlanguage_links_" + LANGUAGE + ".ttl";
    public static final String DBPEDIA_INTERLANGUAGE_LINKS_FILE_URL = "http://downloads.dbpedia.org/3.9/" + LANGUAGE + "/interlanguage_links_" + LANGUAGE + ".ttl.bz2";
    public static final String DBPEDIA_INTERLANGUAGE_LINKS_FILE_BZ2 = DUMP_PATH + "interlanguage_links_" + LANGUAGE + ".ttl.bz2";
    
    private long downloadDBpediaSpotlightFilesTime = 0; 

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#getName()
     */
    @Override
    public String getName() {

        return "DBpedia Spotlight Download Module";
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#run()
     */
    @Override
    public void run() {

        // we start here three threads which download the three files
        long startDownloadDBpediaSpotlightFiles = System.currentTimeMillis();
        this.logger.info("Starting to download dbpedia spotlight files.");
        
        Thread labels           	= this.startDownloadThread(DBPEDIA_LABELS_FILE_URL,           		DBPEDIA_LABELS_FILE_BZ2);
        Thread redirects        	= this.startDownloadThread(DBPEDIA_REDIRECTS_FILE_URL,        		DBPEDIA_REDIRECTS_FILE_BZ2);
        Thread disambiguations  	= this.startDownloadThread(DBPEDIA_DISAMBIGUATIONS_FILE_URL,  		DBPEDIA_DISAMBIGUATIONS_FILE_BZ2);
        Thread interlanguageLinks	= this.startDownloadThread(DBPEDIA_INTERLANGUAGE_LINKS_FILE_URL,  	DBPEDIA_INTERLANGUAGE_LINKS_FILE_BZ2);
        
        this.waitForThreads(labels, redirects, disambiguations, interlanguageLinks);
        this.unzip();
        
        this.downloadDBpediaSpotlightFilesTime = System.currentTimeMillis() - startDownloadDBpediaSpotlightFiles;
        this.logger.info("Downloading the DBpedia files finished in " + TimeUtil.convertMilliSeconds(this.downloadDBpediaSpotlightFilesTime));
    }
    
    /**
     * 
     */
    private void unzip() {

        try {
            
            Process unzip = Runtime.getRuntime().exec("bunzip2 " + DBPEDIA_LABELS_FILE_BZ2);
            if ( unzip.waitFor() != 0 ) throw new RuntimeException("bunzip2 of labels file failed!");
            this.logger.info("Unzipping of labels file finished!");
            
            unzip = Runtime.getRuntime().exec("bunzip2 " + DBPEDIA_REDIRECTS_FILE_BZ2);
            if ( unzip.waitFor() != 0 ) throw new RuntimeException("bunzip2 of redirect file ailed!");
            this.logger.info("Unzipping of redirects file finished!");
            
            unzip = Runtime.getRuntime().exec("bunzip2 " + DBPEDIA_DISAMBIGUATIONS_FILE_BZ2);
            if ( unzip.waitFor() != 0 ) throw new RuntimeException("bunzip2 of dismabiguations file failed!");
            this.logger.info("Unzipping of dismabiguations file finished!");
            
            unzip = Runtime.getRuntime().exec("bunzip2 " + DBPEDIA_INTERLANGUAGE_LINKS_FILE_BZ2);
            if ( unzip.waitFor() != 0 ) throw new RuntimeException("bunzip2 of interlanguage links failed!");
            this.logger.info("Unzipping of interlanguage links file finished!");
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
    }

    /**
     * 
     * @param labels
     * @param redirects
     * @param disambiguations
     */
    private void waitForThreads(Thread labels, Thread redirects, Thread disambiguations, Thread links) {

        try {
            
            labels.join();
            redirects.join();
            disambiguations.join();
            links.join();
        }
        catch (InterruptedException e) {
            
            String error = "Download failed!";   
            e.printStackTrace();
            logger.error(error, e);
            throw new RuntimeException(error, e);
        }
    }

    /**
     * 
     * @param url
     * @param filename
     */
    private Thread startDownloadThread(final String url, final String filename) {
        
        Thread downloadThread = null;
        
        if ( FileDownloader.exists(url) ) {
            
            downloadThread = new Thread() {

                public void run() {

                    FileDownloader.downloadFile(url, filename);
                    logger.info("Starting to download file: " + filename + " from url: " + url);
                }
            };
            downloadThread.start();
        }
        else {
            
            this.logger.info("Remote file " +  url + " does not exist. Could not download!");
        }
        
        return downloadThread;
    }
    
    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#getReport()
     */
    @Override
    public String getReport() {

        return "Downloading the DBpedia files finished in " + TimeUtil.convertMilliSeconds(this.downloadDBpediaSpotlightFilesTime);
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

        return (new File(DBPEDIA_DISAMBIGUATIONS_FILE_BZ2).exists() && new File(DBPEDIA_REDIRECTS_FILE_BZ2).exists() 
        			&& new File(DBPEDIA_LABELS_FILE_BZ2).exists() && new File(DBPEDIA_INTERLANGUAGE_LINKS_FILE_BZ2).exists()) 
                ||
               (new File(DBPEDIA_DISAMBIGUATIONS_FILE).exists() && new File(DBPEDIA_REDIRECTS_FILE).exists() 
            		   && new File(DBPEDIA_LABELS_FILE).exists() && new File(DBPEDIA_INTERLANGUAGE_LINKS_FILE).exists());
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#loadAlreadyAvailableData()
     */
    @Override
    public void loadAlreadyAvailableData() {

        // nothing to do here
    }
}
