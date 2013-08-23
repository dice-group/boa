/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.patternsearch.concurrent;

import java.util.concurrent.Callable;

import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.NaturalLanguageProcessingToolFactory;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.partofspeechtagger.PartOfSpeechTagger;
import de.uni_leipzig.simba.boa.backend.pipeline.module.patternsearch.impl.DefaultPatternSearchModule;
import de.uni_leipzig.simba.boa.backend.search.impl.DefaultPatternSearcher;

/**
 * @author gerb
 *
 */
public class PatternPosTagCallable implements Callable<PatternPosTagCallable> {

	private PartOfSpeechTagger posTagger = null;
	private PatternMapping mapping = null;
	private DefaultPatternSearcher patternSearcher = null;
	private final NLPediaLogger logger                  = new NLPediaLogger(PatternPosTagCallable.class);

	public PatternPosTagCallable(PatternMapping mapping) {
		
		this.mapping = mapping;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public PatternPosTagCallable call() throws Exception {
		
		if ( this.posTagger == null ) this.posTagger = NaturalLanguageProcessingToolFactory.getInstance().createDefaultPartOfSpeechTagger();
		if ( this.patternSearcher == null ) this.patternSearcher = new DefaultPatternSearcher();
		
		logger.info("Starting to POS tag " + mapping.getPatterns().size() + " patterns.");
		
		for ( Pattern pattern : mapping.getPatterns() ) 
			pattern.setPosTaggedString(getPartOfSpeechTags(pattern, pattern.getFoundInSentences().iterator().next()));
		
		logger.info("Finished POS tagging " + mapping.getPatterns().size() + " patterns.");
		
		// save the ram since those instances will live until all callables are finished
		this.posTagger = null;
		this.patternSearcher.close();
		this.patternSearcher = null;
		
		return this;
	}
	
	private String getPartOfSpeechTags(Pattern pattern, int sentenceId) {

    	String[] taggedSplit = this.posTagger.getAnnotatedString(this.patternSearcher.getSentencesByID(sentenceId)).split(" ");
    	String[] patternSplit = pattern.getNaturalLanguageRepresentation().replace("?D?", "").replace("?R?", "").trim().split(" ");
    	int  patternSplitIndex = 0;    	
    	
    	String patternPosTags = "";
    	
    	for (int i = 0; i < taggedSplit.length ; i++) {
    		
    		if ( taggedSplit[i].startsWith(patternSplit[patternSplitIndex] + "_")) {
    			
    			// first or any token except the last
    			if (patternSplitIndex >= 0 && patternSplitIndex < patternSplit.length - 1) {
    				
    				patternPosTags += taggedSplit[i].substring(taggedSplit[i].indexOf("_") + 1) + " ";
    				patternSplitIndex++;
        			continue;
    			}
    			// last token of pattern
    			else {
    				
    				patternPosTags += taggedSplit[i].substring(taggedSplit[i].indexOf("_") + 1);
    				break;
    			}
    		}
    	}
    	
    	return patternPosTags;
	}
}
