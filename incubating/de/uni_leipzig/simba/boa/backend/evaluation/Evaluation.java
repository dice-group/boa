package de.uni_leipzig.simba.boa.backend.evaluation;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.rdf.TripleDao;
import de.uni_leipzig.simba.boa.backend.persistance.hibernate.HibernateFactory;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;

public class Evaluation implements Command {

	private static final List<String> TEST_DATABASES = Arrays.asList("de_wiki_exp");//, "en_news_exp");
	
	private TripleDao tripleDao = (TripleDao) DaoFactory.getInstance().createDAO(TripleDao.class);
	
	public static StringBuffer OUTPUT =  new StringBuffer();
	
	public static void main(String[] args) throws CorruptIndexException, LockObtainFailedException, IOException {

		NLPediaSetup SETUP = new NLPediaSetup(false);
		new EvaluationManager().execute();
	}
	
	@Override
	public void execute() {

//		this.startAutomaticEvaluation();
		this.startManualEvaluation();
	}
	
	private void startAutomaticEvaluation() {
		
		// get the annotated triples out of the files
		EvaluationFileLoader evaluationFileLoader = new EvaluationFileLoader();
		Map<Integer, List<Triple>> annotatorOneFile = evaluationFileLoader.loadAnnotatorFile(EvaluationFileLoader.FIRST_BATCH_ANNOTATOR_ONE_FILE);
		Map<Integer, List<Triple>> annotatorTwoFile = evaluationFileLoader.loadAnnotatorFile(EvaluationFileLoader.FIRST_BATCH_ANNOTATOR_TWO_FILE);
		
		// calculate the scores between multiple annotators
		AnnotatorScorer scorer = new AnnotatorScorer();
		scorer.calculateScores(annotatorOneFile, annotatorTwoFile);
		
		System.out.println(EvaluationManager.OUTPUT.toString());
		EvaluationManager.OUTPUT = new StringBuffer();
		
		Set<Triple> goldStandard = evaluationFileLoader.loadGoldStandard(EvaluationFileLoader.ExcludeRdfTypeStatements.YES);
		
		
	}
	
	private void startManualEvaluation() {
		
		// we want to test different databases
		for (String testDatabase : TEST_DATABASES) {
			
			// switch to db
			HibernateFactory.changeConnection(testDatabase);
			
			// now we serialize the top 100 facts from the different databases
			List<Triple> topNTriples = this.tripleDao.findAllTriples();
			Collections.sort(topNTriples, new Comparator<Triple>(){

				@Override
				public int compare(Triple o1, Triple o2) {

					double x = (o2.getScore() - o1.getScore());
					if ( x < 0 ) return -1;
					if ( x == 0 ) return 0;
					return 1;
				}
				
			});
			int i = 0;
			for ( Triple triple : topNTriples ) {
				
				if ( i++ < 501 ) {
					
					this.writeResults(triple.toString() + "\t" + triple.getScore() +  Constants.NEW_LINE_SEPARATOR);
					
					List<String> sentences = new ArrayList<String>(triple.getLearnedFromSentences());
					sentences = sentences.size() > 2 ? sentences.subList(0, 2) : sentences;
					for (String sentence : sentences ) {
						
						this.writeResults("  " + sentence.toString() + Constants.NEW_LINE_SEPARATOR);
					}
					this.writeResults(Constants.NEW_LINE_SEPARATOR);
				}
			}
		}
	}
	
	private void writeResults(String result) {

		try {
			
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(NLPediaSettings.getSetting("eval.output.file"), true), "UTF-8"));
			if ( result == null ) writer.write(EvaluationManager.OUTPUT.append(Constants.NEW_LINE_SEPARATOR).append(Constants.NEW_LINE_SEPARATOR).toString());				
			else writer.write(result); 
			writer.close();
			EvaluationManager.OUTPUT.delete(0, EvaluationManager.OUTPUT.length());
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
