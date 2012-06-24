package de.uni_leipzig.simba.boa.backend.wordnet.query;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

/**
 *
 * @author ngonga
 */
public class WordnetQuery {

	/**
	 * 
	 */
    static {
        System.setProperty("wordnet.database.dir", NLPediaSettings.BOA_BASE_DIRECTORY + NLPediaSettings.getSetting("wordnet.database.directory"));
    }

    /**
     * 
     * @param word
     * @return
     */
    public static Set<String> getSynsetsForAllSynsetTypes(String word) {

        WordNetDatabase database = WordNetDatabase.getFileInstance();
        
        Set<Synset> synsets = new HashSet<Synset>();
        
        for ( SynsetType type : SynsetType.ALL_TYPES ) {
        	
        	synsets.addAll(Arrays.asList(database.getSynsets(word, type)));
        }
        
        Set<String> results = new TreeSet<String>();
        for (Synset synset : synsets) {
        	
            for(String wordForm : synset.getWordForms()) {
            	
                results.add(wordForm.toLowerCase());
            }
        }
        return results;
    }
    
    public static void main(String[] args) {

    	getSynsetsForAllSynsetTypes("aircraft");
	}
}
