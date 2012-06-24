package de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl.googleNewsCrawler.pageParser;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class GoogleNewsParser {

	private WebClient webClient;

	private String language;

	public int statusCode=0;
	
	/**
	 * 
	 * @param language
	 *            en, fr etc.
	 */
	public GoogleNewsParser(String language) {

		this.language = language;

		BrowserVersion browser = BrowserVersion.FIREFOX_3_6;
		browser.setBrowserLanguage(language);
		webClient = new WebClient(browser);
		webClient.setJavaScriptEnabled(false);		
		webClient.setThrowExceptionOnFailingStatusCode(true);
		webClient.setThrowExceptionOnScriptError(true);
		webClient.setCssEnabled(false);//139.18.252.25

	}

	public String createUrl(String[] searchWords, String options) {// &gl=us
		
		// add google options like &start=20
		if (options == null)
			options = "";
		
		String url = "http://www.google.com/search?hl=" + language + "&tbm=nws";
//		for (String object : searchWords) {
//			url += "&q=" + object;
//		}
		
		url += "&q=";
		boolean first=true;
		for (String object : searchWords) {
			if(first)
			{
				url += object;
				first=false;
			}
			else
				url += "+" + object;
		}
		
		url+=options;
		
		System.out.println(url);
		
		return url;
	}

	public String[] getGoogleNewsUrls(String thema, String subject,
			String options, int nrOfPages) {
		if (nrOfPages < 1)
			nrOfPages = 1;

		if(options==null)
			options="";
		
		ArrayList<String> googleUrls = new ArrayList<String>();

		for (int i = 0; i < nrOfPages; i++) {
			String[] urlPuffer = this.getGoogleNewsUrls(thema, subject, options+ "&start="+(i*10));
			Collections.addAll(googleUrls, urlPuffer);
		}

		return googleUrls.toArray(new String[0]);
	}

	/**
	 * @see getGoogleNewsUrls(String, String, String)
	 * @param thema
	 * @param subject
	 * @return
	 */
	public String[] getGoogleNewsUrls(String thema, String subject) {
		return getGoogleNewsUrls(thema, subject, null);
	}

	/**
	 * 
	 * @param thema
	 *            first query term
	 * @param subject
	 *            second query term
	 * @param options
	 *            options for the google search like &start=20. These will be
	 *            added to the url
	 * @return an empty array or the result urls from google news (never null)
	 */
	public String[] getGoogleNewsUrls(String thema, String subject,
			String options) {
		
		this.statusCode=0;
		String url = null;

		if (subject != null && !subject.isEmpty())
			url = this.createUrl(new String[] { thema, subject }, options);
		else
			url = this.createUrl(new String[] { thema }, options);


				
		HtmlPage page = null;
		try {
			page = webClient.getPage(url);
//			System.out.println(page.asText());
		} catch (FailingHttpStatusCodeException e) {
			System.out.println(e);
			statusCode = e.getStatusCode();
			return new String[0];
		} catch (MalformedURLException e) {
			System.out.println(e);			
			return new String[0];
		} catch (IOException e) {
			System.out.println(e);
			return new String[0];
		}

		String result[] = this.getStringContentFromXpath(page, "//h3/a/@href");

		result = this.extractUrls(result);

		System.out.println(result.length);
		return result;
	}

	public String[] extractUrls(String[] googleUrls) {
		LinkedList<String> resuls = new LinkedList<String>();
		for (String url : googleUrls) {
			String result = extractUrl(url);
			if (result != null)
				resuls.add(result);
		}
		return resuls.toArray(new String[0]);
	}

	public String extractUrl(String googleUrl) {
		// String result = googleUrl.replace("%3A", ":").replace("%2F",
		// "/").replace("%3F", "?")
		// .replace("26", newChar);
		String result = googleUrl;

		String[] parts = result.split("&");
		result = null;
		// System.out.println(Arrays.toString(parts));
		for (String part : parts) {
			if (part.startsWith("/url?q=")) {
				result = part;
				result = result.replaceFirst("/url\\?q=", "");
				try {
					result = URLDecoder.decode(result, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}

		if (result == null)
			return null;

		return result;
	}

	protected String[] getStringContentFromXpath(HtmlPage page, String xpath) {

		LinkedList<String> results = new LinkedList<String>();

		@SuppressWarnings("unchecked")
		List<DomNode> list = (List<DomNode>) page.getByXPath(xpath);
		for (DomNode n : list) {
			results.add(n.getTextContent());
		}

		return results.toArray(new String[0]);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GoogleNewsParser gnp = new GoogleNewsParser("us");

		String[] result = gnp.getGoogleNewsUrls("Music", "",null, 2);

		for (String res : result)
			System.out.println(res);

	}

}
