/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.search.result;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.github.gerbsen.encoding.Encoder.Encoding;
import com.github.gerbsen.file.BufferedFileReader;

import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;


/**
 * @author gerb
 *
 */
public class SearchResultReaderCallable implements Callable<SearchResultReaderCallable> {

	private List<SearchResult> results;
	private File file;
	private Map<Integer, String> alreadyKnowString;
	private final NLPediaLogger logger                  = new NLPediaLogger(SearchResultReaderCallable.class);
	
	public SearchResultReaderCallable(List<SearchResult> results, File file, Map<Integer, String> alreadyKnowString) {
		
		this.results = results;
		this.file = file;
		this.alreadyKnowString = alreadyKnowString;
	}

	@Override
	public SearchResultReaderCallable call() throws Exception {
		
		logger.info("Reading search results from file: " + file.getName());
        BufferedFileReader reader = new BufferedFileReader(file.getAbsolutePath(), Encoding.UTF_8);
        String line = "";

        // every line in each file is a serialized search result
        while ((line = reader.readLine()) != null) {
            
            String[] lineParts                  = line.split(java.util.regex.Pattern.quote("]["));
            
            // we need to do this none-sense to avoid create 32mio different property uris and so on 
            // this should dramatically reduce the memory usage while processing the search results
            for (String part : Arrays.copyOfRange(lineParts, 0, 4) )
                if ( !alreadyKnowString.containsKey(part.hashCode()) ) alreadyKnowString.put(part.hashCode(), part);
            
                try {
        
                    SearchResult searchResult = new SearchResult();
                    searchResult.setProperty(alreadyKnowString.get(lineParts[0].hashCode()));
                    searchResult.setNaturalLanguageRepresentation(alreadyKnowString.get(lineParts[1].hashCode()));
                    searchResult.setFirstLabel(alreadyKnowString.get(lineParts[2].hashCode()));
                    searchResult.setSecondLabel(alreadyKnowString.get(lineParts[3].hashCode()));
                    searchResult.setSentence(Integer.valueOf(lineParts[4]));
                    
                    if ( searchResult.getNaturalLanguageRepresentation().contains("?D?") && searchResult.getNaturalLanguageRepresentation().contains("?R?") )
            			results.add(searchResult);
                }
                catch (Exception e ) {
                    
                    e.printStackTrace();
                    logger.error("Line: " + line, e);
                }
        }
        reader.close();
        
        return this;
	}
}
