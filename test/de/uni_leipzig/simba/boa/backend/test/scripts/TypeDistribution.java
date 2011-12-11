package de.uni_leipzig.simba.boa.backend.test.scripts;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import sun.tools.tree.ThisExpression;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.util.rdf.ClassIndexer;


public class TypeDistribution {

	private static ClassIndexer indexer = new ClassIndexer();
	private static TreeMap<String, Set<String>> types = new TreeMap<String,Set<String>>(); 
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// read the dbpedia ontology and index it
		OntModel ontModel = ModelFactory.createOntologyModel();
		InputStream inStream = FileManager.get().open("/Users/gerb/Development/workspaces/experimental/nlpedia/en_wiki/learn/dbpedia_3.6.owl");
		ontModel.read(inStream, "");
		indexer.index(ontModel);
		System.out.println("indexing done...");
		
		Map<Integer,Pair> distribution = new HashMap<Integer,Pair>();
		readRdfTypes("/Users/gerb/Development/workspaces/experimental/nlpedia/en_wiki/learn/instance_types_en.nt");
		System.out.println("reading rdf types");
		for (Map.Entry<String, Set<String>> entry : types.entrySet()) {
			
			String deepestTypeMatchUri = getTypeForUri(entry.getKey(), entry.getValue());
			
			if ( distribution.containsKey(deepestTypeMatchUri.hashCode())) {
				
				distribution.put(deepestTypeMatchUri.hashCode(), distribution.get(deepestTypeMatchUri.hashCode()).increaseCounter() );
			}
			else {
				
				Pair p = new Pair(deepestTypeMatchUri);
				distribution.put(deepestTypeMatchUri.hashCode(), p);
			}
		}
		System.out.println("sorting and printing");
		List<Pair> pairs = new ArrayList<Pair>(distribution.values());
		
		Collections.sort(pairs, new Comparator<Pair>() {

			@Override
			public int compare(Pair arg0, Pair arg1) {

				return arg0.counter - arg1.counter;
			}
		});
		
		try {
			
			OutputStream stream = new FileOutputStream("/Users/gerb/test.txt");
			
			for (Pair p : pairs) {
				
				String output = p.uri + ": " + p.counter + Constants.NEW_LINE_SEPARATOR;
				stream.write(output.getBytes());
			}
		}
		catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static class Pair {
		
		public int counter = 1;
		public String uri = "";
		
		public Pair(String uri) {
			
			this.uri = uri;
		}
		
		public Pair increaseCounter() {
			
			this.counter++;
			return this;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {

			return uri.hashCode();
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {

			return this.uri == ((Pair) obj).uri;
		}
	}
	
	private static void readRdfTypes(String pathToTypesFile) {

		try {

			BufferedReader in = new BufferedReader(new FileReader(pathToTypesFile));

			String line = "";

			while ((line = in.readLine()) != null) {

				String[] lineParts = line.split(" ");

				String uri = lineParts[0].replaceAll("<", "").replaceAll(">", "");
				String type = lineParts[2].replaceAll("<", "").replaceAll(">", "");

				if (!type.equals("http://www.w3.org/2002/07/owl#Thing")) {

					if (types.get(uri) == null) {

						Set<String> set = new HashSet<String>();
						set.add(type);

						types.put(uri, set);
					}
					else {

						Set<String> set = types.get(uri);
						set.add(type);

						types.put(uri, set);
					}
				}
			}
		}
		catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static String getTypeForUri(String uri, Set<String> types) {

		Map<String, Integer> depth = new HashMap<String, Integer>();

		// calculate the depth for all types in the ontology
		for (String type : types) {

			depth.put(type, new Long(indexer.getHierarchyForClassURI(type).size()).intValue());
		}

		String currentUri = "";
		int biggestCount = 0;

		for (Entry<String, Integer> entry : depth.entrySet()) {

			if (entry.getValue() > biggestCount) {

				biggestCount = entry.getValue();
				currentUri = entry.getKey();
			}
			else
				if (entry.getValue() == biggestCount) {

					System.out.println("multiple deepest types were found for uri: " + uri);
				}
		}

		return currentUri;
	}
}
