package de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl.googleNewsCrawler.pageParserThreads;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl.googleNewsCrawler.fileUtils.WikiOutputFile;
import de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl.googleNewsCrawler.pageParser.BigTextExtractor;


public class BigPageParserThread extends Thread {

	private NLPediaLogger logger = new NLPediaLogger(
			BigPageParserThread.class);
	
	private CrawlerDirector controller;
	private WikiOutputFile output;
	private BigTextExtractor bigTextExtractor;
	
	public BigPageParserThread(CrawlerDirector controller, WikiOutputFile output, String language){
		this.controller=controller;
		this.output=output;
		this.bigTextExtractor= new BigTextExtractor(language);
	}
	
	@Override
	public void run(){
		String uri;
		while((uri=controller.getNextUri())!=null&&interrupted()==false){
			if(uri.compareTo("wait")!=0){
				try {
					output.appendTextBlock(uri, this.bigTextExtractor.extractBigText(uri));
				} catch (IOException e) {
					System.out.println("exception on "+uri);
					e.printStackTrace();
				}catch(Exception e){
					e.printStackTrace();
					System.out.println("fatal error \n"+Arrays.toString(e.getStackTrace()));
					logger.error("fatal error",e);
				}
			}
			else
			{
				System.out.println("wait");
				try {
					TimeUnit.SECONDS.sleep(5);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}
}
