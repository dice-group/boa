package de.uni_leipzig.simba.boa.backend.crawl;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

public class RelationFinder {
	
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

		for ( File file : FileUtils.listFiles(new File(filename), HiddenFileFilter.VISIBLE, TrueFileFilter.INSTANCE) ) {
			
			System.out.println(String.format("Reading background knowledge from file %s", file.getName()));
			
			try {
				
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
				
				String line;
				while ((line = br.readLine()) != null) {

					resultsSet.add(line.split(" \\|\\|\\| "));
				}
			}
			catch (Exception e) {

				e.printStackTrace();
				logger.error("Could not read file: " + file.getName(), e);
			}
		}
		return resultsSet;
		
//		if ( !filename.isEmpty() ) {
//			
//			try {
//
//				BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(filename))));
//
//				int count1Property = 0;
//				int count2Property = 0;
//				
//				String line;
//				while ((line = br.readLine()) != null) {
//
//					resultsSet.add(line.split(" \\|\\|\\| "));
//					
//					if ( lineParts[3].equals("http://dbpedia.org/ontology/subsidiary") && count1Property < 11  ) {
//						
//						count1Property++;
//						 // 0_URI1 ||| 1_LABEL1 ||| 2_LABELS1 ||| 3_PROP ||| 4_URI2 ||| 5_LABEL2 ||| 6_LABELS2 ||| 7_RANGE ||| 8_DOMAIN ||| 9_isSubject/isObject
//						
//						if ( lineParts[9].trim().equals("isSubject") ) {
//							
//							resultsSet.add(new String[] { lineParts[0], lineParts[1], lineParts[2], lineParts[3], lineParts[4], lineParts[5], lineParts[6], lineParts[7], lineParts[8], lineParts[9] });
//						}
//							
//							
//					}
//					if ( lineParts[3].equals("http://dbpedia.org/ontology/birthPlace") && count2Property < 11  ) {
//						
//						count2Property++;
//						// 0_URI1 ||| 1_LABEL1 ||| 2_LABELS1 ||| 3_PROP ||| 4_URI2 ||| 5_LABEL2 ||| 6_LABELS2 ||| 7_RANGE ||| 8_DOMAIN ||| 9_isSubject/isObject
//						
//						if ( lineParts[9].trim().equals("isSubject") ) {
//							
//							resultsSet.add(new String[] { lineParts[0], lineParts[1], lineParts[2], lineParts[3], lineParts[4], lineParts[5], lineParts[6], lineParts[7], lineParts[8], lineParts[9] });
//						}
//						if ( lineParts[9].trim().equals("isObject") ) {
//							
//							resultsSet.add(new String[] { lineParts[4], lineParts[5], lineParts[6], lineParts[3], lineParts[0], lineParts[1], lineParts[2], lineParts[7], lineParts[8], lineParts[9] });
//						}
//					}
//					
//				}
//				br.close();
//			}
//			catch (Exception e) {
//
//				e.printStackTrace();
//				logger.error("Could not read file: " + LABEL_OUTPUT_FOLDER, e);
//			}
//		}
//		
//		return resultsSet;
	}
}
