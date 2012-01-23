package de.uni_leipzig.simba.boa.evaluation;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class EvaluationIndexCreator {

	public static Set<String> sentences = new HashSet<String>();
	private static Directory idx = null;
	
	public static void main(String[] args) throws CorruptIndexException, LockObtainFailedException, IOException {

		EvaluationIndexCreator indexCreator = new EvaluationIndexCreator();
		indexCreator.createGoldStandardIndex();
	}
	
	public static Directory createGoldStandardIndex() {
		
		if ( idx == null ) {
			
			try {
				
				idx = new RAMDirectory();
				Analyzer analyzer = new WhitespaceAnalyzer(Version.LUCENE_30);
				IndexWriter writer = new IndexWriter(idx, analyzer, true, IndexWriter.MaxFieldLength.LIMITED);
				
				System.out.println("Adding " + sentences.size() + " sentences to evaluation index!");
				
				for (String sentence : sentences) {
					
					Document doc = new Document();
					doc.add(new Field("sentence", sentence, Field.Store.YES, Field.Index.NOT_ANALYZED, Field.TermVector.NO));
					doc.add(new Field("sentence-lc", sentence.toLowerCase(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO));
					writer.addDocument(doc);
				}
				writer.optimize();
				writer.close();
			}
			catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (LockObtainFailedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return idx;
	}
}
