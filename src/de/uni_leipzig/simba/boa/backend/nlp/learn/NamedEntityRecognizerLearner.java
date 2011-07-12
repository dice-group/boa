package de.uni_leipzig.simba.boa.backend.nlp.learn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter; 
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.util.rdf.ClassIndexer;

/**
 * 
 * @author Daniel Gerber
 */
public class NamedEntityRecognizerLearner {

	private Map<Integer,String> sentences = new HashMap<Integer,String>();
	private QueryParser exactMatchParser = new QueryParser(Version.LUCENE_30, "sentence", new SimpleAnalyzer());
	private Directory index = null;
	private IndexSearcher indexSearcher = null;
	private ClassIndexer indexer = null;
	private String pathToTrainedSentenceFile = "";
	private String pathToLabelsFile = "";
	private String pathToTypesFile = "";
	private String pathToDBpediaOntology = "";
	private int maxNumberOfDocuments =  Integer.valueOf(NLPediaSettings.getInstance().getSetting("maxNumberOfDocuments"));
	
	private Map<String,String> labels = null; 	
	private Map<String,Set<String>> types = null;
	
	/**
	 * @param args
	 */
	public void learn() {
		
		OntModel ontModel = ModelFactory.createOntologyModel();
		InputStream in = FileManager.get().open(pathToDBpediaOntology);
		ontModel.read(in, ""); 
		
		indexer = new ClassIndexer();
		indexer.index(ontModel);
		
		try {
			
			index = FSDirectory.open(new File(NLPediaSettings.getInstance().getSetting("sentenceIndexDirectory")));
			indexSearcher = new IndexSearcher(index, true);
		}
		catch (CorruptIndexException cie) {
			
			cie.printStackTrace();
		}
		catch (IOException ioe) {
			
			ioe.printStackTrace();
		}

		// read the labels with uris from the ntriples file
		this.labels = readLabels();
		// read the types (multiple) for each resource from the n triples file 
		this.types	= readRdfTypes();
		
		System.out.println("There are " + labels.size() + " labels to search.");
		System.out.println("They maximum number of sentences is " + this.maxNumberOfDocuments);
		
		// go through each label
		for ( Entry<String,String> entry : labels.entrySet() ) {
			
			String uri = entry.getKey();
			String label = entry.getValue();
			
			// find all sentences containing the label
			Map<Integer,String> sentencesContainingLabels = getSentencesContainingLabel(label);
			System.out.println("Found " + sentencesContainingLabels.size() + " sentences containing label: " + label);
			
			// get the most precise type definition for an URI
			String typReplacement = getTypeForUri(uri, types.get(uri)).replace("http://dbpedia.org/ontology/", "");
			String[] tokensOfLabel = label.split(" ");
			
			for ( Entry<Integer,String> sent : sentencesContainingLabels.entrySet() ) {
				
				int indexId = sent.getKey();
				// if the sentence was already found in the sentence list take it from there else use it untagged from the index
				String sentence = sent.getValue();
				if ( sentences.containsKey(indexId) ) sentences.get(indexId);
				
				// replace the first token of the label with _B to show that this is the beginning
				for (int i = 0 ; i < tokensOfLabel.length ; i++) {
					
					if ( i == 0 ) {
						
						sentence = sentence.replaceAll("(?i)" + tokensOfLabel[i], tokensOfLabel[i] + "_B-" + typReplacement);
					}
					else {
						
						sentence = sentence.replaceAll("(?i)" + tokensOfLabel[i], tokensOfLabel[i] + "_I-" + typReplacement);
					}
				}
				sentences.put(indexId, sentence);
			}
		}
 		writeTrainedModelToFile();
	}
	
	private void writeTrainedModelToFile() {

		try {
			
			// write the trained model in utf8 to a file containing "word \tab tag" per line
			BufferedWriter writer	= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(pathToTrainedSentenceFile)), "UTF-8"));
			
			for (String sentence : sentences.values() ) {
				
				writer.write(sentence + Constants.NEW_LINE_SEPARATOR);
			}
			writer.close();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getTypeForUri(String uri, Set<String> types) {

		Map<String,Integer> depth = new HashMap<String,Integer>();
		
		// calculate the depth for all types in the ontology
		for (String type : types) {
			
			System.out.println(type);
			depth.put(type, new Long(indexer.getHierarchyForClassURI(type).size()).intValue());
		}
		
		String currentUri = "";
		int biggestCount = 0;
		
		for (Entry<String,Integer> entry : depth.entrySet()) {
		
			if ( entry.getValue() > biggestCount ) {
				
				biggestCount = entry.getValue();
				currentUri = entry.getKey();
			}
			if ( entry.getValue() == biggestCount ) {
				
				System.out.println("multiple deepest types were found for uri: " + uri);
			}
		}
		
		return currentUri;
	}

	private Map<Integer, String> getSentencesContainingLabel(String label) {

		Map<Integer,String> sentencesContainingLabel = new HashMap<Integer,String>();
		
		try {
			
			ScoreDoc[] hits = indexSearcher.search(exactMatchParser.parse("\""+QueryParser.escape(label)+"\""), null, maxNumberOfDocuments).scoreDocs;

//			if ( hits.length >= n ) System.out.println("Found " + hits.length + " documents for label: \"" + label + "\"");
			
			for (int i = hits.length - 1; i >= 0; i--) {
				
				sentencesContainingLabel.put(hits[i].doc, indexSearcher.doc(hits[i].doc).get("sentence"));
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sentencesContainingLabel;
	}

	/**
	 * @return the english labels for each instance
	 */
	private Map<String,String> readLabels() {
		
		long start = new Date().getTime();

		Map<String,String> labels = new HashMap<String,String>();
		
		try {
			
			BufferedReader in = new BufferedReader(new FileReader(pathToLabelsFile));
			
			String line = ""; 
			
			while ( (line = in.readLine()) != null ) {
				
				String[] lineParts = line.split(">");
				
				String uri		= lineParts[0].replaceAll("<", "").replaceAll(">", "").trim();
				String label	= lineParts[2].replaceAll("\"@en", "");
				label = label.substring(0,label.length() - 1).replaceAll("\"", "");
				
				labels.put(uri, label);
			}
		}
		catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Label file read in " + (new Date().getTime() - start) + "ms.");
		System.out.println("Checking labels for validity!");
		this.checkLabels();
		
		return labels;
	}

	private boolean checkLabels() {

		for ( Entry<String, String> entry : this.labels.entrySet()) {
			
			String uri = entry.getKey();
			String label = entry.getValue();
			
			if ( uri.equals("") || label.equals("") || !uri.startsWith("http://dbpedia.org/ontology/") ) {
				
				System.out.println("\""+uri+"\": " + label + " is not correct!");
			}
		}
		return true;
	}

	/**
	 * returns a map mapping the uri to the various rdf:type definitions of it
	 * 
	 * @return
	 */
	private Map<String,Set<String>> readRdfTypes() {

		long start = new Date().getTime();
		
		Map<String,Set<String>> types = new HashMap<String,Set<String>>();
		
		try {
			
			BufferedReader in = new BufferedReader(new FileReader(pathToTypesFile));
			
			String line = ""; 
			
			while ( (line = in.readLine()) != null ) {
				
				String[] lineParts = line.split(" ");
				
				String uri	= lineParts[0].replaceAll("<", "").replaceAll(">", "");
				String type = lineParts[2].replaceAll("<", "").replaceAll(">", "");
				
				if ( !type.equals("http://www.w3.org/2002/07/owl#Thing") ) {
					
					if ( types.get(uri) == null ) {
						
						Set<String> set = new HashSet<String>();
						set.add(type);
						
						types.put(uri, set);
					}
					else {
						
						types.get(uri).add(type);
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
		System.out.println("Types file read in " + (new Date().getTime() - start) + "ms.");
		System.out.println("Checking types for validity!");
		this.checkTypes();
		
		return types;
	}
	
	private boolean checkTypes() {

		for ( Entry<String, Set<String>> entry : this.types.entrySet()) {
			
			String uri = entry.getKey();
			Set<String> types = entry.getValue();
			
			if ( uri.equals("") || types == null || types.size() == 0 || !uri.startsWith("http://dbpedia.org/ontology/") ) {
				
				System.out.println("\""+uri+"\": types is not correct!");
			}
		}
		return true;
	}
	
	private Map<String, Set<String>> readRdfTypesTEST() {

		Map<String, Set<String>> typeForUris = new HashMap<String,Set<String>>();
		Set<String> types = new HashSet<String>();
		types.add("http://dbpedia.org/ontology/City");
		types.add("http://dbpedia.org/ontology/PopulatedPlace");
		typeForUris.put("http://dbpedia.org/resource/Atlantic_Ocean", types);
		types = new HashSet<String>();
		types.add("http://dbpedia.org/ontology/Person");
		types.add("http://dbpedia.org/ontology/Actor");
		typeForUris.put("http://dbpedia.org/resource/Albert_Einstein", types);
		return typeForUris;
	}

	private Map<String, String> readLabelsTEST() {
		
		Map<String, String> labels = new HashMap<String,String>();
		labels.put("http://dbpedia.org/resource/Albert_Einstein", "Albert Einstein");
		labels.put("http://dbpedia.org/resource/Atlantic_Ocean", "Atlantic Ocean");
		return labels;
	}

	public void setPathToTrainedSentenceFile(String pathToTrainedSentenceFile) {

		this.pathToTrainedSentenceFile = pathToTrainedSentenceFile;
	}

	public void setPathToDBpediaOntology(String pathToDBpediaOntology) {

		this.pathToDBpediaOntology = pathToDBpediaOntology;
	}

	public void setPathToLabelsFile(String pathToLabelsFile) {

		this.pathToLabelsFile = pathToLabelsFile;
	}

	public void setPathToTypesFile(String pathToTypesFile) {

		this.pathToTypesFile = pathToTypesFile;
	}
}
