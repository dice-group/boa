package de.uni_leipzig.simba.boa.backend.knowledgecreation;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uni_leipzig.simba.boa.backend.concurrent.KnowledgeCreationThreadManager;
import de.uni_leipzig.simba.boa.backend.concurrent.PatternMappingPatternPair;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;


public class KnowledgeCreationManager {

    /**
     * 
     * @param mappings
     * @return
     */
    public Map<String, List<Triple>> findNewTriples(Set<PatternMapping> mappings) {
        
        // create the input for the search threads  
        Set<PatternMappingPatternPair> pairs = new HashSet<PatternMappingPatternPair>();
        for ( PatternMapping mapping : mappings) {
            for (Pattern pattern : mapping.getPatterns()) {
                
                // but only those patterns which are higher scored then the threshold
                if ( pattern.getScore() >= NLPediaSettings.getDoubleSetting("pattern.score.threshold.create.knowledge") )
                    pairs.add(new PatternMappingPatternPair(mapping, pattern));
            }
        }
        return KnowledgeCreationThreadManager.startKnowledgeCreationCallables(pairs, NLPediaSettings.getIntegerSetting("number.of.create.knowledge.threads"));
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
        for ( Triple triple : unscoredTriples ) {
            
            double confidence = 0;
            for ( Pattern patternLearnedFrom : triple.getLearnedFromPatterns() ) {
                
                confidence += patternLearnedFrom.getScore();
            }
            // sigmoid function shifted to the right to boost pattern which are learned from more than one pattern
            triple.setConfidence(1D / (1D + Math.pow(Math.E, - confidence * triple.getLearnedFromPatterns().size() + 1)));
            
            if ( triple.getScore() >= NLPediaSettings.getDoubleSetting("triple.score.threshold.create.knowledge") ) {
                
                scoredTriples.add(triple);
            }
        }
        return scoredTriples;
    }
}
