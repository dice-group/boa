package de.uni_leipzig.simba.boa.backend.rdf.uri.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.uri.UriRetrieval;


public class DbpediaUriRetrieval implements UriRetrieval {

	private final NLPediaLogger logger = new NLPediaLogger(DbpediaUriRetrieval.class);
	
    private static final String ENDPOINT	= "http://139.18.2.164:8983/solr/dbpediaCore/select/?q=label:\"";
    private static final String POSTURL		= "\"&fl=id,score&version=2.2&indent=on&start=0&rows=";

    public HashMap<String, Double> getUri(String label, String type, int numberOfEntries) {
    	
        HashMap<String, Double> result = new HashMap<String, Double>();
        result = queryIndex(label, type, numberOfEntries);
        //found something in the index
        if(result.size() > 0) return result;
        //if nothing is found, then generate URI and put it in database
        else result.put(generateUri(label), new Double(1.0));
        return result;
    }

    public String generateUri(String label)
    {
        String uri = "http://nlpedia.de/"+label.replaceAll(" ", "_");
        return uri;
    }

    public HashMap<String, Double> queryIndex(String label, String type, int numberOfEntries) {
        
        HashMap<String, Double> result = new HashMap<String, Double>();
        if(label.length() == 0 || label == null) return result;
        // Send a GET request to the server
        try {

        	// Send data
            String urlStr = DbpediaUriRetrieval.ENDPOINT;
            if (label != null && label.length() > 0) {
                urlStr += label;
            }
            urlStr += DbpediaUriRetrieval.POSTURL + numberOfEntries;
            URL url = new URL(urlStr);
            logger.info("Sending query "+urlStr);
            URLConnection conn = url.openConnection();
            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String uriTag = "<str name=\"id\">";
            String scoreTag = "<float name=\"score\">";
            String docTag = "<doc>", uri=null;
            double score = 0;
            
            while ((line = rd.readLine()) != null) {

                //logger.info(line);
                line = line.trim();
                if (line.startsWith(uriTag)) {
                    uri = line.substring(15, line.indexOf("</str>"));
                }
                else if (line.startsWith(scoreTag)) {
                    score = (Double.parseDouble(line.substring(scoreTag.length(), line.indexOf("</float>")))*100);
                }
                else if (line.startsWith(docTag)) {
                    if(uri!=null) result.put(uri, score);
                    uri = null;
                    score = 0;
                }
            }
            if(uri!=null) result.put(uri, score);
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println("Result = " + result);
        return result;
    }
	
	@Override
	public String getUri(String label) {

		try {
			
			label = URLEncoder.encode(label, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for ( Entry<String,Double> entry: this.getUri(label, "", 1).entrySet() ){
			
			return entry.getKey();
		}
		return "";
	}

}
