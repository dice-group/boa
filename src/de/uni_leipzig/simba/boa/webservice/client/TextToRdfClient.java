package de.uni_leipzig.simba.boa.webservice.client;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;

/**
 * 
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public class TextToRdfClient {
    
    public static void main(String[] args) {

        TextToRdfClient client = new TextToRdfClient();
        System.out.println(client.extractTriples("At the awards ceremony, ``The Simpsons'' creator Matt Groening is shown in the audience.", 0.0, 3, false));
    }
    
    public String extractTriples(String text, double patternScoreThreshold, int contextLookAheadThreshold, boolean dbpediaLinksOnly) {
    
        WebResource resource = Client.create().resource(getBaseURI());
        return resource.path("text2rdf").
                queryParam("text", text).
                queryParam("patternScoreThreshold", String.valueOf(patternScoreThreshold)).
                queryParam("contextLookAheadThreshold", String.valueOf(contextLookAheadThreshold)).
                queryParam("dbpediaLinksOnly", String.valueOf(dbpediaLinksOnly)).
                accept("text/n3").get(String.class);
    }
    
    private static URI getBaseURI() {
        
        return UriBuilder.fromUri(NLPediaSettings.getSetting("ipAndPort")).build();
    }
}
