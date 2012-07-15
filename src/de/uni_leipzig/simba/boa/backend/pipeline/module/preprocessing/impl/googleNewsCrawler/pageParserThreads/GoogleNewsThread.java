package de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl.googleNewsCrawler.pageParserThreads;

import java.util.concurrent.TimeUnit;

import de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl.googleNewsCrawler.pageParser.GoogleNewsParser;

public class GoogleNewsThread extends Thread {

	protected static final int GOOGLENEWSTOLERANCETIME = 3500;// 3500;
	private GoogleNewsParser gnParser;
	private String theme;
	private String[] subjects;
	private CrawlerDirector crawlerDirector;

	public int numberOfGoogleAnswerPages = 2;
	public int problemPages = 0;

	public GoogleNewsThread(CrawlerDirector crawlerDirector, String theme,
			String[] subjects, String language) {
		this.gnParser = new GoogleNewsParser(language);

		this.theme = theme;
		this.subjects = subjects;
		this.crawlerDirector = crawlerDirector;		
	}

	@Override
	public void run() {
		for (String subject : subjects) {
			System.out.println(subject);

			String[] urls = null;

			boolean allright = false;
			int numberOfTries = 0;

			// retry if google news block because I ask to often
			while (allright == false) {
				urls = gnParser.getGoogleNewsUrls(theme, subject, null,
						numberOfGoogleAnswerPages);

				// google news is blocking me
				if (gnParser.statusCode == 503) {
					System.out
							.println("google news is blocking the crawler, I wait for "
									+ ((numberOfTries + 2) * GOOGLENEWSTOLERANCETIME)
									+ " ms");
					try {
						TimeUnit.MILLISECONDS.sleep(((numberOfTries + 2) * GOOGLENEWSTOLERANCETIME));
					} catch (InterruptedException e) {
						this.interrupt();
					}
				}
				else{
					allright=true;
				}
				numberOfTries++;
			}
			crawlerDirector.addUris(urls);

			if (urls.length < 20)
				problemPages++;
			if (urls.length < 11)
				problemPages++;

			try {
				TimeUnit.MILLISECONDS.sleep(GOOGLENEWSTOLERANCETIME);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
