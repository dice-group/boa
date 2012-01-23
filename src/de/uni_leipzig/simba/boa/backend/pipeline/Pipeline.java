package de.uni_leipzig.simba.boa.backend.pipeline;

import java.util.Set;

import de.uni_leipzig.simba.boa.backend.BoaHelper;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.pipeline.configuration.PipelineConfiguration;
import de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule;

/**
 * 
 * @author gerb
 */
public class Pipeline {

	private PipelineConfiguration pipelineConfiguration;
	private final NLPediaLogger logger = new NLPediaLogger(Pipeline.class); 

	/**
	 * Standard constructor reads the pipelineConfiguration from the 
	 * nlpedia_setup.xml file.
	 */
	public Pipeline() {
		
		this.pipelineConfiguration = BoaHelper.loadConfiguration();
	}
	
	public void run() {
	
		// check for all modules if dependencies are available and if settings are correct
		for ( PipelineModule module : this.pipelineConfiguration.getPipelineModules() ) {
			
			module.setModuleInterchangeObject(this.pipelineConfiguration.getModuleInterchangeObject());
			
			// check for missing dependency
			Class<PipelineModule> missingModule = this.checkDependencies(module);
			if ( missingModule != null ) {

				String errorMessage = "Dependency for module: \"" + module.getName() + "\" is not fullfilled! " +
						"Missing dependency : " + missingModule;
				
				this.logger.fatal(errorMessage);
				throw new RuntimeException(errorMessage);
			}
			
			// check for missing setting
			String missingSetting = this.checkSettings(module);
			if ( missingSetting != null && !missingSetting.isEmpty() ) {
				 
				String errorMessage = "Setting for module: \"" + module.getName() + "\" is not fullfilled! " +
						"Missing setting : " + missingSetting;
				
				this.logger.fatal(errorMessage);
				throw new RuntimeException(errorMessage);
			}
		}
		
		String successMessage = "All modules are configured correctly.";
		this.logger.info(successMessage);
		System.out.println(successMessage);
		
		// configuration is correct so we can run each module
		for ( PipelineModule module : this.pipelineConfiguration.getPipelineModules() ) {
			
			module.run();
			module.updateModuleInterchangeObject();
		}
	}
	
	/**
	 * 
	 * @param module
	 * @return
	 */
	public Class<PipelineModule> checkDependencies(PipelineModule module) {

		for ( Class<PipelineModule> dependency : module.getModuleDependencies() ) {
			
			boolean isAvailable = false;
			for ( PipelineModule pipelineModule : this.pipelineConfiguration.getPipelineModules() ) {
				
				if ( dependency.equals(pipelineModule.getClass()) ) {
					
					isAvailable = true;
					break;
				}
			}
			if ( !isAvailable ) return dependency; 
		}
		return null;
	}

	/**
	 * 
	 * @param module
	 * @return
	 */
	public String checkSettings(PipelineModule module) {

		Set<String> moduleSettings = module.getModuleSettings();
		for (String setting : moduleSettings) {
			
			if ( NLPediaSettings.getInstance().getSetting(setting).isEmpty() || 
					NLPediaSettings.getInstance().getSetting(setting) == null ) {
				
				return setting;
			}
		}
		return null;
	}
}
