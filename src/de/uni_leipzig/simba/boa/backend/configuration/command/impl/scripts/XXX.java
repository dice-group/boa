package de.uni_leipzig.simba.boa.backend.configuration.command.impl.scripts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

import de.danielgerber.evaluation.KappaScorer;
import de.danielgerber.file.BufferedFileReader;
import de.danielgerber.file.BufferedFileWriter;
import de.danielgerber.file.BufferedFileWriter.WRITER_WRITE_MODE;
import de.danielgerber.file.FileUtil;
import de.danielgerber.math.MathUtil;
import de.danielgerber.rdf.NtripleUtil;
import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureHelper;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl.Feature;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl.FeatureEnum;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.util.ListUtil;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;


public class XXX {

	// used for sentence segmentation
	private static Reader stringReader;
	private static DocumentPreprocessor preprocessor;
	private static StringBuilder stringBuilder;
	
	private NLPediaLogger logger = new NLPediaLogger(XXX.class);
	
	public static void main(String[] args) {

		NLPediaSetup setup = new NLPediaSetup(true);
		List<String> file1 = FileUtil.readFileInList(NLPediaSettings.BOA_BASE_DIRECTORY + "evaluation/3/Evaluation_3_Kappa_Upmeier.txt", "UTF-8");
		List<String> file2 = FileUtil.readFileInList(NLPediaSettings.BOA_BASE_DIRECTORY + "evaluation/3/Evaluation_3_Kappa_Haack.txt", "UTF-8");
		
		if ( file1.size() != file2.size() ) throw new RuntimeException("Files not of the same size");
		
		int oneYesTwoYes	= 0;
		int oneYesTwoNo		= 0;
		int oneNoTwoNo		= 0;
		int oneNoTwoYes		= 0;
		int lines			= 0;
		
		for (int i = 0; i < Math.min(file1.size(), file2.size()); i++) {

			// skip the lines with comments and empty lines
			if ( file1.get(i).startsWith("#") || file1.get(i).trim().isEmpty() || !file1.get(i).startsWith("[") ) continue;
			lines++;
			
			if ( file1.get(i).startsWith("[x]") && file2.get(i).startsWith("[x]") ) {
				oneYesTwoYes++;continue;
			}
			if ( file1.get(i).startsWith("[x]") && file2.get(i).startsWith("[o]") ) {
				oneYesTwoNo++;continue;
			}
			if ( file1.get(i).startsWith("[o]") && file2.get(i).startsWith("[x]") ) {
				oneNoTwoYes++;continue;
			}
			if ( file1.get(i).startsWith("[o]") && file2.get(i).startsWith("[o]") ) {
				oneNoTwoNo++;continue;
			}
			System.out.println(file1.get(i));
			System.out.println(file2.get(i));
		}
		System.out.println(KappaScorer.getCohenKappaScore(oneYesTwoYes, oneYesTwoNo, oneNoTwoYes, oneNoTwoNo, lines));
	}
	
	public void translateEnglishToKoreanLabels() {
		
		Map<String,String> uriToLabelMapping = NtripleUtil.getSubjectAndObjectsMappingFromNTriple("/Users/gerb/labels_ko.nt", "UTF-8");
		
		BufferedFileReader reader = FileUtil.openReader("/Users/gerb/en_relation_surface.txt");
		BufferedFileWriter writer = FileUtil.openWriter("/Users/gerb/ko_relation_surface.txt", "UTF-8", WRITER_WRITE_MODE.OVERRIDE);
		String line = "";

		while ((line = reader.readLine()) != null ) {
			
			String[] lineParts = line.split(" \\|\\|\\| ");
			
			String subjectUri = lineParts[0];
			String objectUri = lineParts[4];
			
			if ( uriToLabelMapping.containsKey(subjectUri) && uriToLabelMapping.containsKey(objectUri) ) {
				
				lineParts[1] = uriToLabelMapping.get(subjectUri);
				lineParts[2] = uriToLabelMapping.get(subjectUri);
				lineParts[5] = uriToLabelMapping.get(objectUri);
				lineParts[6] = uriToLabelMapping.get(objectUri);
				
				writer.write(StringUtils.join(lineParts, " ||| ") + "\n");
			}
		}
		reader.close();
		writer.close();
	}
	
	public void createPatternFeatureDistribution(){
		
		PatternMappingDao patternDao = (PatternMappingDao)DaoFactory.getInstance().createDAO(PatternMappingDao.class);
		List<Pair> xyz = new ArrayList<Pair>();		
		List<PatternMapping> patternMappings = patternDao.findAllPatternMappings();
		for ( PatternMapping pm : patternMappings ){
			for ( Pattern p :pm.getPatterns()) {
				
				xyz.add(new Pair(p, pm));
			}
		}
		Collections.sort(xyz, new Comparator<Pair>(){

			@Override
			public int compare(Pair arg0, Pair arg1) {

				double x = (arg1.pattern.getScore() - arg0.pattern.getScore());
				if ( x < 0 ) return -1;
				if ( x == 0 ) return 0;
				return 1;
			}
		
		});
		System.out.println("Loading and sorting done");
		System.out.println("Confidence of pattern(0): " + xyz.get(0).pattern.getScore());
		System.out.println("Confidence of pattern(10): " + xyz.get(10).pattern.getScore());
		System.out.println("Confidence of pattern(n): " + xyz.get(xyz.size()-1).pattern.getScore());
		
		List<List<Pair>> subLists = ListUtil.split(xyz, xyz.size() / 10);
		List<Map<Feature,List<Double>>> features = new ArrayList<Map<Feature,List<Double>>>();
		List<Map<Feature,Double>> meanFeatures = new ArrayList<Map<Feature,Double>>();
		
		for (int i = 0; i < subLists.size() ; i++ ) {
			features.add(new HashMap<Feature,List<Double>>());
			
			for (Pair pair : subLists.get(i)) {
				for (Feature f : FeatureFactory.getInstance().getFeatureMap().values()) {
					
						if ( features.get(i).containsKey(f) ) {

							if ( pair.pattern.getFeatures().get(f) != null ) {
								
								double ff = this.normalizeFeature(f, pair.mapping, pair.pattern.getFeatures().get(f));
								features.get(i).get(f).add(ff);
							}
						}
						else {
							
							if ( pair.pattern.getFeatures().get(f) != null ) {
								
								List<Double> d = new ArrayList<Double>();
								double ff = this.normalizeFeature(f, pair.mapping, pair.pattern.getFeatures().get(f));
								d.add(ff);
								features.get(i).put(f, d);
							}
						}
				}
			}
//			System.out.println(features.get(i));
		}
		System.out.println("Pairs normalized!");		
		for ( int i = 0; i < subLists.size() ; i++) {

			meanFeatures.add(new TreeMap<Feature,Double>());
			
			Map<Feature,List<Double>> featureList = features.get(i);
				
			for ( Map.Entry<Feature, List<Double>> f : featureList.entrySet() ) {
				
				meanFeatures.get(i).put(f.getKey(), MathUtil.getAverage(f.getValue()));
			}
		}
		System.out.println("Pairs averaged!");
		for (Map<Feature,Double> means : meanFeatures) {
			
			System.out.println(StringUtils.join(means.values(), "\t").replace(".", ","));
		}
	}
	
	public void generateIndexStatistics() {

//		String[] indexDirs = new String[]{"/Users/gerb/Development/workspaces/experimental/en_wiki_exp/index/stanfordnlp"};
		
		String[] indexDirs = new String[]{	
				"/home/gerber/nlpedia-data/en_wiki_exp/index/stanfordnlp",
//				"/home/gerber/nlpedia-data/de_news_exp/index/stanfordnlp",
//				"/home/gerber/nlpedia-data/en_news_exp/index/stanfordnlp"
				"/home/gerber/nlpedia-data/de_wiki_exp/index/stanfordnlp"
				};
		
		try {
			
			PrintStream newErr = new PrintStream(new ByteArrayOutputStream());
			System.setErr(newErr);
			
			String indexDir = null;
			IndexSearcher indexSearcher = null;
			
			indexDir = indexDirs[0];

			for (String dir : indexDirs) {

				long words = 0L;
				Set<String> uniqueWords = new HashSet<String>();
				
				indexSearcher = new IndexSearcher(FSDirectory.open(new File(dir)), true);
				for ( int i = 0 ; i < indexSearcher.maxDoc() - 2 ; i++) {
					
					if ( i % 10000000 == 0 ) System.out.println("Sentence " + i);
					if ( i % 1000000 == 0 ) this.logger.info("Sentence " + i);
					
					String sentence = indexSearcher.doc(i).get("sentence");
					
					if ( indexDir.contains("news") ) {
						
						// count those words because we want to remove them
						words += StringUtils.countMatches(sentence, ",");
						words += StringUtils.countMatches(sentence, "''");
						words += StringUtils.countMatches(sentence, ".");
						words += StringUtils.countMatches(sentence, "\"");
						// remove the words
						sentence = sentence.replaceAll(",", "").replace("''", "").replace(".", "").replace("\"", "");
						// split the remaining and add them to the word list number
						String[] sentenceParts = sentence.split(" ");
						words += sentenceParts.length;						
						uniqueWords.addAll(new HashSet<String>(Arrays.asList(sentenceParts)));
					}
					else {
						
						String[] sentenceParts = sentence.split(" ");
						uniqueWords.addAll(new HashSet<String>(Arrays.asList(sentenceParts)));
						words += sentenceParts.length;
					}
				}
				this.logger.info(dir + ": " + indexSearcher.maxDoc() + " sentences");
				this.logger.info(dir + ": " + uniqueWords.size() + " unique words");
				this.logger.info(dir + ": " + words + " words");
				System.out.println(dir + ": " + indexSearcher.maxDoc() + " sentences");
				System.out.println(dir + ": " + uniqueWords.size() + " unique words");
				System.out.println(dir + ": " + words + " words");
				indexSearcher.close();
			}
		}
		catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String segmentString(String sentence) {
		
		try {
			
			stringReader = new StringReader(sentence);
			preprocessor = new DocumentPreprocessor(stringReader,  DocumentPreprocessor.DocType.Plain);
			
			Iterator<List<HasWord>> iter = preprocessor.iterator();
			while ( iter.hasNext() ) {
				
				stringBuilder = new StringBuilder();
				
				for ( HasWord word : iter.next() ) {
					stringBuilder.append(word.toString() + " ");
				}
				return stringBuilder.toString().trim();
			}
		}
		catch (ArrayIndexOutOfBoundsException aioobe) {
			
			throw new RuntimeException(aioobe);
		}
		return "";
	}
	
	private static void createEvalFiles() throws IOException {
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream("/Users/gerb/Evaluation_2_Upmeier_1000.txt"))));
		Writer writer = new PrintWriter(new BufferedWriter(new FileWriter("/Users/gerb/Evaluation_2_Upmeier_1000.txt.1")));
		
		String line;
		int i = 1;
		while ((line = reader.readLine()) != null) {
			
			writer.write(i++ +". "+ line + Constants.NEW_LINE_SEPARATOR);
			writer.write("[]" + Constants.NEW_LINE_SEPARATOR);
			writer.write(Constants.NEW_LINE_SEPARATOR);
		}
		reader.close();
		writer.close();
	}
	
	public static void getIndexedSentencesCount() throws CorruptIndexException, IOException {
		
		String[] indexDirs = new String[]{	"/home/gerber/nlpedia-data/en_wiki_exp/index/stanfordnlp",
											"/home/gerber/nlpedia-data/en_news_exp/index/stanfordnlp",
											"/home/gerber/nlpedia-data/de_wiki_exp/index/stanfordnlp",
											"/home/gerber/nlpedia-data/de_news_exp/index/stanfordnlp"};
		String indexDir = null;
		IndexSearcher indexSearcher = null;
		
		indexDir = indexDirs[0];
		indexSearcher = new IndexSearcher(FSDirectory.open(new File(indexDir)), true);
		System.out.println("en_wiki_exp: " + indexSearcher.maxDoc() + " sentences");
		indexSearcher.close();
		
		indexDir = indexDirs[1];
		indexSearcher = new IndexSearcher(FSDirectory.open(new File(indexDir)), true);
		System.out.println("en_news_exp: " + indexSearcher.maxDoc() + " sentences");
		indexSearcher.close();
		
		indexDir = indexDirs[2];
		indexSearcher = new IndexSearcher(FSDirectory.open(new File(indexDir)), true);
		System.out.println("de_wiki_exp: " + indexSearcher.maxDoc() + " sentences");
		indexSearcher.close();
		
		indexDir = indexDirs[3];
		indexSearcher = new IndexSearcher(FSDirectory.open(new File(indexDir)), true);
		System.out.println("de_news_exp: " + indexSearcher.maxDoc() + " sentences");
		indexSearcher.close();
	}
	
	private static void removeDuplicateLinesFromRelationFile() throws UnsupportedEncodingException, FileNotFoundException, IOException {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream("/Users/gerb/plain_relation.txt"))));
		Writer writer = new PrintWriter(new BufferedWriter(new FileWriter("/Users/gerb/plain_relation_without_duplicates.txt")));
		
		Set<String> relations = new TreeSet<String>();
		String line;
		while ((line = br.readLine()) != null) {
			
			relations.add(line);
		}
		for (String s : relations ){
			
			writer.write(s+ Constants.NEW_LINE_SEPARATOR);
		}
		writer.close();
	}
	
	private static void removeSurfaceFormsFromRelationFile() throws UnsupportedEncodingException, FileNotFoundException, IOException{
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream("/Users/gerb/Development/workspaces/experimental/files/en_surface.txt"))));
		
		Writer writer = new PrintWriter(new BufferedWriter(new FileWriter("/Users/gerb/plain_relation.txt")));
		
		// 0_URI1 ||| 1_LABEL1 ||| 2_LABELS1 ||| 3_PROP ||| 4_URI2 ||| 5_LABEL2 ||| 6_LABELS2 ||| 7_RANGE ||| 8_DOMAIN
		
		String line;
		while ((line = br.readLine()) != null) {

			String[] l = line.split(" \\|\\|\\| ");
			String[] newLine = new String[l.length-2];
			for ( int i = 0, j = 0; i < l.length ; i++) {
				
				if ( i != 2 && i != 6 ) {
					
					newLine[j] = l[i];
					j++;
				}
			}
			if ( newLine.length != 7 ) System.out.println("Not 7 length: " + Arrays.toString(newLine));
			if ( !newLine[2].startsWith("http://dbpedia.org/ontology/")) System.out.println("Property flawed: " + Arrays.toString(newLine));
			
			writer.write(StringUtils.join(newLine, " ||| ") + Constants.NEW_LINE_SEPARATOR);
		}
		writer.close();
		br.close();
	}
	
	// this belongs to method for viewing features
	
	private class Pair {
		
		public Pair(Pattern p, PatternMapping pm) {

			this.pattern = p;
			this.mapping = pm;
		}
		Pattern pattern;
		PatternMapping mapping;
	}
	
	private double normalizeFeature(Feature feature, PatternMapping mapping, Double value) {

		if ( feature.getSupportedLanguages().contains(NLPediaSettings.getInstance().getSystemLanguage()) ) {
			
			// exclude everything which is not activated
			if ( feature.isUseForPatternLearning() ) {
				
				// non zero to one values have to be normalized
				if ( !feature.isZeroToOneValue() ) {
					
					Double maximum = 0D;
					// take every mapping into account to find the maximum value
					if ( feature.isNormalizeGlobaly() ) {
						
						maximum = FeatureHelper.calculateGlobalMaximum(feature);
					}
					// only use the current mapping to find the maximum
					else {
						
						maximum = FeatureHelper.calculateLocalMaximum(mapping, feature);
					}
					return value / maximum;
				}
				// we dont need to normalize a 0-1 value
				else {
					
					return value;
				}
			}
		}
		return 0;
	}
}
