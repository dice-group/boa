package de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.AbstractPreprocessingModule;
import de.uni_leipzig.simba.boa.backend.util.TimeUtil;

public class PubMedExtractorModule extends AbstractPreprocessingModule {

	private final static String DOWNLOAD_DIRECTORY = NLPediaSettings.BOA_DATA_DIRECTORY
			+ "pubmed/download/";
	private final static String EXTRACT_DIRECTORY = NLPediaSettings.BOA_DATA_DIRECTORY
			+ "pubmed/raw/";
	private final static Logger logger = LoggerFactory.getLogger(PubMedExtractorModule.class);
	private long totalTime=0;

	@Override
	public String getName() {
		return "PubMed extractor module";
	}

	@Override
	public void run() {
		long begin =System.nanoTime();
		if(! new File(EXTRACT_DIRECTORY).exists()){
			logger.info("Create directory: {}",EXTRACT_DIRECTORY);
			new File(EXTRACT_DIRECTORY).mkdirs();
		}
		for (String file : new File(DOWNLOAD_DIRECTORY).list()) {
			try {
				new ProcessBuilder("tar", "-xvzf", file, "-C",
						EXTRACT_DIRECTORY).start();
			} catch (IOException e) {
				logger .error(e.getMessage(),e);
			}
		}
		totalTime=System.nanoTime()-begin;
	}

	@Override
	public String getReport() {
		return "PubMed extracted in "+TimeUtil.convertMilliSeconds(totalTime);
	}

	@Override
	public void updateModuleInterchangeObject() {
		//nothing to do here
	}

	@Override
	public boolean isDataAlreadyAvailable() {
		if(! new File(EXTRACT_DIRECTORY).exists()){
			return false;
		} else {
			return (new File(EXTRACT_DIRECTORY).listFiles().length>0);
		}
	}

	@Override
	public void loadAlreadyAvailableData() {
		//nothing to do here
	}

}
