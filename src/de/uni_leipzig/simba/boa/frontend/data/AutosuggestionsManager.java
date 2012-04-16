/**
 * 
 */
package de.uni_leipzig.simba.boa.frontend.data;

import java.util.Map;
import java.util.Set;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

import de.danielgerber.format.OutputFormatter;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;


/**
 * @author gerb
 *
 */
public class AutosuggestionsManager {

    private static AutosuggestionsManager INSTANCE = null;
    private static IndexedContainer naturalLanguagePatternContainer = new IndexedContainer();
    
    private AutosuggestionsManager() {}
    
    public static AutosuggestionsManager getInstance() {
        
        if ( AutosuggestionsManager.INSTANCE == null ) {
            
            AutosuggestionsManager.INSTANCE = new AutosuggestionsManager();
        }
        return AutosuggestionsManager.INSTANCE;
    }
    
    public IndexedContainer getNaturalLanguagePatternContainer(Map<String, Set<PatternMapping>> mappingsInDatabases) {
        
        naturalLanguagePatternContainer.addContainerProperty("NLR", String.class, "");
        naturalLanguagePatternContainer.addContainerProperty("PATTERN", String.class, "");
        naturalLanguagePatternContainer.addContainerProperty("DATABASE", String.class, "");
        naturalLanguagePatternContainer.addContainerProperty("MAPPING", String.class, "");

        for (Map.Entry<String, Set<PatternMapping>> database : mappingsInDatabases.entrySet() ) {
            for (PatternMapping mapping : database.getValue() ) {
                for ( Pattern pattern : mapping.getPatterns() ) {
                    
                    Item item = naturalLanguagePatternContainer.addItem(database + " " + mapping.getProperty().getUri() + " " + pattern.getNaturalLanguageRepresentation());
                    
                    if ( item != null ) {
                        
                        item.getItemProperty("NLR").setValue(pattern.getNaturalLanguageRepresentation() + " (" 
                                + database.getKey().substring(database.getKey().lastIndexOf("/") + 1) + ", " 
                                + mapping.getProperty().getPropertyLocalname() + ", "
                                + OutputFormatter.format(pattern.getScore(), "0.000") + ")");
                        item.getItemProperty("PATTERN").setValue(pattern.getNaturalLanguageRepresentation());
                        item.getItemProperty("DATABASE").setValue(database.getKey());
                        item.getItemProperty("MAPPING").setValue(mapping.getProperty().getUri());
                    }
                    else System.out.println("ITEM NULL");
                }
            }
        }
        return naturalLanguagePatternContainer;
    }
}
