/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.postprocessing.impl;

import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl.Feature;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;


/**
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public class DetailedPatternIndexCreationModule extends DefaultPatternIndexCreationModule {

    public DetailedPatternIndexCreationModule() {
        super();
        
        // we want to write this index in another directory
        super.PATTERN_INDEX_DIRECTORY = NLPediaSettings.BOA_DATA_DIRECTORY + "index/pattern/detail/";
    }

    @Override
    public String getReport() {
        
        return "Detailed Pattern Index Creation Module";
    }
    
    @Override
    protected Document createLuceneDocument(PatternMapping mapping, Pattern pattern) {

        Document doc = new Document();
        doc.add(new Field("uri",            mapping.getProperty().getUri(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("nlr-var",        pattern.getNaturalLanguageRepresentation(), Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("nlr-no-var",     pattern.getNaturalLanguageRepresentationWithoutVariables(), Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new NumericField("score",   Field.Store.YES, true).setDoubleValue(pattern.getScore()));
        doc.add(new Field("domain",         mapping.getProperty().getRdfsDomain(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("range",          mapping.getProperty().getRdfsRange(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        
        // add all defined features for this pattern with the keys defined 
        // in the features.xml file (first argument of constructor-arg) 
        for ( Map.Entry<Feature, Double> featureMapping : pattern.getFeatures().entrySet() ) {
            
            doc.add(new NumericField(featureMapping.getKey().getName(),   Field.Store.YES, true).setDoubleValue(featureMapping.getValue()));
        }
        
        doc.add(new Field("generalized",            pattern.getGeneralizedPattern() != null ? pattern.getGeneralizedPattern() : "", Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("pos",                    pattern.getPosTaggedString() != null ? pattern.getPosTaggedString() : "", Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new NumericField("learnedfrom",     Field.Store.YES, true).setIntValue(pattern.getLearnedFromPairs()));
        doc.add(new NumericField("maxlearnedfrom",  Field.Store.YES, true).setIntValue(pattern.getMaxLearnedFrom()));

        return doc;
    }
}
