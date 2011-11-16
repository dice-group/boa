package de.uni_leipzig.simba.boa.backend.configuration.command.impl.scripts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jsoup.Jsoup;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;


public class KeyphraseMetricCommand implements Command {
	

	public static void main(String[] args) throws IOException {

		KeyphraseMetricCommand km = new KeyphraseMetricCommand();
	}
	
	@Override
	public void execute() {

		System.out.println("Executing " + this.getClass().getName());
		String wikipediaPath = "/home/gerber/nlpedia-data/dumps/AA";
		List<File> files = new ArrayList<File>(FileUtils.listFiles(new File(wikipediaPath), HiddenFileFilter.VISIBLE, TrueFileFilter.INSTANCE));  
		System.out.println("Found " + files.size() + " files.");
		
		List<Thread> threadList = new ArrayList<Thread>();
		
		for (int i = 0 ; i < files.size() ; i++ ) {
			
				try {
					
					Thread t = new AnalyzeWikipediaThread(files.get(i));
					t.setName("AnalyzeWikipediaThread-" + files.get(i).getName());
					threadList.add(i, t);
					System.out.println(t.getName() + " started!");
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		for ( Thread t : threadList ) t.start();
		for ( Thread t : threadList ) {
			
			try {
				t.join();	
			}
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public KeyphraseMetricCommand() {}
	
	private class AnalyzeWikipediaThread extends Thread {
		
		private File file;
		private CommonsHttpSolrServer server = null;
		private Writer writer = null;

		public AnalyzeWikipediaThread(File file) throws IOException {
			
			this.file = file;
			
			server = new CommonsHttpSolrServer("http://dbpedia.aksw.org:8080/apache-solr-3.3.0/dbpedia_resources");
			server.setRequestWriter(new BinaryRequestWriter());
		}
		
		public void run() {
			
			try {
				
				writer = new PrintWriter(new BufferedWriter(new FileWriter(file.getParent()+ "/" + file.hashCode()+ ".txt", true)));
				
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
				List<Document> documents = new ArrayList<Document>();
				
				Document document = new Document();
				
				String line;
				while ((line = br.readLine()) != null) {

					if ( line.startsWith("<doc") ) {
						
						document.uri = line.substring(line.lastIndexOf("url=\"") + 5, line.lastIndexOf("\">"));
					}
					else if ( line.startsWith("</doc>") ) {
						// output.append(line); dont append </doc> to document
						documents.add(document);
						document = new Document();
					}
					else {
						document.text.append(line);
					}
					
					if ( documents.size() == 1 ) {
						
						writeKeyPhrasesToFile(documents, server, writer);
						documents = new ArrayList<Document>();
						writer.flush();
					}
				}
				writer.close();
			}
			catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private static class Document {
		
		private String uri = "";
		private StringBuffer text = new StringBuffer();
		private Map<String,Integer> keyphraseTypes = new HashMap<String,Integer>();
		
		private void addTypeList(List<String> keyphraseTypeUris) {
			
			for (String type : keyphraseTypeUris) {
				
				if ( keyphraseTypes.containsKey(type) ) keyphraseTypes.put(type, keyphraseTypes.get(type) + 1);
				else keyphraseTypes.put(type, 1);
			}
		}
	}
	
	private void writeKeyPhrasesToFile(List<Document> documents, CommonsHttpSolrServer server, Writer writer) throws IOException{
		
		// try to get the types for each document and save them to a file
		for (Document doc : documents) {
			
			// use fox to extract keyphrases from text and clean it from html
			Set<String> keyphraseUris = extractKeyphrases(Jsoup.parse(doc.text.toString()).text());
			
			// get the types for each tag and add them to the list, count all occurrences
			for ( String keyphraseUri : keyphraseUris) 
				doc.addTypeList(queryForTypes(keyphraseUri, server));
					
			// write the types to a file
			writer.write(doc.uri + Constants.NEW_LINE_SEPARATOR);
			for (Map.Entry<String, Integer> entry : doc.keyphraseTypes.entrySet() ) {
				
				writer.write("\t" + entry.getKey() + " " + entry.getValue() + Constants.NEW_LINE_SEPARATOR);
			}
			writer.write(Constants.NEW_LINE_SEPARATOR);
		}
	}
	
	private Set<String> extractKeyphrases(String text){
		
		Set<String> result = null;
		try {
			
            // Construct data
            String data = URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode("text", "UTF-8");
            data += "&" + URLEncoder.encode("task", "UTF-8") + "=" + URLEncoder.encode("ke", "UTF-8");
            data += "&" + URLEncoder.encode("output", "UTF-8") + "=" + URLEncoder.encode("rdf", "UTF-8");
            data += "&" + URLEncoder.encode("text", "UTF-8") + "=" + URLEncoder.encode(text, "UTF-8");

            // Send data
            URL url = new URL("http://localhost:4043/api");
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer buffer = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
               buffer.append(line + Constants.NEW_LINE_SEPARATOR);
            }

            //read named entities
            result = read(buffer.toString());
            
            wr.close();
            rd.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
		return result;
	}
	
	private static TreeSet<String> read(String input) {
        
        TreeSet<String> keyphrases = new TreeSet<String>();
        Model model = ModelFactory.createDefaultModel();
        //StringReader reader = new StringReader(input);
        ByteArrayInputStream stream;
        try {
        	
            stream = new ByteArrayInputStream(input.getBytes("UTF-8"));
            model.read(stream, "", "RDF/XML");

            NodeIterator nodeIter = model.listObjectsOfProperty(model.getProperty("http://commontag.org/ns#", "means"));
 
            while (nodeIter.hasNext()) keyphrases.add(nodeIter.next().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // write it to standard out        
        return keyphrases;
    }
	
	private static List<String> queryForTypes(String uri, CommonsHttpSolrServer server) {
		
		QueryResponse response;
		try {
			
			SolrQuery query = new SolrQuery("uri:\""+uri+"\"");
			query.addField("types");
			response = server.query(query);
			SolrDocumentList docList = response.getResults();
			
			// return the first list of types
			for (SolrDocument d : docList) {
				
				List<String> types = (ArrayList<String>) d.get("types");
				return types != null ? types : new ArrayList<String>();
			}
		}
		catch (SolrServerException e) {
			e.printStackTrace();
		}
		return new ArrayList<String>();
	}
}