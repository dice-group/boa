package de.uni_leipzig.simba.boa.backend;

import java.util.Date;
import java.util.Scanner;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.CrawlingCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.CreateIndexCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.CreateKnowledgeCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.ExitCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.LimesCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.PatternFilteringCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.PatternSearchCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.PrintOptionCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.StartQueryCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.StartScriptsCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.StartStatisticsCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.UnknownOptionCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.WriteRelationToFileCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.WriteUrlsToFileCommand;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

/**
 * 
 * @author Daniel Gerber
 */
public class NLPedia {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// Initialize logging, settings, factories etc., needs to be FIRST call!!
		NLPediaSetup setup = new NLPediaSetup(false);
		NLPediaLogger logger = new NLPediaLogger(NLPedia.class);
		
		int option = -1;
	    
	    // show "gui" 
		Command gui = new PrintOptionCommand(System.out);
		gui.execute();
	    
	    do {
	    	
	    	try {
	    		
	    		System.out.print("Select option, press \"1\" for help:\t");
	    		
				Scanner scanner = new Scanner(System.in);
				option = scanner.nextInt();
				
				switch (option) {
					
					case 0: // Exit key pressed
						Command exitOptionCommand = new ExitCommand(System.out, setup);
						exitOptionCommand.execute();
						break;
				
					case 1: // show the options of nlpedia
						Command printOptionCommand = new PrintOptionCommand(System.out);
						printOptionCommand.execute();
						break;
					
					case 2: // start crawling
						Command crawlingOptionCommand = new CrawlingCommand();
						crawlingOptionCommand.execute();
						break;
						
					case 3: // start indexing
						Command indexOptionCommand = new CreateIndexCommand();
						indexOptionCommand.execute();
						break;
						
					case 4: // evaluate pattern
						long start4 = new Date().getTime(); 
						Command patternEvaluationCommand = new PatternFilteringCommand();
						patternEvaluationCommand.execute();
						System.out.println("Pattern search took: " + (new Date().getTime() - start4) + "ms");
						break;
						
					case 5: // query a single phrase
						Command startQueryCommand = new StartQueryCommand();
						startQueryCommand.execute();
						break;
						
					case 6: // start looking for patterns in index and write them to the db
						Command patternSearchCommand = new PatternSearchCommand();
						((PatternSearchCommand)patternSearchCommand).setFoundInIteration(0);
						patternSearchCommand.execute();
						break;
						
					case 7: // query dbpedia and serialize background knowledge
						Command writeRealtionToFileCommand = new WriteRelationToFileCommand();
						writeRealtionToFileCommand.execute();
						break;
						
					case 8: // query google for suitable urls
						Command wirteUrlsToFileCommand = new WriteUrlsToFileCommand();
						wirteUrlsToFileCommand.execute();
						break;
						
					case 9: // start statistics here
						Command startStatisticsCommand = new StartStatisticsCommand();
						startStatisticsCommand.execute();
						break;
						
					case 10: // start scripts here
						Command startScriptCommand = new StartScriptsCommand();
						startScriptCommand.execute();
						break;
						
					case 11: // start scripts here
						Command limesCommand = new LimesCommand();
						limesCommand.execute();
						break;
						
					case 12: // start scripts here
						Command createKnowledgeCommand = new CreateKnowledgeCommand();
						createKnowledgeCommand.execute();
						break;
						
					case 13:
						
						long start13 = new Date().getTime();

						// search the patterns
						Command patternSearchCommand1 = new PatternSearchCommand();
						((PatternSearchCommand)patternSearchCommand1).setFoundInIteration(0);
						patternSearchCommand1.execute();
						
						// evaluate them
						Command patternEvaluationCommand1 = new PatternFilteringCommand();
						patternEvaluationCommand1.execute();
						
						// generate rdf
						Command createKnowledgeCommand1 = new CreateKnowledgeCommand();
						createKnowledgeCommand1.execute();
						
						System.out.println("Pattern search took: " + (new Date().getTime() - start13) + "ms");
						break;
						
					default: // option not supported
						Command unkownOptionCommand = new UnknownOptionCommand(System.out);
						unkownOptionCommand.execute();
						break;
				}
			}
			catch (Exception e) {
				
				System.err.println("Unknown option selected! Most likely your fingers are too fat... try clicking with a pencil!\n");
				e.printStackTrace();
				logger.error("Something went wrong during option selection", e);
			}
			finally {
				
				option = -1; // reset the option
			}
	    } 
	    while ( option != 0 ); 
	}
}
