package de.uni_leipzig.simba.boa.backend.util;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.util.json.JSONArray;
import de.uni_leipzig.simba.boa.backend.util.json.JSONObject;

/**
 * 
 * @author Daniel Gerber <mailto:daniel.gerber@me.com>
 */
public class GoogleQuery {

	/**
	 * This methods returns the top n results for a given query from google.
	 * The input terms do get url encoded. If no query is provided or no 
	 * results are found NULL is returned.
	 * 
	 * @param query - the query to send to google
	 * @param httpReferer - the referer (your website...)
	 * @param limitResults - the maximum number of returned results
	 * @return a map of urls -> page title or NULL if nothing was found
	 */
	public static Map<String,String> queryGoogle(String query, String httpReferer, int limitResults) {
		
		Map<String,String> results = null;
		
		if ( !query.isEmpty() ) {
			
			try {
				
				// Convert spaces to +, etc. to make a valid URL
				query = URLEncoder.encode(query, "UTF-8");

				URL url = new URL("http://ajax.googleapis.com/ajax/services/search/web?start=0&rsz=large&v=1.0&q=" + query);
				URLConnection connection = url.openConnection();
				if ( !httpReferer.isEmpty() ) connection.addRequestProperty("Referer", httpReferer); 

				// Get the JSON response
				String line;
				StringBuilder builder = new StringBuilder();
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

				while ((line = reader.readLine()) != null) {

					builder.append(line);
				}

				JSONObject json = new JSONObject(builder.toString());

				if (json.getJSONObject("responseData") instanceof JSONObject ) {
					
					JSONArray ja = json.getJSONObject("responseData").getJSONArray("results");

					if (ja.length() > 0 ) {
						
						results = new HashMap<String,String>();
						
						for (int i = 0; i < ja.length() && i < limitResults; i++) {
							
							JSONObject j = ja.getJSONObject(i);
							
							results.put(j.getString("url"), j.getString("titleNoFormatting"));
						}
					}
				}
			}
			catch (Exception e) {
				
				e.printStackTrace();
			}
		}
		return results;
	}
}
