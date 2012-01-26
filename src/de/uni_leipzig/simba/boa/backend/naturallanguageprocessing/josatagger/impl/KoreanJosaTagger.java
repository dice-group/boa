package de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.josatagger.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.NaturalLanguageProcessingToolFactory;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.josatagger.JosaTagger;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.partofspeechtagger.impl.KoreanPartOfSpeechTagger;

/**
 * This class separates Korean JOSA from its body.
 * 
 * @author cdh4696
 *
 */
public class KoreanJosaTagger implements JosaTagger{
	private KoreanPartOfSpeechTagger tagger	= ((KoreanPartOfSpeechTagger)NaturalLanguageProcessingToolFactory.getInstance().createDefaultPartOfSpeechTagger());
	
	
	/**
	 * This class gets a Korean sentence as input, and returns the JOSA-separated Korean sentence as its result.
	 *  
	 * @param origSen The original sentence
	 * @return
	 */
	public String getJosaSeparatedSentence(String origSen){
		origSen								= origSen.trim();
		ArrayList<String> perLineResult		= tagger.tagSentencePerWord(origSen);
		if(perLineResult == null){
			// POS Tagging Failed. Just returns the original sentence.
			return origSen;
		}
		Iterator<String> rIter				= perLineResult.iterator();
		String ret							= "";
		try{			
			while(rIter.hasNext()){
//				나는 집에 갑니다.
//				나/noun 는/j 집/n 에/j 가/v ㅂ/e 니다/e
//
//				나는
//				나/noun 는/j
//				집에
//				집/n 에/j
//				갑니다.
//				 가/v ㅂ/e 니다/e ./sf
				
				String origTxt					= rIter.next(); // 나는
				String analyzed					= rIter.next(); // 나/n 는/j
				int firstJosa					= findFirstJOSAIndex(analyzed);
				if(firstJosa < 0){				// No JOSA in this token. Just carry the result.
					int startIdx	= origSen.indexOf(origTxt);
					ret				+= origSen.substring(0, startIdx + origTxt.length());
					origSen			= origSen.substring(startIdx + origTxt.length());
				}else{																			// JOSA found in this token. find out exact location of JOSA, and put a spacebar in between.
					int index		= findJOSACuttingLocation(origTxt, analyzed, firstJosa);	// Found corresponding location of JOSA in original text.
					int startIdx	= origSen.indexOf(origTxt);					
					ret				+= origSen.substring(0, startIdx + index) + " " + origSen.substring(startIdx + index, startIdx + origTxt.length());
					origSen			= origSen.substring(startIdx + origTxt.length());
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
				
		return ret;		
	}
	
	/**
	 * Find out exactly which position the spacebar should be inserted, to separate JOSA from other parts.
	 * @param origTxt original text before morphological analysis & POS tagging.
	 * @param analyzed Analysis result of origTxt, with morphological analysis result along with POS tagging result.
	 * @param firstJOSA location of the first JOSA in the input analyzed. The function should find out the corresponding location of firstJOSA in origTxt.
	 * @return The corresponding location of firstJOSA in origTxt.
	 */
	private static int findJOSACuttingLocation(String origTxt, String analyzed, int firstJosa){
		String nounPart	= analyzed.substring(0, firstJosa);		// Those containing POS tagging results of NON-JOSA.
		StringTokenizer nounTokens	= new StringTokenizer(nounPart, " ");
		int index		= 0;
		while(nounTokens.hasMoreElements()){
			String nt	= nounTokens.nextToken();
			index		+=  nt.lastIndexOf('_');
		}
		
		if(index >= origTxt.length()){ 							// The text is "spreaded" after morphological analysis. We will count from backward.				
			String josaPart	= analyzed.substring(firstJosa + 1);
			StringTokenizer josaTokens	= new StringTokenizer(josaPart, " ");
			josaTokens.nextToken();
			index		= origTxt.length();
			while(josaTokens.hasMoreElements()){
				String jt	= josaTokens.nextToken();
				index		-= jt.lastIndexOf('_');
			}						
		}
		return index;
	}
	
	/**
	 * Locate first index of JOSA from the given sentence.
	 * Return -1 if not found.
	 * @param analyzed POS tagged result.
	 * @return
	 */
	private static int findFirstJOSAIndex(String analyzed){
		try{
			String[] analArr	= analyzed.split(" ");
			for(int i = analArr.length - 1; i >= 0; i--){
				String pos	= getPOS(analArr[i]);
				if(pos.equals("")){
					// A very exotic case.
					// A token contains a spacebar!					
					// In this case, since the watching token is one body to the previous token (which is already seen), 
					// Thus we can just continue for this case.
					// Example: 0500 (yekt)_ 0600 (yekst)_ncn
					continue;
				}
				if(pos.charAt(0) == 'j'){	// JOSA found.
					return analyzed.lastIndexOf(analArr[i]);
				}
			}
		}catch(Exception e){
			System.out.println("JOSA DETECTION FAIL: " + analyzed);
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * Get the POS of the given POS analysis token.
	 * Input Example: 어_ecx
	 * @param POSToken
	 * @return
	 */
	private static String getPOS(String POSToken){
		int idx	= POSToken.lastIndexOf('_');
		return POSToken.substring(idx + 1);
	}
}
