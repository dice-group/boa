package de.uni_leipzig.simba.boa.webservice.server;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
    private static final Set<PatternMapping> mappings = PatternMappingManager.getInstance().getPatternMappings();
    
    @POST
    @Produces("text/n3")
    public String textToRdf (
            @FormParam("text") String text,
            @FormParam("patternMappingUri") String patternMappingUri,
            @FormParam("patternScoreThreshold") double patternScoreThreshold,
            @FormParam("contextLookAheadThreshold") int contextLookAheadThreshold,
            @FormParam("dbpediaLinksOnly") boolean dbpediaLinksOnly) {
        
        try {
            
            logger.info("Trying to extract triples with patternThreshold(" + patternScoreThreshold + ") and contextLookAheadThreshold(" + contextLookAheadThreshold + ") for text: " + text);
            
            NLPediaSettings.setSetting("pattern.score.threshold.create.knowledge", String.valueOf(patternScoreThreshold));
            NLPediaSettings.setSetting("contextLookAhead", String.valueOf(contextLookAheadThreshold));
            NLPediaSettings.setSetting("triple.score.threshold.create.knowledge", "0.0");
            NLPediaSettings.setSetting("number.of.create.knowledge.threads", "1");
            NLPediaSettings.setSetting("knowledgeCreationThreadPoolSize", "1");
            
            Set<PatternMapping> mappings = null;
            if ( !patternMappingUri.isEmpty() ) {
                
                mappings = new HashSet<PatternMapping>();
                for (PatternMapping mapping : TextToRdfWebService.mappings ) if (mapping.getProperty().getUri().equals(patternMappingUri) ) mappings.add(mapping);
            }

            Set<String> results = new TreeSet<String>();
            
            try {
                
                for (Triple triple : EvaluationManager.loadBoaResults(
                        EvaluationIndexCreator.createGoldStandardIndex(new HashSet<String>(sbd.getSentences(URLDecoder.decode(text, "UTF-8")))), 
                        mappings == null ? TextToRdfWebService.mappings : mappings)) {
                    
                    results.add(triple.toN3());
                    if ( !dbpediaLinksOnly ) {
                        
                        results.add("<" + triple.getSubject().getUri() + "> <http://www.w3.org/2000/01/rdf-schema#label> \"" + triple.getSubject().getLabel() + "\"@en ." );
                        results.add("<" + triple.getSubject().getUri() + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <" + triple.getProperty().getRdfsDomain() + "> ." );
                        
                        results.add("<" + triple.getObject().getUri() + "> <http://www.w3.org/2000/01/rdf-schema#label> \"" + triple.getObject().getLabel() + "\"@en ." );
                        results.add("<" + triple.getObject().getUri() + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <" + triple.getProperty().getRdfsRange() + "> ." );
                    }
                }
            }
            catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
                
            return StringUtils.join(results, Constants.NEW_LINE_SEPARATOR);
            
        }
        catch  (Exception e ) {
            
            logger.error("error", e);
        }
        
        return "";
    }
}
