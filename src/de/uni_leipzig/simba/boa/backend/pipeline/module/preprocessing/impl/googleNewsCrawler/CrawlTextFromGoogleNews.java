package de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl.googleNewsCrawler;

import java.io.File;
import java.util.List;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.AbstractPreprocessingModule;
import de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl.googleNewsCrawler.pageParserThreads.CrawlerDirector;

/**
 * This module produces documents in wiki pedia structure to a specific theme.<br>
 * The documents are crawled from google news. As input a specific theme and a lot of example subjects are needed.
 * These subjects come from a sparql endpoint. Therefore only the subject classes are needed. The words will then extracted from the
 * ontology and then used as input for the queries on google news. 
 * 
 * @author Tony Mey
 */
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
		return new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.RAW_DATA_PATH+File.separator+"googeNews"+theme+".txt").exists();		
	}

	@Override
	public void loadAlreadyAvailableData() {
		// nothing to do
	}

	public String getLanguage() {
		return language;
	}

	/**
	 * the language must be a short name that google knows and can use in: <br>
	 * http://www.google.com/search?hl=" + language + "&tbm=nws"
	 * @param language something like en or de
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	public String getEndpoint() {
		return endpoint;
	}

	/**
	 * an possible sparql endpoint where the crawler can receive input in form of subjects/objects <br>
	 * for example: http://dbtune.org/jamendo/sparql/
	 * @param endpoint
	 */
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
	public List<String> getClasses() {
		return classes;
	}

	/**
	 * set the classes that should be accepted at the sparql endpoint. There instances will be used for crawling. <br>
	 * for example: http://purl.org/ontology/mo/MusicArtist
	 * @param classes
	 */
	public void setClasses(List<String> classes) {
		this.classes = classes;
	}

	public String getRelation() {
		return relation;
	}

	/**
	 * relation with the name of the subject like "label" or in music ontology http://xmlns.com/foaf/0.1/name
	 * @param relation
	 */
	public void setRelation(String relation) {
		this.relation = relation;
	}

	public int getLimitPerQuery() {
		return limitPerQuery;
	}

	/**
	 * number of answers for each sparql query
	 * @param limitPerQuery
	 */
	public void setLimitPerQuery(int limitPerQuery) {
		this.limitPerQuery = limitPerQuery;
	}

	public String getTheme() {
		return theme;
	}

	/**
	 * theme of the crawling like "music" will be used for all search queries on google news
	 * @param theme
	 */
	public void setTheme(String theme) {
		this.theme = theme;
	}
}
