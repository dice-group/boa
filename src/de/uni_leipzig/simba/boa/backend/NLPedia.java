package de.uni_leipzig.simba.boa.backend;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.CreateIndexCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.CreateKnowledgeCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.IterationCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.PatternScoreCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.PatternScoreFeatureCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.PatternSearchCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.PrintOptionCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.StartQueryCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.UnknownOptionCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.WriteRelationToFileCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.scripts.StartScriptsCommand;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.pipeline.Pipeline;
import de.uni_leipzig.simba.boa.evaluation.Evaluation;

/**
 * 
 * @author Daniel Gerber
 */
public class NLPedia {

	public static final String CACHE_KEY_PATTERN_MAPPING_LIST = "CACHE_KEY_PATTERN_MAPPING_LIST";
	public static final String CACHE_KEY_TRIPLE_LIST = "CACHE_KEY_TRIPLE_LIST";
	
	private static Map<String,Object> cache = new HashMap<String,Object>();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// this is used to surpress the "error" messages from stanford etc.
//		PrintStream newErr = new PrintStream(new ByteArrayOutputStream());
//		System.setErr(newErr);
		
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
					
					case 2: // start indexing
						Command indexOptionCommand = new CreateIndexCommand();
						indexOptionCommand.execute();
						break;
						
					case 3: // evaluate pattern
						Command patternConfidenceMeasureCommand = new PatternScoreFeatureCommand(null);
						patternConfidenceMeasureCommand.execute();
						break;
						
					case 4: // query a single phrase
						Command patternScoreCommand = new PatternScoreCommand(null);
						patternScoreCommand.execute();
						break;
						
					case 5: // start looking for patterns in index and write them to the db
						Command patternSearchCommand = new PatternSearchCommand(null);
						patternSearchCommand.execute();
						break;
						
					case 6: // query dbpedia and serialize background knowledge
						Command writeRealtionToFileCommand = new WriteRelationToFileCommand();
						writeRealtionToFileCommand.execute();
						break;
						
					case 7: // start scripts here
						Command startScriptCommand = new StartScriptsCommand();
						startScriptCommand.execute();
						break;
						
					case 8: // 
						Command createKnowledgeCommand = new CreateKnowledgeCommand(null);
						createKnowledgeCommand.execute();
						break;
						
					case 9: 
						Command startQueryCommand = new StartQueryCommand();
						startQueryCommand.execute();
						break;
						
					case 10:
						
						Command iterationCommand = new IterationCommand();
						iterationCommand.execute();
						break;
						
					case 11:
						
						Command evaluationCommand = new Evaluation();
						evaluationCommand.execute();
						break;
						
					case 12:
						
						Pipeline pipeline = new Pipeline();
						pipeline.run();
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

	/**
	 * @return the cache
	 */
	public static Map<String,Object> getCache() {

		return cache;
	}

	/**
	 * @param cache the cache to set
	 */
	public static void setCache(Map<String,Object> cache) {

		NLPedia.cache = cache;
	}
}
