package de.uni_leipzig.simba.boa.backend.test.scripts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.crawl.RelationFinder;
import edu.stanford.nlp.util.StringUtils;

/**
 * This thing needs at least 4GB of RAM.
 * 
 * 
 * @author gerb
 */
public class CreateSurfaceFormList {
	
	public static void main(String[] args) throws IOException {

		NLPediaSetup s = new NLPediaSetup(true);
		Map<String,Set<String>> urisToLabels = getSurfaceForms();
//		printSurfaceFormsToFile(urisToLabels);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream("/Users/gerb/en.txt"))));
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/Users/gerb/en_surface.txt"), "UTF-8"));
		
		String line;
		while ((line = br.readLine()) != null) {
		
			String[] lineParts = line.split(" \\|\\|\\| ");
			
			String firstUri		= lineParts[0];
			String secondUri	= lineParts[3];

			if ( urisToLabels.get(firstUri) != null ) {
				
				lineParts[1] = lineParts[1] + " ||| " + StringUtils.join(urisToLabels.get(firstUri), "_&_");
			}
			else {
				lineParts[1] = lineParts[1] + " ||| " + lineParts[1].toLowerCase();
			}
			if ( urisToLabels.get(secondUri) != null ) {
				
				lineParts[4] = lineParts[4] + " ||| " + StringUtils.join(urisToLabels.get(secondUri), "_&_");
			}
			else {
				lineParts[4] = lineParts[4] + " ||| " + lineParts[4].toLowerCase();
			}
			writer.write(StringUtils.join(lineParts, " ||| ") + Constants.NEW_LINE_SEPARATOR);
		}
		writer.close();
	}
	
	private static void printSurfaceFormsToFile(Map<String, Set<String>> urisToLabels ) throws IOException {
		
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/Users/gerb/uriToSurfaceForm.txt"), "UTF-8"));
		for ( Map.Entry<String, Set<String>> entry : urisToLabels.entrySet() ) {
			
			writer.write(entry.getKey() + "\t" + StringUtils.join(entry.getValue(), "\t") + Constants.NEW_LINE_SEPARATOR);
		}
		writer.close();
	}
	
	private static Map<String,Set<String>> getSurfaceForms() throws IOException {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream("/Users/gerb/surface_forms-Wikipedia-TitRedDis.tsv"))));
		
		Map<String,Set<String>> uriToLabels = new HashMap<String,Set<String>>();
		
		String line = "";
		while ( (line = br.readLine()) != null ) {
			
			String[] lineParts = line.split("\t");
			if ( lineParts.length > 2 ) System.out.println("Something wrong, more then 2 parts per line + " + line);
			
			if ( uriToLabels.containsKey("http://dbpedia.org/resource/"+ lineParts[1]) ) {
				
				uriToLabels.get("http://dbpedia.org/resource/"+ lineParts[1]).add(lineParts[0]);
			}
			else {
				
				Set<String> labels = new HashSet<String>();
				labels.add(lineParts[0]);
				uriToLabels.put("http://dbpedia.org/resource/"+ lineParts[1], labels);
			}
		}
		br.close();
		return uriToLabels;
	}
	
	private void createMappings() {
		
//		Directory directory = FSDirectory.open(new File("/Users/gerb/Downloads/06-09-2011/tmp/Index.wikipediaTraining.Merged.SnowballAnalyzer.DefaultSimilarity.fresh"));
//		// create index searcher in read only mode
//		IndexSearcher indexSearcher = new IndexSearcher(directory, true);
//		System.out.println("Index opened!");
//		
//		BufferedWriter writer1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/Users/gerb/Desktop/uri_to_surface_form_en.txt"), "UTF-8"));
//		
//		int max = indexSearcher.maxDoc();
//		for (int id = 0; id < max ; id++) {
//			
//			Set<String> surfaceNames = new HashSet<String>(); 
//			Field[] sfFields = indexSearcher.doc(id).getFields("SURFACE_FORM");
//			for (Field f : sfFields) {
//				
//				surfaceNames.add(f.stringValue());
//			}
//			String uri = indexSearcher.doc(id).get("URI");
//			
//			if ( id % 100000 == 0 ) {
//			
//				System.out.println("Iteration: " + id);
//			}
//			urisToLabels.put(uri, surfaceNames);
//		}
//		
//		for ( Map.Entry<String,Set<String>> e : urisToLabels.entrySet() ) {
//			
//			for (String sss : e.getValue()) writer1.write(e.getKey() + "\t" + sss + Constants.NEW_LINE_SEPARATOR);
//		}
//		writer1.close();

	}
}