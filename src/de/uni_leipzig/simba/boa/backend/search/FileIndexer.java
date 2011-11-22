package de.uni_leipzig.simba.boa.backend.search;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;

import de.danielgerber.file.FileUtil;
import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.nlp.NamedEntityRecognizer;
import de.uni_leipzig.simba.boa.backend.nlp.SentenceDetection;
import de.uni_leipzig.simba.boa.backend.util.ProgressBarUtil;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;

/**
 * This terminal application creates an Apache Lucene index in a folder and adds
 * files into this index based on the input of the user.
 */
public class FileIndexer {
	
	private NLPediaLogger logger = new NLPediaLogger(FileIndexer.class);
//	private static NamedEntityRecognizer nerTagger = new NamedEntityRecognizer();

	private IndexWriter writer;
	private SentenceDetection sentenceDetection = new SentenceDetection();

	/**
	 * Constructor
	 * 
	 * @param indexDir
	 *            the name of the folder in which the index should be created
	 * @param overwriteIndex
	 *            overwrite an existing index or append
	 * @throws java.io.IOException
	 */
	public FileIndexer(String indexDir, boolean overwriteIndex, int ramBufferSizeInMb) throws Exception {

		// index the files
//		this.indexFileOrDirectory(indexDir, ramBufferSizeInMb);
		this.createDocumentFormatDirectory(indexDir, ramBufferSizeInMb);
	}

	private void createDocumentFormatDirectory(String indexDir, int ramBufferSizeInMb) throws Exception {

		Directory indexDirectory = FSDirectory.open(new File(indexDir));
		Analyzer analyzer = new WhitespaceAnalyzer();

		this.writer = new IndexWriter(indexDirectory, analyzer, true, IndexWriter.MaxFieldLength.LIMITED);
		this.writer.setRAMBufferSizeMB(ramBufferSizeInMb);
		
		for (File file : FileUtils.listFiles(new File(NLPediaSettings.getInstance().getSetting("sentenceFileDirectory")), HiddenFileFilter.VISIBLE, TrueFileFilter.INSTANCE)) {
			
			this.logger.info("Indexing file " + file + " with index ");
			System.out.println("\nIndexing file: " + file);
			System.out.println("File size: " + (double) file.length() / (1024 * 1024) + "MB and ");

			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			List<IndexDocument> documents = new ArrayList<IndexDocument>();

			IndexDocument document = new IndexDocument();
			
			int indexDocumentCount = 0;
			
			String line;
			while ((line = br.readLine()) != null) {

				if (line.startsWith("<doc")) {

					document.uri = line.substring(line.lastIndexOf("url=\"") + 5, line.lastIndexOf("\">"));
				}
				else 
					if (line.startsWith("</doc>")) {
						// output.append(line); dont append </doc> to document
						documents.add(document);
						document = new IndexDocument();
					}
					else {
						document.text.append(line);
					}

				if (documents.size() == 1000) {
					
					indexDocumentCount += 1000;
					System.out.println("Indexed " + indexDocumentCount);
					indexDocuments(documents);
					documents = new ArrayList<IndexDocument>();
				}
			}
		}
		// close the index
		this.writer.optimize();
		this.writer.close();
	}
	
	private void indexDocuments(List<IndexDocument> documents) throws CorruptIndexException, IOException {

		for (IndexDocument doc : documents) {
			
			for (String sentence : doc.getSentences() ) {
				
				writer.addDocument(this.createLuceneDocument(doc.uri, sentence));
			}
		}
	}

	private Document createLuceneDocument(String uri, String sentence) {

		Document luceneDocument = new Document();
		luceneDocument.add(new Field("uri", uri, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO));
		luceneDocument.add(new Field("sentence", sentence, Field.Store.YES, Field.Index.NOT_ANALYZED, Field.TermVector.NO));
		luceneDocument.add(new Field("sentence-lc", sentence.toString().toLowerCase(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO));
		return luceneDocument;
	}

	class IndexDocument {

		public String uri = "";
		public StringBuffer text = new StringBuffer();
		
		public List<String> getSentences() {
			
			String text = Jsoup.parse(this.text.toString()).text();
			return sentenceDetection.getSentences(text, NLPediaSettings.getInstance().getSetting("sentenceBoundaryDisambiguation"));
		}
	}

	/**
	 * Indexes the sentenceFileDirectory specified in nlpedia_config.xml.
	 * 
	 * @throws java.io.IOException
	 */
	public void indexFileOrDirectory(String indexDir, int ramBufferSizeInMb) throws IOException {

		File directory = new File(NLPediaSettings.getInstance().getSetting("sentenceFileDirectory"));

		Directory indexDirectory = FSDirectory.open(new File(indexDir));
		Analyzer analyzer = new WhitespaceAnalyzer();

		File files[] = directory.listFiles();

		Document doc = null;
		int i = 1;

		System.out.println("Index directory: " + NLPediaSettings.getInstance().getSetting("sentenceIndexDirectory"));

		// create the index writer and close it, hack for large index, TODO remove
		this.writer = new IndexWriter(indexDirectory, analyzer, true, IndexWriter.MaxFieldLength.LIMITED);
		this.writer.close();

		// go through all files in the data
		for (File file : files) {
			
			// create the index writer
			this.writer = new IndexWriter(indexDirectory, analyzer, false, IndexWriter.MaxFieldLength.LIMITED);

			this.writer.setRAMBufferSizeMB(ramBufferSizeInMb);

			this.logger.info("Indexing file " + file + " with index ");

			long fileSize = file.length();
			int linesOfFile = FileUtil.countLinesOfFile(file.getAbsolutePath());

			System.out.println("\nIndexing file[" + (i++) + "] " + file);
			System.out.println("File size: " + (double) fileSize / (1024 * 1024) + "MB and ");

			// only index txt files
			if (file.getName().endsWith(".txt") && !file.isDirectory()) {

				try {

					BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
					String line = null;

					int j = 0;

					while ((line = br.readLine()) != null) {

						if (j++ % 50000 == 0) {

							ProgressBarUtil.printProgBar((int) ((((double) j) / ((double) linesOfFile)) * 100));
						}
					
						doc = new Document();
						doc.add(new Field("sentence", line, Field.Store.YES, Field.Index.NOT_ANALYZED, Field.TermVector.NO));
						doc.add(new Field("sentence-lc", line.toLowerCase(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO));
						this.writer.addDocument(doc);
					}
					br.close();
					this.logger.info("Added: " + file.getAbsolutePath() + " to index.");
				}
				catch (Exception e) {

					this.logger.error("Could not add: " + file + " to index", e);
					e.printStackTrace();
				}
			}
			else {

				this.logger.info("The file: " + file.getAbsolutePath() + "could not be indexed. Wrong file type!");
			}

			// close the index
			this.writer.optimize();
			this.writer.close();
		}
		this.logger.info("Added " + writer.numDocs() + " files to index");
	}
	
	private static final String NER_PERSON = "_I-ORG";
	private static final String NER_LOCATION = "_I-ORG";
	private static final String NER_ORGANISATION = "_I-ORG";
//	private static final String NER_PERSON = "_PERSON";
//	private static final String NER_ORGANISATION = "_ORGANIZATION";
//	private static final String NER_LOCATION = "_LOCATION";
	/**
	 * This methods first needs to check if the line is smaller than 256 characters. Then it
	 * has to NER tag the sentence and if the sentence contains two or more entities then this will be
	 * added to the training data. The sentence shall not be added to the index! 
	 * 
	 * @param line
	 */
	private static boolean sentenceContainsMoreThanTwoEntities(String line) {

		String nerTaggedLine = null;//nerTagger.recognizeEntitiesInString(line);
		
		if ( (nerTaggedLine.contains(NER_PERSON) && nerTaggedLine.contains(NER_ORGANISATION)) || 
			 (nerTaggedLine.contains(NER_ORGANISATION) && nerTaggedLine.contains(NER_LOCATION))	|| 
			 (nerTaggedLine.contains(NER_LOCATION) && nerTaggedLine.contains(NER_PERSON)) ) {
			
			return true;
		}
		List<String> occurrences =  new ArrayList<String>();
		String[] orgs = StringUtils.substringsBetween(line, NER_ORGANISATION, NER_ORGANISATION);
		String[] pers = StringUtils.substringsBetween(line, NER_PERSON, NER_PERSON);
		String[] locs = StringUtils.substringsBetween(line, NER_LOCATION, NER_LOCATION);
		
		if ( orgs != null ) occurrences.addAll(Arrays.asList(orgs));
		if ( pers != null ) occurrences.addAll(Arrays.asList(pers));
		if ( locs != null ) occurrences.addAll(Arrays.asList(locs));
		
		int i = 0;
		List<String> tokens = Arrays.asList(nerTaggedLine.split("_O "));
		for (String token : tokens) {
			
			if ( token.contains(NER_PERSON) || token.contains(NER_ORGANISATION) || token.contains(NER_ORGANISATION) ) {
				i++;
			}
			if ( i >= 2 ) {
				
				return true;
			}
		}
		return false;
	}
	
	private static void createFile() throws IOException {
		
		File file = new File("/Users/gerb/Downloads/12-09-2011/short_abstracts_en.nt");
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		
		Writer bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/Users/gerb/en_abstracts_sentences.txt"), "UTF-8"));

		
		String line = "";
		while ((line = br.readLine()) != null ) {
			
			line = org.apache.commons.lang.StringEscapeUtils.unescapeJava(line.substring(line.indexOf("\"")+1, line.indexOf("\"@en")));
			StringReader stringReader = new StringReader(line);
			DocumentPreprocessor preprocessor = new DocumentPreprocessor(stringReader,  DocumentPreprocessor.DocType.Plain);
			
			Iterator<List<HasWord>> iter = preprocessor.iterator();
			while ( iter.hasNext() ) {
				
				StringBuilder stringBuilder = new StringBuilder();
				
				for ( HasWord word : iter.next() ) {
					stringBuilder.append(word.toString() + " ");
				}
				
				bw.write(stringBuilder.toString() + Constants.NEW_LINE_SEPARATOR);
			}
		}
		br.close();
	    bw.close();
	}
	
	public static void main(String[] args) throws IOException {
		
//		createFile();
		
		File file = new File("/Users/gerb/en_abstracts_sentences.txt");
		Random randomGenerator = new Random();
		
		// calculate the lines to get randomly from the file
		List<Integer> lines = new ArrayList<Integer>();
		int linesOfFile = FileUtil.countLinesOfFile(file.getAbsolutePath());
		System.out.printf("The file has %s lines.\n", linesOfFile);
		
	    for (int idx = 1 ; idx <= 100000 ; idx++) {
	    	
	    	int y = randomGenerator.nextInt(linesOfFile);
	    	while ( lines.contains(y) ) y = randomGenerator.nextInt(linesOfFile + 1);
	    	lines.add(y);
	    }
	    Collections.sort(lines);
	    System.out.println("Random lines calculated!");
	    
	    String line;
	    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
	    
	    List<String> sentencesToWrite = new ArrayList<String>();
	    for ( int i = 0, j = 0 ; i < linesOfFile ; i++ ) {
	    	
	    	line = br.readLine();
	    	
	    	if ( i == lines.get(j) ) {
	    		
	    		if ( line.length() < 128 && line.length() > 80 && sentenceContainsMoreThanTwoEntities(line) ) {
	    			
//	    			System.out.println(line);
	    			sentencesToWrite.add(line);
	    		}
	    		j++;
	    	}
	    	if (j == lines.size() - 1 ) break;
	    }
	    Collections.shuffle(sentencesToWrite);
	    	
	    Writer bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/Users/gerb/en_eval_sentences_2k.txt"), "UTF-8"));
	    for (String sent : sentencesToWrite.subList(0, 2000) ) {
	    	bw.write(sent);
			bw.write(Constants.NEW_LINE_SEPARATOR);
	    }
	    bw.close();
	    br.close();
	}
}
