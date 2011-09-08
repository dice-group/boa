package de.uni_leipzig.simba.boa.backend.test.scripts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

import javatools.administrative.Sleep;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.crawl.RelationFinder;
import de.uni_leipzig.simba.boa.backend.rdf.store.Store;
import edu.stanford.nlp.util.StringUtils;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
import com.hp.hpl.jena.util.FileManager;

public class ResolveGermanUris {

	private Map<String, String> enUriToDeLabelMapping = new HashMap<String, String>();

//	public static void main(String[] args) throws FileNotFoundException, IOException {
//	    String fileNameOrUri = "/Users/gerb/Downloads/08-09-2011/labels_de.nt";
//	    Model model = ModelFactory.createDefaultModel();
//	    InputStream is = FileManager.get().open(fileNameOrUri);
//	    if (is != null) {
//	        model.read(is, null, "N-TRIPLE");
//	        model.write(System.out, "TURTLE");
//	    } else {
//	        System.err.println("cannot read " + fileNameOrUri);;
//	    }
//		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/Users/gerb/Downloads/08-09-2011/labels_de.nt"), "UTF-8"));
//		
//		String line;
//		while ((line = br.readLine()) != null) {
//			
//			System.out.println(line);
//		}
//		
//		br.close();
//		
//		NxParser nxp = new NxParser(new FileInputStream("/Users/gerb/Downloads/08-09-2011/labels_de.nt"),false);
//		
//		  while (nxp.hasNext()) {
//		    Node[] ns = nxp.next();
//					
//		    for (Node n: ns) {
//		      System.out.print(n.toN3());
//		      System.out.print(" ");
//		    }
//		    System.out.println(".");
//		  }
//	}
	
	
	public static void main(String[] args) throws Exception {

		NLPediaSetup setup = new NLPediaSetup(true);
		ResolveGermanUris asd = new ResolveGermanUris();
		asd.writeNewFile();
		asd.transformEnglishFile();
	}

	private void transformEnglishFile() throws IOException {

		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/Users/gerb/Downloads/02-09-2011/all_de.txt"), "UTF-8"));

		List<String[]> relations = RelationFinder.getRelationFromFile("");
		for (String[] line : relations) {

			String subjectUri = line[0];
			String objectUri = line[3];

			if ( this.enUriToDeLabelMapping.get("<"+subjectUri+">") != null ) {
				
				line[1] = this.enUriToDeLabelMapping.get("<"+subjectUri+">");
			}
			if ( this.enUriToDeLabelMapping.get("<"+objectUri+">") != null ) {
				
				line[3] = this.enUriToDeLabelMapping.get("<"+objectUri+">");
			}
//			if ( this.enUriToDeLabelMapping.get("<"+subjectUri+">") != null && this.enUriToDeLabelMapping.get("<"+objectUri+">") != null ) {
//				
//				line[1] = this.enUriToDeLabelMapping.get("<"+subjectUri+">");
//				line[4] = this.enUriToDeLabelMapping.get("<"+objectUri+">");
//				writer.write(StringUtils.join(line, " ||| ") + Constants.NEW_LINE_SEPARATOR);
//			}
			writer.write(StringUtils.join(line, " ||| ") + Constants.NEW_LINE_SEPARATOR);
		}
		writer.close();
	}

	private void writeNewFile() throws Exception {

		try {
			
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/Users/gerb/Downloads/02-09-2011/labels_de.nt"), "UTF-8"));
				
			String line;
			int i = 0;
			while ((line = br.readLine()) != null) {

				String[] lineParts = line.split(" ");
				
				String subject		= lineParts[0];
				
				String label;
				if ( lineParts[2].contains("\"@de") ) label = lineParts[2];
				else {
					label = lineParts[2];
					for (String s : Arrays.copyOfRange(lineParts, 3, lineParts.length) ) {
						
						if ( s.contains("\"@de") ) {
							label += " "+ s;
							break;
						}
						label += " "+ s;
					}
				}
				label = fixEncoding(label);
				
				this.enUriToDeLabelMapping.put(subject, label.substring(1, label.length()-4));
			}
			br.close();
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

	private static String fixEncoding(String label) {

		Map<String,String> fixes = new HashMap<String,String>();
		fixes.put("\\\\u+00C0","À");
		fixes.put("\\\\u+00C1","Á");
		fixes.put("\\\\u+00C2","Â");
		fixes.put("\\\\u+00C3","Ã");
		fixes.put("\\\\u+00C4","Ä");
		fixes.put("\\\\u+00C5","Å");
		fixes.put("\\\\u+00C6","Æ");
		fixes.put("\\\\u+00C7","Ç");
		fixes.put("\\\\u+00C8","È");
		fixes.put("\\\\u+00C9","É");
		fixes.put("\\\\u+00CA","Ê");
		fixes.put("\\\\u+00CB","Ës");
		fixes.put("\\\\u+00CC","Ì");
		fixes.put("\\\\u+00CD","Í");
		fixes.put("\\\\u+00CE","Î");
		fixes.put("\\\\u+00CF","Ïs");
		fixes.put("\\\\u+00D0","Ð");
		fixes.put("\\\\u+00D1","Ñ");
		fixes.put("\\\\u+00D2","Ò");
		fixes.put("\\\\u+00D3","Ó");
		fixes.put("\\\\u+00D4","Ô");
		fixes.put("\\\\u+00D5","Õ");
		fixes.put("\\\\u+00D6","Ö");
		fixes.put("\\\\u+00D7","×");
		fixes.put("\\\\u+00D8","Ø");
		fixes.put("\\\\u+00D9","Ù");
		fixes.put("\\\\u+00DA","Ú");
		fixes.put("\\\\u+00DB","Û");
		fixes.put("\\\\u+00DC","Ü");
		fixes.put("\\\\u+00DD","Ý");
		fixes.put("\\\\u+00DE","Þ");
		fixes.put("\\\\u+00DF","ß");
		fixes.put("\\\\u+00E0","à");
		fixes.put("\\\\u+00E1","á");
		fixes.put("\\\\u+00E2","â");
		fixes.put("\\\\u+00E3","ã");
		fixes.put("\\\\u+00E4","ä");
		fixes.put("\\\\u+00E5","å");
		fixes.put("\\\\u+00E6","æ");
		fixes.put("\\\\u+00E7","ç");
		fixes.put("\\\\u+00E8","è");
		fixes.put("\\\\u+00E9","é");
		fixes.put("\\\\u+00EA","ê");
		fixes.put("\\\\u+00EB","ë");
		fixes.put("\\\\u+00EC","ì");
		fixes.put("\\\\u+00ED","í");
		fixes.put("\\\\u+00EE","î");
		fixes.put("\\\\u+00EF","ï");
		fixes.put("\\\\u+00F0","ð");
		fixes.put("\\\\u+00F1","ñ");
		fixes.put("\\\\u+00F2","ò");
		fixes.put("\\\\u+00F3","ó");
		fixes.put("\\\\u+00F4","ô");
		fixes.put("\\\\u+00F5","õ");
		fixes.put("\\\\u+00F6","ö");
		fixes.put("\\\\u+00F7","÷");
		fixes.put("\\\\u+00F8","ø");
		fixes.put("\\\\u+00F9","ù");
		fixes.put("\\\\u+00FA","ú");
		fixes.put("\\\\u+00FB","û");
		fixes.put("\\\\u+00FC","ü");
		fixes.put("\\\\u+00FD","ý");
		fixes.put("\\\\u+00FE","þ");
		fixes.put("\\\\u+00FF","ÿ");
		fixes.put("\\\\u+0100","Ā");
		fixes.put("\\\\u+0101","ā");
		fixes.put("\\\\u+0102","Ă");
		fixes.put("\\\\u+0103","ă");
		fixes.put("\\\\u+0104","Ą");
		fixes.put("\\\\u+0105","ą");
		fixes.put("\\\\u+0106","Ć");
		fixes.put("\\\\u+0107","ć");
		fixes.put("\\\\u+0108","Ĉ");
		fixes.put("\\\\u+0109","ĉ");
		fixes.put("\\\\u+010A","Ċ");
		fixes.put("\\\\u+010B","ċ");
		fixes.put("\\\\u+010C","Č");
		fixes.put("\\\\u+010D","č");
		fixes.put("\\\\u+010E","Ď");
		fixes.put("\\\\u+010F","ď");
		fixes.put("\\\\u+0110","Đ");
		fixes.put("\\\\u+0111","đ");
		fixes.put("\\\\u+0112","Ē");
		fixes.put("\\\\u+0113","ē");
		fixes.put("\\\\u+0114","Ĕ");
		fixes.put("\\\\u+0115","ĕ");
		fixes.put("\\\\u+0116","Ė");
		fixes.put("\\\\u+0117","ė");
		fixes.put("\\\\u+0118","Ę");
		fixes.put("\\\\u+0119","ę");
		fixes.put("\\\\u+011A","Ě");
		fixes.put("\\\\u+011B","ě");
		fixes.put("\\\\u+011C","Ĝ");
		fixes.put("\\\\u+011D","ĝ");
		fixes.put("\\\\u+011E","Ğ");
		fixes.put("\\\\u+011F","ğ");
		fixes.put("\\\\u+0120","Ġ");
		fixes.put("\\\\u+0121","ġ");
		fixes.put("\\\\u+0122","Ģ");
		fixes.put("\\\\u+0123","ģ");
		fixes.put("\\\\u+0124","Ĥ");
		fixes.put("\\\\u+0125","ĥ");
		fixes.put("\\\\u+0126","Ħ");
		fixes.put("\\\\u+0127","ħ");
		fixes.put("\\\\u+0128","Ĩ");
		fixes.put("\\\\u+0129","ĩ");
		fixes.put("\\\\u+012A","Ī");
		fixes.put("\\\\u+012B","ī");
		fixes.put("\\\\u+012C","Ĭ");
		fixes.put("\\\\u+012D","ĭ");
		fixes.put("\\\\u+012E","Į");
		fixes.put("\\\\u+012F","į");
		fixes.put("\\\\u+0130","İ");
		fixes.put("\\\\u+0131","ı");
		fixes.put("\\\\u+0132","Ĳ");
		fixes.put("\\\\u+0133","ĳ");
		fixes.put("\\\\u+0134","Ĵ");
		fixes.put("\\\\u+0135","ĵ");
		fixes.put("\\\\u+0136","Ķ");
		fixes.put("\\\\u+0137","ķ");
		fixes.put("\\\\u+0138","ĸ");
		fixes.put("\\\\u+0139","Ĺ");
		fixes.put("\\\\u+013A","ĺ");
		fixes.put("\\\\u+013B","Ļ");
		fixes.put("\\\\u+013C","ļ");
		fixes.put("\\\\u+013D","Ľ");
		fixes.put("\\\\u+013E","ľ");
		fixes.put("\\\\u+013F","Ŀ");
		fixes.put("\\\\u+0140","ŀ");
		fixes.put("\\\\u+0141","Ł");
		fixes.put("\\\\u+0142","ł");
		fixes.put("\\\\u+0143","Ń");
		fixes.put("\\\\u+0144","ń");
		fixes.put("\\\\u+0145","Ņ");
		fixes.put("\\\\u+0146","ņ");
		fixes.put("\\\\u+0147","Ň");
		fixes.put("\\\\u+0148","ň");
		fixes.put("\\\\u+0149","ŉ");
		fixes.put("\\\\u+014A","Ŋ");
		fixes.put("\\\\u+014B","ŋ");
		fixes.put("\\\\u+014C","Ō");
		fixes.put("\\\\u+014D","ō");
		fixes.put("\\\\u+014E","Ŏ");
		fixes.put("\\\\u+014F","ŏ");
		fixes.put("\\\\u+0150","Ő");
		fixes.put("\\\\u+0151","ő");
		fixes.put("\\\\u+0152","Œ");
		fixes.put("\\\\u+0153","œ");
		fixes.put("\\\\u+0154","Ŕ");
		fixes.put("\\\\u+0155","ŕ");
		fixes.put("\\\\u+0156","Ŗ");
		fixes.put("\\\\u+0157","ŗ");
		fixes.put("\\\\u+0158","Ř");
		fixes.put("\\\\u+0159","ř");
		fixes.put("\\\\u+015A","Ś");
		fixes.put("\\\\u+015B","ś");
		fixes.put("\\\\u+015C","Ŝ");
		fixes.put("\\\\u+015D","ŝ");
		fixes.put("\\\\u+015E","Ş");
		fixes.put("\\\\u+015F","ş");
		fixes.put("\\\\u+0160","Š");
		fixes.put("\\\\u+0161","š");
		fixes.put("\\\\u+0162","Ţ");
		fixes.put("\\\\u+0163","ţ");
		fixes.put("\\\\u+0164","Ť");
		fixes.put("\\\\u+0165","ť");
		fixes.put("\\\\u+0166","Ŧ");
		fixes.put("\\\\u+0167","ŧ");
		fixes.put("\\\\u+0168","Ũ");
		fixes.put("\\\\u+0169","ũ");
		fixes.put("\\\\u+016A","Ū");
		fixes.put("\\\\u+016B","ū");
		fixes.put("\\\\u+016C","Ŭ");
		fixes.put("\\\\u+016D","ŭ");
		fixes.put("\\\\u+016E","Ů");
		fixes.put("\\\\u+016F","ů");
		fixes.put("\\\\u+0170","Ű");
		fixes.put("\\\\u+0171","ű");
		fixes.put("\\\\u+0172","Ų");
		fixes.put("\\\\u+0173","ų");
		fixes.put("\\\\u+0174","Ŵ");
		fixes.put("\\\\u+0175","ŵ");
		fixes.put("\\\\u+0176","Ŷ");
		fixes.put("\\\\u+0177","ŷ");
		fixes.put("\\\\u+0178","Ÿ");
		fixes.put("\\\\u+0179","Ź");
		fixes.put("\\\\u+017A","ź");
		fixes.put("\\\\u+017B","Ż");
		fixes.put("\\\\u+017C","ż");
		fixes.put("\\\\u+017D","Ž");
		fixes.put("\\\\u+017E","ž");
		fixes.put("\\\\u+017F","ſ");
		fixes.put("\\\\u+2010","‐");
		fixes.put("\\\\u+2011","‑");
		fixes.put("\\\\u+2012","‒");
		fixes.put("\\\\u+2013","–");
		fixes.put("\\\\u+2014","—");
		fixes.put("\\\\u+2015","―");
		fixes.put("\\\\u+2016","‖");
		fixes.put("\\\\u+2017","‗");
		fixes.put("\\\\u+2018","‘");
		fixes.put("\\\\u+2019","’");
		fixes.put("\\\\u+201A","‚");
		fixes.put("\\\\u+201B","‛");
		fixes.put("\\\\u+201C","“");
		fixes.put("\\\\u+201D","”");
		fixes.put("\\\\u+201E","„");
		fixes.put("\\\\u+201F","‟");
		fixes.put("\\\\u+2020","†");
		fixes.put("\\\\u+2021","‡");
		fixes.put("\\\\u+2022","•");
		fixes.put("\\\\u+2023","‣");
		fixes.put("\\\\u+2024","․");
		fixes.put("\\\\u+2025","‥");
		fixes.put("\\\\u+2026","…");
		fixes.put("\\\\u+2027","‧");
		fixes.put("\\\\u+2028"," ");
		fixes.put("\\\\u+2029"," ");
		fixes.put("\\\\u+202A"," ");
		fixes.put("\\\\u+202B"," ");
		fixes.put("\\\\u+202C"," ");
		fixes.put("\\\\u+202D"," ");
		fixes.put("\\\\u+202E"," ");
		fixes.put("\\\\u+202F"," ");
		fixes.put("\\\\u+2030","‰");
		fixes.put("\\\\u+2031","‱");
		fixes.put("\\\\u+2032","′");
		fixes.put("\\\\u+2033","″");
		fixes.put("\\\\u+2034","‴");
		fixes.put("\\\\u+2035","‵");
		fixes.put("\\\\u+2036","‶");
		fixes.put("\\\\u+2037","‷");
		fixes.put("\\\\u+2038","‸");
		fixes.put("\\\\u+2039","‹");
		fixes.put("\\\\u+203A","›");
		fixes.put("\\\\u+203B","※");
		fixes.put("\\\\u+203C","‼");
		fixes.put("\\\\u+203D","‽");
		fixes.put("\\\\u+203E","‾");
		fixes.put("\\\\u+203F","‿");
		fixes.put("\\\\u+2040","⁀");
		fixes.put("\\\\u+2041","⁁");
		fixes.put("\\\\u+2042","⁂");
		fixes.put("\\\\u+2043","⁃");
		fixes.put("\\\\u+2044","⁄");
		fixes.put("\\\\u+2045","⁅");
		fixes.put("\\\\u+2046","⁆");
		fixes.put("\\\\u+2047","⁇");
		fixes.put("\\\\u+2048","⁈");
		fixes.put("\\\\u+2049","⁉");
		fixes.put("\\\\u+204A","⁊");
		fixes.put("\\\\u+204B","⁋");
		fixes.put("\\\\u+204C","⁌");
		fixes.put("\\\\u+204D","⁍");
		fixes.put("\\\\u+204E","⁎");
		fixes.put("\\\\u+204F","⁏");
		fixes.put("\\\\u+2050","⁐");
		fixes.put("\\\\u+2051","⁑");
		fixes.put("\\\\u+2052","⁒");
		fixes.put("\\\\u+2053","⁓");
		fixes.put("\\\\u+2054","⁔");
		fixes.put("\\\\u+2055","⁕");
		fixes.put("\\\\u+2056","⁖");
		fixes.put("\\\\u+2057","⁗");
		fixes.put("\\\\u+2058","⁘");
		fixes.put("\\\\u+2059","⁙");
		fixes.put("\\\\u+205A","⁚");
		fixes.put("\\\\u+205B","⁛");
		fixes.put("\\\\u+205C","⁜");
		fixes.put("\\\\u+205D","⁝");
		fixes.put("\\\\u+205E","⁞");
		fixes.put("\\\\u+20AC","€");
		fixes.put("\\\\u+2122","™");
		
		for ( String utf8 : fixes.keySet() ) {
			
			label = label.replaceAll(utf8, fixes.get(utf8));
		}
		return label;
	}
	
	private Map<String, String> createMapping() {

		Map<String,String> mapping =  new HashMap<String,String>();
		try {

			BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream("/Users/gerb/Downloads/02-09-2011/interlanguage_links_en.n3"))));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/Users/gerb/Downloads/02-09-2011/new_de_labels.n3"), "UTF-8"));
			
			String line;
			while ((line = br.readLine()) != null) {

				String[] lineParts = line.split(" ");

				if (!lineParts[2].startsWith("<http://el.") && !lineParts[2].startsWith("<http://db") ) {
					
					mapping.put(lineParts[2], lineParts[0]);
					writer.write(lineParts[2] + " "+ lineParts[1] +" "+ lineParts[0] + Constants.NEW_LINE_SEPARATOR);
				}
			}
			br.close();
			writer.close();
		}
		catch (Exception e) {

			e.printStackTrace();
		}
		
		return mapping;
	}
}
