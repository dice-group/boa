/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.crawl.Crawler;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import edu.uci.ics.crawler4j.crawler.CrawlController;


/**
 * 
 * @author Daniel Gerber
 */
public class CrawlingCommand implements Command {

	private NLPediaLogger logger = new NLPediaLogger(CrawlingCommand.class);
	
	/**
	 * 
	 */
	public void execute() {
		
		 // read seed url file
        List<List<URL>> seedUrls = this.readSeedUrls();
        
        // crawl each page separately to avoid to much data in ram
        for ( List<URL> seedUrlPart : seedUrls ) {
        	
        	CrawlController controller;
        	
			try {
				
				// set the domain for all crawlers -> see Crawler.shouldVisit()
	        	NLPediaSettings.getInstance().setComplexSetting("seedUrlPart", seedUrlPart);
	        	
				controller = new CrawlController(NLPediaSettings.getInstance().getSetting("crawlDirectory"));
				for ( URL url : seedUrlPart) {
	        		
	        		controller.addSeed(url.toString());
	        	}
	        	controller.start(Crawler.class, new Integer(NLPediaSettings.getInstance().getSetting("numberOfThreads")).intValue());
			}
			catch (Exception e) {
				
				this.logger.fatal("Crawling went wrong!", e);
				e.printStackTrace();
			}
        }
	}
	
	/**
	 * @return
	 */
	private List<List<URL>> readSeedUrls(){
		
		List<List<URL>> seedList = new ArrayList<List<URL>>();
		
		File directory = new File(NLPediaSettings.getInstance().getSetting("urlSeedFileDirectory"));
		
		System.out.println("Directory of seed file: " + NLPediaSettings.getInstance().getSetting("urlSeedFileDirectory"));
		this.logger.info("Directory of seed file: " + NLPediaSettings.getInstance().getSetting("urlSeedFileDirectory"));
		
		try {
			
			File files[] = directory.listFiles();
			for (File f : files) {
				
				System.out.println(f.getAbsolutePath());
				BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(f))));

				List<URL> urls = new ArrayList<URL>();
				
				String line;
				while ((line = br.readLine()) != null) {
					
					urls.add(new URL(line));
				}
				br.close();
				seedList.add(urls);
			}
		}
		catch (Exception e) {
			
			e.printStackTrace();
			this.logger.fatal("Could not read seed url file.", e);
		}
		return seedList;
	}
}
