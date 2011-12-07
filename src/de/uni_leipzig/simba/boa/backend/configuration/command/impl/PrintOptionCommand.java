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
			"||     2. Start crawling                              ||\n" + 
			"||     3. Create Lucene index                         ||\n" + 
			"||     4. Measure patterns                            ||\n" + 
			"||     5. Score patterns                              ||\n" +
			"||     6. Search for patterns                         ||\n" +
			"||     7. Get relations from dbpedia                  ||\n" +
			"||     8. Write urls for crawling to file             ||\n" +
			"||     9. Print Statistics                            ||\n" +
			"||    10. Start Scripts                               ||\n" + 
			"||    11. Query text with patterns                    ||\n" + 
			"||    12. Query keyphrase                             ||\n" +
			"||    13. Search - Evaluation - Generate              ||\n" +
			"||    14. Evaluation                                  ||\n" +
			"========================================================\n";
		
		this.out.println(options);
	}
}
