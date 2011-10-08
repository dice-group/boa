package de.uni_leipzig.simba.boa.backend.entity.context;

import java.util.ArrayList;


public final class RightContext extends Context {

	public RightContext(String nerTaggedString, String sentence,String patternWithOutVariables) {

		this.words = new ArrayList<String>();
		this.setPattern(patternWithOutVariables);
		this.createRightContext(nerTaggedString, sentence);
		
		String[] patternChunks = patternWithOutVariables.split(" "); 
		this.buffer = patternChunks[patternChunks.length - 1];
	}
	
	@Override
	public String getSuitableEntity(String entityType) {

		String entity = "";
		String entityMapping = Context.namedEntityRecognitionMappings.get(entityType);
//		System.out.println(entityMapping);
		
		if ( this.words.size() < 3 ) {
			
//			System.out.println("List smaller than 3");
			
			if ( this.words.size() == 0 ) throw new AssertionError("The word list of this context can't be empty");
			
			if ( this.words.size() == 1 ) {
				
				if ( this.words.get(0).contains(entityMapping) ) entity += this.words.get(0).substring(0, this.words.get(0).indexOf("_"));
			}
			
			if ( this.words.size() == 2 ) {
				
				if ( this.words.get(0).contains(entityMapping) ) entity += this.words.get(0).substring(0, this.words.get(0).indexOf("_"));
				if ( this.words.get(1).contains(entityMapping) ) entity += this.words.get(1).substring(0, this.words.get(1).indexOf("_"));
			}
//			System.out.println("rightentity: "+ entity.trim());
			return entity.trim();
		}
		else {
			
			for (int j = 0; j < this.words.size() - 2; j++ ) {
				
				String currentWord					= this.words.get(j);
				// cover the end of the context with withspaces
				String wordBeforeCurrentWord		= (j + 1) <= this.words.size() - 1 ? this.words.get(j + 1) : ""; 
				String wordBeforeBeforeCurrentWord 	= (j + 2) <= this.words.size() - 1 ? this.words.get(j + 2) : "";
				
//				System.out.println("j:" +j);
//				System.out.println(currentWord);
//				System.out.println(wordBeforeCurrentWord);
//				System.out.println(wordBeforeBeforeCurrentWord);
//				System.out.println();
				
				if ( currentWord.contains(entityMapping) &&		!wordBeforeCurrentWord.contains(entityMapping) && 	wordBeforeBeforeCurrentWord.contains(entityMapping) ) {
					
					entity = entity + " " + currentWord + " " + wordBeforeCurrentWord + " " + wordBeforeBeforeCurrentWord;
					j = j + 3;
				}
				if ( currentWord.contains(entityMapping) &&		!wordBeforeCurrentWord.contains(entityMapping) && 	!wordBeforeBeforeCurrentWord.contains(entityMapping) ) {
					
					entity = entity + " " + currentWord;
					break;
				}
				if ( currentWord.contains(entityMapping) && 	wordBeforeCurrentWord.contains(entityMapping) && 	wordBeforeBeforeCurrentWord.contains(entityMapping) ) {
					
					entity = entity + " " + currentWord + " " + wordBeforeCurrentWord + " " + wordBeforeBeforeCurrentWord;
					j = j + 3;
				}
				if ( currentWord.contains(entityMapping) && 	wordBeforeCurrentWord.contains(entityMapping) && 	!wordBeforeBeforeCurrentWord.contains(entityMapping) ) {
					
					entity = entity + " " + currentWord + " " + wordBeforeCurrentWord;
					j = j + 2;
				}
				if ( !currentWord.contains(entityMapping) && 	!wordBeforeCurrentWord.contains(entityMapping) && 	wordBeforeBeforeCurrentWord.contains(entityMapping) ) {
					
					if ( entity.contains(entityMapping) ) break;
					else {
						
						entity = entity + " " + wordBeforeBeforeCurrentWord;
						j = j + 3;
					}
				}
				if ( !currentWord.contains(entityMapping) && 	!wordBeforeCurrentWord.contains(entityMapping) && 	!wordBeforeBeforeCurrentWord.contains(entityMapping) ) {
					
					if ( entity.contains(entityMapping) ) break;
					j = j + 3;
				}
				if ( !currentWord.contains(entityMapping) && 	wordBeforeCurrentWord.contains(entityMapping) && 	wordBeforeBeforeCurrentWord.contains(entityMapping) ) {
					
					String[] temp = entity.split(" ");
					if ( temp[temp.length-1].contains(entityMapping) ) {
						
						entity = entity + " " + currentWord + " " + wordBeforeCurrentWord + " " + wordBeforeBeforeCurrentWord;
						j = j + 3;
					}
					else {
						
						entity = entity + " " + wordBeforeCurrentWord + " " + wordBeforeBeforeCurrentWord;
						j = j + 3;
					}
				}
				if ( !currentWord.contains(entityMapping) &&	 wordBeforeCurrentWord.contains(entityMapping) && 	!wordBeforeBeforeCurrentWord.contains(entityMapping) ) {
					
					String[] temp = entity.split(" ");
					if ( temp[temp.length-1].contains(entityMapping) ) {
						
						entity = entity + " " + currentWord + " " + wordBeforeCurrentWord;
						j = j + 2;
					}
					else break;
				}
			}
			String[] foundEntity = entity.trim().split(" ");
			String result = "";
//			System.out.println("rightentitybefore: " + entity.trim());
			for (int i = 0; i < foundEntity.length ; i++) {
				
//				System.out.println("right["+i+"]:" +foundEntity[i]);
				result += " " + foundEntity[i].substring(0, foundEntity[i].indexOf("_"));
			}
//			System.out.println("rightentityafter: " + result);
			return result.trim();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return RightContext.class.getSimpleName() + this.words;
	}

	/**
	 * find the pattern again and split it into two parts
	 * 
	 * @param nerTaggedString
	 */
	private void createRightContext(String nerTaggedString, String sentenceWithoutNerTags) throws StringIndexOutOfBoundsException {

		String leftPatternString = sentenceWithoutNerTags.substring(0, sentenceWithoutNerTags.toLowerCase().indexOf(this.pattern.toLowerCase()) - 1).trim();
		String[] words = nerTaggedString.split(" ");
		
        for(int i = leftPatternString.split(" ").length + this.pattern.split(" ").length; i < words.length; i++){
        	this.words.add(words[i]);
        }
	}

	@Override
	public int getSuitableEntityDistance(String entityType) {

		String entityMapping = Context.namedEntityRecognitionMappings.get(entityType);
		
		for (int i = 0; i < this.words.size(); i++) {
			
			if ( this.words.get(i).contains(entityMapping) ) return i + 1; // start with 1 to avoid division by zero
		}
		return 0;
	}
	
	
		
//		String regex = "";
//		
//		String asda = this.pattern.replaceAll("[a-zA-Z],", " ,").trim();
//		System.out.println("asda: " + asda);
//		
//		for (String patternPart : this.pattern.replaceAll(",", " ,").trim().split(" ") ) {
//			
//			regex += Pattern.quote(patternPart) + "_(O|I|B|Date)(-(MISC|PER|ORG|LOC))* ";
//		}
//		System.out.println("REGEX: " + regex);
//		String test = nerTaggedString.replaceFirst(regex, this.pattern);
//		this.setWords(test.split(Pattern.quote(this.pattern))[1]);
//	}
}
