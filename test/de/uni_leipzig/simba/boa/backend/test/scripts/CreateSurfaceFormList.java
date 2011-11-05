package de.uni_leipzig.simba.boa.backend.test.scripts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;

import weka.core.tokenizers.NGramTokenizer;

import de.danielgerber.file.FilenameFilters;
import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.crawl.RelationFinder;
import de.uni_leipzig.simba.boa.backend.extraction.EnglishNumberToWords;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import edu.stanford.nlp.util.StringUtils;

/**
 * This thing needs at least 4GB of RAM.
 * 
 * 
 * @author gerb
 */
public class CreateSurfaceFormList implements Command {

	private static NLPediaLogger logger = new NLPediaLogger(CreateSurfaceFormList.class);
	private static Map<String,Set<String>> urisToLabels;
	
	public void execute() {
		
		File directory = new File(NLPediaSettings.getInstance().getSetting("plainRelationFiles"));
		
		try {
			
			urisToLabels = readSurfaceForms();
			
			for ( File f : FileUtils.listFiles(directory, HiddenFileFilter.VISIBLE, TrueFileFilter.INSTANCE) ) {
				
				System.out.println("Reading file " + f.getName());
				String lastPart = f.getAbsolutePath().replace("/"+f.getName(), "");
				lastPart = lastPart.substring(lastPart.lastIndexOf("/") + 1);
				String fileName = directory.getAbsolutePath().substring(0, directory.getAbsolutePath().lastIndexOf("/")) + "/surface/"+ lastPart + "/" + f.getName();
				new File(directory.getAbsolutePath().substring(0, directory.getAbsolutePath().lastIndexOf("/")) + "/surface/"+ lastPart + "/").mkdir();
				
				Set<String> linesToWrite = new HashSet<String>();
				Writer writer =  new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
				
				BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(f))));
				
				String line;
				while ((line = br.readLine()) != null) {
				
					// 0_URI1 ||| 1_LABEL1 ||| 2_PROP ||| 3_URI2 ||| 4_LABEL2 ||| 5_RANGE ||| 6_DOMAIN
					String[] lineParts = line.split(" \\|\\|\\| ");
					
					// some labels contain new line characters
					if ( lineParts.length != 7 ) {
						
//						System.out.println(line);
						continue;
					}
					
					String firstUri		= lineParts[0];
					String secondUri	= lineParts[3];
					
					// we found labels for the resource in the surface form file
					if ( urisToLabels.get(firstUri) != null ) {
						
						lineParts[1] = lineParts[1] + " ||| " + StringUtils.join(urisToLabels.get(firstUri), "_&_").toLowerCase();
					}
					else {
						lineParts[1] = lineParts[1] + " ||| " + lineParts[1].toLowerCase();
					}
					
					// property namespace labels and uris are mixed in objects of tripel
					if ( lineParts[2].contains("/property/") ) {
						
						// so we found a uri where it doesn't belong try to get a name for it 
						if ( lineParts[3].startsWith("http://") ) {
							
							// we found labels for the resource in the surface form file
							if ( urisToLabels.get(secondUri) != null ) {
								
								Set<String> labels = urisToLabels.get(secondUri);
								String firstLabel = labels.iterator().next();
								lineParts[4] = firstLabel + " ||| " + StringUtils.join(labels, "_&_").toLowerCase();
								lineParts[3] = firstLabel; // there cant be an uri as second resource so replace them
							}
							// no label found, we dont want to add an uri as a label so skip this triple
							else continue;
						}
						// so we found some text, try to make some sense of it and create surface forms
						else {
							
							try {
								
								lineParts[4] = lineParts[4] + " ||| " + createDatatypePropertyLabels(lineParts[4], lineParts[5]).toLowerCase();
							}
							catch (ParseException e) {
								
								// dont add this triple because the date could not get parsed
								logger.error(e.getLocalizedMessage() + " _:_ " + line + Constants.NEW_LINE_SEPARATOR);
								continue;
							}
							catch (NumberFormatException nfe) {
								
								// dont add this triple because the date could not get parsed
								logger.error(nfe.getLocalizedMessage() + " _:_ " + line + Constants.NEW_LINE_SEPARATOR);
								continue;
							}
						}
					}
					// ontology namespace, so object properties do ONLY have resources as object, datatype properties do only have strings attached 
					else {
						
						// object property found
						if ( lineParts[3].startsWith("http://") ) {
							
							// we found labels for the resource in the surface form file
							if ( urisToLabels.get(secondUri) != null ) {
								
								lineParts[4] = lineParts[4] + " ||| " + StringUtils.join(urisToLabels.get(secondUri), "_&_").toLowerCase();
							}
							else {
								
								lineParts[4] = lineParts[4] + " ||| " + lineParts[4].toLowerCase();
							}
						}
						// datatype property found, create surface forms
						else {
							
							try {
								lineParts[4] = lineParts[4] + " ||| " + createDatatypePropertyLabels(lineParts[4], lineParts[5]).toLowerCase();
							}
							catch (ParseException e) {
								// dont add this triple because the date could not get parsed
								logger.error(e.getLocalizedMessage() + " _:_ " + line + Constants.NEW_LINE_SEPARATOR);
								continue;
							}
							catch (NumberFormatException nfe) {
								
								// dont add this triple because the date could not get parsed
								logger.error(nfe.getLocalizedMessage() + " _:_ " + line + Constants.NEW_LINE_SEPARATOR);
								continue;
							}
						}
					}
					
					// 0_URI1 ||| 1_LABEL1 ||| 2_LABELS1 ||| 3_PROP ||| 4_URI2 ||| 5_LABEL2 ||| 6_LABELS2 ||| 7_RANGE ||| 8_DOMAIN				
					linesToWrite.add(StringUtils.join(lineParts, " ||| "));
				}
				
				// only write unique lines to file
				for (String sent : linesToWrite) writer.write(sent + Constants.NEW_LINE_SEPARATOR);
				writer.close();
			}
		}
		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private String createDatatypePropertyLabels(String objectLabel, String predicateType) throws ParseException, NumberFormatException, IOException {

		StringBuffer labels = new StringBuffer();
		
		if ( predicateType.equals("null") ) {
			
			// test the string if it is empty, if yes then skip this triple 
			String tempLabel = objectLabel.replaceAll("'|\"|,|-|\\.|/|~", "");
			tempLabel = objectLabel.replace("none", "").replace("None", "").replace("see below", "");
			if ( tempLabel.trim().isEmpty() ) throw new NumberFormatException();
			
			String ret = null;
			
			// try to parse as double
			Double d = null;
			try {
				
				d = Double.valueOf(objectLabel);
			}
			catch ( NumberFormatException nfe ) {}
			if ( d != null ) {
				
				ret = createDatatypePropertyLabels(String.valueOf(d), "http://www.w3.org/2001/XMLSchema#double");
			}
			
			// try to parse as an integer
			Integer i = null;
			try {
				
				i = Integer.valueOf(objectLabel);
			}
			catch (NumberFormatException nfe) {}
			if ( i != null ) {
				
				ret = createDatatypePropertyLabels(String.valueOf(i), "http://www.w3.org/2001/XMLSchema#nonNegativeInteger");
			}
			
			// not an integer nor a double -> it has to be string
			if ( i == null && d == null ) {
				
				Set<String> surfaceForms = urisToLabels.get(objectLabel.replace(" ", "_"));
				// replace whitespace with _ and try to get surface forms from the "index"
				if ( surfaceForms != null ) {
					
					ret = StringUtils.join(surfaceForms, "_&_");
				}
				else {
					ret = objectLabel;
				}
			}
			labels.append(ret);
		}
		else if ( predicateType.equals("http://www.w3.org/2001/XMLSchema#double") ) {
			
			// make sure its a number
			Double d = new Double(objectLabel);
			Integer i = d.intValue();
			
			Set<String> variations = new HashSet<String>();
			variations.add(String.valueOf(d));
			variations.add(String.valueOf(i)); // rounded to integer
			variations.add(String.valueOf(((i/5)*5))); // rounded down to next 5: 104 -> 100
			variations.add(String.valueOf(((i/10)*10))); // rounded down to next 10
			variations.add(new DecimalFormat("#.0").format(d));
			variations.add(new DecimalFormat("#.00").format(d));
			labels.append(StringUtils.join(variations, "_&_"));
		}
		else if ( predicateType.equals("http://www.w3.org/2001/XMLSchema#nonNegativeInteger") ) {
			
			labels.append(handleNonNegativeInteger(objectLabel));
		}
		else if ( predicateType.equals("http://www.w3.org/2001/XMLSchema#string") ) {
			
			labels.append(handleString(objectLabel));
		}
		else if ( predicateType.equals("http://www.w3.org/2001/XMLSchema#date") ) {
			
			String d = getDateString(objectLabel, "yyyy-MM-dd", "d");
			String MM = getDateString(objectLabel, "yyyy-MM-dd", "MM");
			String MMM = getDateString(objectLabel, "yyyy-MM-dd", "MMM");
			String MMMM = getDateString(objectLabel, "yyyy-MM-dd", "MMMM");
			String yy = getDateString(objectLabel, "yyyy-MM-dd", "yy");
			String yyyy = getDateString(objectLabel, "yyyy-MM-dd", "yyyy");
			
			labels.append(d + getOrdinalFor(Integer.valueOf(d)) + " of " + MMMM + " " + yyyy).append("_&_").
			append(d + " " + MMMM + " " + yyyy).append("_&_").
			append(d + "." + MM + "." + yyyy).append("_&_").
			append(d + "." + MM + "." + yy).append("_&_").
			append(d + " " + MMM + " " + yyyy).append("_&_").
			append(d + " " + MMMM  + " '" + yy).append("_&_").
			append(d + " " + MMM  + " '" + yy).append("_&_").
			append(MMM + " " + d  + " , " + yyyy).append("_&_").
			append(MMMM + " " + yyyy).append("_&_").
			append(MMM + " " + yyyy).append("_&_").
			append(MMMM).append("_&_").
			append(yyyy);
		}
		else {
			
			logger.error("There is something obiously wrong: " + objectLabel + "  " + predicateType);
		}
		
		return labels.toString();
	}
	
	private String handleString(String objectLabel) {

		Set<String> variations = new HashSet<String>();
		
		// create some ngrams
		NGramTokenizer ngt = new NGramTokenizer();
		ngt.setDelimiters(" ");
		ngt.setNGramMaxSize(2);
		ngt.setNGramMinSize(2);
		ngt.tokenize(objectLabel);
		while (ngt.hasMoreElements())
			variations.add(String.valueOf(ngt.nextElement()));
		
		Set<String> surfaceForms = urisToLabels.get(objectLabel.replace(" ", "_"));
		// replace whitespace with _ and try to get surface forms from the "index"
		if ( surfaceForms != null ) variations.addAll(surfaceForms);
		
		// for some labels there are multiple ones coded in divided by comma  
		if (objectLabel.contains(",")) variations.addAll(Arrays.asList(objectLabel.split(",")));
		
		// sepcial cases
		if (objectLabel.contains("C++") ) variations.add("C + +");
		if (objectLabel.contains("C#") ) variations.add("C #");
		
		return StringUtils.join(variations, "_&_");
	}

	private String handleNonNegativeInteger(String parseCandidate) {

		if ( parseCandidate.contains(".") ) parseCandidate = parseCandidate.replaceAll("\\.[0-9]+$", "");
		Integer i = Integer.valueOf(parseCandidate);
		Set<String> variations = new HashSet<String>();
		if ( i == 1 ) variations.add("first");
		if ( i == 2 ) variations.add("second");
		if ( i == 3 ) variations.add("third");
		if ( i == 4 ) variations.add("fourth");
		if ( i == 5 ) variations.add("fifth");
		if ( i > 0 && i < 32 ) variations.add(i + getOrdinalFor(i));
		variations.add(i.toString());
		variations.add(String.valueOf(((i/5)*5))); // rounded down to next 5: 104 -> 100
		variations.add(String.valueOf(((i/10)*10))); // rounded down to next 10
		if ( i > 100 ) variations.add(String.valueOf(((i/100)*100))); // rounded down to next 100
		if ( i > 1000 )variations.add(String.valueOf(((i/1000)*1000))); // rounded down to next 1000
		if ( i > 1000000 ) variations.add(String.format("%.1f", i / 1000000.0));
		if ( i > 1000000 ) variations.add(String.format("%.0f", i / 1000000.0));
		if ( i > 1000000000 ) variations.add(String.format("%.1f", i / 1000000000.0));
		if ( i > 1000000000 ) variations.add(String.format("%.0f", i / 1000000000.0));
		
		return StringUtils.join(variations, "_&_");
	}

	private String getDateString(String dateString, String fromPattern, String toPattern) throws ParseException {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fromPattern);
		Date date1 = simpleDateFormat.parse(dateString);
		simpleDateFormat.applyPattern(toPattern);
		return simpleDateFormat.format(date1);
	}
	
	public String getOrdinalFor(int value) {

		int hundredRemainder = value % 100;
		int tenRemainder = value % 10;
		if (hundredRemainder - tenRemainder == 10) {
			return "th";
		}

		switch (tenRemainder) {
		case 1:
			return "st";
		case 2:
			return "nd";
		case 3:
			return "rd";
		default:
			return "th";
		}
	}

	private void printSurfaceFormsToFile(Map<String, Set<String>> urisToLabels ) throws IOException {
		
		urisToLabels = getSurfaceForms();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/Users/gerb/uriToSurfaceForm.txt"), "UTF-8"));
		for ( Map.Entry<String, Set<String>> entry : urisToLabels.entrySet() ) {
			
			writer.write(entry.getKey() + "\t" + StringUtils.join(entry.getValue(), "\t") + Constants.NEW_LINE_SEPARATOR);
		}
		writer.close();
	}
	
	
	private Map<String,Set<String>> readSurfaceForms() throws IOException {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream("/Users/gerb/uri_surface_form.tsv"))));
		
		Map<String,Set<String>> uriToLabels = new HashMap<String,Set<String>>();
		
		String line = "";
		while ( (line = br.readLine()) != null ) {
			
			String[] lineParts = line.split("\t");
			
			uriToLabels.put(lineParts[0], new HashSet<String>(Arrays.asList(Arrays.copyOfRange(lineParts, 1, lineParts.length))));
		}
		br.close();
		return uriToLabels;
	}
	
	private Map<String,Set<String>> getSurfaceForms() throws IOException {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream("/Users/gerb/Development/workspaces/experimental/surface_forms-Wikipedia-TitRedDis.tsv"))));
		
		Map<String,Set<String>> uriToLabels = new HashMap<String,Set<String>>();
		
		String line = "";
		while ( (line = br.readLine()) != null ) {
			
			String[] lineParts = line.split("\t");
			if ( lineParts.length > 2 ) System.out.println("Something wrong, more then 2 parts per line + " + line);
			
			if ( uriToLabels.containsKey("http://dbpedia.org/resource/"+ lineParts[1]) ) {
				
				uriToLabels.get("http://dbpedia.org/resource/"+ lineParts[1]).add(lineParts[0]);
			}
			else {
				
				Set<String> labels = new HashSet<String>();
				labels.add(lineParts[0]);
				uriToLabels.put("http://dbpedia.org/resource/"+ lineParts[1], labels);
			}
		}
		br.close();
		return uriToLabels;
	}
	
//	private void createMappings() {
//		
//		Directory directory = FSDirectory.open(new File("/Users/gerb/Downloads/06-09-2011/tmp/Index.wikipediaTraining.Merged.SnowballAnalyzer.DefaultSimilarity.fresh"));
//		// create index searcher in read only mode
//		IndexSearcher indexSearcher = new IndexSearcher(directory, true);
//		System.out.println("Index opened!");
//		
//		BufferedWriter writer1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/Users/gerb/Desktop/uri_to_surface_form_en.txt"), "UTF-8"));
//		
//		int max = indexSearcher.maxDoc();
//		for (int id = 0; id < max ; id++) {
//			
//			Set<String> surfaceNames = new HashSet<String>(); 
//			Field[] sfFields = indexSearcher.doc(id).getFields("SURFACE_FORM");
//			for (Field f : sfFields) {
//				
//				surfaceNames.add(f.stringValue());
//			}
//			String uri = indexSearcher.doc(id).get("URI");
//			
//			if ( id % 100000 == 0 ) {
//			
//				System.out.println("Iteration: " + id);
//			}
//			urisToLabels.put(uri, surfaceNames);
//		}
//		
//		for ( Map.Entry<String,Set<String>> e : urisToLabels.entrySet() ) {
//			
//			for (String sss : e.getValue()) writer1.write(e.getKey() + "\t" + sss + Constants.NEW_LINE_SEPARATOR);
//		}
//		writer1.close();
//
//	}
}