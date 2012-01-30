package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.comparator.PatternComparatorGenerator;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature;
import de.uni_leipzig.simba.boa.backend.persistance.serialization.SerializationManager;

public class FeatureHelper {
	
	private static final String PATTERN_MAPPING_FOLDER = NLPediaSettings.BOA_DATA_DIRECTORY + "patternmappings/";
	
	private static Set<PatternMapping> mappings = SerializationManager.getInstance().deserializePatternMappings(PATTERN_MAPPING_FOLDER);

	/**
	 * Returns the specific feature score for the pattern with the 
	 * maximum value of this feature.
	 * 
	 * @param mapping - the mapping which is examined
	 * @param feature - the feature of interest
	 * @return the maximum feature value of the patterns
	 */
	public static Double calculateLocalMaximum(PatternMapping mapping, Feature feature){
		
		return Collections.max(mapping.getPatterns(), PatternComparatorGenerator.getPatternFeatureComparator(feature)).getFeatures().get(feature);
	}
	
	/**
	 * Calculates the maximum of all patterns for all pattern mappings
	 * for a given feature.
	 * 
	 * @param feature - the feature to be examined
	 * @return the maximum value for this feature from all patterns 
	 */
	public static Double calculateGlobalMaximum(Feature feature){
		
		Double maximum = 0D;
		for ( PatternMapping mapping : mappings ) maximum = Math.max(maximum, calculateLocalMaximum(mapping, feature));

		return maximum;
	}
}
