package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.io.PrintStream;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;

/**
 * 
 * @author Daniel Gerber
 */
public class ExitCommand implements Command {

	private PrintStream out = null;
	private NLPediaSetup setup = null;
	
	public ExitCommand(PrintStream out, NLPediaSetup setup) {
		
		this.out = out;
		this.setup = setup;
	}
	
	@Override
	public void execute() {
		
		this.setup.destroy();
		this.out.println("Good Bye!");
	}
}
