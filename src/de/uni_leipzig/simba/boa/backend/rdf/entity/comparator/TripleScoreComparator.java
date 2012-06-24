/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.rdf.entity.comparator;

import java.util.Comparator;

import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;


/**
 * @author gerb
 *
 */
public class TripleScoreComparator implements Comparator<Triple> {

    @Override
    public int compare(Triple triple1, Triple triple2) {

        double x = (triple2.getScore() - triple1.getScore());
        if ( x < 0 ) return -1;
        if ( x == 0 ) return 0;
        return 1;
    }
}
