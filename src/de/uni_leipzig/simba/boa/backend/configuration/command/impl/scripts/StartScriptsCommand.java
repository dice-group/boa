package de.uni_leipzig.simba.boa.backend.configuration.command.impl.scripts;

import java.util.Scanner;

import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.ResetPatternCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.UnknownOptionCommand;
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
		    		System.out.println("\t\t2. Reset pattern confidence measures");
		    		System.out.println("\t\t3. Create learned from distribution");
		    		System.out.println("\t\t4. Create pos distirbution");
		    		System.out.println("\t\t5. Create pattern index");
		    		System.out.println("\t\t6. Create machine learning input");
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
					
						case 1: // look if generated triples are present in dbpedia
							
							Command resetPatternCommand = new ResetPatternCommand();
							resetPatternCommand.execute();
							break;
							
						case 2:
							
							Command createLearndFromDistributionCommand = new CreateLearndFromDistributionCommand();
							createLearndFromDistributionCommand.execute();
							break;
							
						case 3:
							
							Command posDistributionCommand = new PosDistributionCommand();
							posDistributionCommand.execute();
							break;	
					
						case 4:
							
							Command createQaIndexCommand = new CreateQuestionAnsweringIndexCommand();
							createQaIndexCommand.execute();
							break;
							
						default: // option not supported
							Command unkownOptionCommand = new UnknownOptionCommand(System.out);
							unkownOptionCommand.execute();
							break;
					}
				}
				catch (Exception e) {
					
					System.out.println("Unknown option selected! Most likely your fingers are too fat... try clicking with a pencil!\n");
					e.printStackTrace();
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
