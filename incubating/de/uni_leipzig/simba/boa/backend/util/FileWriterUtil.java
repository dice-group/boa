package de.uni_leipzig.simba.boa.backend.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

public class FileWriterUtil {
	
	private static NLPediaLogger logger = new NLPediaLogger(FileWriterUtil.class);
	
	public FileWriterUtil(){}
	
	public static synchronized boolean writeSentences(List<String> sentences, String urlForfilename) {
		
		BufferedWriter bufferedWriter = null;

		try {
			
			// remove all none allowed characters from filename
			urlForfilename = urlForfilename.replaceAll("http://", "").replaceAll("/", ".");
			
			String filename = NLPediaSettings.getInstance().getSetting("sentenceFileDirectory") + urlForfilename + ".txt";
			
			File file = new File(filename);
			if (file.exists()) {
				
				// open file in append mode
				bufferedWriter = new BufferedWriter(new FileWriter(filename, true));
			}
			else {
				
				bufferedWriter = new BufferedWriter(new FileWriter(filename, false));
			}

			FileWriterUtil.logger.debug("Writing " + sentences.size() + " sentences to file " + NLPediaSettings.getInstance().getSetting("sentenceFileDirectory") + filename + ".txt");
			
			for (String sentence : sentences ) {

				bufferedWriter.append(sentence);
				bufferedWriter.newLine();
			}
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		finally {
			// Close the BufferedWriter
			try {
				if (bufferedWriter != null) {
					bufferedWriter.flush();
					bufferedWriter.close();
				}
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		return false;
	}
}
