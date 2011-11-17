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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

	private void transformEnglishFile() throws IOException {

		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/Users/gerb/Downloads/02-09-2011/all_de.txt"), "UTF-8"));

		List<String[]> relations = RelationFinder.getRelationFromFile("");
		for (String[] line : relations) {

			String subjectUri = line[0];
			String objectUri = line[3];

			if ( this.enUriToDeLabelMapping.get("<"+subjectUri+">") != null ) {
				
				line[1] = this.enUriToDeLabelMapping.get("<"+subjectUri+">");
			}
			if ( this.enUriToDeLabelMapping.get("<"+objectUri+">") != null ) {
				
				line[3] = this.enUriToDeLabelMapping.get("<"+objectUri+">");
			}
//			if ( this.enUriToDeLabelMapping.get("<"+subjectUri+">") != null && this.enUriToDeLabelMapping.get("<"+objectUri+">") != null ) {
//				
//				line[1] = this.enUriToDeLabelMapping.get("<"+subjectUri+">");
//				line[4] = this.enUriToDeLabelMapping.get("<"+objectUri+">");
//				writer.write(StringUtils.join(line, " ||| ") + Constants.NEW_LINE_SEPARATOR);
//			}
			writer.write(StringUtils.join(line, " ||| ") + Constants.NEW_LINE_SEPARATOR);
		}
		writer.close();
	}

	private void writeNewFile() throws Exception {

		try {
			
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/Users/gerb/Downloads/02-09-2011/labels_de.nt"), "UTF-8"));
				
			String line;
			int i = 0;
			while ((line = br.readLine()) != null) {

				String[] lineParts = line.split(" ");
				
				String subject		= lineParts[0];
				
				String label;
				if ( lineParts[2].contains("\"@de") ) label = lineParts[2];
				else {
					label = lineParts[2];
					for (String s : Arrays.copyOfRange(lineParts, 3, lineParts.length) ) {
						
						if ( s.contains("\"@de") ) {
							label += " "+ s;
							break;
						}
						label += " "+ s;
					}
				}
				
				this.enUriToDeLabelMapping.put(subject, label.substring(1, label.length()-4));
			}
			br.close();
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

	private static Map<String, String> createMapping() {

		Map<String,String> enUriToDeUri =  new HashMap<String,String>();
		try {

			BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream("/Users/gerb/interlanguage_links_de.nt"))));
			
			String line;
			while ((line = br.readLine()) != null) {

				String[] lineParts = line.replace(">", "").replace("<", "").split(" ");

				if (!lineParts[2].startsWith("<http://el.")) {
					
					enUriToDeUri.put(lineParts[2], lineParts[0]);
				}
			}
			br.close();
		}
		catch (Exception e) {

			e.printStackTrace();
		}
		return enUriToDeUri;
	}
	
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
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(new File("/Users/gerb/de_uri_surface_form.tsv")))));
		Writer writer =  new PrintWriter(new BufferedWriter(new FileWriter("/Users/gerb/new_de_uri_surface_form.tsv")));
		
		String line = "";
		while ((line = br.readLine()) != null) {

			String[] lineParts = line.split("\t");
			writer.write("http://de.dbpedia.org/resource/"+lineParts[lineParts.length - 1] +" " + StringUtils.join(Arrays.copyOfRange(lineParts, 0, lineParts.length - 1),"\t") + Constants.NEW_LINE_SEPARATOR);
		}
		writer.close();
		br.close();
	}
}
