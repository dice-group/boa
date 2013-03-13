/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.github.gerbsen.file.BufferedFileWriter;
import com.github.gerbsen.file.BufferedFileWriter.WRITER_WRITE_MODE;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.AbstractPreprocessingModule;
import de.uni_leipzig.simba.boa.backend.util.TimeUtil;
import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;
import edu.jhu.nlp.wikipedia.WikiXMLParser;
import edu.jhu.nlp.wikipedia.WikiXMLParserFactory;


/**
 * @author gerb
 *
 */
public class WikiXmlJWikipediaExtractorModule extends AbstractPreprocessingModule {

    private final NLPediaLogger logger = new NLPediaLogger(WikiXmlJWikipediaExtractorModule.class);
    
    // for the report
    private long wikipediaExtractionTime;

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#getName()
     */
    @Override
    public String getName() {

        return "WikiXmlJ Extraction Module";
    }

    /**
     */
    @Override
    public void run() {

        long startWikipediaExtractionTime = System.currentTimeMillis();
        this.logger.info("Starting to extract wikipedia dump");
        
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
            
            final BufferedFileWriter writer = new BufferedFileWriter(outputPath + NLPediaSettings.BOA_LANGUAGE + "wiki.xml", "UTF-8", WRITER_WRITE_MODE.OVERRIDE);
            
            WikiXMLParser wxsp = WikiXMLParserFactory.getSAXParser(extractorInputPath);
            wxsp.setPageCallback(new PageCallbackHandler() { 
            	int id = 1;
            	
	            public void process(WikiPage page) {
	            	
	            	writer.write("<doc id=\""+ (id++) +"\" url=\"http://dbpedia.org/resource/"+wikiEncode(page.getTitle())+"\">");
	            	writer.write(removeInfoBox(page));
	            	writer.write("</doc>");
	            }

				private String removeInfoBox(WikiPage page) {
					
					String text = page.getText();
					while ( text.contains("{|") ) {
						
						int startIndex = text.indexOf("{|");
						int endIndex = text.indexOf("|}", startIndex);
						
						text = text.replace(text.substring(startIndex, endIndex), "");
					}
					
					return text;
				}
            });
            
            wxsp.parse();
            writer.close();
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
        } catch (Exception e) {
        	
        	e.printStackTrace();
            String error = "Extracting process was interrupted!";
            this.logger.error(error, e);
            throw new RuntimeException(error, e);
		}
        this.wikipediaExtractionTime = System.currentTimeMillis() - startWikipediaExtractionTime;
        this.logger.info("Finished extracting wikipedia dump in " + TimeUtil.convertMilliSeconds(this.wikipediaExtractionTime) + ".");
    }
    
    private String wikiEncode(String title) {
    	
        // replace spaces by underscores.
        // Note: MediaWiki apparently replaces only spaces by underscores, not other whitespace. 
        String encoded = title.trim().replace(' ', '_');
        
        // normalize duplicate underscores
        encoded = encoded.replaceAll("_+", "_");
        
        // trim underscores from start 
        encoded = encoded.replaceAll("^_", "");
        
        // trim underscores from end 
        encoded = encoded.replaceAll("_$", "");

        encoded = StringUtils.capitalize(encoded);

        // URL-encode everything but ':' '/' '&' and ',' - just like MediaWiki
        try {
			encoded = URLEncoder.encode(encoded, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        encoded = encoded.replace("%3A", ":");
        encoded = encoded.replace("%2F", "/");
        encoded = encoded.replace("%26", "&");
        encoded = encoded.replace("%2C", ",");

        return encoded;
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
