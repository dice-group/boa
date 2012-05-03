/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.search;

import java.util.List;

import org.apache.lucene.store.Directory;

import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;


/**
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public class PatternSearcherFactory {

    private static PatternSearcherFactory INSTANCE;
    
    private final NLPediaLogger logger = new NLPediaLogger(PatternSearcherFactory.class);
    
    // the default pattern searcher and the list of all available searcher 
    private String defaultPatternSearcher;
    private List<String> patternSearcher;
    
    /**
     * Singleton
     */
    private PatternSearcherFactory() {}
    
    /**
     * @return the instance of this singleton
     */
    public static PatternSearcherFactory getInstance() {
        
        if ( INSTANCE == null ) {
            
            INSTANCE = new PatternSearcherFactory();
        }
        return INSTANCE;
    }
    
    /**
     * @return the default pattern searcher
     */
    @SuppressWarnings("unchecked")
    private PatternSearcher createDefaultPatternSearcher() {

        try {

            return (PatternSearcher) createNewInstance(
                    (Class<? extends PatternSearcher>) Class.forName(defaultPatternSearcher));
        }
        catch (ClassNotFoundException e) {

            e.printStackTrace();
            String error = "Could not load default pattern searcher " + defaultPatternSearcher;
            this.logger.error(error, e);
            throw new RuntimeException(error, e);
        }
    }
    
    /**
     * 
     * @param index
     * @return
     */
    public PatternSearcher createDefaultPatternSearcher(Directory index) {
        
        try {
            
            if ( index == null ) return this.createDefaultPatternSearcher();
            
            Class<? extends PatternSearcher> searcherClass = (Class<? extends PatternSearcher>) Class.forName(defaultPatternSearcher);
            return searcherClass.getDeclaredConstructor(Directory.class).newInstance(index);
        }
        catch (Exception e) {
            
            e.printStackTrace();
            String error = "Could not load Default Pattern Searcher with predefined index: " + index;
            this.logger.error(error, e);
            throw new RuntimeException(error, e);
        }
    }
    
    /**
     * Returns a new instance of the specified patternSearcherClass.
     * The specific implementations of the patternSearcher have to be declared
     * in the patternsearcher.xml file.
     * 
     * @param patternSearcherClass - the patternSearcherClass to be instantiated
     * @return the instantiated object
     * @throws RuntimeExcpetion if no class could be found
     */
    public PatternSearcher createPatternSearcher(Class<? extends PatternSearcher> patternSearcherClass) {

        if ( this.patternSearcher.contains(patternSearcherClass.getName()) ) {
            
            return (PatternSearcher) createNewInstance(patternSearcherClass);
        }
        String error = "Could not load pattern searcher " + patternSearcherClass.getName();
        this.logger.error(error);
        throw new RuntimeException(error);
    }
    
    /**
     * Instantiates a pattern searcher.
     * 
     * @param the pattern searcher to be instantiated
     * @return a new instance of the searcher
     * @throw RuntimeException if something wents wrong
     */
    private PatternSearcher createNewInstance(Class<? extends PatternSearcher> patternSearcher){
        
        try {
            
            return patternSearcher.newInstance();
        }
        catch (InstantiationException e) {

            e.printStackTrace();
            String error = "Could not instantiate class: " + patternSearcher;
            this.logger.fatal(error, e);
            throw new RuntimeException(error, e);
        }
        catch (IllegalAccessException e) {
            
            e.printStackTrace();
            String error = "Could not instantiate class: " + patternSearcher;
            this.logger.fatal(error, e);
            throw new RuntimeException(error, e);
        }
    }

    
    /**
     * @return the defaultPatternSearcher
     */
    public String getDefaultPatternSearcher() {
    
        return defaultPatternSearcher;
    }

    
    /**
     * @param defaultPatternSearcher the defaultPatternSearcher to set
     */
    public void setDefaultPatternSearcher(String defaultPatternSearcher) {
    
        this.defaultPatternSearcher = defaultPatternSearcher;
    }

    
    /**
     * @return the patternSearcher
     */
    public List<String> getPatternSearcher() {
    
        return patternSearcher;
    }

    
    /**
     * @param patternSearcher the patternSearcher to set
     */
    public void setPatternSearcher(List<String> patternSearcher) {
    
        this.patternSearcher = patternSearcher;
    }
}
