package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.io.PrintStream;

import de.uni_leipzig.simba.boa.backend.configuration.command.Command;


public class PrintOptionCommand implements Command {

	private PrintStream out = null;
	
	public PrintOptionCommand(PrintStream out) {
		
		this.out = out;
	}
	
	@Override
	public void execute() {

		String options = 
			"========================================================\n" +
			"||            NLPedia Command Line Interface          ||\n" +
			"========================================================\n" +
			"||                                                    ||\n" + 
			"|| Options:                                           ||\n" + 
			"||     0. Exit NLPedia CLI                            ||\n" + 
			"||     1. Print options                               ||\n" + 
			"||     2. Create Lucene index                         ||\n" + 
			"||     3. Measure patterns                            ||\n" + 
			"||     4. Score patterns                              ||\n" +
			"||     5. Search for patterns                         ||\n" +
			"||     6. Get relations from dbpedia                  ||\n" +
			"||     7. Start Scripts                               ||\n" + 
			"||     8. Query text with patterns                    ||\n" + 
			"||     9. Query keyphrase                             ||\n" +
			"||    10. Search - Evaluation - Generate              ||\n" +
			"||    11. Evaluation                                  ||\n" +
			"||    12. Start Pipeline                              ||\n" +
			"========================================================\n";
		
		this.out.println(options);
	}
}
