/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.knowledgecreation.cuncurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.store.Directory;

import de.uni_leipzig.simba.boa.backend.concurrent.BoaCallable;
import de.uni_leipzig.simba.boa.backend.concurrent.PatternMappingPatternPair;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.featureextraction.concurrent.PatternFeatureExtractionCallable;
import de.uni_leipzig.simba.boa.backend.knowledgecreation.TripleGenerator;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;
import de.uni_leipzig.simba.boa.backend.search.PatternSearcher;
import de.uni_leipzig.simba.boa.backend.search.PatternSearcherFactory;

/**
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 * 
 */
public class KnowledgeCreationCallable extends BoaCallable<Map<String, List<Triple>>> {

    private final NLPediaLogger logger = new NLPediaLogger(PatternFeatureExtractionCallable.class);

    private List<PatternMappingPatternPair> patternMappingPatternPairs;
    private PatternSearcher patternSearcher;

    public KnowledgeCreationCallable(Directory index, List<PatternMappingPatternPair> patternMappingPatternPairSubList) {

        this.patternMappingPatternPairs = patternMappingPatternPairSubList;
        
        // in case we run the evaluation we have a different index not the default one
        this.patternSearcher = PatternSearcherFactory.getInstance().createDefaultPatternSearcher(index);
        
        this.logger.info("PatternMapping-Pattern-Pairs to create knowledge for: " + this.patternMappingPatternPairs.size() + "!");
    }

    @Override
    public Collection<Map<String, List<Triple>>> call() throws Exception {
        
        TripleGenerator tripleGenerator = new TripleGenerator();
        List<Map<String, List<Triple>>> results = new ArrayList<Map<String, List<Triple>>>();
        results.add(new HashMap<String,List<Triple>>());
        
        for (PatternMappingPatternPair pair : this.patternMappingPatternPairs) {

            Set<String> sentences = this.patternSearcher.getExactMatchSentences(
                    pair.getPattern().getNaturalLanguageRepresentationWithoutVariables(),
                    NLPediaSettings.getIntegerSetting("max.number.of.documents.generation"));
            
            this.logger.debug("\tCreating knowledge for pattern mapping: " + pair.getMapping().getProperty().getUri() + 
                    " / \"" + pair.getPattern().getNaturalLanguageRepresentation() + "\" with " + sentences.size() + " sentences");

            for (String sentence : sentences) {

                // there will never be a left argument if the sentence begins with the pattern
                if (sentence.toLowerCase().startsWith(pair.getPattern().getNaturalLanguageRepresentationWithoutVariables().toLowerCase())) continue;

                Triple triple = tripleGenerator.createTriple(pair.getMapping(), pair.getPattern(), sentence);
                if ( triple != null ) this.addTriple(results, triple);
            }
            this.progress++;
        }
        int tripleCount = 0;
        for ( List<Triple> triples : results.get(0).values()) tripleCount += triples.size();
        this.logger.info(this.name + " finished with " + tripleCount + " triples!");
                
        return results;
    }
    
    private void addTriple(List<Map<String, List<Triple>>> results, Triple triple) {

        // the results will always have only one entry, so if we 
        // already have a mapping between uri and triples, then add it 
        if ( results.get(0).containsKey(triple.getProperty().getUri()) ) {
            
            results.get(0).get(triple.getProperty().getUri()).add(triple);
        }
        else {
            
            // we dont have a mapping so we need to create one and add the triple
            List<Triple> tripleList = new ArrayList<Triple>();
            tripleList.add(triple);
            // put the mapping in the global results
            results.get(0).put(triple.getProperty().getUri(), tripleList);
        }
    }

    @Override
    public double getProgress() {

        return (double) (this.progress) / (this.patternMappingPatternPairs.size());
    }

    @Override
    public int getNumberTotal() {

        return this.patternMappingPatternPairs.size();
    }

    @Override
    public int getNumberOfResultsSoFar() {

        return -1;
    }

}
