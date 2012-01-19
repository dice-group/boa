package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.lucene.queryParser.ParseException;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.search.PatternSearcher;

/**
 * 
 * @author Daniel Gerber
 */
public class StartQueryCommand implements Command {

	NLPediaLogger logger = new NLPediaLogger(StartQueryCommand.class);
	
	@Override
	public void execute() {

		try {
			
			System.out.print("Please enter a keyphrase:\t");
			
			Scanner scanner = new Scanner(new BufferedInputStream(System.in), "UTF-8");
			String keyphrase = scanner.nextLine();
			
			PatternSearcher patternSearcher = new PatternSearcher(NLPediaSettings.BOA_DATA_DIRECTORY + NLPediaSettings.getInstance().getSetting("sentenceIndexDirectory"));
			List<String> sentences = new ArrayList<String>(patternSearcher.getExactMatchSentences(keyphrase, 1000));
			for (String sentence : sentences) {
				
				System.out.println(sentence);
			}
			System.out.println("Size of result list for keyphrase querying:\t" + sentences.size());
			this.logger.debug("Size of result list for keyphrase querying:\t" + sentences.size());
		}
		catch (IOException ioe) {
			
			ioe.printStackTrace();
			this.logger.error("Could not read input from System.in", ioe);
		}
		catch (ParseException pe) {

			pe.printStackTrace();
			this.logger.error("Could not read index in directory " + NLPediaSettings.getInstance().getSetting("sentenceIndexDirectory"), pe);
		}
	}
}
