package de.uni_leipzig.simba.boa.backend.crawl;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

public class RelationFinder {
	
	private static final String LABEL_OUTPUT_FILE			= NLPediaSettings.getInstance().getSetting("labelOutputFile");
	private static final NLPediaLogger logger				= new NLPediaLogger(RelationFinder.class); 
	
	/**
	 * This message returns the values stored in the file "labelOutputFile".
	 * The values in this file should be like this:
	 * 		<label of subject> <property> <label of object>
	 * 
	 * @return list of triples
	 */
	public static List<String[]> getRelationFromFile(String filename) {
		
		List<String[]> resultsSet = new ArrayList<String[]>();
		
		if ( !filename.isEmpty() ) {
			
			try {

				BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(filename))));

				String line;
				while ((line = br.readLine()) != null) {

					String[] lineParts = line.split(" \\|\\|\\| ");

					if (!lineParts[1].equals("disambiguates") && !lineParts[1].equals("blankName") && !lineParts[1].equals("wikiPageWikiLink") ) {
						
						// label1 (Domain), label2 (Range), property, range, domain, isSubject;
						resultsSet.add(new String[] { lineParts[0], lineParts[1], lineParts[2], lineParts[3], lineParts[4], lineParts[5] });
					}
				}
				br.close();
			}
			catch (Exception e) {

				e.printStackTrace();
				logger.error("Could not read file: " + LABEL_OUTPUT_FILE, e);
			}
		}
		else {
			
			try {

				BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(LABEL_OUTPUT_FILE + "_subject.txt"))));

				String line;
				while ((line = br.readLine()) != null) {

					String[] lineParts = line.split(" \\|\\|\\| ");

					if (!lineParts[1].equals("disambiguates") && !lineParts[1].equals("blankName") && !lineParts[1].equals("wikiPageWikiLink") ) {
						
						// uri, label1 (Domain), uri, label2 (Range), property, range, domain, isSubject;
						resultsSet.add(new String[] { lineParts[0], lineParts[1], lineParts[2], lineParts[3], lineParts[4], lineParts[5], lineParts[6], lineParts[7] });
					}
				}
				br.close();
			}
			catch (Exception e) {

				e.printStackTrace();
				logger.error("Could not read file: " + LABEL_OUTPUT_FILE, e);
			}
			
			try {

				BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(LABEL_OUTPUT_FILE + "_object.txt"))));

				String line;
				while ((line = br.readLine()) != null) {

					String[] lineParts = line.split(" \\|\\|\\| ");

					// uri, label1 (range), property, uri, label2 (domain), range, domain, isSubject;
					if (!lineParts[1].equals("disambiguates") && !lineParts[1].equals("blankName") && !lineParts[1].equals("wikiPageWikiLink") ) {
						
						resultsSet.add(new String[] { lineParts[3], lineParts[4], lineParts[2], lineParts[0], lineParts[1], lineParts[5], lineParts[6], lineParts[7]});
					}
				}
				br.close();
			}
			catch (Exception e) {

				e.printStackTrace();
				logger.error("Could not read file: " + LABEL_OUTPUT_FILE, e);
			}
		}
		
		return resultsSet;
	}
}
