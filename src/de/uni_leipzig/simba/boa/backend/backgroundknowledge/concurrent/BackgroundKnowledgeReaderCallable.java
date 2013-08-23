/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.backgroundknowledge.concurrent;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import com.github.gerbsen.file.BufferedFileReader;
import com.github.gerbsen.file.FileUtil;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.BackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.BackgroundKnowledgeManager;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.impl.DatatypePropertyBackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.impl.ObjectPropertyBackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.search.result.SearchResultReaderCallable;
import de.uni_leipzig.simba.boa.backend.wordnet.query.WordnetQuery;

/**
 * @author gerb
 *
 */
public class BackgroundKnowledgeReaderCallable implements Callable<BackgroundKnowledgeReaderCallable> {

	private List<BackgroundKnowledge> backgroundKnowledge;
	private File file;
	private boolean isObjectProperty;
	private final NLPediaLogger logger				= new NLPediaLogger(BackgroundKnowledgeReaderCallable.class);

	public BackgroundKnowledgeReaderCallable(
			List<BackgroundKnowledge> backgroundKnowledge, File file, boolean isObjectProperty) {
		
		this.backgroundKnowledge = backgroundKnowledge;
		this.file = file;
		this.isObjectProperty = isObjectProperty;
	}

	@Override
	public BackgroundKnowledgeReaderCallable call() throws Exception {
		
		this.backgroundKnowledge.addAll(this.getBackgroundKnowledge(file.getAbsolutePath(), isObjectProperty));
		return this;
	}

	
	/**
	 * 
	 * @param filename
	 * @return
	 */
	public List<BackgroundKnowledge> getBackgroundKnowledge(String filename, boolean isObjectProperty) {
        
        List<BackgroundKnowledge> backgroundKnowledge = new ArrayList<BackgroundKnowledge>();
        this.logger.info(String.format("Reading background knowledge from file %s", filename));

        BufferedFileReader br = FileUtil.openReader(filename, "UTF-8");

        String line;
        while ((line = br.readLine()) != null) {

            try {
                
                backgroundKnowledge.add(BackgroundKnowledgeManager.getInstance().createBackgroundKnowledge(line, isObjectProperty));
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e ) {
                
                System.out.println(line);
            }
        }
        br.close();
        return backgroundKnowledge;
    }
	
	
}
