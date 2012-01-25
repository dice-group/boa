package de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.sentenceboundarydisambiguation.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.InvalidFormatException;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.namedentityrecognition.impl.StanfordNLPNamedEntityRecognition;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.sentenceboundarydisambiguation.SentenceBoundaryDisambiguation;

/**
 * 
 * @author Daniel Gerber
 */
public final class OpenNLPSentenceBoundaryDisambiguation implements SentenceBoundaryDisambiguation {

	private final NLPediaLogger logger	= new NLPediaLogger(StanfordNLPNamedEntityRecognition.class);
	private SentenceDetector sentenceDetector;
	
	public OpenNLPSentenceBoundaryDisambiguation() {
		
		try {
			
			InputStream sentenceModelFile = new FileInputStream("data/training/en-sent.bin");
			SentenceModel sentenceModel = new SentenceModel(sentenceModelFile);
			
			this.sentenceDetector = new SentenceDetectorME(sentenceModel);
			sentenceModelFile.close();
		}
		catch (FileNotFoundException e) {
			
			this.logger.fatal("Could not read sentence model file!", e);
			e.printStackTrace();
			throw new RuntimeException("Could not read sentence model file!", e);
		}
		catch (InvalidFormatException e) {
			
			this.logger.fatal("Sentence model file is not in the correct format!", e);
			e.printStackTrace();
			throw new RuntimeException("Sentence model file is not in the correct format!", e);
		}
		catch (IOException e) {
			
			this.logger.fatal("Something went wrong during sentence file loading/closing!", e);
			e.printStackTrace();
			throw new RuntimeException("Something went wrong during sentence file loading/closing!", e);
		}
	}
	
	@Override
	public List<String> getSentences(String text) {

		return Arrays.asList(this.sentenceDetector.sentDetect(text));
	}
}
