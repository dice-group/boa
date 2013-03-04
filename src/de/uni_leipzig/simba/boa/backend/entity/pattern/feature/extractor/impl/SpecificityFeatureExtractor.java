package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.uni_leipzig.simba.boa.backend.concurrent.PatternMappingPatternPair;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.AbstractFeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.serialization.PatternMappingManager;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.util.TimeUtil;

/**
 * 
 * @author Daniel Gerber
 */
public class SpecificityFeatureExtractor extends AbstractFeatureExtractor {
	
	private NLPediaLogger logger = new NLPediaLogger(SpecificityFeatureExtractor.class);
	
	@Override
	public void score(PatternMappingPatternPair pair) {

		long start = new Date().getTime();
		
		int occurrences = findPatternMappingsWithSamePattern(pair.getPattern().getNaturalLanguageRepresentation());
		Double specificity = (double) this.mappings.size() / (double) occurrences ; 
			
		specificity = Math.log(specificity) / Math.log(2);
		specificity = specificity.isInfinite() || specificity.isNaN() ? 0D : specificity; 
		
		pair.getPattern().getFeatures().put(FeatureFactory.getInstance().getFeature("SPECIFICITY"), specificity >= 0 ? specificity : 0);
		pair.getPattern().getFeatures().put(FeatureFactory.getInstance().getFeature("SPECIFICITY_OCCURRENCE"), (double) occurrences);
		this.logger.debug("Specificity feature for " + pair.getMapping().getProperty().getLabel() + "/\"" + pair.getPattern().getNaturalLanguageRepresentation() + "\"  finished in " + TimeUtil.convertMilliSeconds((new Date().getTime() - start)) + ".");
	}
	
	/**
     * 
     * @param database
     * @param naturalLanguageRepresentation
     * @return
     */
    public int findPatternMappingsWithSamePattern(String naturalLanguageRepresentation) {

    	int occurrence = 0;
    	
        for ( PatternMapping mapping : this.mappings )
            for (Pattern pattern : mapping.getPatterns())
                if ( pattern.getNaturalLanguageRepresentation().equalsIgnoreCase(naturalLanguageRepresentation) ) {
                    
                    occurrence += pattern.getNumberOfOccurrences();
                    break; // there will be only one pattern per pattern mapping with the same natural language representation, so go to next pattern mapping
                }
                
        return occurrence;
    }
}
