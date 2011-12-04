package de.uni_leipzig.simba.boa.backend.rdf.uri.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.uri.UriRetrieval;


public class DbpediaUriRetrieval implements UriRetrieval {

	private final NLPediaLogger logger = new NLPediaLogger(DbpediaUriRetrieval.class);
	
    private CommonsHttpSolrServer server;

    public DbpediaUriRetrieval() {

    	try {
    		
			server = new CommonsHttpSolrServer("http://dbpedia.aksw.org:8080/apache-solr-3.3.0/dbpedia_resources");
			server.setRequestWriter(new BinaryRequestWriter());
		}
		catch (MalformedURLException e) {
			
			this.logger.error("CommonsHttpSolrServer could not be created", e);
		}
    }

    public String generateUri(String label)
    {
        String uri = "http://nlpedia.de/"+label.replaceAll(" ", "_");
        return uri;
    }

    public String queryIndexForUri(String label) {
        
		try {
			
			SolrQuery query = new SolrQuery("label:\""+label+"\"");
			query.addField("uri");
			QueryResponse response = server.query(query);
			SolrDocumentList docList = response.getResults();
			
			// return the first list of types
			for (SolrDocument d : docList) {
				
				String uri = (String) d.get("uri");
				return uri != null ? uri : "";
			}
		}
		catch (SolrServerException e) {
			this.logger.error("Query could not be executed for label: \"" + label + "\"", e);
		}
		return "";
    	
    }
	
	@Override
	public String getUri(String label) {
		
		String uri = queryIndexForUri(label);
		if ( uri == null || uri.isEmpty() ) {
			
			uri = generateUri(label);
		}
		return uri;
	}
	
	public static void main(String[] args) {

		DbpediaUriRetrieval r = new DbpediaUriRetrieval();
		System.out.println(r.getUri("Fox Mulder"));
	}

}
