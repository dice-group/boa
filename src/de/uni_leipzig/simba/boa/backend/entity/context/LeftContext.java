package de.uni_leipzig.simba.boa.backend.entity.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


public class LeftContext extends Context {

	public LeftContext(String nerTaggedString, String sentence, String patternWithOutVariables) {

		this.words = new ArrayList<String>();
		this.setPattern(patternWithOutVariables);
		this.createLeftContext(nerTaggedString, sentence);
		this.buffer = patternWithOutVariables.trim().split(" ")[0];
	}

	@Override
	public String getSuitableEntity(String entityType) {

		String entity = "";
		String entityMapping = Context.namedEntityRecognitionMappings.get(entityType);
		
		// handle list smaller 3 separatly
		if ( this.words.size() < 3 ) {
			
			if ( this.words.size() == 0 ) throw new AssertionError("The word list of this context can't be empty");
			
			if ( this.words.size() == 1 ) {
				
				if ( this.words.get(0).contains(entityMapping) ) entity += this.words.get(0).substring(0, this.words.get(0).indexOf("_"));
			}
			
			if ( this.words.size() == 2 ) {
				
				if ( this.words.get(0).contains(entityMapping) ) entity += this.words.get(0).substring(0, this.words.get(0).indexOf("_"));
				if ( this.words.get(1).contains(entityMapping) ) entity += " " + this.words.get(1).substring(0, this.words.get(1).indexOf("_"));
			}
			
			return entity.trim();
		}
		else {
			
			for (int j = this.words.size() - 1; j >= 2; ) {
				
				String currentWord					= this.words.get(j);
				String wordBeforeCurrentWord		= this.words.get(j - 1);
				String wordBeforeBeforeCurrentWord 	= this.words.get(j - 2);
				
//				System.out.println("j:" +j);
//				System.out.println("ENTITY: " + entity);
//				System.out.println(currentWord);
//				System.out.println(wordBeforeCurrentWord);
//				System.out.println(wordBeforeBeforeCurrentWord);
//				System.out.println();
				
				if ( currentWord.contains(entityMapping) &&		!wordBeforeCurrentWord.contains(entityMapping) && 	wordBeforeBeforeCurrentWord.contains(entityMapping) ) {
					
					entity = entity + " " + currentWord + " " + wordBeforeCurrentWord + " " + wordBeforeBeforeCurrentWord;
					j = j - 3;
				}
				if ( currentWord.contains(entityMapping) &&		!wordBeforeCurrentWord.contains(entityMapping) && 	!wordBeforeBeforeCurrentWord.contains(entityMapping) ) {
					
					entity = entity + " " + currentWord;
					break;
				}
				if ( currentWord.contains(entityMapping) && 	wordBeforeCurrentWord.contains(entityMapping) && 	wordBeforeBeforeCurrentWord.contains(entityMapping) ) {
					
					entity = entity + " " + currentWord + " " + wordBeforeCurrentWord + " " + wordBeforeBeforeCurrentWord;
					j = j - 3;
				}
				if ( currentWord.contains(entityMapping) && 	wordBeforeCurrentWord.contains(entityMapping) && 	!wordBeforeBeforeCurrentWord.contains(entityMapping) ) {
					
					entity = entity + " " + currentWord + " " + wordBeforeCurrentWord;
					j = j - 2;
				}
				if ( !currentWord.contains(entityMapping) && 	!wordBeforeCurrentWord.contains(entityMapping) && 	wordBeforeBeforeCurrentWord.contains(entityMapping) ) {
					
					if ( entity.contains(entityMapping) ) break;
					else {
						
						entity = entity + " " + wordBeforeBeforeCurrentWord;
						j = j -3;
					}
				}
				if ( !currentWord.contains(entityMapping) && 	!wordBeforeCurrentWord.contains(entityMapping) && 	!wordBeforeBeforeCurrentWord.contains(entityMapping) ) {
					
					if ( entity.contains(entityMapping) ) break;
					j = j - 3;
				}
				if ( !currentWord.contains(entityMapping) && 	wordBeforeCurrentWord.contains(entityMapping) && 	wordBeforeBeforeCurrentWord.contains(entityMapping) ) {
					
					String[] temp = entity.split(" ");
					if ( temp[temp.length-1].contains(entityMapping) ) {
						
						entity = entity + " " + currentWord + " " + wordBeforeCurrentWord + " " + wordBeforeBeforeCurrentWord;
						j = j - 3;
					}
					else {
						
						entity = entity + " " + wordBeforeCurrentWord + " " + wordBeforeBeforeCurrentWord;
						j = j - 3;
					}
				}
				if ( !currentWord.contains(entityMapping) &&	 wordBeforeCurrentWord.contains(entityMapping) && 	!wordBeforeBeforeCurrentWord.contains(entityMapping) ) {
					
					String[] temp = entity.split(" ");
					if ( temp[temp.length-1].contains(entityMapping) ) {
						
						entity = entity + " " + currentWord + " " + wordBeforeCurrentWord;
						j = j - 2;
					}
					else break;
				}
			}
//			System.out.println(entity);
			// reverse the order because we moved from right to left
			String[] foundEntity = entity.trim().split(" ");
			String result = "";
			for (int i = foundEntity.length - 1; i >= 0 ; i--) {
				
//				System.out.println(foundEntity[i]);
				result += " " + foundEntity[i].substring(0, foundEntity[i].indexOf("_"));
			}//System.out.println(result.trim());
			return result.trim();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return LeftContext.class.getSimpleName() + " " + this.words;
	}
	
	/**
	 * find the pattern again and split it into two parts
	 * 
	 * @param nerTaggedString
	 */
	private void createLeftContext(String nerTaggedString, String sentenceWithoutNerTags) throws StringIndexOutOfBoundsException {

		String leftPatternString = sentenceWithoutNerTags.substring(0, sentenceWithoutNerTags.indexOf(this.pattern) - 1).trim();
        String[] words = nerTaggedString.split(" ");
        
        for(int i = 0; i < leftPatternString.split(" ").length; i++){
           this.words.add(words[i]);
        }
	}
}
