package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.util.Scanner;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.scripts.AskDbpediaForTriple;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.scripts.PlainTextToSentencePerLineCommand;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;


public class StartScriptsCommand implements Command {

	private NLPediaLogger logger = new NLPediaLogger(StartScriptsCommand.class);
	
	@Override
	public void execute() {
		
		int option = -1;
		boolean goBack = false;

		 do {
		    	
		    	try {
		    		
		    		System.out.println("\t\t0. Go back");
		    		System.out.println("\t\t1. Create sentence per line file");
		    		System.out.println("\t\t2. Ask DBpedia for triples");
		    		System.out.println("\t\t3. Reset pattern evaluation");
		    		System.out.println("\t\t");
		    		System.out.println("");
		    		
		    		System.out.print("Select option:\t");
		    		
					Scanner scanner = new Scanner(System.in);
					option = scanner.nextInt();
					
					switch (option) {
						
						case 0:
							
							// ensure wo go out of the while loop
							goBack = true;
							break;
					
						case 1: // Take a plain text input file and detect sentences and write them to a file one per line
							
							String input = NLPediaSettings.getInstance().getSetting("rawDataInputDirectory");
							String ouput = NLPediaSettings.getInstance().getSetting("sentencePerLineOutputFile");
							
							Command plainTextToSentencePerLineCommand = new PlainTextToSentencePerLineCommand(input, ouput);
							plainTextToSentencePerLineCommand.execute();
							break;
							
						case 2: // look if generated triples are present in dbpedia
							
							Command askDbpediaCommand = new AskDbpediaForTriple();
							askDbpediaCommand.execute();
							break;
						
						case 3: // look if generated triples are present in dbpedia
							
							Command resetPatternCommand = new ResetPatternCommand();
							resetPatternCommand.execute();
							break;
					
						default: // option not supported
							Command unkownOptionCommand = new UnknownOptionCommand(System.out);
							unkownOptionCommand.execute();
							break;
					}
				}
				catch (Exception e) {
					
					System.out.println("Unknown option selected! Most likely your fingers are too fat... try clicking with a pencil!\n");
					logger.error("Something went wrong during option selection", e);
				}
				finally {
					
					if ( !goBack ) {
						
						option = -1; // reset the option
					}
				}
		    } 
		    while ( option != 0 ); 
	}
}
