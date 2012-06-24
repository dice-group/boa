package de.uni_leipzig.simba.boa.webservice.client;

import java.net.URI;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;

/**
 * 
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public class TextToRdfClient {
    
    public static void main(String[] args) {

        NLPediaSetup s = new NLPediaSetup(true);
        
        TextToRdfClient client = new TextToRdfClient();
        
        String text = "``Death Proof'' , written and directed by Quentin Tarantino was a very good movie.";
        
        for ( int i = 0; i < 10 ; i++) {
            
            text += text;
        }
        
        long start = System.currentTimeMillis();
        System.out.println(
                "Took " + (System.currentTimeMillis() - start) + "ms to extract: " + client.extractTriples(text, 
                        "http://dbpedia.org/ontology/director", 0.5, 1, true));
    }
    
    /**
     * 
     * 
     * @param text
     * @param patternMappingUri
     * @param patternScoreThreshold
     * @param contextLookAheadThreshold
     * @param dbpediaLinksOnly
     * @return
     */
    public String extractTriples(String text, String patternMappingUri, double patternScoreThreshold, int contextLookAheadThreshold, boolean dbpediaLinksOnly) {
    
        MultivaluedMap<String,String> data = new MultivaluedMapImpl();
        data.add("text", text);
        data.add("patternMappingUri", patternMappingUri);
        data.add("patternScoreThreshold", String.valueOf(patternScoreThreshold));
        data.add("contextLookAheadThreshold", String.valueOf(contextLookAheadThreshold));
        data.add("dbpediaLinksOnly", "true");
        
        WebResource resource = Client.create().resource(getBaseURI()).path("text2rdf");
        return resource.type("application/x-www-form-urlencoded").post(String.class, data);
    }
    
    private static URI getBaseURI() {
        
        return UriBuilder.fromUri(NLPediaSettings.getSetting("ipAndPort")).build();
    }
}
