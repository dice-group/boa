package de.uni_leipzig.simba.boa.backend.test.scripts;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

/**
 * <p>
 * This is a tutorial for text analyzer in lucene.
 * Analyzer is an interface in lucene, which defines methods to tokenize text.
 * Please read lucene API at:
 * </p>
 * <ul>
 * <li>http://lucene.apache.org/java/2_3_2/api/org/apache/lucene/analysis/Analyzer.html</li>
 * <li>http://lucene.apache.org/java/2_3_2/api/org/apache/lucene/analysis/package-summary.html</li>
 * </ul>
 * <p>To run the program in a command line mode, use the parameter:</p>
 * <ul>
 * <li>args[0]: the text to be tokenized</li>
 * </ul>
 * Note that if you involve white space in main parameters, you need to include the parameters using "".
 * For example, if you want to use the sentence "This is a tutorial for Lucene 2.3.2's analyzer.",
 * you need to use " at both sides of the sentence when calling the program.
 */
public class TutorialAnalyzer {
	
	public static void main(String[] args){
		try{
			
			String text = "Victoria Bitter is brewed by Carlton & United Beverages , a subsidiary of Foster 's Group , brewers of Fosters Lager .";
			
			RAMDirectory idx = new RAMDirectory();
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
			
			// StandardAnalyzer: http://lucene.apache.org/java/2_3_2/api/org/apache/lucene/analysis/standard/StandardAnalyzer.html
			System.out.println("\nNow we are using StandardAnalyzer ...");
			printTokens(text, analyzer);
			System.out.println("\nNow we are create index for StandardAnalyzer ...");
			createIndex(idx, analyzer);
			queryIndex(idx, analyzer);
			// You will see that StandardAnalyzer can tokenize text, transform text to lowercase, and remove stop words.
			
			System.out.println("\n#################################################################\n");
			
			// SimpleAnalyzer: http://lucene.apache.org/java/2_3_2/api/org/apache/lucene/analysis/SimpleAnalyzer.html
			System.out.println("\nNow we are using SimpleAnalyzer .... ");
			analyzer = new SimpleAnalyzer();
			printTokens(text, analyzer);
			System.out.println("\nNow we are create index for SimpleAnalyzer ...");
			createIndex(idx, analyzer);
			queryIndex(idx, analyzer);
			// You will see that SimpleAnalyzer can tokenize text by non-letter characters and transform text to lowercase.
			
			System.out.println("\n#################################################################\n");
			
			// StopAnalyzer: http://lucene.apache.org/java/2_3_2/api/org/apache/lucene/analysis/StopAnalyzer.html
			System.out.println("\nNow we are using StopAnalyzer .... ");
			analyzer = new StopAnalyzer(Version.LUCENE_30);
			printTokens(text, analyzer);
			System.out.println("\nNow we are create index for StopAnalyzer ...");
			createIndex(idx, analyzer);
			queryIndex(idx, analyzer);
			// You will see that StopAnalyzer can tokenize text by non-letter characters and remove all stop words.
			
			System.out.println("\n#################################################################\n");
			
			// KeywordAnalyzer: http://lucene.apache.org/java/2_3_2/api/org/apache/lucene/analysis/KeywordAnalyzer.html
			System.out.println("\nNow we are using KeywordAnalyzer .... ");
			analyzer = new KeywordAnalyzer();
			printTokens(text, analyzer);
			System.out.println("\nNow we are create index for KeywordAnalyzer ...");
			createIndex(idx, analyzer);
			queryIndex(idx, analyzer);
			// You will see that KeywordAnalyzer will treate all the text as a single token, nothing will be tokenized or processed.
			
			System.out.println("\n#################################################################\n");
			
			// LetterTokenizer: http://lucene.apache.org/java/2_3_2/api/org/apache/lucene/analysis/WhitespaceAnalyzer.html
			System.out.println("\nNow we are using WhitespaceAnalyzer .... ");
			analyzer = new WhitespaceAnalyzer();
			printTokens(text, analyzer);
			System.out.println("\nNow we are create index for WhitespaceAnalyzer ...");
			createIndex(idx, analyzer);
			queryIndex(idx, analyzer);
			// You will see that WhitespaceAnalyzer tokenize the text by white space.
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	private static void queryIndex(RAMDirectory idx, Analyzer analyzer) {

		Searcher searcher;
		try {
			
			String s1 = ", a subsidiary of";
			String s2 = "a subsidiary of";
			searcher = new IndexSearcher(idx);
			System.out.println("Query index for: \"" + s1 + "\"");
	        search(searcher, s1, analyzer);
//	        System.out.println("Query index for: \"" + s2 + "\"");
//	        search(searcher, s2, analyzer);
	        searcher.close();
		}
		catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private static void createIndex(RAMDirectory idx, Analyzer analyzer) {

		try {
			
			String s1 = "Victoria Bitter is brewed by Carlton & United Beverages , a subsidiary of Foster 's Group , brewers of Fosters Lager .";
			String s2 = "Victoria Bitter is brewed by Carlton & United Beverages which is a subsidiary of Foster 's Group , brewers of Fosters Lager .";
			
			IndexWriter writer = new IndexWriter(idx, analyzer, true, IndexWriter.MaxFieldLength.LIMITED);
			
			Document doc = new Document();
			doc.add(new Field("sentence1", s1, Field.Store.YES, Field.Index.ANALYZED, 	Field.TermVector.NO));
			doc.add(new Field("sentence2", s1, Field.Store.YES, Field.Index.ANALYZED, 	Field.TermVector.WITH_POSITIONS_OFFSETS));
			doc.add(new Field("sentence3", s1, Field.Store.YES, Field.Index.NOT_ANALYZED, Field.TermVector.NO));
			doc.add(new Field("sentence4", s1, Field.Store.YES, Field.Index.NOT_ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS));
			writer.addDocument(doc);
			
			Document doc1 = new Document();
			doc1.add(new Field("sentence1", s2, Field.Store.YES, Field.Index.ANALYZED, 	Field.TermVector.NO));
			doc1.add(new Field("sentence2", s2, Field.Store.YES, Field.Index.ANALYZED, 	Field.TermVector.WITH_POSITIONS_OFFSETS));
			doc1.add(new Field("sentence3", s2, Field.Store.YES, Field.Index.NOT_ANALYZED, Field.TermVector.NO));
			doc1.add(new Field("sentence4", s2, Field.Store.YES, Field.Index.NOT_ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS));
			writer.addDocument(doc1);
			
			
			writer.optimize();
			writer.close();
		}
		catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (LockObtainFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	/**
	 * This method will tokenize the text using the provided analyzer and print each token.
	 */
	public static void printTokens(String text, Analyzer analyzer) throws IOException {

		TokenStream tokenStream = analyzer.tokenStream("FIELDNAME", new StringReader(text)); // Using this method, you will get a tokenstream
		TermAttribute termAttribute = tokenStream.getAttribute(TermAttribute.class);

		System.out.print("Token: ");
		while (tokenStream.incrementToken()) {
		    String term = termAttribute.term();
		    System.out.print("/" + term);
		}
		System.out.println();
		tokenStream.close();
	}
	
	private static void search(Searcher searcher, String queryString, Analyzer analyzer) throws ParseException, IOException {

		QueryParser parser = new QueryParser(Version.LUCENE_30, "sentence", analyzer);
		
		// Build a Query object
		Query query1 = parser.parse("+sentence1:\"" + queryString + "\"");
		Query query2 = parser.parse("+sentence2:\"" + queryString + "\"");
		Query query3 = parser.parse("+sentence3:\"" + queryString + "\"");
		Query query4 = parser.parse("+sentence4:\"" + queryString + "\"");
		Query query5 = parser.parse("+sentence1:\"Victoria Bitter\" && sentence1:\"Carlton & United Beverages\"");
		Query query6 = parser.parse("+sentence2:\"Victoria Bitter\" && sentence2:\"Carlton & United Beverages\"");

		// Search for the query
		ScoreDoc[] hits = searcher.search(query1, null, 10).scoreDocs;
		System.out.println("Index hits: " + hits.length + " for query1: " + query1.toString());
		for (int i = 0; i < hits.length; i++) {

			System.out.println(searcher.doc(hits[i].doc).get("sentence1"));
		}
		// Search for the query
		hits = searcher.search(query2, null, 10).scoreDocs;
		System.out.println("Index hits: " + hits.length + " for query2: " + query2.toString());
		for (int i = 0; i < hits.length; i++) {

			System.out.println(searcher.doc(hits[i].doc).get("sentence2"));
		}
		// Search for the query
		hits = searcher.search(query3, null, 10).scoreDocs;
		System.out.println("Index hits: " + hits.length + " for query3: " + query3.toString());
		for (int i = 0; i < hits.length; i++) {

			System.out.println(searcher.doc(hits[i].doc).get("sentence3"));
		}
		// Search for the query
		hits = searcher.search(query4, null, 10).scoreDocs;
		System.out.println("Index hits: " + hits.length + " for query4: " + query4.toString());
		for (int i = 0; i < hits.length; i++) {

			System.out.println(searcher.doc(hits[i].doc).get("sentence4"));
		}
		// Search for the query
		hits = searcher.search(query5, null, 10).scoreDocs;
		System.out.println("Index hits: " + hits.length + " for query5: " + query5.toString());
		for (int i = 0; i < hits.length; i++) {

			System.out.println(searcher.doc(hits[i].doc).get("sentence1"));
		}
		// Search for the query
		hits = searcher.search(query6, null, 10).scoreDocs;
		System.out.println("Index hits: " + hits.length + " for query6: " + query6.toString());
		for (int i = 0; i < hits.length; i++) {

			System.out.println(searcher.doc(hits[i].doc).get("sentence2"));
		}
	}
}