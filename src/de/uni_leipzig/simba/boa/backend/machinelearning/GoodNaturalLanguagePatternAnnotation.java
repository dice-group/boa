package de.uni_leipzig.simba.boa.backend.machinelearning;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.helper.StringUtil;

import de.danielgerber.file.BufferedFileWriter;
import de.danielgerber.file.BufferedFileWriter.WRITER_WRITE_MODE;
import de.danielgerber.file.FileUtil;
import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.FeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureHelper;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl.Feature;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.serialization.PatternMappingManager;
import edu.stanford.nlp.util.StringUtils;


public class GoodNaturalLanguagePatternAnnotation {

    private static final NLPediaSetup setup   = new NLPediaSetup(true);
    private static final DecimalFormat format = new DecimalFormat("0.0000");
    
    /**
     * @param args
     */
    public static void main(String[] args) {

        BufferedFileWriter writer = FileUtil.openWriter("/Users/gerb/eval.txt", "UTF-8", WRITER_WRITE_MODE.OVERRIDE);
        writer.write(writeFeatureNames());

        FeatureHelper.createLocalMaxima(PatternMappingManager.getInstance().getPatternMappings());
        
        for (String propertyUri : top50Properties ) {
            
            PatternMapping mapping = getMapping(propertyUri);
            
            if ( mapping != null ) {
               
                List<Pattern> patterns = filterPatterns(new ArrayList<Pattern>(mapping.getPatterns()));
                if ( patterns.size() < 10 ) System.out.println(mapping.getProperty().getUri() + " has only " + patterns.size() + " patterns!");

                for ( Pattern p : patterns )
                    writer.write(createLine(mapping, p));
            }
            else {
                
                System.out.println("No mapping for " + propertyUri + " found!" );
            }
        }
        writer.close();
    }
    
    private static String writeFeatureNames() {

        List<String> features = new ArrayList<String>();
        for ( FeatureExtractor featureExtractor : FeatureFactory.getInstance().getFeatureExtractorMap().values() )
            if ( featureExtractor.isActivated() )
                for ( Feature feature : featureExtractor.getHandeledFeatures() ) 
                    if ( feature.getSupportedLanguages().contains(NLPediaSettings.getSystemLanguage()) )
                        // exclude everything which is not activated
                        if ( feature.isUseForPatternLearning() ) 
                            features.add(feature.getName());
                            
        return StringUtil.join(features, "\t");
    }

    private static String createLine(PatternMapping mapping, Pattern pattern) {
        
        List<String> line = new ArrayList<String>();
        for ( Double feature : pattern.buildNormalizedFeatureVector(mapping)) line.add(format.format(feature));
        line.add("MANUAL");
        line.add(mapping.getProperty().getUri());
        line.add(pattern.getNaturalLanguageRepresentation());
        
        return StringUtils.join(line, "\t"); 
    }
    
    /**
     * 
     * @param patterns
     * @return
     */
    private static List<Pattern> filterPatterns(List<Pattern> patterns) {

        List<Pattern> results = new ArrayList<Pattern>();
       
        final String feature = "SUPPORT_NUMBER_OF_PAIRS_LEARNED_FROM";
//        final String feature = "TOTAL_OCCURRENCE";
        Collections.sort(patterns, new Comparator<Pattern>() {

            @Override
            public int compare(Pattern pattern1, Pattern pattern2) {
                
                double x = pattern1.getFeatures().get(FeatureFactory.getInstance().getFeature(feature)) - pattern2.getFeatures().get(FeatureFactory.getInstance().getFeature(feature));
                if ( x < 0 ) return 1;
                if ( x == 0 ) return 0;
                return -1;
            }
        });
        
        for ( Pattern pattern : patterns) {
            
            if ( isSuitable(pattern) ) results.add(pattern);
            if ( results.size() > 10 ) break;
        }
        
        return results;
    }

    /**
     * 
     * @param pattern
     * @return
     */
    private static boolean isSuitable(Pattern pattern) {

        Set<String> naturalLanguageRepresentationChunks = new HashSet<String>(Arrays.asList(pattern.getNaturalLanguageRepresentationWithoutVariables().toLowerCase().split(" ")));
        Set<String> stopwords = Constants.STOP_WORDS;
        stopwords.add("'s");
        naturalLanguageRepresentationChunks.removeAll(stopwords);
        
        return naturalLanguageRepresentationChunks.size() > 0;
    }

    /**
     * 
     * @param uri
     * @return
     */
    private static PatternMapping getMapping(String uri) {
        
        for (PatternMapping mapping : PatternMappingManager.getInstance().getPatternMappings()) {
            
            if ( mapping.getProperty().getUri().equals(uri) ) return mapping;
        }
        return null;
    }
    
    private static List<String> top50Properties = 
            Arrays.asList("http://dbpedia.org/ontology/team",
                            "http://dbpedia.org/ontology/birthPlace",
                            "http://dbpedia.org/ontology/country",
                            "http://dbpedia.org/ontology/isPartOf",
                            "http://dbpedia.org/ontology/genre",
                            "http://dbpedia.org/ontology/family",
                            "http://dbpedia.org/ontology/type",
                            "http://dbpedia.org/ontology/starring",
                            "http://dbpedia.org/ontology/location",
                            "http://dbpedia.org/ontology/occupation",
                            "http://dbpedia.org/ontology/order",
                            "http://dbpedia.org/ontology/currentMember",
                            "http://dbpedia.org/ontology/class",
                            "http://dbpedia.org/ontology/kingdom",
                            "http://dbpedia.org/ontology/recordLabel",
                            "http://dbpedia.org/ontology/deathPlace",
                            "http://dbpedia.org/ontology/producer",
                            "http://dbpedia.org/ontology/phylum",
                            "http://dbpedia.org/ontology/timeZone",
                            "http://dbpedia.org/ontology/genus",
                            "http://dbpedia.org/ontology/writer",
                            "http://dbpedia.org/ontology/subsequentWork",
                            "http://dbpedia.org/ontology/previousWork",
                            "http://dbpedia.org/ontology/formerTeam",
                            "http://dbpedia.org/ontology/associatedMusicalArtist",
                            "http://dbpedia.org/ontology/associatedBand",
                            "http://dbpedia.org/ontology/city",
                            "http://dbpedia.org/ontology/artist",
                            "http://dbpedia.org/ontology/battle",
                            "http://dbpedia.org/ontology/format",
                            "http://dbpedia.org/ontology/language",
                            "http://dbpedia.org/ontology/hometown",
                            "http://dbpedia.org/ontology/director",
                            "http://dbpedia.org/ontology/region",
                            "http://dbpedia.org/ontology/binomialAuthority",
                            "http://dbpedia.org/ontology/successor",
                            "http://dbpedia.org/ontology/nationality",
                            "http://dbpedia.org/ontology/award",
                            "http://dbpedia.org/ontology/district",
                            "http://dbpedia.org/ontology/division",
                            "http://dbpedia.org/ontology/area",
                            "http://dbpedia.org/ontology/instrument",
                            "http://dbpedia.org/ontology/almaMater",
                            "http://dbpedia.org/ontology/party",
                            "http://dbpedia.org/ontology/musicalBand",
                            "http://dbpedia.org/ontology/musicalArtist",
                            "http://dbpedia.org/ontology/state",
                            "http://dbpedia.org/ontology/computingPlatform",
                            "http://dbpedia.org/ontology/recordedIn",
                            "http://dbpedia.org/ontology/managerClub"); 
}
