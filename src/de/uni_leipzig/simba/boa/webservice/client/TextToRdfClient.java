package de.uni_leipzig.simba.boa.webservice.client;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * 
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public class TextToRdfClient {
    
    public static void main(String[] args) {

        TextToRdfClient client = new TextToRdfClient();
        System.out.println(client.extractTriples("Émile Zola's novel Germinal takes its name from the calendar.", 0.0, 3, false));
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
        
//        return UriBuilder.fromUri("http://localhost:8080/boa/").build();
        return UriBuilder.fromUri("http://139.18.2.164:8080/boa/").build();
    }
}
