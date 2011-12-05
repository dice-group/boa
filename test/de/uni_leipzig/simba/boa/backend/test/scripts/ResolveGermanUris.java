package de.uni_leipzig.simba.boa.backend.test.scripts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;

import javatools.administrative.Sleep;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.crawl.RelationFinder;
import de.uni_leipzig.simba.boa.backend.rdf.store.Store;
import edu.stanford.nlp.util.StringUtils;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
import com.hp.hpl.jena.util.FileManager;

public class ResolveGermanUris {

	private Map<String, String> enUriToDeLabelMapping = new HashMap<String, String>();

//	public static void main(String[] args) throws FileNotFoundException, IOException {
//	    String fileNameOrUri = "/Users/gerb/Downloads/08-09-2011/labels_de.nt";
//	    Model model = ModelFactory.createDefaultModel();
//	    InputStream is = FileManager.get().open(fileNameOrUri);
//	    if (is != null) {
//	        model.read(is, null, "N-TRIPLE");
//	        model.write(System.out, "TURTLE");
//	    } else {
//	        System.err.println("cannot read " + fileNameOrUri);;
//	    }
//		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/Users/gerb/Downloads/08-09-2011/labels_de.nt"), "UTF-8"));
//		
//		String line;
//		while ((line = br.readLine()) != null) {
//			
//			System.out.println(line);
//		}
//		
//		br.close();
//		
//		NxParser nxp = new NxParser(new FileInputStream("/Users/gerb/Downloads/08-09-2011/labels_de.nt"),false);
//		
//		  while (nxp.hasNext()) {
//		    Node[] ns = nxp.next();
//					
//		    for (Node n: ns) {
//		      System.out.print(n.toN3());
//		      System.out.print(" ");
//		    }
//		    System.out.println(".");
//		  }
//	}
	
	
//	public static void main(String[] args) throws Exception {
//
//		NLPediaSetup setup = new NLPediaSetup(true);
//		ResolveGermanUris asd = new ResolveGermanUris();
//		asd.writeNewFile();
//		asd.transformEnglishFile();
//	}
//
//	private static void transformEnglishFile() throws IOException {
//
//		Map<String,String> urisToDeLabels = createUriToDeLabel();
//		Map<String, String> enUriToDeUri = createUriMapping();
//		
//		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/Users/gerb/de_relation_plain.txt"), "UTF-8"));
//
//		List<String[]> relations = RelationFinder.getRelationFromFile("/Users/gerb/asd");
//		
//		System.out.println("Done reading..");
//		for (String[] line : relations) {
//
//			String subjectUri = line[0];
//			String objectUri = line[3];
//			
//			if ( urisToDeLabels.get(subjectUri) != null ) {
//				
//				line[1] = urisToDeLabels.get(subjectUri);
//			}
//			if ( urisToDeLabels.get(objectUri) != null ) {
//				
//				line[3] = urisToDeLabels.get(objectUri);
//			}
//			if ( this.enUriToDeLabelMapping.get("<"+subjectUri+">") != null && this.enUriToDeLabelMapping.get("<"+objectUri+">") != null ) {
//				
//				line[1] = this.enUriToDeLabelMapping.get("<"+subjectUri+">");
//				line[4] = this.enUriToDeLabelMapping.get("<"+objectUri+">");
//				writer.write(StringUtils.join(line, " ||| ") + Constants.NEW_LINE_SEPARATOR);
//			}
//			writer.write(StringUtils.join(line, " ||| ") + Constants.NEW_LINE_SEPARATOR);
//		}
//		writer.close();
//	}
//	
//	private static Map<String,String> createUriToDeLabel() throws IOException{
//		
//		BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(new File("/Users/gerb/new_de_uri_surface_form.tsv")))));
//		
//		Map<String,String> map = new HashMap<String,String>();
//		
//		String line = "";
//		while ((line = br.readLine()) != null) {
//
//			String uri = line.substring(0, line.indexOf(" "));
//			String[] rest =  line.substring(line.indexOf(" ")+1).split("\t");
//			
//			map.put(uri, rest[0]);
//		}
//		br.close();
//		return map;
//	}
//
//	private static Map<String, String> createUriMapping() {
//
//		Map<String,String> enUriToDeUri =  new HashMap<String,String>();
//		try {
//
//			BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream("/Users/gerb/interlanguage_links_de.nt"))));
//			
//			String line;
//			while ((line = br.readLine()) != null) {
//
//				String[] lineParts = line.replace(">", "").replace("<", "").split(" ");
//
//				if (!lineParts[2].startsWith("<http://el.")) {
//					
//					enUriToDeUri.put(lineParts[2], lineParts[0]);
//				}
//			}
//			br.close();
//		}
//		catch (Exception e) {
//
//			e.printStackTrace();
//		}
//		return enUriToDeUri;
//	}
	
//		public static void main(String[] args) throws IOException {
//
//			BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(new File("/Users/gerb/en_surface.txt")))));
//			Writer writer =  new PrintWriter(new BufferedWriter(new FileWriter("/Users/gerb/de_relation_plain.txt")));
//			
//			Map<String,String> enUriToDeUri = createMapping();
//			
//			String line;
//			while ((line = br.readLine()) != null) {
//				
//				String[] lineParts = line.split(" \\|\\|\\| ");
//				
//				// 0_URI1 ||| 1_LABEL1 ||| 2_LABELS1 ||| 3_PROP ||| 4_URI2 ||| 5_LABEL2 ||| 6_LABELS2 ||| 7_RANGE ||| 8_DOMAIN
//				// to
//				// 0_URI1 ||| 1_LABEL1 ||| 2_PROP ||| 3_URI2 ||| 4_LABEL2 ||| 5_RANGE ||| 6_DOMAIN
//				lineParts = ArrayUtils.removeElement(lineParts, lineParts[2]);
//				lineParts = ArrayUtils.removeElement(lineParts, lineParts[5]);
//				
//				String sub = enUriToDeUri.get(lineParts[0]);
//				if (sub!=null) lineParts[0] = sub;  
//				String obj = enUriToDeUri.get(lineParts[3]);
//				if (obj!=null) lineParts[3] = obj;
//				
//				
//				writer.write(StringUtils.join(lineParts, " ||| ") + Constants.NEW_LINE_SEPARATOR);
//			}
//		}
	
	public static void main(String[] args) throws IOException {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(new File("/Users/gerb/surface_forms-Wikipedia-TitRedDis.tsv")))));
		Writer writer =  new PrintWriter(new BufferedWriter(new FileWriter("/Users/gerb/en_uri_to_label_mapping.tsv")));
		
		String line = "";
		while ((line = br.readLine()) != null) {

			String[] parts = line.split("\t");
			
			writer.write("http://dbpedia.org/resource/" + parts[1] + " " + parts[0] + Constants.NEW_LINE_SEPARATOR);
		}
		writer.close();
		
		br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(new File("/Users/gerb/en_uri_to_label_mapping.tsv")))));
		writer =  new PrintWriter(new BufferedWriter(new FileWriter("/Users/gerb/en_uri_surface_form.tsv")));
		
		Map<String,Set<String>> labels = new TreeMap<String,Set<String>>();
		
		line = "";
		while ((line = br.readLine()) != null) {

			String uri = line.substring(0, line.indexOf(" ")).trim();//.replace("http://dbpedia.org/resource/", "");
			String label = line.substring(line.indexOf(" ") + 1).trim();
			
			if ( labels.containsKey(uri) ) labels.get(uri).add(label);
			else {
				
				Set<String> newLabels = new TreeSet<String>();
				newLabels.add(label);
				labels.put(uri, newLabels);
			}
		}
		System.out.println("Found uris: " + labels.size());
		int max = 0, numberOfLabels = 0;
		for ( Map.Entry<String,Set<String>> entry : labels.entrySet()) {
			
//			if ( entry.getKey().equals("http://dbpedia.org/resource/The")) continue; // over 100k surface forms...
//			if ( entry.getKey().equals("http://dbpedia.org/resource/List")) continue; // over 100k surface forms...
			
			numberOfLabels += entry.getValue().size();
			max = Math.max(max, entry.getValue().size());
			
			if ( entry.getValue().size() >= 100 ) {
				
				System.out.println(entry.getKey() + ": " + entry.getValue().size());
				System.out.println(new ArrayList<String>(entry.getValue()).subList(0, 99));
			}
		}
		System.out.println("Average of Surfaceforms: " + ((double)numberOfLabels / (double)labels.size()));
		System.out.println("Maximum of Surfaceforms: " + max);
		System.out.println("NUmber  of Surfaceforms: " + numberOfLabels);
		
		for (Map.Entry<String, Set<String>> entry : labels.entrySet()) {
			
			writer.write(entry.getKey() + "\t" + StringUtils.join(entry.getValue(), "\t") + Constants.NEW_LINE_SEPARATOR);
		}
		
		writer.close();
		br.close();
	}
}
