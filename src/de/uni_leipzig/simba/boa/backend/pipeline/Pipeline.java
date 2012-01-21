package de.uni_leipzig.simba.boa.backend.pipeline;

import de.uni_leipzig.simba.boa.backend.BoaHelper;
import de.uni_leipzig.simba.boa.backend.pipeline.configuration.PipelineConfiguration;
import de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule;

/**
 * 
 * @author gerb
 */
public class Pipeline {

	private PipelineConfiguration pipelineConfiguration;

	/**
	 * Standard constructor reads the pipelineConfiguration from the 
	 * nlpedia_setup.xml file.
	 */
	public Pipeline() {
		
		this.pipelineConfiguration = BoaHelper.loadConfiguration();
	}
	
	public void run() {
	
		for ( PipelineModule module : this.pipelineConfiguration.getPipelineModules() ) {
			
			module.setModuleInterchangeObject(this.pipelineConfiguration.getModuleInterchangeObject());
			module.run();
			module.updateModuleInterchangeObject();
		}
	}
}
