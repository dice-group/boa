package de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.partofspeechtagger.impl;

import java.io.IOException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.StringTokenizer;

import kr.ac.kaist.swrc.jhannanum.hannanum.Workflow;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.ChartMorphAnalyzer.ChartMorphAnalyzer;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.PosTagger.HmmPosTagger.HMMTagger;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor.UnknownMorphProcessor.UnknownProcessor;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.InformalSentenceFilter.InformalSentenceFilter;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.SentenceSegmentor.SentenceSegmentor;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.partofspeechtagger.PartOfSpeechTagger;
import de.uni_leipzig.simba.boa.backend.nlp.PosTagger;

public class KoreanPartOfSpeechTagger  implements PartOfSpeechTagger{
	private final NLPediaLogger logger = new NLPediaLogger(PosTagger.class);
	private static KoreanPartOfSpeechTagger INSTANCE		= null;
	
	private Workflow wf;
	
	/**
	 * @return
	 */
	public static KoreanPartOfSpeechTagger getInstance() {
		
		if ( KoreanPartOfSpeechTagger.INSTANCE == null ) {			
			KoreanPartOfSpeechTagger.INSTANCE = new KoreanPartOfSpeechTagger();
		}
		
		return KoreanPartOfSpeechTagger.INSTANCE;
	}
	
	private KoreanPartOfSpeechTagger(){
		try {
			String baseDir	= NLPediaSettings.BOA_BASE_DIRECTORY + NLPediaSettings.getInstance().getSetting("korPosTaggerResource");
			wf				= new Workflow();
			wf.appendPlainTextProcessor(new SentenceSegmentor(), null);
			wf.appendPlainTextProcessor(new InformalSentenceFilter(), null);
			wf.setMorphAnalyzer(new ChartMorphAnalyzer(), baseDir + "/conf/plugin/MajorPlugin/MorphAnalyzer/ChartMorphAnalyzer.json", baseDir );
			wf.appendMorphemeProcessor(new UnknownProcessor(), null);
			wf.setPosTagger(new HMMTagger(), baseDir + "/conf/plugin/MajorPlugin/PosTagger/HmmPosTagger.json", baseDir);
			
			wf.activateWorkflow(false);
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Analyze morphemes of the given sentence, and add POS tags to it.
	 * Returns empty string if the POS tagging fails.
	 * @param sentence the target sentence
	 * @return POS tagged result.
	 */
	public String getAnnotatedString(String sentence){	
		ArrayList<String> perLineResult		= tagSentencePerWord(sentence);
		if(perLineResult == null){
			// POS Tagging Failed!
			return "";
		}
		String ret							= "";
		Iterator<String> resultIter			= perLineResult.iterator();
		while(resultIter.hasNext()){
			resultIter.next();
			ret					+= resultIter.next() + " ";
		}

		return ret.trim();
	}
	
	/**
	 * Analyze morphemes of the given sentence, and add POS tags to it.
	 * Returns empty string if the POS tagging fails.
	 * @param sentence the target sentence
	 * @return POS tagged result.
	 */
	public String getAnnotations(String sentence){	
		ArrayList<String> perLineResult		= tagSentencePerWord(sentence);
		if(perLineResult == null){
			// POS Tagging Failed!
			return "";
		}
		String ret							= "";
		Iterator<String> resultIter			= perLineResult.iterator();
		while(resultIter.hasNext()){
			resultIter.next();
			String taggedR		= resultIter.next();
			for (String s : taggedR.split(" ")){
				ret				+= s.substring(s.lastIndexOf('/') + 1) + " ";
			}
		}

		return ret.trim();
	}
	

	
	/**
	 * Since the form of Korean texts after morphological analysis could be greatly different from the original text,
	 * We provide a result in which one line represents one word.
	 * First line - original text, second line - POS-tagged.
	 * Returns null if the POS tagging fails.
	 * @return
	 */
	public ArrayList<String> tagSentencePerWord(String sentence){
		sentence				= sentence.trim();		
		ArrayList<String> ret	= new ArrayList<String>();
		String result			= "";
		try{
			wf.analyze(sentence);
			result				= wf.getResultOfDocument();
			StringTokenizer tok	= new StringTokenizer(result, "\n");
	
			while(tok.hasMoreElements()){
				ret.add(tok.nextToken());
				String analyzed	= tok.nextToken().trim();
				String lineAnal	= "";
				StringTokenizer tok2	= new StringTokenizer(analyzed, "+");
				while(tok2.hasMoreElements()){
					String token	= tok2.nextToken();
					int idxSlash	= token.lastIndexOf('/');
					while(idxSlash < 0){
						token		+= tok2.nextToken();
						idxSlash	= token.lastIndexOf('/');
					}
					lineAnal			= lineAnal + token.substring(0, idxSlash) + "_" + token.substring(idxSlash + 1) + " ";					
				}
				ret.add(lineAnal.trim());
			}
		}catch(NullPointerException ne){
			// The POS tagger fails to process the sentence.
			// It fails when the input contains single Korean character, which is very unrealistic in real texts (except linguistic things).
			// Example: 체로키를 뜻하는 낱말인 tsalagi는 로마자로 옮겨 쓰면 ts, l, g가 되는데 이는 실제로는 한국어 ᄌ, ᄅ, ᄀ 에 더 가깝다.
			// Luckily, there are not so many case (Total 6 sentences for the whole corpus), so for now I just ignored that sentence.			
			return null;
		}catch(Exception e){
			System.out.println(sentence);
			System.out.println(result);
			e.printStackTrace();
		}
		return ret;
	}
	
//	/**
//	 * all input strings get trimmed
//	 * 
//	 * @param sentence the sentence to be tagged in this case a pattern most likely
//	 * @param label1 the label left of the pattern
//	 * @param label2 the label right of the pattern
//	 * @return an pos annotated string
//	 */
//	public String getPosTagsForSentence(String sentence, String label1, String label2) {
//		
//		// add the surfaceForms ot the front/end to improve accuracy and tag it 
//		String[] taggedSentence = this.tagSentence(label1.trim() + " " + sentence.trim() + " " + label2.trim()).split(" ");
//		String[] sentenceWithoutLabels = // remove the tagged surfaceForms 
//			Arrays.copyOfRange(taggedSentence, label1.split(" ").length, taggedSentence.length - label2.split(" ").length );
//		// remove the words, to only have the pos tags
//		StringBuilder builder = new StringBuilder();
//		for ( String s : sentenceWithoutLabels ) {
//			
//			builder.append(s.substring(s.lastIndexOf("/") + 1) + " ");
//		}
//		return builder.toString().trim();
//	}
	

}
