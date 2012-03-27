package de.uni_leipzig.simba.boa.webservice.server;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import de.danielgerber.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.serialization.PatternMappingManager;
import de.uni_leipzig.simba.boa.backend.evaluation.EvaluationIndexCreator;
import de.uni_leipzig.simba.boa.backend.evaluation.EvaluationManager;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.NaturalLanguageProcessingToolFactory;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.sentenceboundarydisambiguation.SentenceBoundaryDisambiguation;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;
import edu.stanford.nlp.util.StringUtils;

@Path("")
public class TextToRdfWebService {
   
    private static final NLPediaSetup setup = new NLPediaSetup(false);
    private static final NLPediaLogger logger = new NLPediaLogger(TextToRdfWebService.class); 
    private static final SentenceBoundaryDisambiguation sbd = NaturalLanguageProcessingToolFactory.getInstance().createDefaultSentenceBoundaryDisambiguation();
    private static final List<PatternMapping> mappings = PatternMappingManager.getInstance().getPatternMappings();
    
    @GET
    @Produces("text/n3")
    public String textToRdf (
            @QueryParam("text") String text,
            @QueryParam("patternScoreThreshold") double patternScoreThreshold,
            @QueryParam("contextLookAheadThreshold") int contextLookAheadThreshold,
            @QueryParam("dbpediaLinksOnly") boolean dbpediaLinksOnly) {
        
        logger.info("Trying to extract triples with patternThreshold(" + patternScoreThreshold + ") and contextLookAheadThreshold(" + contextLookAheadThreshold + ") for text: " + text);
        
        NLPediaSettings.setSetting("pattern.score.threshold.create.knowledge", String.valueOf(patternScoreThreshold));
        NLPediaSettings.setSetting("contextLookAhead", String.valueOf(contextLookAheadThreshold));
        NLPediaSettings.setSetting("triple.score.threshold.create.knowledge", "0.0");
        NLPediaSettings.setSetting("number.of.create.knowledge.threads", "1");
        NLPediaSettings.setSetting("knowledgeCreationThreadPoolSize", "1");

        Set<String> results = new TreeSet<String>();
        
        for (Triple triple : EvaluationManager.loadBoaResults(EvaluationIndexCreator.createGoldStandardIndex(new HashSet<String>(sbd.getSentences(text))), new HashSet<PatternMapping>(mappings))) {
            
            results.add(triple.toN3());
            if ( !dbpediaLinksOnly ) {
                
                results.add("<" + triple.getSubject().getUri() + "> <http://www.w3.org/2000/01/rdf-schema#label> \"" + triple.getSubject().getLabel() + "\"@en ." );
                results.add("<" + triple.getSubject().getUri() + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <" + triple.getProperty().getRdfsDomain() + "> ." );
                
                results.add("<" + triple.getObject().getUri() + "> <http://www.w3.org/2000/01/rdf-schema#label> \"" + triple.getObject().getLabel() + "\"@en ." );
                results.add("<" + triple.getObject().getUri() + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <" + triple.getProperty().getRdfsRange() + "> ." );
            }
        }
            
        return StringUtils.join(results, Constants.NEW_LINE_SEPARATOR);
    }
}
