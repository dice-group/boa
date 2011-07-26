package de.uni_leipzig.simba.boa.backend.nlp.learn;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.util.SerializationUtil;
import de.uni_leipzig.simba.boa.backend.util.rdf.ClassIndexer;

/**
 * 
 * @author Daniel Gerber
 */
public class NamedEntityRecognizerLearner {

	private final NLPediaLogger logger = new NLPediaLogger(NamedEntityRecognizerLearner.class);

	private Map<Integer, String> sentences = new HashMap<Integer, String>();
	private QueryParser exactMatchParser = new QueryParser(Version.LUCENE_30, "sentence", new SimpleAnalyzer());
	private Directory index = null;
	private IndexSearcher indexSearcher = null;
	private ClassIndexer indexer = null;

	private static String PREFIX = NLPediaSettings.getInstance().getSetting("learnPrefix");

	private String pathToTrainedSentenceFile = PREFIX + "trained_sentences.txt";
	private String pathToLabelsFile = PREFIX + "labels_en.nt";
	private String pathToTypesFile = PREFIX + "instance_types_en.nt";
	private String pathToDBpediaOntology = PREFIX + "dbpedia_3.6.owl";
	private String pathToSerializedFile = PREFIX + "knowledge.ser";

	private int maxNumberOfDocuments = Integer.valueOf(NLPediaSettings.getInstance().getSetting("maxNumberOfLearnSentences"));

	/**
	 * @param args
	 */
	public void learn() {

		long start = new Date().getTime();
		System.out.print("Reading ontology ... ");
		OntModel ontModel = ModelFactory.createOntologyModel();
		InputStream in = FileManager.get().open(pathToDBpediaOntology);
		ontModel.read(in, "");
		indexer = new ClassIndexer();
		indexer.index(ontModel);
		System.out.print("DONE in " + (new Date().getTime() - start) + "ms!\n");

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
		
		SerializationUtil sUtil = new SerializationUtil();
		
		BackgroundKnowledge knowledge = null;
		
		if ( sUtil.isDeserializeable(pathToSerializedFile) ) {
			
			start = new Date().getTime();
			System.out.print("Deserialize data ... ");
			knowledge = sUtil.deserializeObject(new BackgroundKnowledge(), pathToSerializedFile);
			System.out.print("DONE in " + (new Date().getTime() - start) + "ms!\n");
		}
		else {
			
			knowledge = new BackgroundKnowledge(pathToTypesFile, pathToLabelsFile);
			
			start = new Date().getTime();
			System.out.print("Serialize data ... ");
			sUtil.serializeObject(knowledge, pathToSerializedFile);
			System.out.print("DONE in " + (new Date().getTime() - start) + "ms!\n");
		}
		
		System.out.println("There are " + knowledge.getLabels().size() + " labels to search.");
		System.out.println("There are " + knowledge.getTypes().size() + " types to search.");
		System.out.println("The maximum number of sentences is " + this.maxNumberOfDocuments);

		int n = 0;
		
		Map<String,Set<String>> types = knowledge.getTypes();
		Map<String,String> labels = knowledge.getLabels();
		
		// go through each type since not all labels have types
		for ( Entry<String,Set<String>> entry : types.entrySet() ) {
				
			try {
				
				if (n++ % 1000 == 0 ) {
					
					this.printProgBar((int) ( ( ((double) n) / ((double)types.size() ) ) * 100));
					this.logger.info("After "+ n + " label searches are " + this.sentences.size() + " trained sentences in the list!");
				}

				String uri = entry.getKey();
				Set<String> type = entry.getValue();
				
				if (uri != null & type != null) {

					String label = labels.get(uri);
					
					if ( label != null  && !label.isEmpty() ) {
						
						// find all sentences containing the label
						Map<Integer, String> sentencesContainingLabels = getSentencesContainingLabel(label);
						
						String typeReplacement = getTypeForUri(uri, type);
						typeReplacement = typeReplacement.replace("http://dbpedia.org/ontology/", "");

						String[] tokensOfLabel = label.split(" ");

						for (Entry<Integer, String> sent : sentencesContainingLabels.entrySet()) {

							int indexId = sent.getKey();
							// if the sentence was already found in the sentence
							// list, take it from there else use it untagged
							// from the index
							String sentence = sent.getValue();
							if (sentences.containsKey(indexId)) sentences.get(indexId);

							String replacement = "";
							for (int i = 0; i < tokensOfLabel.length; i++) {
								
								replacement += tokensOfLabel[i] + "___" + typeReplacement + " ";
							}
							
							// replace the whole label with the complete replacement
							sentences.put(indexId, sentence.replace(label, replacement));
						}
					}
				}
			}
			catch (Exception e) {

				logger.error("Something went wrong during creation of NER training data", e);
			}
		}
		writeTrainedModelToFile();
	}

	private void writeTrainedModelToFile() {

		try {

			// write the trained model in utf8 to a file containing
			// "word \tab tag" per line
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(pathToTrainedSentenceFile)), "UTF-8"));

			System.out.println("\nWriting " + sentences.values().size() + " sentences to file!");

			for (String sentence : sentences.values()) {

				for (String token : sentence.split(" ")) {

					if (!token.contains("___")) {

						writer.write(token + "\t" + "O" + System.getProperty("line.separator"));
					}
					else {

						String[] tokens = token.split("___");
						if (tokens.length == 2) {
							
							writer.write(tokens[0] + "\t" + tokens[1] + System.getProperty("line.separator"));
						}
					}
				}
			}
			writer.close();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getTypeForUri(String uri, Set<String> types) {

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

	private Map<Integer, String> getSentencesContainingLabel(String label) {

		Map<Integer, String> sentencesContainingLabel = new HashMap<Integer, String>();

		try {

			ScoreDoc[] hits = indexSearcher.search(exactMatchParser.parse("\"" + QueryParser.escape(label) + "\""), null, maxNumberOfDocuments).scoreDocs;

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
	
	public void printProgBar(int percent){
        StringBuilder bar = new StringBuilder("[");

        for(int i = 0; i < 50; i++){
            if( i < (percent/2)){
                bar.append("=");
            }else if( i == (percent/2)){
                bar.append(">");
            }else{
                bar.append(" ");
            }
        }

        bar.append("]   " + percent + "%     ");
        System.out.print("\r" + bar.toString());
    }
}
