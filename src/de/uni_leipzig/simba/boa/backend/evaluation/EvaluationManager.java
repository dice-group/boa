package de.uni_leipzig.simba.boa.backend.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.store.Directory;

import de.danielgerber.file.FileUtil;
import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.knowledgecreation.KnowledgeCreationManager;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Resource;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;


public class EvaluationManager {

    private KnowledgeCreationManager knowledgeCreationManager = new KnowledgeCreationManager();
    
    /**
     * 
     * @param index
     * @param mappings
     * @return
     */
    public Set<Triple> loadBoaResults(Directory index, Set<PatternMapping> mappings) {

        // results set
        Set<Triple> createdTriples = new HashSet<Triple>();
        
        // let the manager create the triples and then collect them in the result set
        Map<String,Set<Triple>> urisToTriples = knowledgeCreationManager.mergeAndScoreTriples(knowledgeCreationManager.findNewTriples(mappings));
        for (Map.Entry<String, Set<Triple>> entry : urisToTriples.entrySet() ) {
            
            createdTriples.addAll(entry.getValue());
        }
        return createdTriples;
    }
    
    /**
     * 
     * @return
     */
    public Map<Triple,String> loadEvaluationSentences() {

        List<String> evaluationFiles = FileUtil.readFileInList(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.EVALUATION_PATH + "Evaluation_3_Upmeier.txt", "UTF-8");
        evaluationFiles.addAll(FileUtil.readFileInList(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.EVALUATION_PATH + "Evaluation_3_Haack.txt", "UTF-8"));
        
        // clean this list of sentences
        List<String> cleanedEvaluationFiles = new ArrayList<String>();
        for (String line : evaluationFiles) {
            
            if ( line.startsWith("#") ) {
                                    
                if ( line.contains("http://dbpedia.org/ontology/") ) {
                    
                    line = line.replace("#", "").trim();
                }
                cleanedEvaluationFiles.add(line);
            }
            else if ( !line.trim().equals("") ) cleanedEvaluationFiles.add(line);
        }
        
        // now we can create the mapping
        Map<Triple,String> tripleToSentences = new HashMap<Triple,String>();
        Iterator<String> evalIterater = cleanedEvaluationFiles.iterator();
        
        String currentUri = "";
        
        while (evalIterater.hasNext() ) {
            
            String firstLine = evalIterater.next();
            
            if ( firstLine.startsWith("http://") ) { 
                
                currentUri = firstLine; 
                firstLine = evalIterater.next();
            }
            
            String secondLine = evalIterater.next(); 
            
            if ( secondLine.startsWith("[x]") ) {
                
                firstLine = firstLine.substring(firstLine.indexOf(".") + 1);
                String[] parts          = StringUtils.substringsBetween(firstLine, "[", "]");
                String[] subjectObject  = parts[2].split(",");
                
                String subject = subjectObject[0];
                String object = subjectObject[1];
                
                Triple t = new Triple();
                t.setSubject(new Resource(subject));
                t.setProperty(new Property(currentUri));
                t.setObject(new Resource(object));
                
                tripleToSentences.put(t, secondLine.substring(4)); // always cut "[x] "
            }
        }
        return tripleToSentences;
    }
}
