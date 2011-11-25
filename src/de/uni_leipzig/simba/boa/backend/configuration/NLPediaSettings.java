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
		
		return Language.getLanguage(this.getSetting("language"));
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
	 * @param key the key of the setting
	 * @param value the value of the setting
	 */
	public void setSetting(String key, String value) {

		this.nlpediaSettings.put(key, value);
	}
	
	/**
	 * logs this settings to the log file, similar to toString
	 */
	public void logSettings() {

		this.logger.info("NLPediaSettings are as follows: ");
		for (Entry<String,String> entry : this.nlpediaSettings.entrySet()) {
			
			this.logger.info("\t" + entry.getKey() + ": " + entry.getValue());
		}
	}
}
