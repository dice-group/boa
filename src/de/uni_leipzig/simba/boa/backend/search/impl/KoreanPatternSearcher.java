package de.uni_leipzig.simba.boa.backend.search.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.ScoreDoc;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.lucene.LuceneIndexHelper;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.partofspeechtagger.impl.KoreanPartOfSpeechTagger;

public class KoreanPatternSearcher extends DefaultPatternSearcher{
	
	/**
	 * Class to store the POS tagging result for one word (for Korean).
	 * 
	 * For example, for Korean word "측천무후의":
	 *  orig	= "측천무후의"
	 *  ttArr	= { {측천무후, ncn}, {의, j}}
	 * 
	 * @author user
	 *
	 */
	class POSTaggingResult{
		public class TaggedToken{
			public String token;
			public String POS;
		}
		public String orig;
		public int offset;			// Offset of the starting point inside the sentence
		public TaggedToken[] ttArr;	// POS Tagging result.
	}
	
	/**
	 * Starting offsets of two labels inside the sentence.
	 * Intermediate result before constructing the results.
	 * 
	 * @author user
	 *
	 */
	class LabelOffset{
		public int firstIndex;
		public int secondIndex;
		public boolean isReverse	= false;
	}
	
	@ Override
	protected List<String> findMatchedText(String sentence, String firstLabel, String secondLabel){
		sentence					= sentence.toLowerCase();
		POSTaggingResult[] wtr		= analyzePOS(sentence);
		List<String> currentMatches = new ArrayList<String>();
		firstLabel					= firstLabel.trim();
		secondLabel					= secondLabel.trim();
		
		int[] labelJudge			= new int[sentence.length()];
		initializeIntegerArr(labelJudge);		
		locateWords(sentence, firstLabel, labelJudge, 1);
		locateWords(sentence, secondLabel, labelJudge, 2);
		
		List<LabelOffset> loList	= getLabelOffsets(labelJudge);
		
		for(LabelOffset lo : loList){
			// Get pattern, and put it into the list!
			String match			= getPattern(sentence, wtr, lo, firstLabel, secondLabel);
			int ptnKind				= getPatternKind(match);
			String actualMatch		= refinePattern(match, ptnKind);
			currentMatches.add(actualMatch);
		}
        return currentMatches;
	}
	
	
	/**
	 * Analyze POS of the given Korean sentence,
	 * and refine the result into the form of array.
	 * 
	 * ret[i] = the analysis result of a word, whos position includes i-th offset of the sentence.
	 * 
	 * @param sentence
	 * @return
	 */
	public POSTaggingResult[] analyzePOS(String sentence){
		
		KoreanPartOfSpeechTagger kpos	= (KoreanPartOfSpeechTagger)this.posTagger;
		ArrayList<String> posTagged		= kpos.tagSentencePerWord(sentence);
		Iterator<String> posIter		= posTagged.iterator();

		POSTaggingResult current		= null;
		POSTaggingResult[] ret			= new POSTaggingResult[sentence.length() + 1];
		for(int i = 0; i < sentence.length(); i++){
			if(sentence.charAt(i) == ' ' || sentence.charAt(i) == '\t'){
				ret[i]					= current;
			}else{
				POSTaggingResult tr		= new POSTaggingResult();
				tr.offset				= i;
				String origWord			= posIter.next();
				String analResult		= posIter.next().trim();
				StringTokenizer eachAnal	= new StringTokenizer(analResult, " ");
				tr.ttArr					= new POSTaggingResult.TaggedToken[eachAnal.countTokens()];
				tr.orig						= origWord;
				int cnt				= 0;
				while(eachAnal.hasMoreElements()){
					String nextAnal		= eachAnal.nextToken();
					while(nextAnal.indexOf('_') == -1){
						nextAnal		+= eachAnal.nextToken();
					}
					tr.ttArr[cnt]		= tr.new TaggedToken();
					tr.ttArr[cnt].token	= nextAnal.substring(0, nextAnal.lastIndexOf('_'));
					tr.ttArr[cnt].POS	= nextAnal.substring(nextAnal.lastIndexOf('_') + 1);
					cnt++;
				}
				if(cnt < tr.ttArr.length){
					POSTaggingResult.TaggedToken[] tmp	= new POSTaggingResult.TaggedToken[cnt];
					for(int j = 0; j < tmp.length; j++){
						tmp[j]			= tr.ttArr[j];
					}
					tr.ttArr			= tmp;
				}
					
				for(int j = i; j < (i + origWord.length()); j++){
					ret[j]				= tr;
				}
				i						+= (origWord.length() - 1);
				current					= tr;
			}	
		}
		ret[ret.length - 1]	= ret[ret.length - 2];
		return ret;
	}
	
	private void initializeIntegerArr(int[] arr){
		for(int i = 0; i < arr.length; i++){
			arr[i]	= 0;
		}
	}
	
	/**
	 * Find the given label from the given sentence, and mark its starting position to the arr using the given label code.
	 * @param sentence
	 * @param label
	 * @param arr output
	 * @param labelCode
	 */
	private void locateWords(String sentence, String label, int[] arr, int labelCode){
		int startIdx				= 0;
		while(true){
			startIdx				= sentence.indexOf(label, startIdx);
			if(startIdx >= 0){
				arr[startIdx]		= labelCode;
				startIdx			+= label.length();
			}else{
				break;
			}
		}		
	}
	
	/**
	 * Retrieve label offset pair for pattern extraction.
	 * 
	 * @return
	 */
	private List<LabelOffset> getLabelOffsets(int[] labelJudge){
		List<LabelOffset> ret		= new ArrayList<LabelOffset>();
		int prevLabel				= 0;
		int prevOffset				= -1;
		for(int i = 0; i < labelJudge.length; i++){
			if(labelJudge[i] != 0){
				if(prevLabel == 0){
					prevLabel 		= labelJudge[i];
					prevOffset		= i;
				}else if(prevLabel != labelJudge[i]){
					// New pair discovered.
					LabelOffset lo	= new LabelOffset();
					lo.firstIndex	= prevOffset;
					lo.secondIndex	= i;					
					prevLabel			= labelJudge[i];
					prevOffset			= i;
					if(labelJudge[i] == 1){
						lo.isReverse	= true;
					}
					ret.add(lo);
				}else{
					// Duplication of the same label.
					prevOffset			= i;
				}
			}
		}		
		return ret;
	}
	
	/**
	 * Extract pattern from the sentence using the information given by LabelOffset.
	 * 
	 * @param sentence
	 * @param lo
	 * @return
	 */
	private String getPattern(String sentence, POSTaggingResult[] wtr, LabelOffset lo, String firstLabel, String secondLabel){
		// Method 1. Just in-between.
//		return getPatternJustInBetween(sentence, lo, firstLabel, secondLabel);
		
		// Method 2. Til first verb.
		return getPatternTilFirstVerb(sentence, wtr, lo, firstLabel, secondLabel);
	}
	
	/**
	 * Baseline method for pattern extraction.
	 * Get those just in-between.
	 * @param sentence
	 * @param lo
	 * @param firstLabel
	 * @param secondLabel
	 * @return
	 */
	private String getPatternJustInBetween(String sentence, LabelOffset lo, String firstLabel, String secondLabel){
		if(!lo.isReverse){
			return "?D? " + sentence.substring(lo.firstIndex + firstLabel.length(), lo.secondIndex) + " ?R?";
		}
		return "?R? " + sentence.substring(lo.firstIndex + secondLabel.length(), lo.secondIndex) + " ?D?";
	}
	
	/**
	 * Locate first verb after second offset, and create patterns til that.
	 * @param sentence
	 * @param lo Location information of the first/second labels.
	 * @param firstLabel  Desired first label.
	 * @param secondLabel Desired second label.
	 * @return
	 */
	private String getPatternTilFirstVerb(String sentence, POSTaggingResult[] wtr, LabelOffset lo, String firstLabel, String secondLabel){
		// Find first verb after the second argument.
		int findIdx	= lo.secondIndex - 1;
		if(lo.isReverse){
			findIdx	+= firstLabel.length();
		}else{
			findIdx	+= secondLabel.length();
		}
		int firstVerbIndex	= findFirstVerbIdx(wtr, findIdx);
		if(firstVerbIndex == -1){
			firstVerbIndex	= sentence.length();
		}
		
		
		
		if(!lo.isReverse){
			String ptn	=  "?D? " + sentence.substring(lo.firstIndex + firstLabel.length(), lo.secondIndex) + " ?R? " + sentence.substring(lo.secondIndex + secondLabel.length(), firstVerbIndex);
//			System.out.println(sentence + "\n" + ptn);
			return ptn;
		}
		
		String ptn	= "?R? " + sentence.substring(lo.firstIndex + secondLabel.length(), lo.secondIndex) + " ?D? " + sentence.substring(lo.secondIndex + firstLabel.length(), firstVerbIndex);
		return ptn;
	}
	
	/**
	 * Returns last index of the first verb.
	 * @return
	 */
	private int findFirstVerbIdx(POSTaggingResult[] wtr, int startIdx){
		POSTaggingResult prev	= wtr[startIdx];		// Start looking from the next words.
		for(int i = startIdx; i < wtr.length; i++){
			if(prev == wtr[i]){
				continue;
			}
			
			int internalOffset	= 0;
			for(int j = 0; j < wtr[i].ttArr.length; j++){
				if(wtr[i].ttArr[j].POS.startsWith("pv") || wtr[i].ttArr[j].POS.startsWith("nc") || wtr[i].ttArr[j].POS.startsWith("e")){
					return wtr[i].offset + internalOffset + wtr[i].ttArr[j].token.length(); 
				}
				internalOffset	+= wtr[i].ttArr[j].token.length();
			}
			prev	= wtr[i];
		}
		
		// No verb exists.
		return -1;
	}
	
	@Override
	protected String getCorrectCaseNLR(String lowerCase, String normalCase, String pattern) {
		return pattern;
	}
	
	/**
	 * Judge the kind of the given pattern.
	 * Returns 1 for possesive patterns.
	 * Returns 2 for predicative patterns.
	 * 
	 * @param pattern
	 * @return
	 */
	private int getPatternKind(String pattern){
		int firstValIdx		= pattern.indexOf("?D?");
		int secondValIdx	= pattern.indexOf("?R?");
		if(firstValIdx > secondValIdx){
			int tmp			= firstValIdx;
			firstValIdx		= secondValIdx;
			secondValIdx	= tmp;
		}
		
		char firstJosa	= pattern.substring(firstValIdx + 3).trim().charAt(0);
		if(firstJosa == '의'){	// Possesive.
			return 1;
		}
		if(firstJosa == '와' || firstJosa == '과'){	// Conjunctive.
			int intermediatePatternLength	= pattern.substring(firstValIdx + 3, secondValIdx).trim().length();
			// Is the intermediate pattern really meaningful?
			if(intermediatePatternLength > 2){
				return 1;
			}
		}
		
		if(firstJosa == '은' || firstJosa == '는'){	// Subjective.
			int intermediatePatternLength	= pattern.substring(firstValIdx + 3, secondValIdx).trim().length();
			// If there exists noun in between, it should return 1.
			// TO BE IMPLEMENTED.
		}
		
		return 2;
	}
	
	/**
	 * Remove redundant part of the pattern based on its kind.
	 * 	 
	 * @param pattern
	 * @param ptnKind 1 for possesive pattern, 2 for predicative pattern.
	 * @return
	 */
	private String refinePattern(String pattern, int ptnKind){
		int firstValIdx		= pattern.indexOf("?D?");
		int secondValIdx	= pattern.indexOf("?R?");
		if(firstValIdx > secondValIdx){
			int tmp			= firstValIdx;
			firstValIdx		= secondValIdx;
			secondValIdx	= tmp;
		}
		
		if(ptnKind == 1){
			return pattern.substring(firstValIdx, secondValIdx + 3);
		}
		
		return pattern.substring(firstValIdx, firstValIdx + 3) + " " + pattern.substring(secondValIdx, pattern.length());
	}
	
	@Override
	protected Set<String> getSentencesFromIndex(ScoreDoc[] hits) {
	
	    Set<String> sentences = new HashSet<String>();
        
        // collect all sentences
        for ( int n = 0 ; n < hits.length; n++){
            
            sentences.add(hits[n].doc + " " + LuceneIndexHelper.getFieldValueByDocId(indexSearcher, hits[n].doc, "originalsentence"));
        }
        return sentences;
	}
	
	@Override
	public boolean isPatternSuitable(String naturalLanguageRepresentation) {
		String patternWithoutVariables = naturalLanguageRepresentation
				.substring(0, naturalLanguageRepresentation.length() - 3)
				.substring(3).trim();

		// patterns are only allowed to have 256 characters
		if (naturalLanguageRepresentation.length() > 256
				|| naturalLanguageRepresentation.isEmpty())
			return false;

		// pattern need to start with either ?D? or ?R? and have to end with ?D?
		// or ?R?
		if ((!naturalLanguageRepresentation.startsWith("?D?") && !naturalLanguageRepresentation
				.startsWith("?R?"))
				|| (!naturalLanguageRepresentation.endsWith("?D?") && !naturalLanguageRepresentation
						.endsWith("?R?"))
				&& (!naturalLanguageRepresentation.startsWith("?D? ?R?") && !naturalLanguageRepresentation
						.startsWith("?R? ?D?")))
			return false;

		// patterns need to have only one domain and only one range
		if (StringUtils.countMatches(naturalLanguageRepresentation, "?D?") != 1
				|| StringUtils.countMatches(naturalLanguageRepresentation,
						"?R?") != 1)
			return false;

		// patterns need to be bigger/equal than min chunk size and
		// smaller/equal then max chunk size
		// true or correct if the number of stop-words in the pattern is not
		// equal to the number of tokens
		Set<String> naturalLanguageRepresentationChunks = new HashSet<String>(
				Arrays.asList(patternWithoutVariables.toLowerCase().split(" ")));
		if (naturalLanguageRepresentationChunks.size() >= MAX_PATTERN_CHUNK_LENGTH)
			return false;

		return true;
	}

}
