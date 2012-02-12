package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.comparator;

import java.util.Comparator;

import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl.Feature;

/**
 * Compares a pair of features by its name. Returns order in 
 * the manner as a string comparator would.
 * 
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public class FeatureNameComparator implements Comparator<Feature> {

    @Override
    public int compare(Feature feature1, Feature feature2) {
        
        return feature1.getName().compareTo(feature2.getName());
    }
}
