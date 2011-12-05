package de.uni_leipzig.simba.boa.backend.entity.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.stanford.nlp.util.StringUtils;


public class FastLeftContext extends Context {

	public FastLeftContext(String nerTaggedString, String sentence, String patternWithOutVariables) {

		this.cleanWords = new ArrayList<String>();
		this.taggedWords = new ArrayList<String>();
		this.setPattern(patternWithOutVariables);
		this.createLeftContext(nerTaggedString, sentence);
	}
	
	@Override
	public int getSuitableEntityDistance(String entityType) {

		String entityMapping = Context.namedEntityRecognitionMappings.get(entityType);
		
		// from 0 to the size of the left context without the pattern
		for (int i = this.taggedWords.size() - this.pattern.split(" ").length, j = 0; i >=  0 ; i--, j++) {
			
			if ( this.taggedWords.get(i).contains(entityMapping) ) return j;
		}
		return -1;
	}
	
	@Override
	public String getSuitableEntity(String entityType) {

		String entityMapping = Context.namedEntityRecognitionMappings.get(entityType);
		
		List<String> entity = new ArrayList<String>();
		
		boolean found = false;
		
		// the words are in reversed order
		for ( int i = taggedWords.size() - 1 ; i >= 0 ; i-- ) {
			
			// we found a word which contains a suitable tag
			if ( taggedWords.get(i).contains(entityMapping) ) {
				
				// we found one before
				if ( found ) {

					entity.add(this.cleanWords.get(this.taggedWords.indexOf(taggedWords.get(i))));
				}
				// we found it for the first time so change the flag
				else {

					found = true;
					entity.add(this.cleanWords.get(this.taggedWords.indexOf(taggedWords.get(i))));
				}
			}
			// current word does not contain a suitable tag
			else {
				
				// we did find a suitable one before
				if ( found ) {
					
					break; // complete entity 
				}
				else {
					
					continue; // go to next word
				}
			}
		}
		
		Collections.reverse(entity);
		
		return StringUtils.join(entity, " ");
	}
	
	/**
	 * find the pattern again and split it into two parts
	 * 
	 * @param nerTaggedString
	 */
	private void createLeftContext(String nerTaggedString, String sentenceWithoutNerTags) throws StringIndexOutOfBoundsException {

		String leftContextString = sentenceWithoutNerTags.substring(0, sentenceWithoutNerTags.toLowerCase().lastIndexOf(this.pattern.toLowerCase()) - 1).trim();
        String[] taggedWords = nerTaggedString.split(" ");
        String[] cleanWords = sentenceWithoutNerTags.split(" ");
        
        for( int i = 0; i < leftContextString.split(" ").length + this.pattern.split(" ").length - 1 ; i++){
           
        	this.taggedWords.add(taggedWords[i]);
        	this.cleanWords.add(cleanWords[i]);
        }
	}
}
