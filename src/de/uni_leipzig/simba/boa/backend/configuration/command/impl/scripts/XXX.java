package de.uni_leipzig.simba.boa.backend.configuration.command.impl.scripts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

import com.sun.tools.internal.jxc.apt.Const;

import cern.colt.Arrays;

import de.danielgerber.file.FileUtil;
import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import edu.stanford.nlp.util.StringUtils;


public class XXX implements Command {

	@Override
	public void execute() {

		try {
			getIndexedSentencesCount();
		}
		catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, IOException {

		createEvalFiles();
	}
	
	private static void createEvalFiles() throws IOException {
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream("/Users/gerb/Evaluation_2_Upmeier_1000.txt"))));
		Writer writer = new PrintWriter(new BufferedWriter(new FileWriter("/Users/gerb/Evaluation_2_Upmeier_1000.txt.1")));
		
		String line;
		int i = 1;
		while ((line = reader.readLine()) != null) {
			
			writer.write(i++ +". "+ line + Constants.NEW_LINE_SEPARATOR);
			writer.write("[]" + Constants.NEW_LINE_SEPARATOR);
			writer.write(Constants.NEW_LINE_SEPARATOR);
		}
		reader.close();
		writer.close();
	}
	
	public static void getIndexedSentencesCount() throws CorruptIndexException, IOException {
		
		String[] indexDirs = new String[]{	"/home/gerber/nlpedia-data/en_wiki_exp/index/stanfordnlp",
											"/home/gerber/nlpedia-data/en_news_exp/index/stanfordnlp",
											"/home/gerber/nlpedia-data/de_wiki_exp/index/stanfordnlp",
											"/home/gerber/nlpedia-data/de_news_exp/index/stanfordnlp"};
		String indexDir = null;
		IndexSearcher indexSearcher = null;
		
		indexDir = indexDirs[0];
		indexSearcher = new IndexSearcher(FSDirectory.open(new File(indexDir)), true);
		System.out.println("en_wiki_exp: " + indexSearcher.maxDoc() + " sentences");
		indexSearcher.close();
		
		indexDir = indexDirs[1];
		indexSearcher = new IndexSearcher(FSDirectory.open(new File(indexDir)), true);
		System.out.println("en_news_exp: " + indexSearcher.maxDoc() + " sentences");
		indexSearcher.close();
		
		indexDir = indexDirs[2];
		indexSearcher = new IndexSearcher(FSDirectory.open(new File(indexDir)), true);
		System.out.println("de_wiki_exp: " + indexSearcher.maxDoc() + " sentences");
		indexSearcher.close();
		
		indexDir = indexDirs[3];
		indexSearcher = new IndexSearcher(FSDirectory.open(new File(indexDir)), true);
		System.out.println("de_news_exp: " + indexSearcher.maxDoc() + " sentences");
		indexSearcher.close();
	}
	
	private static void removeDuplicateLinesFromRelationFile() throws UnsupportedEncodingException, FileNotFoundException, IOException {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream("/Users/gerb/plain_relation.txt"))));
		Writer writer = new PrintWriter(new BufferedWriter(new FileWriter("/Users/gerb/plain_relation_without_duplicates.txt")));
		
		Set<String> relations = new TreeSet<String>();
		String line;
		while ((line = br.readLine()) != null) {
			
			relations.add(line);
		}
		for (String s : relations ){
			
			writer.write(s+ Constants.NEW_LINE_SEPARATOR);
		}
		writer.close();
	}
	
	private static void removeSurfaceFormsFromRelationFile() throws UnsupportedEncodingException, FileNotFoundException, IOException{
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream("/Users/gerb/Development/workspaces/experimental/files/en_surface.txt"))));
		
		Writer writer = new PrintWriter(new BufferedWriter(new FileWriter("/Users/gerb/plain_relation.txt")));
		
		// 0_URI1 ||| 1_LABEL1 ||| 2_LABELS1 ||| 3_PROP ||| 4_URI2 ||| 5_LABEL2 ||| 6_LABELS2 ||| 7_RANGE ||| 8_DOMAIN
		
		String line;
		while ((line = br.readLine()) != null) {

			String[] l = line.split(" \\|\\|\\| ");
			String[] newLine = new String[l.length-2];
			for ( int i = 0, j = 0; i < l.length ; i++) {
				
				if ( i != 2 && i != 6 ) {
					
					newLine[j] = l[i];
					j++;
				}
			}
			if ( newLine.length != 7 ) System.out.println("Not 7 length: " + Arrays.toString(newLine));
			if ( !newLine[2].startsWith("http://dbpedia.org/ontology/")) System.out.println("Property flawed: " + Arrays.toString(newLine));
			
			writer.write(StringUtils.join(newLine, " ||| ") + Constants.NEW_LINE_SEPARATOR);
		}
		writer.close();
		br.close();
	}
}
