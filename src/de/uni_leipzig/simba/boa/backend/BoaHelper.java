package de.uni_leipzig.simba.boa.backend;

import java.io.File;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.pipeline.configuration.PipelineConfiguration;
import de.uni_leipzig.simba.boa.backend.util.BeanUtility;

/**
 * 
 * @author gerb
 */
public class BoaHelper {

	/**
	 * 
	 * @return
	 */
	public static PipelineConfiguration loadConfiguration() {

		return (PipelineConfiguration) BeanUtility.getBean(new File(NLPediaSetup.NLPEDIA_SETUP_FILE), PipelineConfiguration.class.getSimpleName());
	}
}