package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.io.IOException;
import java.util.Date;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.search.FileIndexer;


/**
 * 
 * @author Daniel Gerber
 */
public class CreateIndexCommand implements Command {

	NLPediaLogger logger = new NLPediaLogger(CreateIndexCommand.class);
	
	@Override
	public void execute() {

		String indexDirectory		= NLPediaSettings.getInstance().getSetting("sentenceIndexDirectory");
		boolean	overwriteIndex		= new Boolean(NLPediaSettings.getInstance().getSetting("overwriteIndex")).booleanValue();
		int ramBufferMaxSizeInMb	= new Integer(NLPediaSettings.getInstance().getSetting("ramBufferMaxSizeInMb")).intValue();

		Date startIndexDate = new Date();
		System.out.println("Indexing started ...");
		this.logger.info("Indexing started ...");
		
		try {
			
			new FileIndexer(indexDirectory, overwriteIndex, ramBufferMaxSizeInMb);
		}
		catch (Exception e) {
			
			this.logger.fatal("Indexing went wrong.", e);
			e.printStackTrace();
		}
		
		System.out.println("Indexing took " + (new Date().getTime() - startIndexDate.getTime()) + "ms.");
		this.logger.info("Indexing took " + (new Date().getTime() - startIndexDate.getTime()) + "ms.");
	}
}
