package de.uni_leipzig.simba.boa.backend.knowledgecreation;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.store.Directory;

import de.uni_leipzig.simba.boa.backend.concurrent.KnowledgeCreationThreadManager;
import de.uni_leipzig.simba.boa.backend.concurrent.PatternMappingPatternPair;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;

/**
 * 
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public class KnowledgeCreationManager {

    /**
     * 
     * @param index 
     * @param mappings
     * @return
     */
    public Map<String, List<Triple>> findNewTriples(Directory index, Set<PatternMapping> mappings) {
        
        // create the input for the search threads  
        Set<PatternMappingPatternPair> pairs = new HashSet<PatternMappingPatternPair>();
        for ( PatternMapping mapping : mappings) {
            for (Pattern pattern : mapping.getPatterns()) {
                
                // but only those patterns which are higher scored then the threshold
                if ( pattern.getScore() >= NLPediaSettings.getDoubleSetting("pattern.score.threshold.create.knowledge") )
                    pairs.add(new PatternMappingPatternPair(mapping, pattern));
            }
        }
        return KnowledgeCreationThreadManager.startKnowledgeCreationCallables(index, pairs, NLPediaSettings.getIntegerSetting("number.of.create.knowledge.threads"));
    }
    
    /**
     * 
     * @param newKnowledge
     * @return
     */
    public Map<String, Set<Triple>> mergeAndScoreTriples(Map<String, List<Triple>> newKnowledge) {

        Map<String,Set<Triple>> results =  new HashMap<String,Set<Triple>>();
        Map<Integer,Triple> mergedTriples;
        
        for ( Map.Entry<String, List<Triple>> entry : newKnowledge.entrySet()) {
            
            mergedTriples = new HashMap<Integer,Triple>();
            
            String propertyUri = entry.getKey();
            List<Triple> triples = entry.getValue();
            
            for ( Triple triple : triples ) {
                
                // we have seen this triple before, so merge it
                if ( mergedTriples.containsKey(triple.hashCode()) ) {
                    
                    // subject, predicate, object is the same, so don't change it
                    // the triple also has not a score yet
                    // the only things we need to merge are the patterns and the sentences this
                    // triple has been learned from
                    Triple knownTriple = mergedTriples.get(triple.hashCode());
                    knownTriple.getLearnedFromPatterns().addAll(triple.getLearnedFromPatterns());
                    knownTriple.getLearnedFromSentences().addAll(triple.getLearnedFromSentences());
                }
                else // we can simply put it in the list
                    mergedTriples.put(triple.hashCode(), triple);
            }
            results.put(propertyUri, this.calculateConfidence(mergedTriples.values()));
        }
        return results;
    }
    
    /**
     * 
     * @param unscoredTriples
     * @return
     */
    private Set<Triple> calculateConfidence(Collection<Triple> unscoredTriples) {

        Set<Triple> scoredTriples = new HashSet<Triple>();
        
        double maximum = 0;
        
        for ( Triple triple : unscoredTriples )
            for ( Pattern patternLearnedFrom : triple.getLearnedFromPatterns() ) {
                
                triple.setScore(triple.getScore() + patternLearnedFrom.getScore());
                maximum = Math.max(maximum, triple.getScore());
            }
        
        for ( Triple triple : unscoredTriples ) {

            // sigmoid function shifted to the right to boost pattern which are learned from more than one pattern
            // x (the triple score) needs to be between 0 and 1 in order to only get values between 0 and 1
            Double score = 1D / (1D + Math.pow(Math.E, - (2 * triple.getLearnedFromPatterns().size() * (triple.getScore() / maximum)) 
                    + triple.getLearnedFromPatterns().size()));
            
            if ( score.isNaN() ) triple.setScore(0D);
            else triple.setScore(score);
            
            // we only want the triple if its above a threshold
            if ( triple.getScore() >= NLPediaSettings.getDoubleSetting("triple.score.threshold.create.knowledge") ) 
              scoredTriples.add(triple);
        }
        return scoredTriples;
    }
}
