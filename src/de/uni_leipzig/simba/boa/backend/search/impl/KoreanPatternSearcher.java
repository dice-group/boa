package de.uni_leipzig.simba.boa.backend.search.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class KoreanPatternSearcher extends DefaultPatternSearcher{
	
	@ Override
	protected List<String> findMatchedText(String sentence, String firstLabel, String secondLabel){
		String sentenceLowerCase    = sentence.toLowerCase();
		List<String> currentMatches = new ArrayList<String>();
		
        // subject comes first
        String[] match1 = StringUtils.substringsBetween(sentenceLowerCase, firstLabel, secondLabel);
        if (match1 != null) {

            for (int j = 0; j < match1.length; j++) 
                currentMatches.add("?D? " + match1[j].trim() + " ?R?");
        }
        // object comes first
        String[] match2 = StringUtils.substringsBetween(sentenceLowerCase, secondLabel, firstLabel);
        if (match2 != null) {

            for (int j = 0; j < match2.length; j++) 
                currentMatches.add("?R? " + match2[j].trim() + " ?D?");
        }

        return currentMatches;
	}

}
