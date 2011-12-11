package de.uni_leipzig.simba.boa.backend.test;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.JUnit4TestAdapter;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.nlp.SentenceDetection;

public class IndexUtf8Test {

	// initialize logging and settings
	NLPediaSetup setup = null;
	NLPediaLogger logger = null;

	public static junit.framework.Test suite() {

		return new JUnit4TestAdapter(IndexUtf8Test.class);
	}

	@Before
	public void setUp() {

		this.setup = new NLPediaSetup(true);
		this.logger = new NLPediaLogger(IndexUtf8Test.class);
	}

	@After
	public void cleanUpStreams() {

		this.setup.destroy();

	}

	@Test
	public void testEncoding() throws CorruptIndexException, LockObtainFailedException, IOException {
		
		RAMDirectory idx = new RAMDirectory();
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
		IndexWriter writer = new IndexWriter(idx, analyzer, true, IndexWriter.MaxFieldLength.LIMITED);
		
		File file = new File("/Users/gerb/utf8.test");
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		List<String> sentences = new ArrayList<String>();
		String line;
		while ((line = br.readLine()) != null) {
			
			sentences.add(line);
		}
		System.out.printf("There are %s sentences in the list.\n", sentences.size());
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(sentences.get(0));
		buffer.append(sentences.get(1));
		
		// filter them
		SentenceDetection sd = new SentenceDetection();
		sentences = sd.getSentences(buffer.toString(), Constants.SENTENCE_BOUNDARY_DISAMBIGUATION_STANFORD_NLP);
		
		for (String s : sentences) {
			
			System.out.println(s);
			
			Document doc = new Document();
			doc.add(new Field("sentence", s, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO));
			writer.addDocument(doc);
		}
		writer.optimize();
		writer.close();
		
		IndexSearcher searcher = new IndexSearcher(idx);
		System.out.println(searcher.doc(0).get("sentence"));
		searcher.close();
	}
}
