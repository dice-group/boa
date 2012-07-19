package de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl.googleNewsCrawler.pageParser;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.List;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class MTVNewsParser {

	protected static final int GOOGLEPOLITENESSTIMEBETWEENPAGE = 1000;

	private WebClient webClient;

	private String language;

	public int statusCode = 0;

	public MTVNewsParser(String language) {

		this.language = language;

		BrowserVersion browser = BrowserVersion.FIREFOX_3_6;
		browser.setBrowserLanguage(language);
		webClient = new WebClient(browser);
		webClient.setJavaScriptEnabled(false);
		webClient.setThrowExceptionOnFailingStatusCode(true);
		webClient.setThrowExceptionOnScriptError(true);
		webClient.setCssEnabled(false);// 139.18.252.25
	}

	/**
	 * @see getGoogleNewsUrls(String, String, String)
	 * @param thema
	 * @param subject
	 * @return
	 */
	public String[] getNewsUrls(String thema, String subject) {
		this.statusCode = 0;
		String url = null;

		if (subject != null && !subject.isEmpty())
			url = this.createUrl(subject);
		else
			url = this.createUrl(thema);

		HtmlPage page = null;
		try {
			page = webClient.getPage(url);
			// System.out.println(page.asText());
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
		
		String result[] = this.getStringContentFromXpath(page, 
				"//div" +
				"/div[@class=\"mtvn-t mtvn-t2\"]/a/@href");//"//h3/a/@href");

//		result = this.extractUrls(result);

		System.out.println(result.length);
		return result;
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

	public String[] extractUrls(String[] googleUrls) {
		LinkedList<String> resuls = new LinkedList<String>();
		for (String url : googleUrls) {
			String result = extractUrl(url);
			if (result != null)
				resuls.add(result);
		}
		return resuls.toArray(new String[0]);
	}

	public String createUrl(String searchWord) {// &gl=us

		// http://www.mtv.com/search/article/?q=black%20and%20blue
		String url = "http://www.mtv.com/search/article/";
		// for (String object : searchWords) {
		// url += "&q=" + object;
		// }

		url += "?q=";

		String[] words = searchWord.split(" ");

		boolean first = true;
		for (String w : words) {
			if (first) {
				url += w;
				first = false;
			} else
				url += "%20" + w;
		}

		// boolean first=true;
		// for (String object : searchWords) {
		// if(first)
		// {
		// url += object;
		// first=false;
		// }
		// else
		// url += "%20" + object;
		// }
		//
		// url+=options;

		System.out.println(url);

		return url;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MTVNewsParser gnp = new MTVNewsParser("en");

		String[] result = gnp.getNewsUrls("Music", "Madonna");

		for (String res : result)
			System.out.println(res);
	}

}
