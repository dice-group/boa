package de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl.googleNewsCrawler;

import java.io.File;
import java.util.List;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.AbstractPreprocessingModule;
import de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl.googleNewsCrawler.pageParserThreads.CrawlerDirector;

public class CrawlTextFromGoogleNews extends AbstractPreprocessingModule {

	private NLPediaLogger logger = new NLPediaLogger(
			CrawlTextFromGoogleNews.class);

	private String language;
	private String endpoint;
	private List<String> classes;
	private String relation;
	private int limitPerQuery;
	private String theme;

	@Override
	public String getName() {

		return "Crawl Text from pages to a theme and instances found by google news";
	}

	@Override
	public void run() {
		// get instances
		List<String> entityNames = EntityCrawler.getEntityNames(endpoint,
				classes, relation, limitPerQuery);

		// crawl text
		CrawlerDirector director = new CrawlerDirector(language,
				NLPediaSettings.BOA_DATA_DIRECTORY + Constants.RAW_DATA_PATH+File.separator+"googeNews"+theme+".txt",
				null, false);

		director.startCrawler(theme, entityNames.toArray(new String[0]));
	}

	@Override
	public String getReport() {

		return "see logs";
	}

	@Override
	public void updateModuleInterchangeObject() {
		// I don't no what this function should do
	}

	@Override
	public boolean isDataAlreadyAvailable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void loadAlreadyAvailableData() {
		// nothing to do
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public List<String> getClasses() {
		return classes;
	}

	public void setClasses(List<String> classes) {
		this.classes = classes;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public int getLimitPerQuery() {
		return limitPerQuery;
	}

	public void setLimitPerQuery(int limitPerQuery) {
		this.limitPerQuery = limitPerQuery;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
