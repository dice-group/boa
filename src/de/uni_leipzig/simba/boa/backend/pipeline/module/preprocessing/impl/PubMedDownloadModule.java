package de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gerbsen.www.FileDownloader;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.AbstractPreprocessingModule;
import de.uni_leipzig.simba.boa.backend.util.TimeUtil;

public class PubMedDownloadModule extends AbstractPreprocessingModule{

	private final static String DOWNLOAD_DIRECTORY=NLPediaSettings.BOA_DATA_DIRECTORY+"pubmed/download/";
	private final static String BASE_URL="ftp://ftp.ncbi.nlm.nih.gov/pub/pmc/";
	private final static String[] FILENAMES = {"articles.A-B.tar.gz","articles.C-H.tar.gz","articles.I-N.tar.gz","articles.O-Z.tar.gz"};
	private static final Logger logger=LoggerFactory.getLogger(PubMedDownloadModule.class);
	private long totalTime;
	
	@Override
	public String getName() {
		return "PubMed download module";
	}

	@Override
	public void run() {
		if(!new File(DOWNLOAD_DIRECTORY).exists()){
			new File(DOWNLOAD_DIRECTORY).mkdirs();
		}
		logger.info("Download pubmed started");
		long begin = System.nanoTime();
		for(String filename:FILENAMES){
			FileDownloader.downloadFile(BASE_URL+filename, DOWNLOAD_DIRECTORY+filename);
		}
		totalTime = System.nanoTime()-begin;
		logger.info("Download pubmed finished");
		
	}

	@Override
	public String getReport() {
		return "Finished downloading of PubMed in "+TimeUtil.convertMilliSeconds(totalTime);
	}

	@Override
	public void updateModuleInterchangeObject() {
		//nothing to do here
	}

	@Override
	public boolean isDataAlreadyAvailable() {
		for(String filename:FILENAMES){
			if (new File(DOWNLOAD_DIRECTORY+filename).exists()){
				return true;
			} //no else
		}
		return false;
	}

	@Override
	public void loadAlreadyAvailableData() {
		//nothing to do here
	}

	public static void main(String...args){
		PubMedDownloadModule d = new PubMedDownloadModule();
		d.run();
	}
	
}
