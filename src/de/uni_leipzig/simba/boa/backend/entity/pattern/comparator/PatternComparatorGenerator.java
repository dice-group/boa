package de.uni_leipzig.simba.boa.backend.entity.pattern.comparator;

import java.util.Comparator;

import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl.Feature;

/**
 * 
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public class PatternComparatorGenerator {

    /**
     * Returns a specific comparator for a given feature.
     * 
     * @param feature the feature a pattern should be compared with
     * @return a feature specific comparator for patterns
     */
    public static Comparator<Pattern> getPatternFeatureComparator(final Feature feature) {
        
        return new Comparator<Pattern>() {

            @Override
            public int compare(Pattern pattern1, Pattern pattern2) {

                double x = pattern1.getFeatures().get(feature) - pattern2.getFeatures().get(feature);
                if ( x < 0 ) return -1;
                if ( x == 0 ) return 0;
                return 1;
            }
        };
    }
}
