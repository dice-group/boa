package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import de.uni_leipzig.simba.boa.backend.configuration.command.Command;


public class SearchPatternQueryDatabaseIteration implements Command {

	@Override
	public void execute() {

		for ( Integer i = 0; i < 2 ; i++ ) {
			
			PatternSearchCommand patternSearchCommand = new PatternSearchCommand();
			patternSearchCommand.setFoundInIteration(i);
			patternSearchCommand.execute();
			
			CreateKnowledgeCommand createKnowledgecommand = new CreateKnowledgeCommand();
			createKnowledgecommand.execute();	
		}
	}
}
