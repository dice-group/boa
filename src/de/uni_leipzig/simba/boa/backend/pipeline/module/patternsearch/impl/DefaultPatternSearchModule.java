/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.patternsearch.impl;

import de.uni_leipzig.simba.boa.backend.concurrent.ThreadManager;
import de.uni_leipzig.simba.boa.backend.pipeline.module.patternsearch.AbstractPatternSearchModule;
import de.uni_leipzig.simba.boa.backend.search.PatternSearcher;


/**
 * @author gerb
 *
 */
public class DefaultPatternSearchModule extends AbstractPatternSearchModule {

	/* (non-Javadoc)
	 * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#getName()
	 */
	@Override
	public String getName() {

		return "Default Pattern Search Module (SPO languages - de/en)";
	}

	/* (non-Javadoc)
	 * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#run()
	 */
	@Override
	public void run() {
		
		ThreadManager threadManager = new ThreadManager();
		threadManager.startPatternSearchCallables(this.moduleInterchangeObject.getBackgroundKnowledge(), 4);
	}
	
	/* (non-Javadoc)
	 * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#isDataAlreadyAvailable()
	 */
	@Override
	public boolean isDataAlreadyAvailable() {

		// TODO Auto-generated method stub
		return false;
	}


	/* (non-Javadoc)
	 * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#getReport()
	 */
	@Override
	public String getReport() {

		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#updateModuleInterchangeObject()
	 */
	@Override
	public void updateModuleInterchangeObject() {

		// TODO Auto-generated method stub

	}

	@Override
	public void loadAlreadyAvailableData() {

		// TODO Auto-generated method stub
		
	}
}
