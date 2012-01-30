package de.uni_leipzig.simba.boa.backend.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Language;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

/**
 * Provides settings for nlpedia toolkit.<br>
 * Must defined as bean in nlpedia_setup.xml, loaded by NLPediaSetup.
 */
public class NLPediaSettings {

	// needed for logging memory consumption
	public static final long MEGABYTE = 1024L * 1024L;
	
	public static String BOA_BASE_DIRECTORY;
	public static String BOA_DATA_DIRECTORY;
	public static String BOA_LANGUAGE;
	
	private NLPediaLogger logger = new NLPediaLogger(NLPediaSettings.class);
	private Map<String, String> nlpediaSettings	= null;
	private Map<String, Object> complexSettings = null;
	private static NLPediaSettings INSTANCE		= null;
	
	/**
	 * @return
	 */
	public static NLPediaSettings getInstance() {
		
		if ( NLPediaSettings.INSTANCE == null ) {
			
			NLPediaSettings.INSTANCE = new NLPediaSettings();
		}
		
		return NLPediaSettings.INSTANCE;
	}
	
	/**
	 * 
	 */
	private NLPediaSettings(){
		
		this.complexSettings = new HashMap<String,Object>();
	}
	
	public Language getSystemLanguage() {
		
		return Language.getLanguage(NLPediaSettings.BOA_LANGUAGE);
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void setComplexSetting(String key, Object value) {
		
		this.complexSettings.put(key, value);
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public Object getComplexSetting(String key) {
		
		return this.complexSettings.get(key);
	}
	
	/**
	 * used for spring
	 * 
	 * @return
	 */
	public Map<String, String> getNlpediaSettings() {
		
		return this.nlpediaSettings;
	}
	
	/**
	 * used for spring
	 * 
	 * @param nlpediaSettings
	 */
	public void setNlpediaSettings(Map<String, String> nlpediaSettings) {
		
		this.nlpediaSettings = nlpediaSettings;
	}
	
	/**
	 * @param setting - the setting asked for, have a look at the nlpedia_setup.xml
	 * @return the setting for the key or null if no key exists
	 */
	public String getSetting(String setting) {
		
		return this.nlpediaSettings.get(setting);
	}
	
	/**
	 * @param setting which has an integer value
	 * @return the integer value of the setting
	 */
	public int getIntegerSetting(String setting){
		
		return Integer.valueOf(this.getSetting(setting));
	}
	
	/**
	 * @param setting which has an double value 
	 * @return the double value of the setting
	 */
    public double getDoubleSetting(String setting) {

        return Double.valueOf(this.getSetting(setting));
    }

	/**
	 * @param key the key of the setting
	 * @param value the value of the setting
	 */
	public void setSetting(String key, String value) {

		this.nlpediaSettings.put(key, value);
	}

	
	/**
	 * @return the BOA_BASE_DIRECTORY
	 */
	public String getBoaBaseDirectory() {
	
		return BOA_BASE_DIRECTORY;
	}

	
	/**
	 * @return the boaDataDirectory
	 */
	public String getBoaDataDirectory() {
	
		return BOA_DATA_DIRECTORY;
	}

	
	/**
	 * @param BOA_BASE_DIRECTORY the BOA_BASE_DIRECTORY to set
	 */
	public void setBoaBaseDirectory(String baseDirectory) {
	
		BOA_BASE_DIRECTORY = baseDirectory;
	}

	
	/**
	 * @param boaDataDirectory the boaDataDirectory to set
	 */
	public void setBoaDataDirectory(String boaDataDirectory) {
	
		BOA_DATA_DIRECTORY = boaDataDirectory;
	}

	
	/**
	 * @return the language
	 */
	public String getBoaLanguage() {
	
		return BOA_LANGUAGE;
	}

	
	/**
	 * @param language the language to set
	 */
	public void setBoaLanguage(String boaLanguage) {
	
		BOA_LANGUAGE = boaLanguage;
	}

	/**
	 * logs this settings to the log file, similar to toString
	 */
	public void logSettings() {

		this.logger.info("NLPediaSettings are as follows: ");
		this.logger.info("\tBOA-Base-Directory: " + BOA_BASE_DIRECTORY);
		this.logger.info("\tBOA-Data-Directory: " + BOA_DATA_DIRECTORY);
		
		for (Entry<String,String> entry : this.nlpediaSettings.entrySet()) {
			
			this.logger.info("\t" + entry.getKey() + ": " + entry.getValue());
		}
	}
	
	public void printMemoryUsage() {
		
		long memory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		System.out.println("Used memory is megabytes: " + (memory / NLPediaSettings.MEGABYTE));
		this.logger.info("Used memory is megabytes: " + (memory / NLPediaSettings.MEGABYTE));
	}
}
