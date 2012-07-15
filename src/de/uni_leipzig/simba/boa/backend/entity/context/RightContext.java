package de.uni_leipzig.simba.boa.backend.entity.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.stanford.nlp.util.StringUtils;


public class RightContext extends Context {

	public RightContext(String nerTaggedString, String sentence,String patternWithOutVariables) throws IllegalArgumentException {

		this.cleanWords = new ArrayList<String>();
		this.taggedWords = new ArrayList<String>();
		this.sentence     = sentence;
		this.setPattern(patternWithOutVariables);
		this.createRightContext(nerTaggedString, sentence);
	}
	
	@Override
	public int getSuitableEntityDistance(String entityType) {

		String entityMapping = Context.namedEntityRecognitionMappings.get(entityType);
		
		// from 0 to the size of the left context without the pattern
		for (int i = this.taggedWords.size() - (this.taggedWords.size() - (this.pattern.split(" ").length - 1)), j = 0; i < this.taggedWords.size() ; i++, j++) {
			// if entityMapping is null, take proper name of any category
			if (entityMapping == null) {
				for (String tag : Context.namedEntityRecognitionTags)
					if (this.taggedWords.get(i).contains(tag))
						return j;
			}
			else if ( this.taggedWords.get(i).contains(entityMapping) ) return j;
		}
		return -1;
	}

	@Override
	public String getSuitableEntity(String entityType) {

		String entityMapping = Context.namedEntityRecognitionMappings.get(entityType);
		
		List<String> entity = new ArrayList<String>();
		
		boolean found = false;
		
		// the words are in reversed order
		for ( String word : this.taggedWords ) {
			
			// if entityMapping is null, take proper name of any category
			if (entityMapping == null)
				for (String tag : Context.namedEntityRecognitionTags)
					if (word.contains(tag)) {
						entityMapping = tag;
						break;
					}

			// we found a word which contains a suitable tag
			if ( (entityMapping != null) && (word.contains(entityMapping)) ) {
				
				// we found one before
				if ( found ) {

					entity.add(this.cleanWords.get(this.taggedWords.indexOf(word)));
				}
				// we found it for the first time so change the flag
				else {

					found = true;
					entity.add(this.cleanWords.get(this.taggedWords.indexOf(word)));
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
		
		return StringUtils.join(entity, " ");
	}

	private void createRightContext(String nerTaggedString, String sentenceWithoutNerTags) throws IllegalArgumentException {

		String leftContextString = sentenceWithoutNerTags.substring(0, sentenceWithoutNerTags.toLowerCase().indexOf(this.pattern.toLowerCase()) - 1).trim();
		String[] taggedWords = nerTaggedString.split(" ");
        //String[] cleanWords = sentenceWithoutNerTags.split(" ");
		String[] cleanWords = new String[taggedWords.length];
		for (int i = 0; i < taggedWords.length; i++)
			cleanWords[i] = taggedWords[i].substring(0, taggedWords[i].lastIndexOf('_'));

		//System.out.println(Arrays.toString(taggedWords));
		//System.out.println(Arrays.toString(cleanWords));
		
        if ( taggedWords.length != cleanWords.length ) 
            throw new IllegalArgumentException("Could not create a context because the tagged string and the clean string have not the same size.\n" +
                    "cleanWords:  " + Arrays.toString(cleanWords) + "\n" +
                    "taggedWords: " + Arrays.toString(taggedWords));
        
        for ( int i = leftContextString.split(" ").length + this.pattern.split(" ").length - 1 ; i < cleanWords.length ; i++){
        	
        	this.cleanWords.add(cleanWords[i]);
        	this.taggedWords.add(taggedWords[i]);
        }
	}
	
	/* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        builder.append(cleanWords);
        return builder.toString();
    }
}
