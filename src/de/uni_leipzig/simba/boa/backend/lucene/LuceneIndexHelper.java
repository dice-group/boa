/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.util.PatternUtil;


/**
 * @author gerb
 *
 */
public class LuceneIndexHelper {

	private static NLPediaLogger logger = new NLPediaLogger(LuceneIndexHelper.class);
	
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
