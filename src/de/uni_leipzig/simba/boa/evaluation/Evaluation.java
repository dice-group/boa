package de.uni_leipzig.simba.boa.evaluation;

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
		new Evaluation().execute();
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
		
		System.out.println(Evaluation.OUTPUT.toString());
		Evaluation.OUTPUT = new StringBuffer();
		
		Set<Triple> goldStandard = evaluationFileLoader.loadGoldStandard(EvaluationFileLoader.ExcludeRdfTypeStatements.YES);
		
		// we want to test different databases
		for (String testDatabase : TEST_DATABASES) {
			
			double maxFMeasure = 0D;
			
			// switch to db
			HibernateFactory.changeConnection(testDatabase);
			
			// we want to see if the knowledge creation threshold, i.e. the pattern score produced by the NN matters
			for ( double scoreThreshold : Arrays.asList(0.1D, 0.2D, 0.3D, 0.4D, 0.5D, 0.6D, 0.7D, 0.8D, 0.9D, 1.0D) ) {
				// take only the first n best scored pattern
				for ( int topNPattern : Arrays.asList(1, 3, 5, 10, 20, 50, 100, 1000) ) {
					
					NLPediaSettings.getInstance().setSetting("score.threshold.create.knowledge", String.valueOf(scoreThreshold));
					NLPediaSettings.getInstance().setSetting("top.n.pattern", String.valueOf(topNPattern));
					
					// filter out triples which might occur randomly, due to bad patterns
					for ( double tripleScoreThreshold : Arrays.asList(0.1D, 0.2D, 0.3D, 0.4D, 0.5D, 0.6D, 0.7D, 0.8D, 0.9D, 1.0D) ) {
						
						Set<Triple> testData		= evaluationFileLoader.loadBoa(EvaluationIndexCreator.createGoldStandardIndex(), tripleScoreThreshold);
						
						PrecisionRecallFMeasure precisionRecallFMeasure = new PrecisionRecallFMeasure(goldStandard, testData);
						DecimalFormat decimalFormat = new DecimalFormat("#.###");
						
						String output = goldStandard.size() + " " +
										testData.size() + " " +
										NLPediaSettings.getInstance().getSetting("score.threshold.create.knowledge") + " " + 
										NLPediaSettings.getInstance().getSetting("top.n.pattern") + " " +
										tripleScoreThreshold + " " +
										decimalFormat.format(precisionRecallFMeasure.getPrecision()) + " " +
										decimalFormat.format(precisionRecallFMeasure.getRecall()) + " " +
										decimalFormat.format(precisionRecallFMeasure.getFMeasure());	
						
						System.out.println( "GSS: " + goldStandard.size() + " BS: " + testData.size() +
											" ST: " + NLPediaSettings.getInstance().getSetting("score.threshold.create.knowledge") + " TNP: " + NLPediaSettings.getInstance().getSetting("top.n.pattern") + " TST: " + tripleScoreThreshold + 
											" P: " + precisionRecallFMeasure.getPrecision() + " R: " + precisionRecallFMeasure.getRecall() + " F: " + precisionRecallFMeasure.getFMeasure());
						
						maxFMeasure = Math.max(maxFMeasure, precisionRecallFMeasure.getFMeasure());
						this.writeResults(output + Constants.NEW_LINE_SEPARATOR);
						for ( Triple t : testData ) {
							this.writeResults(t.toString()+Constants.NEW_LINE_SEPARATOR);
						}
						this.writeResults(Constants.NEW_LINE_SEPARATOR);
					}
				}
			}
			this.writeResults("Maximum F-Measure: " + String.valueOf(maxFMeasure));
		}
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

					double x = (o2.getConfidence() - o1.getConfidence());
					if ( x < 0 ) return -1;
					if ( x == 0 ) return 0;
					return 1;
				}
				
			});
			int i = 0;
			for ( Triple triple : topNTriples ) {
				
				if ( i++ < 501 ) {
					
					this.writeResults(triple.toString() + "\t" + triple.getConfidence() +  Constants.NEW_LINE_SEPARATOR);
					
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
			
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(NLPediaSettings.getInstance().getSetting("eval.output.file"), true), "UTF-8"));
			if ( result == null ) writer.write(Evaluation.OUTPUT.append(Constants.NEW_LINE_SEPARATOR).append(Constants.NEW_LINE_SEPARATOR).toString());				
			else writer.write(result); 
			writer.close();
			Evaluation.OUTPUT.delete(0, Evaluation.OUTPUT.length());
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
