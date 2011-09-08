package de.uni_leipzig.simba.boa.backend.test.scripts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
		Directory directory = FSDirectory.open(new File("/Users/gerb/Downloads/06-09-2011/tmp/Index.wikipediaTraining.Merged.SnowballAnalyzer.DefaultSimilarity.fresh"));

		Map<String,Set<String>> urisToLabels = new TreeMap<String,Set<String>>();
		
		// create index searcher in read only mode
		IndexSearcher indexSearcher = new IndexSearcher(directory, true);
		System.out.println("Index opened!");
		
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/Users/gerb/Desktop/surface.txt"), "UTF-8"));
		BufferedWriter writer1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/Users/gerb/Desktop/1.txt"), "UTF-8"));
		
		List<String[]> relations =  RelationFinder.getRelationFromFile(NLPediaSettings.getInstance().getSetting("labelOutputFile"));
		
		int max = indexSearcher.maxDoc();
		for (int id = 0; id < max ; id++) {
			
			Set<String> surfaceNames = new HashSet<String>(); 
			Field[] sfFields = indexSearcher.doc(id).getFields("SURFACE_FORM");
			for (Field f : sfFields) {
				
				surfaceNames.add(f.stringValue());
			}
			String uri = indexSearcher.doc(id).get("URI");
			
			if ( id % 100000 == 0 ) {
			
				System.out.println("Iteration: " + id);
			}
			urisToLabels.put(uri, surfaceNames);
		}
		
		for ( Map.Entry<String,Set<String>> e : urisToLabels.entrySet() ) {
			
			for (String sss : e.getValue()) writer1.write(e.getKey() + "\t" + sss + Constants.NEW_LINE_SEPARATOR);
		}
		writer1.close();
		
		
		// URI1 ||| LABEL1 ||| PROP ||| URI2 ||| LABEL2 ||| RANGE ||| DOMAIN ||| isSubject
		for (String[] line : relations) {

			String firstUri		= line[0].replace("http://dbpedia.org/resource/", "");
			String secondUri	= line[3].replace("http://dbpedia.org/resource/", "");

			if ( urisToLabels.get(firstUri) != null ) {
				
				line[1] = line[1] + " ||| " + StringUtils.join(urisToLabels.get(firstUri), "_&_");
			}
			else {
				line[1] = line[1] + " ||| " + line[1].toLowerCase();
			}
			if ( urisToLabels.get(secondUri) != null ) {
				
				line[4] = line[4] + " ||| " + StringUtils.join(urisToLabels.get(secondUri), "_&_");
			}
			else {
				line[4] = line[4] + " ||| " + line[4].toLowerCase();
			}
			writer.write(StringUtils.join(line, " ||| ") + Constants.NEW_LINE_SEPARATOR);
		}
		writer.close();
	}
}