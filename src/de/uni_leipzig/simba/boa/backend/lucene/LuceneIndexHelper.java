/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

/**
 * @author gerb
 *
 */
public class LuceneIndexHelper {

	private static NLPediaLogger logger = new NLPediaLogger(LuceneIndexHelper.class);
	public enum LuceneIndexType {
	    
	    RAM_INDEX,
	    DIRECTORY_INDEX;
	}
	
	private static IndexSearcher indexSearcher;
	
	/**
	 * 
	 * @param indexDir
	 * @return
	 */
	public static Directory openIndex(String indexDir) {
		
		try {
			
			return NIOFSDirectory.open(new File(indexDir));
		}
		catch (IOException e) {

			e.printStackTrace();
			String error = "Could not open index for directory: " + indexDir;
			logger.fatal(error, e);
			throw new RuntimeException(error, e);
		}
	}
	
	public synchronized static IndexSearcher getIndexSearcher(String indexDir) {
	    
	    if ( LuceneIndexHelper.indexSearcher != null ) return indexSearcher;
	    else {
	        
	        // create the index writer configuration and create a new index writer
	        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_34, new LowerCaseWhitespaceAnalyzer());
	        indexWriterConfig.setOpenMode(!LuceneIndexHelper.isIndexExisting(indexDir) ? OpenMode.CREATE : OpenMode.APPEND);
	        IndexWriter writer = LuceneIndexHelper.createIndex(indexDir, indexWriterConfig, 
	                NLPediaSettings.getBooleanSetting("useRamDirectory") ? LuceneIndexType.RAM_INDEX : LuceneIndexType.DIRECTORY_INDEX);
	        
	        try {
	            
	            indexSearcher = new IndexSearcher(IndexReader.open(writer, false));
	            return indexSearcher;
	        }
	        catch (CorruptIndexException e) {
	            
	            e.printStackTrace();
	            String error = "Could not open index searcher for directory: " + indexDir;
	            logger.fatal(error, e);
	            throw new RuntimeException(error, e);
	        }
	        catch (IOException e) {
	            
	            e.printStackTrace();
	            String error = "Could not open index searcher for directory: " + indexDir;
	            logger.fatal(error, e);
	            throw new RuntimeException(error, e);
	        }
	    }
	}
	
    /**
     * Returns the sentence index by the given id.
     * 
     * @param id
     * @param IndexSearcher
     * @return
     */
    public static String getFieldValueByDocId(IndexSearcher searcher, Integer id, String fieldName) {

        try {

            return searcher.doc(id).get(fieldName);
        }
        catch (CorruptIndexException e) {
            
            e.printStackTrace();
            String error = "Could not get document with id: " + id + " from index.";
            logger.error(error, e);
            throw new RuntimeException(error, e);
        }
        catch (IOException e) {
            
            e.printStackTrace();
            String error = "Could not get document with id: " + id + " from index.";
            logger.error(error, e);
            throw new RuntimeException(error, e);
        }
    }

    /**
     * Returns the sentences from the index with the given ids. Uses the method
     * DefaultPatternSearcher.getSentencesByID() to query the index
     * 
     * @param listOfIds
     * @param indexSearcher
     * @return
     */
    public static List<String> getFieldValueByIds(IndexSearcher searcher, List<Integer> ids, String fieldname) {

        List<String> sentences = new ArrayList<String>();
        for (Integer id : ids) {

            sentences.add(getFieldValueByDocId(searcher, id, fieldname));
        }
        return sentences;
    }
	
	/**
     * Opens a Lucene IndexWriter with the specified settings.
     * This method catches all Lucene exceptions.
     * 
     * @param file - the directory where to write the index
     * @param indexWriterConfig - the config for the index writer
     * @return an opened indexwriter for the specified settings.
     * @throws RuntimeException if something goes wrong
     */
    public static IndexWriter createIndex(String absoluteFilePath, IndexWriterConfig indexWriterConfig, LuceneIndexType indexType) {

        try {
            
            Directory index = null;
            if ( indexType.equals(LuceneIndexType.RAM_INDEX) ) {
                
                index = new RAMDirectory(NIOFSDirectory.open(new File(absoluteFilePath)));
            }
            else if ( indexType.equals(LuceneIndexType.DIRECTORY_INDEX) ){
                
                index = NIOFSDirectory.open(new File(absoluteFilePath));
            }
            else throw new RuntimeException("Unknown IndexType provided!");
            
            return new IndexWriter(index, indexWriterConfig);
        }
        catch (CorruptIndexException e) {
            
            logger.fatal("Could not create index", e);
            e.printStackTrace();
            throw new RuntimeException("Could not create index", e);
        }
        catch (LockObtainFailedException e) {
            
            logger.fatal("Could not create index", e);
            e.printStackTrace();
            throw new RuntimeException("Could not create index", e);
        }
        catch (IOException e) {
            
            logger.fatal("Could not create index", e);
            e.printStackTrace();
            throw new RuntimeException("Could not create index", e);
        }
    }
    
    /**
     * Writes all sentences of all sentences in the given list to the Lucene index.
     * 
     * @param writer - the writer to write the sentences
     * @param documents - all documents to be processed
     */
    public static void indexDocument(IndexWriter writer, Document document) {
        
        try {
            
            writer.addDocument(document);
        }
        catch (CorruptIndexException e) {
            
            logger.fatal("Could not index list of documents", e);
            e.printStackTrace();
            throw new RuntimeException("Could not index list of documents", e);
        }
        catch (IOException e) {
            
            logger.fatal("Could not index list of documents", e);
            e.printStackTrace();
            throw new RuntimeException("Could not index list of documents", e);
        }
    }
    
    /**
     * Closes a given index writer without Lucene exceptions.
     * This method performs a optimize step before closing.
     * 
     * @param writer the writer to close
     * @throws RuntimeException if something goes wrong
     */
    public static void closeIndexWriter(IndexWriter writer) {

        try {
            
            // close the index and do a full optimize
            writer.optimize();
            writer.close();
        }
        catch (CorruptIndexException e) {
            
            logger.fatal("Could not close/optimize index", e);
            e.printStackTrace();
            throw new RuntimeException("Could not close/optimize index", e);
        }
        catch (IOException e) {
            
            logger.fatal("Could not close/optimize index", e);
            e.printStackTrace();
            throw new RuntimeException("Could not close/optimize index", e);
        }
    }
	
	/**
	 * 
	 * @param indexDirectory
	 * @return
	 */
	public static boolean isIndexExisting(String indexDirectory) {
	    
	    try {
            
            return IndexReader.indexExists(FSDirectory.open(new File(indexDirectory)));
        }
        catch (IOException e) {
            
            e.printStackTrace();
            String error = "Check if index exists failed!";
            logger.fatal(error, e);
            throw new RuntimeException(error, e);
        }
	}
	
	/**
	 * 
	 * @param directory
	 * @param readonly
	 * @return
	 */
	public static IndexSearcher openIndexSearcher(Directory directory, boolean readonly){
		
		try {
			
			return new IndexSearcher(directory, readonly);
		}
		catch (CorruptIndexException e) {
			
			e.printStackTrace();
			String error = "Could not open index for directory: " + directory.toString();
			logger.fatal(error, e);
			throw new RuntimeException(error, e);
		}
		catch (IOException e) {
			
			e.printStackTrace();
			String error = "Could not open index for directory: " + directory.toString();
			logger.fatal(error, e);
			throw new RuntimeException(error, e);
		}
	}
}
