package de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.sentenceboundarydisambiguation.impl;

import java.util.ArrayList;
import java.util.List;

import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.sentenceboundarydisambiguation.SentenceBoundaryDisambiguation;

/**
 * 
 * @author Daniel Gerber
 * @author Donghyun Choi
 */
public class KoreanSentenceBoundaryDisambiguation implements SentenceBoundaryDisambiguation {

	@Override
	public List<String> getSentences(String text) {

		List<String> sentences = new ArrayList<String>();
		
		int beginPoint = 0;
		
		for (int i = 0; i < text.length(); i++) {
			
			char val = text.charAt(i);
			
			if (val == '.') {
				
				if (i > 0 && (text.charAt(i - 1) >= 48 && text.charAt(i - 1) <= 57) && i < text.length() - 1 && (text.charAt(i + 1) >= 48 && text.charAt(i + 1) <= 57)) {}
				else {
					
					if ((i > 0 && ((text.charAt(i - 1) >= 65 && text.charAt(i - 1) <= 90) || (text.charAt(i - 1) >= 97 && text.charAt(i - 1) <= 122)))) {}
					else {
						
						if ((i < text.length() - 1 && ((text.charAt(i + 1) >= 65 && text.charAt(i + 1) <= 90) || (text.charAt(i + 1) >= 97 && text.charAt(i + 1) <= 122)))) {}
						else {
							
							if (i > 0 && (text.charAt(i - 1) >= 48 && text.charAt(i - 1) <= 57) && i < text.length() - 1 && (text.charAt(i + 1) == ' ')) {}
							else {
								
								if (i < text.length() - 1 && text.charAt(i + 1) == '.') {}
								else {
									
									if (i > 3 && (text.charAt(i - 1) == '.') && (text.charAt(i - 2) == '.') && (text.charAt(i - 3) == '.')) {}
									else {
										
										if (i > 0 && (text.charAt(i - 1) == '-')) {}
										else {
											
											if (i > 1 && ((text.charAt(i - 2) == '\r' || text.charAt(i - 2) == '\n'))) {}
											else {
												
												if (i > 0 && text.charAt(i - 1) >= 48 && text.charAt(i - 1) <= 57) {}
												else {
													// Split!
													String sentence = text.substring(beginPoint, i + 1).trim();
													
													if (!sentence.trim().equals("")) sentences.add(sentence);
													beginPoint = i + 1;
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			else {
				
				if (val == '\n' || val == '\r') {
					
					if (i < text.length() - 1 && ((text.charAt(i + 1) == '\n' || text.charAt(i + 1) == 'r'))) {}
					else {
						// Split!
						String sentence = text.substring(beginPoint, i + 1).trim();
						
						if (!sentence.trim().equals("")) sentences.add(sentence);
						beginPoint = i + 1;
					}
				}
			}
		}
		if (beginPoint < text.length()) { 
			
			String sentence = text.substring(beginPoint).trim();
			
			if (!sentence.trim().equals("")) sentences.add(sentence);
		}
		return sentences;
	}
}
