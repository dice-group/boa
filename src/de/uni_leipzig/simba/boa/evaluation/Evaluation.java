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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;

import com.sleepycat.je.log.FileManager.FileMode;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.persistance.hibernate.HibernateFactory;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;

public class Evaluation implements Command {

	private static final List<String> TEST_DATABASES = Arrays.asList("en_wiki_exp");//, "en_news_exp");
	
	public static StringBuffer OUTPUT =  new StringBuffer();
	
	public static void main(String[] args) throws CorruptIndexException, LockObtainFailedException, IOException {

		NLPediaSetup SETUP = new NLPediaSetup(false);
		new Evaluation().execute();
	}
	
	@Override
	public void execute() {

		// get the annotated triples out of the files
		EvaluationFileLoader evaluationFileLoader = new EvaluationFileLoader();
		Map<Integer, List<Triple>> annotatorOneFile = evaluationFileLoader.loadAnnotatorFile(EvaluationFileLoader.FIRST_BATCH_ANNOTATOR_ONE_FILE);
		Map<Integer, List<Triple>> annotatorTwoFile = evaluationFileLoader.loadAnnotatorFile(EvaluationFileLoader.FIRST_BATCH_ANNOTATOR_TWO_FILE);
//				Evaluation.OUTPUT.append("----").append(Constants.NEW_LINE_SEPARATOR);
		
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
				for ( int topNPattern : Arrays.asList(1, 3, 5, 10, 20, 50, 100) ) {
					
					NLPediaSettings.getInstance().setSetting("score.threshold.create.knowledge", String.valueOf(scoreThreshold));
					NLPediaSettings.getInstance().setSetting("top.n.pattern", String.valueOf(topNPattern));
					
					// filter out triples which might occur randomly, due to bad patterns
					for ( double tripleScoreThreshold : Arrays.asList(0.1D, 0.2D, 0.3D, 0.4D, 0.5D, 0.6D, 0.7D, 0.8D, 0.9D, 1.0D) ) {
						
						Set<Triple> testData		= evaluationFileLoader.loadBoa(EvaluationIndexCreator.createGoldStandardIndex(), tripleScoreThreshold);
						
						PrecisionRecallFMeasure precisionRecallFMeasure = new PrecisionRecallFMeasure(goldStandard, testData);
						DecimalFormat decimalFormat = new DecimalFormat("#.###");
						
						String output = goldStandard.size() + " " +
										testData.size() + " " +
										scoreThreshold + " " + 
										topNPattern + " " +
										tripleScoreThreshold + " " +
										decimalFormat.format(precisionRecallFMeasure.getPrecision()) + " " +
										decimalFormat.format(precisionRecallFMeasure.getRecall()) + " " +
										decimalFormat.format(precisionRecallFMeasure.getFMeasure());
						
						System.out.println(	"GSS: " + goldStandard.size() + " BS: " + testData.size() + " " +
											"ST: " + scoreThreshold + " TNP: " + topNPattern + " TST: " + tripleScoreThreshold + 
											"P: " + precisionRecallFMeasure.getPrecision() + " R: " + precisionRecallFMeasure.getRecall() + " F: " + precisionRecallFMeasure.getFMeasure());
						
						maxFMeasure = Math.max(maxFMeasure, precisionRecallFMeasure.getFMeasure());
						this.writeResults(output);
					}
				}
			}
			this.writeResults("Maximum F-Measure: " + String.valueOf(maxFMeasure));
		}
	}
	
	private void writeResults(String result) {

		try {
			
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(NLPediaSettings.getInstance().getSetting("eval.output.file"), true), "UTF-8"));
			if ( result == null ) writer.write(Evaluation.OUTPUT.append(Constants.NEW_LINE_SEPARATOR).append(Constants.NEW_LINE_SEPARATOR).toString());				
			else writer.write(result + Constants.NEW_LINE_SEPARATOR); 
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
