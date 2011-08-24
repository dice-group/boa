package de.uni_leipzig.simba.boa.backend.search;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import de.danielgerber.file.FileUtil;
import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.nlp.NamedEntityRecognizer;
import de.uni_leipzig.simba.boa.backend.util.ProgressBarUtil;

/**
 * This terminal application creates an Apache Lucene index in a folder and adds
 * files into this index based on the input of the user.
 */
public class FileIndexer {

	private NLPediaLogger logger = new NLPediaLogger(FileIndexer.class);
	private NamedEntityRecognizer nerTagger = new NamedEntityRecognizer();
	private int numberOfTrainedSentences = 0;

	private IndexWriter writer;

	/**
	 * Constructor
	 * 
	 * @param indexDir
	 *            the name of the folder in which the index should be created
	 * @param overwriteIndex
	 *            overwrite an existing index or append
	 * @throws java.io.IOException
	 */
	public FileIndexer(String indexDir, boolean overwriteIndex, int ramBufferSizeInMb) throws IOException {

		// index the files
		this.indexFileOrDirectory(indexDir, ramBufferSizeInMb);
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
//			int linesOfFile = FileUtil.countLinesOfFile(file.getAbsolutePath());

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

//							ProgressBarUtil.printProgBar((int) ((((double) j) / ((double) linesOfFile)) * 100));
						}
						
						if ( numberOfTrainedSentences <= 510 ) {
						
							line = this.createTrainingData(line);
						}
						if ( line != null ) {	
							doc = new Document();
							// doc.add(new Field("sentence", line, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS));
							doc.add(new Field("sentence", line, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO));

							this.writer.addDocument(doc);
						}
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

	/**
	 * This methods first needs to check if the line is smaller than 256 characters. Then it
	 * has to NER tag the sentence and if the sentence contains two or more entities then this will be
	 * added to the training data. The sentence shall not be added to the index! 
	 * 
	 * @param line
	 */
	private String createTrainingData(String line) {

		// sentence to long for evaluation
//		if ( line.length() >= 128 || line.length() <= 100 ) return line;
		
		String nerTaggedLine = nerTagger.recognizeEntitiesInString(line);
		System.out.println(nerTaggedLine);
		
		if ( (nerTaggedLine.contains("_PERSON") && nerTaggedLine.contains("_ORGANIZATION")) || 
			 (nerTaggedLine.contains("_ORGANIZATION") && nerTaggedLine.contains("_LOCATION"))	|| 
			 (nerTaggedLine.contains("_LOCATION") && nerTaggedLine.contains("_PERSON")) ) {
			
			writeSentenceToTrainingFile(line);
			return null;
		}
		List<String> occurrences =  new ArrayList<String>();
		String[] orgs = StringUtils.substringsBetween(line, "_ORGANIZATION", "_ORGANIZATION");
		String[] pers = StringUtils.substringsBetween(line, "_PERSON", "_PERSON");
		String[] locs = StringUtils.substringsBetween(line, "_LOCATION", "_LOCATION");
		
		if ( orgs != null ) occurrences.addAll(Arrays.asList(orgs));
		if ( pers != null ) occurrences.addAll(Arrays.asList(pers));
		if ( locs != null ) occurrences.addAll(Arrays.asList(locs));
		
		int i = 0;
		List<String> tokens = Arrays.asList(nerTaggedLine.split("_O "));
		for (String token : tokens) {
			
			if ( token.contains("_PERSON") || token.contains("_ORGANIZATION") || token.contains("_ORGANIZATION") ) {
				i++;
			}
		}
		if ( i >= 2 ) {
			
			writeSentenceToTrainingFile(line);
			return null;
		}
		return line;
	}
	
	private void writeSentenceToTrainingFile(String line) {

		try {
			
			this.numberOfTrainedSentences++;
			
			BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/Users/gerb/EVALUATION_500_WIKI.txt", true), "UTF-8"));
			br.write(line);
			br.write(Constants.NEW_LINE_SEPARATOR);
			br.close();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		
		File directory = new File("/Users/gerb/_EVAL/");
		File files[] = directory.listFiles();
		Random randomGenerator = new Random();
		
		for (File file : files) {
			
			// calculate the lines to get randomly from the file
			Set<Integer> lines = new HashSet<Integer>();
			int linesOfFile = FileUtil.countLinesOfFile(file.getAbsolutePath());
		    for (int idx = 1 ; idx <= 550 ; idx++) {
		    	
		    	int y = randomGenerator.nextInt(linesOfFile);
		    	while ( lines.contains(y) ) y = randomGenerator.nextInt(linesOfFile + 1);
		    	lines.add(y);
		    }
		    
		    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/Users/gerb/EVAL_SENT.txt"), "UTF-8"));
		    
		    for ( Integer lineNumber : lines ) {
		    	
		    	BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		    	
		    	for (int i = 1 ; i <= lineNumber; i++ ) {
		    		
		    		String tempLine = br.readLine();
		    		if ( i == lineNumber ) {
		    			
		    			bw.write(tempLine);
		    			bw.write(Constants.NEW_LINE_SEPARATOR);
		    		}
		    	}
		    	br.close();
		    }
		    bw.close();
		}
	}
	
	
//	public static void main(String[] args) throws IOException {
//		
//		NLPediaSetup setup = new NLPediaSetup(true);
//		File directory = new File(NLPediaSettings.getInstance().getSetting("sentenceFileDirectory"));
//		File files[] = directory.listFiles();
//		
//		NamedEntityRecognizer tagger = new NamedEntityRecognizer();
//		
//		for (File file : files) {
//			
//			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/Users/gerb/SENTENCES_2_ENTITIES.txt", true), "UTF-8"));
//			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
//			String line = null;
//			
//			int i = 0;
//			while ((line = br.readLine()) != null) { i++;
//				
//				System.out.println(i);
//				String nerTagged = tagger.recognizeEntitiesInString(line);
//				
//				if ( containsMoreThanOneEntity(nerTagged) ) {
//					
//					bw.write(line);
//					bw.write(Constants.NEW_LINE_SEPARATOR);
//				}
//			}
//			
//			bw.close();
//			br.close();
//		}
//	}

	private static boolean containsMoreThanOneEntity(String nerTaggedLine) {

		// two different tags were found
		if ( (nerTaggedLine.contains("_PERSON") && nerTaggedLine.contains("_ORGANIZATION")) || 
			 (nerTaggedLine.contains("_ORGANIZATION") && nerTaggedLine.contains("_LOCATION"))	|| 
			 (nerTaggedLine.contains("_LOCATION") && nerTaggedLine.contains("_PERSON")) ) {
			
			return true;
		}
		
		// check for two tags of the same kind
		List<String> occurrences =  new ArrayList<String>();
		String[] orgs = StringUtils.substringsBetween(nerTaggedLine, "_ORGANIZATION", "_ORGANIZATION");
		String[] pers = StringUtils.substringsBetween(nerTaggedLine, "_PERSON", "_PERSON");
		String[] locs = StringUtils.substringsBetween(nerTaggedLine, "_LOCATION", "_LOCATION");
		
		if ( orgs != null ) occurrences.addAll(Arrays.asList(orgs));
		if ( pers != null ) occurrences.addAll(Arrays.asList(pers));
		if ( locs != null ) occurrences.addAll(Arrays.asList(locs));
		
		// use _O as tag separator so succeeding tags get combined to one token 
		// -> A_Person B_Person of_O C_Person D_Person
		// -> [A_Person B_Person], [of], [C_Person D_Person]
		int i = 0;
		List<String> tokens = Arrays.asList(nerTaggedLine.split("_O "));
		for (String token : tokens) {
			
			if ( token.contains("_PERSON") || token.contains("_ORGANIZATION") || token.contains("_ORGANIZATION") ) i++;
			if ( i >= 2 ) return true;
		}
		return false;
	}
}
