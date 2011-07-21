package de.uni_leipzig.simba.boa.backend.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

/**
 * This terminal application creates an Apache Lucene index in a folder and adds
 * files into this index based on the input of the user.
 */
public class FileIndexer {

	private NLPediaLogger logger = new NLPediaLogger(FileIndexer.class);
	
	private IndexWriter writer;

	/**
	 * Constructor
	 * 
	 * @param indexDir
	 *            the name of the folder in which the index should be created
	 * @param overwriteIndex
	 * 			  overwrite an existing index or append
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
		
		Directory indexDirectory	= FSDirectory.open(new File(indexDir));
		Analyzer analyzer	= new SimpleAnalyzer();
		
		File files[] = directory.listFiles();
		
		Document doc			= null;
		int i = 1;
		
		System.out.println("Index directory: " + NLPediaSettings.getInstance().getSetting("sentenceIndexDirectory"));
		
		// create the index writer
		this.writer = new IndexWriter(
					indexDirectory,
					analyzer,
					true,
					IndexWriter.MaxFieldLength.LIMITED);
		
		this.writer.close();
		
		// go through all files in the data 
		for (File file : files) {
			
			// create the index writer
			this.writer = new IndexWriter(
						indexDirectory,
						analyzer,
						false,
						IndexWriter.MaxFieldLength.LIMITED);
			
			this.writer.setRAMBufferSizeMB(ramBufferSizeInMb);
			
			this.logger.info("Indexing file " + file + " with index ");
			
			long fileSize = file.length();
			
			System.out.println("Indexing file["+(i++)+"] " + file);
		    System.out.println("File size: " + (double)fileSize/(1024*1024) + "MB");
			
			// only index txt files
			if ( file.getName().endsWith(".txt") ) {
				
				try {

					BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
					String line = null;
					
//					NamedEntityRecognizer ner = new NamedEntityRecognizer();
					
					int j = 0;
					
					while( (line = br.readLine()) != null){ 
						
						if ( j++ % 10000 == 0 ) System.out.println("Iteration: " + j);
						
//						String[] lineParts = line.split(" \\|\\|\\| ");
						
//						if ( lineParts.length == 2 ) {
							
							doc = new Document();
							doc.add(new Field("sentence", line, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS));
//							doc.add(new Field("sentenceNotAnalyzed", line, Field.Store.YES, Field.Index.NOT_ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS));
//							doc.add(new Field("generalization", ner.recognizeEntitiesInString(line), Field.Store.YES, Field.Index.ANALYZED));
//							doc.add(new Field("partOfSpeach", lineParts[1], Field.Store.YES, Field.Index.NOT_ANALYZED));
//							doc.add(new Field("path", file.getAbsolutePath(), Field.Store.YES, Field.Index.NOT_ANALYZED));
							
							this.writer.addDocument(doc);
//						}
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
}
