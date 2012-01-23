package de.uni_leipzig.simba.boa.backend.entity.context;

import java.util.ArrayList;


public class SlowLeftContext extends Context {

	public SlowLeftContext(String nerTaggedString, String sentence, String patternWithOutVariables) {

		this.cleanWords = new ArrayList<String>();
		this.setPattern(patternWithOutVariables);
		this.createLeftContext(nerTaggedString, sentence);
	}

	@Override
	public String getSuitableEntity(String entityType) {

		String entity = "";
		String entityMapping = Context.namedEntityRecognitionMappings.get(entityType);
		
		// handle list smaller 3 separatly
		if ( this.cleanWords.size() < 3 ) {
			
			if ( this.cleanWords.size() == 0 ) throw new AssertionError("The word list of this context can't be empty");
			
			if ( this.cleanWords.size() == 1 ) {
				
				if ( this.cleanWords.get(0).contains(entityMapping) ) entity += this.cleanWords.get(0).substring(0, this.cleanWords.get(0).indexOf("_"));
			}
			
			if ( this.cleanWords.size() == 2 ) {
				
				if ( this.cleanWords.get(0).contains(entityMapping) ) entity += this.cleanWords.get(0).substring(0, this.cleanWords.get(0).indexOf("_"));
				if ( this.cleanWords.get(1).contains(entityMapping) ) entity += " " + this.cleanWords.get(1).substring(0, this.cleanWords.get(1).indexOf("_"));
			}
//			System.out.println("leftcontext:"+ entity.trim());
			return entity.trim();
		}
		else {
			
			for (int j = this.cleanWords.size() - 1; j >= 0; ) {
				
				String currentWord					= this.cleanWords.get(j);
				// cover the end of the context with withspaces
				String wordBeforeCurrentWord		= (j - 1) >= 0 ? this.cleanWords.get(j - 1) : ""; 
				String wordBeforeBeforeCurrentWord 	= (j - 2) >= 0 ? this.cleanWords.get(j - 2) : "";
				
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
						j = j - 3;
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
					
					if ( entity.isEmpty() ) {
						
						entity = wordBeforeCurrentWord;
						j = j - 3;
					}
					else {
						
						String[] temp = entity.split(" ");
						if ( temp[temp.length-1].contains(entityMapping) ) {
							
							entity = entity + " " + currentWord + " " + wordBeforeCurrentWord;
							j = j - 2;
						}
						else break;
					}
				}
			}
//			System.out.println("leftentity: " + entity);
			// reverse the order because we moved from right to left
			String[] foundEntity = entity.trim().split(" ");
			String result = "";
			for (int i = foundEntity.length - 1; i >= 0 ; i--) {
				
//				System.out.println(foundEntity[i]);
				result += " " + foundEntity[i].substring(0, foundEntity[i].indexOf("_"));
			}//System.out.println("leftentity: " + result.trim());
			return result.trim();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return LeftContext.class.getSimpleName() + " " + this.cleanWords;
	}
	
	/**
	 * find the pattern again and split it into two parts
	 * 
	 * @param nerTaggedString
	 */
	private void createLeftContext(String nerTaggedString, String sentenceWithoutNerTags) throws StringIndexOutOfBoundsException {

		String leftPatternString = sentenceWithoutNerTags.substring(0, sentenceWithoutNerTags.toLowerCase().indexOf(this.pattern.toLowerCase()) - 1).trim();
        String[] words = nerTaggedString.split(" ");
        
        // add one token from the pattern to the left context
        // this is for cases where one part of the entity left to the pattern occurs inside the pattern like: ?D? Dickens 's novel ?R?
        for(int i = 0; i < leftPatternString.split(" ").length + 1; i++){
           this.cleanWords.add(words[i]);
        }
	}

	@Override
	public int getSuitableEntityDistance(String entityType) {

		String entityMapping = Context.namedEntityRecognitionMappings.get(entityType);
		
		for (int i = this.cleanWords.size() - 1, j = 1; i >= 0 ; i--, j++) {
			
			if ( this.cleanWords.get(i).contains(entityMapping) ) return j;
		}
		return 0;
	}
}
