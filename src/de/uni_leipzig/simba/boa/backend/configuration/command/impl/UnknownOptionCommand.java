package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.io.PrintStream;

import de.uni_leipzig.simba.boa.backend.configuration.command.Command;

/**
 * 
 * @author Daniel Gerber
 */
public class UnknownOptionCommand implements Command {

	private PrintStream out = null;
	
	public UnknownOptionCommand(PrintStream out) {
		
		this.out = out;
	}
	
	@Override
	public void execute() {
		
		this.out.println("Unknown option selected!\n");
		Command printOptionCommand = new PrintOptionCommand(this.out);
		printOptionCommand.execute();
	}
}
