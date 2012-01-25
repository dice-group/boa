package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.lucene.LowerCaseWhitespaceAnalyzer;

/**
 * 
 * @author Daniel Gerber
 */
public class StartQueryCommand implements Command {

	NLPediaLogger logger = new NLPediaLogger(StartQueryCommand.class);
	
	@Override
	public void execute() {

		try {
			
			System.out.print("Please enter a keyphrase:\t");
			
			Scanner scanner = new Scanner(new BufferedInputStream(System.in), "UTF-8");
			String keyphrase = scanner.nextLine();
			String indexPath = "/Users/gerb/Development/workspaces/experimental/en_wiki_exp/index";
			
			List<String> sentences = new ArrayList<String>(this.getExactMatchSentences(indexPath, keyphrase, 1000));
			for (String sentence : sentences) {
				
				System.out.println(sentence);
			}
			System.out.println("Size of result list for keyphrase querying:\t" + sentences.size());
			this.logger.debug("Size of result list for keyphrase querying:\t" + sentences.size());
		}
		catch (IOException ioe) {
			
			ioe.printStackTrace();
			this.logger.error("Could not read input from System.in", ioe);
		}
		catch (ParseException pe) {

			pe.printStackTrace();
			this.logger.error("Could not read index in directory " + NLPediaSettings.getInstance().getSetting("sentenceIndexDirectory"), pe);
		}
	}
	
	public Set<String> getExactMatchSentences(String indexPath, String keyphrase, int maxNumberOfDocuments) throws ParseException, IOException {

		Directory directory = FSDirectory.open(new File(indexPath));
		Analyzer analyzer = new LowerCaseWhitespaceAnalyzer();

		// create index searcher in read only mode
		IndexSearcher indexSearcher = new IndexSearcher(directory, true);
		QueryParser parser = new QueryParser(Version.LUCENE_34, "sentence", analyzer);
		
		ScoreDoc[] hits = indexSearcher.search(parser.parse("+sentence:\"" + QueryParser.escape(keyphrase) + "\""), null, maxNumberOfDocuments).scoreDocs;
		TreeSet<String> list = new TreeSet<String>();

		// reverse order because longer sentences come last, longer sentences
		// most likely contain less it,he,she
		for (int i = hits.length - 1; i >= 0; i--) {

			// get the indexed string and put it in the result
			list.add(indexSearcher.doc(hits[i].doc).get("sentence"));
		}
		return list;
	}
}
