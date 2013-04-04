package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import de.uni_leipzig.simba.boa.backend.concurrent.PatternMappingGeneralizedPatternPair;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.AbstractFeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl.Feature;
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
	public void score(PatternMappingGeneralizedPatternPair pair) {

		long start = new Date().getTime();
		
		int totalOccurrences = 0;
		SummaryStatistics specificityStat = new SummaryStatistics();
		
		for ( Pattern pattern : pair.getGeneralizedPattern().getPatterns() ) {
			
			int occurrences = findPatternMappingsWithSamePattern(pattern.getNaturalLanguageRepresentation());
			Double specificity = (double) this.mappings.size() / (double) occurrences ; 
				
			specificity = Math.log(specificity) / Math.log(2);
			specificity = specificity.isInfinite() || specificity.isNaN() ? 0D : specificity; 
			
			setValue(pattern, "SPECIFICITY", specificity >= 0 ? specificity : 0, specificityStat);
			setValue(pattern, "SPECIFICITY_OCCURRENCE", (double) occurrences);
			this.logger.debug("Specificity feature for " + pair.getMapping().getProperty().getLabel() + "/\"" + pattern.getNaturalLanguageRepresentation() + "\"  finished in " + TimeUtil.convertMilliSeconds((new Date().getTime() - start)) + ".");
			
			totalOccurrences += occurrences;
		}
		
		Map<Feature,Double> features = pair.getGeneralizedPattern().getFeatures();
		// the sum of all patterns occ of this generalized pattern
		features.put(FeatureFactory.getInstance().getFeature("SPECIFICITY_OCCURRENCE"), (double) totalOccurrences);
		features.put(FeatureFactory.getInstance().getFeature("SPECIFICITY"), specificityStat.getMean());
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
