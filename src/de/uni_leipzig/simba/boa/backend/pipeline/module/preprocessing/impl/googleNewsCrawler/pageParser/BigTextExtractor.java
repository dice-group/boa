package de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl.googleNewsCrawler.pageParser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class BigTextExtractor {

	private WebClient webClient;
	
	private String language;
	
	/**
	 * 
	 * @param language en, fr, etc.
	 */
	public BigTextExtractor(String language){
		
		this.language=language;
		
		BrowserVersion browser = BrowserVersion.FIREFOX_3_6;
		browser.setBrowserLanguage(language);
		webClient = new WebClient(browser);
		webClient.setJavaScriptEnabled(false);
		webClient.setThrowExceptionOnFailingStatusCode(true);
		webClient.setThrowExceptionOnScriptError(true);
		webClient.setCssEnabled(false);
	}
	
	public String extractBigText(String url){
		
		String result =null;
		
		HtmlPage page = null;
		try {
			page = webClient.getPage(url);

		} catch (FailingHttpStatusCodeException e) {
			System.out.println(e);
			return null;
		} catch (MalformedURLException e) {
			System.out.println(e);
			return null;
		} catch (IOException e) {
			System.out.println(e);
			return null;
		}
		
		String results[] = this.getStringContentFromXpath(page,
				"//p");
		
		StringBuffer sb= new StringBuffer();
		for(String line:results){
			sb.append(line).append("\n");
		}
		result=sb.toString();
		
		return result;
	}
	
	protected String[] getStringContentFromXpath(HtmlPage page, String xpath) {

		LinkedList<String> results= new LinkedList<String>();
		
		@SuppressWarnings("unchecked")
		List<DomNode> list = (List<DomNode>) page.getByXPath(xpath);
//		for(DomNode n:list){
//			results.add(n.getTextContent());
//		}
		
		HashMap<DomNode, Integer> nodeMap= new HashMap<DomNode, Integer>(); 
		
		for(DomNode n:list){
			DomNode parent = n.getParentNode();
			
			if(!nodeMap.containsKey(parent))
				nodeMap.put(parent,0);
			
			nodeMap.put(parent,nodeMap.get(parent)+1);
		}
		
		// hier test für minimale anzahl an sätze
		for(DomNode n:list){
			if(nodeMap.get(n.getParentNode())>0)
//				results.add(n.getTextContent());
				results.add(n.asText());
		}
		
		return results.toArray(new String[0]);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BigTextExtractor bte= new BigTextExtractor("en");
		System.out.println(bte.extractBigText("http://latimesblogs.latimes.com/music_blog/2012/05/music-apps-the-new-face-of-music-retail-narm-2012.html"));
//		System.out.println(bte.extractBigText("http://www.philly.com/philly/entertainment/music/20120503_New_old_music_from_a__lsquo_vocal_string_quartet_rsquo_.html"));

	}

}
