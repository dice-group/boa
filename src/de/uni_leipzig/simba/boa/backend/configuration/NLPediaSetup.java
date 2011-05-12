package de.uni_leipzig.simba.boa.backend.configuration;

import java.io.File;
import java.util.Date;

import org.springframework.beans.BeansException;

import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.entity.pattern.evaluation.PatternEvaluatorFactory;
import de.uni_leipzig.simba.boa.backend.logging.LoggingConfigurator;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.util.BeanUtility;
import de.uni_leipzig.simba.boa.frontend.BoaFrontendApplication;

/**
 * NLpediaSetup starts up and shuts down the NLPedia application.
 */
public class NLPediaSetup {

	public static String NLPEDIA_SETUP_FILE;

	private NLPediaLogger logger;
	private NLPediaSettings settings;
	
	/**
	 * Default constructor.
	 * 
	 * @param servletContext - the context of the nlpediaServlet
	 */
	public NLPediaSetup(boolean isTestCase) {
		
		this.logger = new NLPediaLogger(NLPediaSetup.class);
		this.configure(isTestCase);
	}
	
	/**
	 * Starts up and configures the nlpedia system.
	 */
	public void configure(boolean isTestCase) {
		
		// initializing logging system (log4j)
		LoggingConfigurator.getInstance().init();
		
		this.logStartup();
		
		String path = getClass().getProtectionDomain().getCodeSource().getLocation().toString();
		if ( path.contains(".jar") ) {
			
			path = path.substring(5, path.indexOf("nlpedia.jar"));
			NLPEDIA_SETUP_FILE = path + "WebContent/WEB-INF/config/nlpedia_setup.xml";
		}
		else if ( path.contains("WEB-INF") ) {
			
			path = path.substring(5, path.indexOf("WEB-INF"));
			NLPEDIA_SETUP_FILE = path + "WEB-INF/config/nlpedia_setup.xml";
		}
		else {
			
			path = path.substring(5, path.indexOf("build/classes/"));
			NLPEDIA_SETUP_FILE = path + "WebContent/WEB-INF/config/nlpedia_setup.xml";
		}
		
		
		// initializing settings and factories (nlpedia_setup.xml)
		logger.info("Initializing nlpedia...");
		File setupFile = new File(NLPediaSetup.NLPEDIA_SETUP_FILE);
		
		try {
			
			// load application settings
			this.settings = (NLPediaSettings) BeanUtility.getBean(setupFile, NLPediaSettings.class.getSimpleName());
			this.settings.logSettings();
			logger.info("Initialized settings...");
			
			if ( isTestCase ) {
				
				this.settings.setSetting("hibernateConnectionUrl", "jdbc:mysql://127.0.0.1:3306/test");
				this.settings.setSetting("hibernateHbm2ddlAuto", "create");
			}
			
			// load factories
			BeanUtility.getBeansOfType(setupFile, DaoFactory.class);
			logger.info("Initialized DaoFactory...");
			
			BeanUtility.getBeansOfType(setupFile, PatternEvaluatorFactory.class);
			logger.info("Initialized PatternEvaluatorFactory...");
		}
		catch (BeansException be) {
			
			String errorMsg = "Error while initializing setup: " + be.getMessage() + 
					" Please check the setup file: " + setupFile.getPath();
			this.logger.fatal(errorMsg, be);
			throw new RuntimeException(errorMsg, be);
		}
		this.logger.info("nlpedia successfully initialized!");
	}
	
	
	/**
	 * @return the settings
	 */
	public NLPediaSettings getSettings() {
	
		return this.settings;
	}

	
	/**
	 * @param settings the settings to set
	 */
	public void setSettings(NLPediaSettings settings) {
	
		this.settings = settings;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return "NLPediaSetup [settings=" + settings + "]";
	}

	/**
	 * Show "startup" screen in the application log file.
	 */
	private void logStartup() {
		
		logger.info("*********************************************");
		logger.info("*             STARTUP NLPedia               *");
		logger.info("*********************************************");
		logger.info(" StartTimeStamp: " + new Date());
		logger.info("*********************************************");
	}
	
	/**
	 * Show "shutdown" screen in the application log file.
	 */
	private void logShutdown() {
		
		logger.info("*********************************************");
		logger.info("*           SHUTDOWN NLPedia                *");
		logger.info("*********************************************");
		logger.info(" StopTimeStamp: " + new Date());
		logger.info("*********************************************");
	}
	
	/**
	 * Destroy method
	 * <br>
	 * Logs shutdown message and calls LoggingConfigurator.destroy.
	 */
	public void destroy() {
		
		this.logShutdown();
		LoggingConfigurator.getInstance().destroy();
	}
}
