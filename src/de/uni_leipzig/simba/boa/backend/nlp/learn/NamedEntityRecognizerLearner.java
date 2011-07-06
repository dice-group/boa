package de.uni_leipzig.simba.boa.backend.nlp.learn;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter; 
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

/**
 * 
 * @author Daniel Gerber
 */
public class NamedEntityRecognizerLearner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		NLPediaSetup setup 		= new NLPediaSetup(true);
		NLPediaLogger logger	= new NLPediaLogger(NamedEntityRecognizerLearner.class);
		
		Map<String,String> labels 		= readLabels(); 
		Map<String,Set<String>> types	= readUris();
		Set<String> sentences			= readSentences();
		
		for ( String sentence : sentences ) {
			
			for ( Entry<String,String> entry : labels.entrySet() ) {
				
				String uri = entry.getKey();
				String replacement = entry.getValue().replaceAll(" ", "_");
				
				Set<String> typesForUri = types.get(uri);
				for (String type : typesForUri) {
					
					replacement = replacement + "&&" + type;  
				}
				sentence = sentence.replaceAll(entry.getValue(), replacement);
			}
		}
		
		 try {
			 
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("/home/gerber/sentences.txt")));
			for (String sentence : sentences ) {
				
				String[] tokens = sentence.split(" ");
				for (String token : tokens) {
					
					if ( token.contains("&&") ) {
						
						token = token.replace("&&", "\t");
					}
					else {
						
						token = token + "\tO";
					}
					out.append(token + Constants.NEW_LINE_SEPARATOR);
				}
			}
			out.close();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @return the english labels for each instance
	 */
	private static Map<String,String> readLabels() {
		
		Map<String,String> labels = new HashMap<String,String>();
		
		try {
			
			BufferedReader in = new BufferedReader(new FileReader("/Users/gerb/labels_en.nt"));
			
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
		
		return labels;
	}

	/**
	 * returns a map mapping the uri to the various rdf:type definitions of it
	 * 
	 * @return
	 */
	private static Map<String,Set<String>> readUris() {

		Map<String,Set<String>> types = new HashMap<String,Set<String>>();
		
		try {
			
			BufferedReader in = new BufferedReader(new FileReader("/Users/gerb/instance_types_en.nt"));
			
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
		
		return types;
	}
	
	private static Set<String> readSentences() {

		Set<String> sentences = new HashSet<String>();
		
		try {
			
			BufferedReader in = new BufferedReader(new FileReader(NLPediaSettings.getInstance().getSetting("sentenceFileDirectory")));
			
			String line = ""; 
			while ( (line = in.readLine()) != null ) {
				
				sentences.add(line);
			}
		}
		catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sentences;
	}
}
