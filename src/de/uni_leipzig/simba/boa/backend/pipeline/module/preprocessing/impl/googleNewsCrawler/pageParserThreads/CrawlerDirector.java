package de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl.googleNewsCrawler.pageParserThreads;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl.googleNewsCrawler.fileUtils.FileWithSubjectsReader;
import de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl.googleNewsCrawler.fileUtils.URLStore;
import de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl.googleNewsCrawler.fileUtils.WikiOutputFile;

public class CrawlerDirector {

	private NLPediaLogger logger = new NLPediaLogger(
			CrawlerDirector.class);
	
	private WikiOutputFile output;

	private HashSet<String> uris;
	private Queue<String> uriStack = new LinkedList<String>();
	private URLStore urlStore=null;

	private final int nrOfCrawler = 6;
	private String language;

	public CrawlerDirector(String language, String outPutFile,
			String urlStoreFile, boolean appendToExistingData){
		this.language = language;
		try {
			output = new WikiOutputFile(outPutFile, !appendToExistingData);
		} catch (IOException e) {
			logger.error("cann't create the output file: "+outPutFile,e);
		}

		uris = new LinkedHashSet<String>();

		if (urlStoreFile != null)
			if (appendToExistingData) {
				urlStore = URLStore.createOpenExistingURLStore(urlStoreFile,
						uris);
			} else {
				urlStore = URLStore.createNewURLStore(urlStoreFile);
			}

	}

	public void startCrawler(String theme, String[] subjects) {
		// initialize all

		// create news crawler
		GoogleNewsThread inputThread = new GoogleNewsThread(this, theme, subjects,
				language);
		inputThread.start();

		// wait that the stack fills a bit
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
		}

		System.out.println("crawler starts");

		Thread[] executiveThreads = new Thread[nrOfCrawler];

		// create big text parser
		for (int i = 0; i < nrOfCrawler; i++) {
			executiveThreads[i] = new BigPageParserThread(this, output,
					language);
			executiveThreads[i].start();
		}

		// wait that googleNewsThread is finish and then that bigPageParser is
		// finish
		boolean finish = false;
		while (finish == false) {

			if (inputThread.isAlive() == false) {
				if (uriStack.isEmpty())
					finish = true;
			}

			try {
				TimeUnit.SECONDS.sleep(10);
			} catch (InterruptedException e) {
			}
		}
		System.out.println("finish, stop crawler");
		// stop crawler
		for (int i = 0; i < nrOfCrawler; i++) {
			executiveThreads[i].interrupt();
		}

		System.out.println("problems with google: "+inputThread.problemPages);
		
	}

	public void addUris(String[] newUris) {
		ArrayList<String> addableUris = new ArrayList<String>(newUris.length);

		// find new uris
		for (String u : newUris) {
			if (!this.uris.contains(u))
				addableUris.add(u);
		}

		// add new uris
		uriStack.addAll(addableUris);
		uris.addAll(addableUris);
	}

	public synchronized String getNextUri() {

		String uri;
		try {
			uri = this.uriStack.poll();
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("wait");
			return "wait";
		}
		if (uri != null) {
			System.out.println(uri);

			if (urlStore != null)
				try {
					urlStore.addUrl(uri);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			return uri;
		} else {
			System.out.println("wait");
			return "wait";
		}
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		String[] subjects = FileWithSubjectsReader
				.readSubjectsFromFile("artists.txt");

		CrawlerDirector cd = new CrawlerDirector("en", "output.txt",
				"urlstore.txt", false);
		cd.startCrawler("Music", subjects);
		// cd.startCrawler("Music", new String[] { "Black Eyed Peace",
		// "Madonna",
		// "Nicki Minaj" });

	}

}
