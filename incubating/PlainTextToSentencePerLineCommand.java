package de.uni_leipzig.simba.boa.backend.configuration.command.impl.scripts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.nlp.SentenceDetection;

public class PlainTextToSentencePerLineCommand implements Command {

	private String pathToInputDirectory = null;
	private String pathToOutputFile = null;
	private NLPediaLogger logger = new NLPediaLogger(PlainTextToSentencePerLineCommand.class);

	public PlainTextToSentencePerLineCommand(String pathToInputDirectory, String pathToOutputFile) {

		this.pathToInputDirectory = pathToInputDirectory;
		this.pathToOutputFile = pathToOutputFile;
	}

	@Override
	public void execute() {

		try {

			createFromPlainTextFiles();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

	private void createFromPlainTextFiles() {

		try {

			// Writer writer = new PrintWriter(new BufferedWriter(new
			// FileWriter(this.pathToOutputFile, true)));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.pathToOutputFile), "UTF-8"));

			SentenceDetection sd = new SentenceDetection();

			String line = "";
			StringBuffer buffer = new StringBuffer();

			File files[] = new File(this.pathToInputDirectory).listFiles();

			for (File file : files) {

				Date start = new Date();

				this.logger.debug("Processing file: " + file.getAbsolutePath());
				System.out.println("Processing file: " + file.getAbsolutePath());

				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

				int i = 0;

				while ((line = br.readLine()) != null) {

					if (!line.startsWith("=") && !line.startsWith("[")) {

						buffer.append(line);
						if (i++ == 1000000) {

							for (String sentence : sd.getSentences(buffer.toString(), NLPediaSettings.getInstance().getSetting("sentenceBoundaryDisambiguation"))) {

								writer.write(sentence + System.getProperty("line.separator"));
							}

							buffer = new StringBuffer();
							i = 0;
						}
					}
				}
				// write the last 10000-x lines
				for (String sentence : sd.getSentences(buffer.toString(), NLPediaSettings.getInstance().getSetting("sentenceBoundaryDisambiguation"))) {

					writer.write(sentence);
					writer.write(System.getProperty("line.separator"));
				}
				buffer = new StringBuffer();
				br.close();

				System.out.println("File took " + (new Date().getTime() - start.getTime()) + "ms.");
			}
			writer.close();
		}
		catch (Exception e) {

			e.printStackTrace();
		}
	}
}
