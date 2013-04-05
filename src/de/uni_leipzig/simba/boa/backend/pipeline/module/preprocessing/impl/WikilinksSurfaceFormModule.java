/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.github.gerbsen.encoding.Encoder.Encoding;
import com.github.gerbsen.file.BufferedFileReader;
import com.github.gerbsen.file.BufferedFileWriter;
import com.github.gerbsen.file.BufferedFileWriter.WRITER_WRITE_MODE;
import com.github.gerbsen.www.FileDownloader;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.AbstractPreprocessingModule;
import de.uni_leipzig.simba.boa.backend.util.TimeUtil;
import edu.stanford.nlp.util.StringUtils;


/**
 * @author gerb
 *
 */
public class WikilinksSurfaceFormModule extends AbstractPreprocessingModule {

    private NLPediaLogger logger = new NLPediaLogger(WikilinksSurfaceFormModule.class);
    
    public static final String DUMP_PATH							= NLPediaSettings.BOA_DATA_DIRECTORY + Constants.DBPEDIA_DUMP_PATH;
    public static final String LANGUAGE								= NLPediaSettings.BOA_LANGUAGE;
    
    public static final String DBPEDIA_LABELS_FILE                  = DUMP_PATH + "labels_" + LANGUAGE + ".nt";
    public static final String DBPEDIA_LABELS_FILE_URL              = "http://downloads.dbpedia.org/3.8/" + LANGUAGE + "/labels_" + LANGUAGE + ".nt.bz2";
    public static final String DBPEDIA_LABELS_FILE_BZ2              = DUMP_PATH + "labels_" + LANGUAGE + ".nt.bz2";

    public static final String DBPEDIA_REDIRECTS_FILE               = DUMP_PATH + "redirects_" + LANGUAGE + ".nt";
    public static final String DBPEDIA_REDIRECTS_FILE_URL           = "http://downloads.dbpedia.org/3.8/" + LANGUAGE + "/redirects_" + LANGUAGE + ".nt.bz2";
    public static final String DBPEDIA_REDIRECTS_FILE_BZ2           = DUMP_PATH + "redirects_" + LANGUAGE + ".nt.bz2";
    
    public static final String DBPEDIA_DISAMBIGUATIONS_FILE         = DUMP_PATH + "disambiguations_" + LANGUAGE + ".nt";
    public static final String DBPEDIA_DISAMBIGUATIONS_FILE_URL     = "http://downloads.dbpedia.org/3.8/" + LANGUAGE + "/disambiguations_" + LANGUAGE + ".nt.bz2";
    public static final String DBPEDIA_DISAMBIGUATIONS_FILE_BZ2     = DUMP_PATH + "disambiguations_" + LANGUAGE + ".nt.bz2";
    
    public static final String DBPEDIA_INTERLANGUAGE_LINKS_FILE     = DUMP_PATH + "interlanguage_links_" + LANGUAGE + ".ttl";
    public static final String DBPEDIA_INTERLANGUAGE_LINKS_FILE_URL = "http://downloads.dbpedia.org/3.8/" + LANGUAGE + "/interlanguage_links_" + LANGUAGE + ".ttl.bz2";
    public static final String DBPEDIA_INTERLANGUAGE_LINKS_FILE_BZ2 = DUMP_PATH + "interlanguage_links_" + LANGUAGE + ".ttl.bz2";
    
    private long downloadDBpediaSpotlightFilesTime = 0; 

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#getName()
     */
    @Override
    public String getName() {

        return "Google Wikilinks Surface Form Module";
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#run()
     */
    @Override
    public void run() {

        // we start here three threads which download the three files
        long startDownloadDBpediaSpotlightFiles = System.currentTimeMillis();
        this.logger.info("Starting to download dbpedia spotlight files.");
        
        this.startDownloadsThread();
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
    
    private void startDownloadsThread() {
    	
    	
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

        return false;
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#loadAlreadyAvailableData()
     */
    @Override
    public void loadAlreadyAvailableData() {

        // nothing to do here
    }
    
    public static void main(String[] args) {
		
    	BufferedFileReader reader = new BufferedFileReader("/Users/gerb/Development/workspaces/experimental/boa/qa/en/google/data-00000-of-00010", Encoding.UTF_8);
    	Map<String, Set<String>> urisToSurfaceForms = new TreeMap<String,Set<String>>();
    	String line = "";
    	while ( (line = reader.readLine()) != null) {
    		
    		if ( line.startsWith("MENTION") ) {
    			
    			// MENTION Lincoln Continental Mark IV 40110 http://en.wikipedia.org/wiki/Lincoln_Continental_Mark_IV
    			String[] parts = line.split("\t");
				if ( !urisToSurfaceForms.containsKey(parts[3]) ) urisToSurfaceForms.put(parts[3], new HashSet<String>());
    			urisToSurfaceForms.get(parts[3]).add(parts[1]);
    		}
    	}
    	
    	BufferedFileWriter writer = new BufferedFileWriter("/Users/gerb/Development/workspaces/experimental/boa/qa/en/google/test.tsv", Encoding.UTF_8, WRITER_WRITE_MODE.OVERRIDE);
    	for ( Map.Entry<String, Set<String>> entry : urisToSurfaceForms.entrySet()) {
    		writer.write(entry.getKey() + "\t" + StringUtils.join(entry.getValue(), "\t"));
    	}
    	writer.close();
    	reader.close();
	}
}
