package de.uni_leipzig.simba.boa.backend;

import java.util.Date;
import java.util.Scanner;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.CrawlingCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.CreateIndexCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.IterationCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.LimesCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.PatternConfidenceMeasureCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.PatternFilterCommand;
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
						
						System.out.println("Good Bye!");
						System.exit(1);
				
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
						// filter patterns
						Command patternFilterCommand = new PatternFilterCommand(null);
						patternFilterCommand.execute();
						
						// calculate confidence, hand over the filtered patterns
						Command patternConfidenceMeasureCommand = new PatternConfidenceMeasureCommand(((PatternFilterCommand) patternFilterCommand).getPatternMappingList());
						patternConfidenceMeasureCommand.execute();
						
						System.out.println("Pattern filter and confidence measurement took: " + (new Date().getTime() - start4) + "ms");
						break;
						
					case 5: // query a single phrase
						Command startQueryCommand = new StartQueryCommand();
						startQueryCommand.execute();
						break;
						
					case 6: // start looking for patterns in index and write them to the db
						Command patternSearchCommand = new PatternSearchCommand();
						((PatternSearchCommand)patternSearchCommand).setIteration(0);
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
//						Command createKnowledgeCommand = new CreateKnowledgeCommand();
//						createKnowledgeCommand.execute();
						break;
						
					case 13:
						
						Command iterationCommand = new IterationCommand();
						iterationCommand.execute();
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
