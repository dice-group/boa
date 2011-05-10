package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.crawl.RelationFinder;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.util.GoogleQuery;

/**
 * 
 * @author Daniel Gerber
 */
public class WriteUrlsToFileCommand implements Command {

	@Override
	public void execute() {

		final String LABEL_OUTPUT_FILE		= NLPediaSettings.getInstance().getSetting("labelOutputFile");
		final NLPediaLogger logger 			= new NLPediaLogger(WriteUrlsToFileCommand.class);
		
		PrintWriter writer = null;
		
		try {
			
			writer = new PrintWriter(new BufferedWriter(new FileWriter("/Users/gerb/Development/workspaces/java-ws/nlpedia/data/url/city.txt")));
			
			List<String[]> labelTriples = RelationFinder.getRelationFromFile(LABEL_OUTPUT_FILE);
			List<String> urls = new ArrayList<String>();
			
			for (int i = 0 ; i < labelTriples.size() ; i++ ) {
				
				// be patient with google, otherwise google will punish you hard!
				Thread.sleep(200);
				Map<String, String> queryResults = GoogleQuery.queryGoogle(labelTriples.get(i)[0] + " " + labelTriples.get(i)[1], "http://exmaple.com", 5);
				
				if ( queryResults != null ) {
					
					for (String url : queryResults.keySet()) {

						writer.write(url);
						writer.write(System.getProperty("line.separator"));
						logger.info(url);
					}
				} 
			}
		}
		catch (IOException e) {

			e.printStackTrace();
		}
		catch (InterruptedException e) {

			e.printStackTrace();
		}
	}
}
