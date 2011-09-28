package de.uni_leipzig.simba.boa.backend.configuration.command.impl.scripts;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;


public class CreateQuestionAnsweringIndexCommand implements Command {

	public void execute() {

		// create the index 
		try {
			
			String directory = NLPediaSettings.getInstance().getSetting("question.answering.index");
			Directory indexDirectory = FSDirectory.open(new File(directory));
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
			IndexWriter writer = new IndexWriter(indexDirectory, analyzer, true, IndexWriter.MaxFieldLength.LIMITED);
			
			// get the pattern mappings
			PatternMappingDao pmDao = (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
			List<PatternMapping> patternMappings = pmDao.findAllPatternMappings();
			
			// fill the index with data 
			for ( PatternMapping mapping : patternMappings ) {
				
				System.out.println(String.format("Indexing pattern mapping %s", mapping.getProperty().getUri()));
				
				for (Pattern pattern : mapping.getPatterns() ) {
					
					System.out.println(String.format("Adding pattern %s", pattern.getNaturalLanguageRepresentationWithoutVariables()));
					
					Document doc = new Document();
					doc.add(new Field("uri", mapping.getProperty().getUri(), Field.Store.YES, Field.Index.NOT_ANALYZED));
					doc.add(new Field("nlr", pattern.getNaturalLanguageRepresentationWithoutVariables().trim(), Field.Store.YES, Field.Index.ANALYZED));
					doc.add(new NumericField("confidence", Field.Store.YES, true).setDoubleValue(pattern.getConfidence()));
					writer.addDocument(doc);
				}
			}
			
			// close the index
			writer.optimize();
			writer.close();
		}
		catch (IOException e) { e.printStackTrace(); }
	}
	
	public static void main(String[] args) throws ParseException, CorruptIndexException, IOException {

		NLPediaSetup setup = new NLPediaSetup(false);

		CreateQuestionAnsweringIndexCommand e = new CreateQuestionAnsweringIndexCommand();
		e.execute();
		
		// ####################################################
		
		String searchPhrase = "host";
		double confidenceThreshold = 0D;
		
		Query query1 = new TermQuery(new Term("nlr", searchPhrase));
		Query query2 = NumericRangeQuery.newDoubleRange("confidence", confidenceThreshold, 1D, true, true);

		BooleanQuery booleanQuery = new BooleanQuery();
		booleanQuery.add(query1, BooleanClause.Occur.MUST);
		booleanQuery.add(query2, BooleanClause.Occur.MUST);
		
		System.out.println(booleanQuery);
		
		String directory = NLPediaSettings.getInstance().getSetting("question.answering.index");
		IndexSearcher indexSearcher = new IndexSearcher(FSDirectory.open(new File(directory)), true);
		
		ScoreDoc[] hits = indexSearcher.search(booleanQuery, 100).scoreDocs;
		
//		Set<String> uris = new HashSet<String>();
		for (int i = 0; i < hits.length && i < 5; i++) {
			
			System.out.println(indexSearcher.doc(hits[i].doc).get("uri"));
			System.out.println(indexSearcher.doc(hits[i].doc).get("nlr"));
			System.out.println(indexSearcher.doc(hits[i].doc).get("confidence"));
		}
	}
}
