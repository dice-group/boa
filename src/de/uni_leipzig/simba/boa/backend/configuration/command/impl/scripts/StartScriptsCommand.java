package de.uni_leipzig.simba.boa.backend.configuration.command.impl.scripts;

import java.util.Scanner;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.CreateMachineLearningCommand;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.LoadKnowledgeCommand;
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
		    		System.out.println("\t\t1. Create sentence per line file");
		    		System.out.println("\t\t2. Ask DBpedia for triples");
		    		System.out.println("\t\t3. Reset pattern confidence measures");
		    		System.out.println("\t\t4. Create learned from distribution");
		    		System.out.println("\t\t5. Create knowledge statistics");
		    		System.out.println("\t\t6. Train NER model");
		    		System.out.println("\t\t7. Create pos distirbution");
		    		System.out.println("\t\t8. Load knowledge");
		    		System.out.println("\t\t9. Create pattern index");
		    		System.out.println("\t\t10. Create machine learning input");
		    		System.out.println("\t\t11. Create surface forms");
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
							
						case 4:
							
							Command createLearndFromDistributionCommand = new CreateLearndFromDistributionCommand();
							createLearndFromDistributionCommand.execute();
							break;
							
						case 5:
							
							Command createKnowledgeStatisticsCommand = new KnowledgeStatisticsCommand();
							createKnowledgeStatisticsCommand.execute();
							break;
							
						case 6:
							
							Command trainNerModelCommand = new TrainNerModelCommand();
							trainNerModelCommand.execute();
							break;
							
						case 7:
							
							Command posDistributionCommand = new PosDistributionCommand();
							posDistributionCommand.execute();
							break;	
					
						case 8:
							
							Command loadKnowledgeCommand = new LoadKnowledgeCommand();
							loadKnowledgeCommand.execute();
							break;
							
						case 9:
							
							Command createQaIndexCommand = new CreateQuestionAnsweringIndexCommand();
							createQaIndexCommand.execute();
							break;
							
						case 10:
							
							Command createMLCommand = new CreateMachineLearningCommand();
							createMLCommand.execute();
							break;
							
						case 11:
							
							Command createSurfaceFormCommand = new CreateSurfaceFormCommand();
							createSurfaceFormCommand.execute();
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
