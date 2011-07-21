package de.uni_leipzig.simba.boa.backend.crawl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.nlp.SentenceDetection;
import de.uni_leipzig.simba.boa.backend.util.FileWriterUtil;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 * 
 * @author Daniel Gerber <mailto:dgerber@informatik.uni-leipzig.de>
 * 
 */
public class Crawler extends WebCrawler {

	private CrawlerData crawlerData				= null;
	private NLPediaLogger logger				= null;
	private SentenceDetection sentenceDetection = null;
	
	private Pattern filters = Pattern.compile(NLPediaSettings.getInstance().getSetting("filterFilesRegex"));

	/**
	 * initialize local crawler data and loggers
	 */
	public Crawler() {

		this.crawlerData		= new CrawlerData();
		this.logger				= new NLPediaLogger(Crawler.class);
		this.sentenceDetection	= new SentenceDetection();
	}

	/**
	 * determines if a given URL needs to be visited. To enforce that the crawler
	 * crawls only in a set of given domains it checks if the current URL starts 
	 * with a domain configured in the seedURLS setting.
	 * 
	 * @param url the url to be checked for possible crawling
	 */
	public boolean shouldVisit(WebURL url) {

		String href = url.getURL().toLowerCase();

		if (filters.matcher(href).matches()) {
			
			this.logger.debug("URL <"+ href + "> is not in the set of pages to visit, not supported file extension.");
			return false;
		}
		for ( URL seedUrl : (List<URL>) NLPediaSettings.getInstance().getComplexSetting("seedUrlPart")) {
			
			if ( href.startsWith(seedUrl.toString()) ) {
				
				this.logger.debug("Link: <" + href + "> matches with domain: <" + NLPediaSettings.getInstance().getSetting("seedURL") + ">");
				return true;
			}
		}
		return false;
	}

	/**
     * get the text of the currently visited page and parse it. the sentences get
     * checked for validity and only valid ones are stored in the local data list 
     * like. sentence ||| pos_tagged_sentence
     * 
     * @param page - the page to be processed
     */
	public void visit(Page page) {

		this.logger.debug("Getting text of url: " + page.getWebURL().getURL() + ".");
		
		URL url = null;
		try {
			
			url = new URL(page.getWebURL().getURL());
		}
		catch (MalformedURLException e) {
			
			this.logger.debug("URL " + url.toString() + "could not be parsed.");
			e.printStackTrace();
		}
		
		List<String> sentences = this.sentenceDetection.getSentences(page.getText(), NLPediaSettings.getInstance().getSetting("sentenceBoundaryDisambiguation"));
		
		if ( sentences != null ) FileWriterUtil.writeSentences(sentences, url.getHost());
	}

	/**
	 * 
	 */
	public Object getMyLocalData() {
		
		return this.crawlerData;
	}
	
	/**
	 * nothing to do here at the moment
	 */
	public void onStart() {

	}
	
	/**
	 * nothing to do here at the moment
	 */
	public void onBeforeExit() {

	}
}
