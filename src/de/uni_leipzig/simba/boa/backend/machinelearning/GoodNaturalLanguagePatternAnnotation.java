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

import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

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
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws InterruptedException {

        BufferedFileWriter writer = FileUtil.openWriter("/Users/gerb/eval.txt", "UTF-8", WRITER_WRITE_MODE.OVERRIDE);
        writer.write(writeFeatureNames());

//        FeatureHelper.createLocalMaxima(PatternMappingManager.getInstance().getPatternMappings());
        List<String> lines = new ArrayList<String>();
        for (String propertyUri : top50Properties ) {
        	
        	QueryEngineHTTP qexec = new QueryEngineHTTP("http://dbpedia.org/sparql", "ASK { <"+propertyUri+"> a owl:DatatypeProperty } ");
            qexec.addDefaultGraph("http://dbpedia.org");
            
            if ( qexec.execAsk() ) System.out.println(propertyUri);
            
            
            Thread.sleep(1000);
//            PatternMapping mapping = getMapping(propertyUri);
//            
//            if ( mapping != null ) {
//               
//                List<Pattern> patterns = filterPatterns(new ArrayList<Pattern>(mapping.getPatterns()));
//                if ( patterns.size() < 10 ) System.out.println(mapping.getProperty().getUri() + " has only " + patterns.size() + " patterns!");
//
//                for ( Pattern p : patterns )
//                    lines.add(createLine(mapping, p));
//            }
//            else {
//                
//                System.out.println("No mapping for " + propertyUri + " found!" );
//            }
        }
        Collections.shuffle(lines);
        for ( String line : lines ) writer.write(line);
        
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
            Arrays.asList("http://dbpedia.org/ontology/album",
            		"http://dbpedia.org/ontology/areaCode",
            		"http://dbpedia.org/ontology/author",
            		"http://dbpedia.org/ontology/battle",
            		"http://dbpedia.org/ontology/birthDate",
            		"http://dbpedia.org/ontology/birthPlace",
            		"http://dbpedia.org/ontology/capital",
            		"http://dbpedia.org/ontology/child",
            		"http://dbpedia.org/ontology/country",
            		"http://dbpedia.org/ontology/creator",
            		"http://dbpedia.org/ontology/crosses",
            		"http://dbpedia.org/ontology/currency",
            		"http://dbpedia.org/ontology/date",
            		"http://dbpedia.org/ontology/deathCause",
            		"http://dbpedia.org/ontology/deathDate",
            		"http://dbpedia.org/ontology/deathPlace",
            		"http://dbpedia.org/ontology/developer",
            		"http://dbpedia.org/ontology/director",
            		"http://dbpedia.org/ontology/elevation",
            		"http://dbpedia.org/ontology/formationYear",
            		"http://dbpedia.org/ontology/foundationPlace",
            		"http://dbpedia.org/ontology/genre",
            		"http://dbpedia.org/ontology/governmentType",
            		"http://dbpedia.org/ontology/ground",
            		"http://dbpedia.org/ontology/height",
            		"http://dbpedia.org/ontology/highestPlace",
            		"http://dbpedia.org/ontology/isPartOf",
            		"http://dbpedia.org/ontology/keyPerson",
            		"http://dbpedia.org/ontology/language",
            		"http://dbpedia.org/ontology/largestCity",
            		"http://dbpedia.org/ontology/leaderName",
            		"http://dbpedia.org/ontology/league",
            		"http://dbpedia.org/ontology/locatedInArea",
            		"http://dbpedia.org/ontology/location",
            		"http://dbpedia.org/ontology/numberOfEmployees",
            		"http://dbpedia.org/ontology/officialLanguage",
            		"http://dbpedia.org/ontology/orderInOffice",
            		"http://dbpedia.org/ontology/owner",
            		"http://dbpedia.org/ontology/producer",
            		"http://dbpedia.org/ontology/programmingLanguage",
            		"http://dbpedia.org/ontology/publisher",
            		"http://dbpedia.org/ontology/seasonNumber",
            		"http://dbpedia.org/ontology/series",
            		"http://dbpedia.org/ontology/sourceCountry",
            		"http://dbpedia.org/ontology/spokenIn",
            		"http://dbpedia.org/ontology/spouse",
            		"http://dbpedia.org/ontology/starring",
            		"http://dbpedia.org/property/accessioneudate",
            		"http://dbpedia.org/property/awards",
            		"http://dbpedia.org/property/borderingstates",
            		"http://dbpedia.org/property/classis",
            		"http://dbpedia.org/property/country",
            		"http://dbpedia.org/property/currency",
            		"http://dbpedia.org/property/currencyCode",
            		"http://dbpedia.org/property/densityrank",
            		"http://dbpedia.org/property/design",
            		"http://dbpedia.org/property/designer",
            		"http://dbpedia.org/property/elevationM",
            		"http://dbpedia.org/property/entranceCount",
            		"http://dbpedia.org/property/foundation",
            		"http://dbpedia.org/property/ground",
            		"http://dbpedia.org/property/industry",
            		"http://dbpedia.org/property/location",
            		"http://dbpedia.org/property/locationCountry",
            		"http://dbpedia.org/property/mineral",
            		"http://dbpedia.org/property/museum",
            		"http://dbpedia.org/property/numEmployees",
            		"http://dbpedia.org/property/office",
            		"http://dbpedia.org/property/officialLanguages",
            		"http://dbpedia.org/property/populationTotal",
            		"http://dbpedia.org/property/publisher",
            		"http://dbpedia.org/property/rulingParty",
            		"http://dbpedia.org/property/spouse",
            		"http://dbpedia.org/property/starring",
            		"http://dbpedia.org/property/title"); 
}
