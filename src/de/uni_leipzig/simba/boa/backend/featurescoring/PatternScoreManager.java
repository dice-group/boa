package de.uni_leipzig.simba.boa.backend.featurescoring;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import de.danielgerber.file.BufferedFileWriter;
import de.danielgerber.file.BufferedFileWriter.WRITER_WRITE_MODE;
import de.danielgerber.file.FileUtil;
import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.extractor.FeatureExtractor;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureFactory;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl.Feature;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;
import de.uni_leipzig.simba.boa.backend.featurescoring.machinelearningtrainingfile.MachineLearningTrainingFile;
import de.uni_leipzig.simba.boa.backend.featurescoring.machinelearningtrainingfile.entry.MachineLearningTrainingFileEntry;
import de.uni_leipzig.simba.boa.backend.featurescoring.machinelearningtrainingfile.factory.MachineLearningTrainingFileFactory;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.pipeline.module.patternfeatureextraction.impl.DefaultPatternFeatureExtractionModule;

/**
 * 
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public class PatternScoreManager {

    private NLPediaLogger logger = new NLPediaLogger(PatternScoreManager.class);
    
    /**
     * 
     * @param filepath
     */
    public void writeNetworkTrainingFile(MachineLearningTrainingFile machineLearningFile, String filepath) {

        // save regular boa version
        BufferedFileWriter writer = FileUtil.openWriter(filepath, "UTF-8", WRITER_WRITE_MODE.OVERRIDE);
        writer.writeLineNoNewLine(machineLearningFile.toString());
        writer.close();
    }
    
    /**
     * 
     * @param mappings
     * @return
     */
    public MachineLearningTrainingFile createNeuronalNetworkTrainingFile(Set<PatternMapping> mappings) {
        
        List<MachineLearningTrainingFileEntry> trainingFileEntries  = new ArrayList<MachineLearningTrainingFileEntry>();
        List<String> featureNames                                   = new ArrayList<String>();
        
        // get the feature names but only those which are activated for the current language
        for ( FeatureExtractor featureExtractor : FeatureFactory.getInstance().getFeatureExtractorMap().values() )
            if ( featureExtractor.isActivated() )
                for ( Feature feature : featureExtractor.getHandeledFeatures() )
                    if ( feature.getSupportedLanguages().contains(NLPediaSettings.getSystemLanguage()) )
                        if ( feature.isUseForPatternLearning() ) 
                            featureNames.add(feature.getName());
        
        this.logger.info("Finished generation of feature names");

        // collect all the patterns
        for (PatternMapping mapping : mappings) {
            for (Pattern pattern : mapping.getPatterns() ) {
                
                this.logger.debug("Generation of training example: " + mapping.getProperty().getUri() + "/" + pattern.getNaturalLanguageRepresentation());
                trainingFileEntries.add(MachineLearningTrainingFileFactory.getInstance().getDefaultMachineLearningTrainingFileEntry(
                                                                                            mapping.getProperty().getUri(),
                                                                                            pattern.getNaturalLanguageRepresentation(),
                                                                                            pattern.buildNormalizedFeatureVector(mapping),
                                                                                            null));
            }
            // shuffle rather more then less
            Collections.shuffle(trainingFileEntries);
        }
        
        // create a file which contains all possible patterns, but there are shuffled so that we don't evaluate to much pattern of the same mapping
        return MachineLearningTrainingFileFactory.getInstance().getDefaultMachineLearningTrainingFile(featureNames, trainingFileEntries);
    }
    
    /**
     * 
     * @param patternMappings
     * @param neuronalNetworkTrainingFile
     * @return
     */
    public MachineLearningTrainingFile updateNetworkTrainingFile(Set<PatternMapping> patternMappings, MachineLearningTrainingFile neuronalNetworkTrainingFile) {
        
        List<String> featureNames = new ArrayList<String>();
        
        // get the feature names but only those which are activated for the current language
        for ( FeatureExtractor featureExtractor : FeatureFactory.getInstance().getFeatureExtractorMap().values() )
            if ( featureExtractor.isActivated() )
                for ( Feature feature : featureExtractor.getHandeledFeatures() )
                    if ( feature.getSupportedLanguages().contains(NLPediaSettings.getSystemLanguage()) )
                        if ( feature.isUseForPatternLearning() ) 
                            featureNames.add(feature.getName());
                
        neuronalNetworkTrainingFile.setFeatureNames(featureNames);
        
        for (PatternMapping mapping : patternMappings) {
            for (Pattern pattern : mapping.getPatterns()) {
                
                MachineLearningTrainingFileEntry entry = neuronalNetworkTrainingFile.getEntry(mapping.getProperty().getUri(), pattern.getNaturalLanguageRepresentation());
                List<Double> features = pattern.buildNormalizedFeatureVector(mapping);
                
                if ( entry != null )                  
                    entry.setFeatures(features);
                else 
                    neuronalNetworkTrainingFile.addEntry(MachineLearningTrainingFileFactory.getInstance().getDefaultMachineLearningTrainingFileEntry(
                                                            mapping.getProperty().getUri(),
                                                            pattern.getNaturalLanguageRepresentation(),
                                                            features,
                                                            null));
                    
            }
            // only shuffle the new & not annotated entries 
            Collections.shuffle(neuronalNetworkTrainingFile.getNotAnnotatedEntries());
        }
        return neuronalNetworkTrainingFile;
    }
    
    /**
     * 
     * @param filepath
     * @param encoding
     * @return
     */
    public MachineLearningTrainingFile readNetworkTrainingFile(String filepath, String encoding) {
        
        List<MachineLearningTrainingFileEntry> entries = new ArrayList<MachineLearningTrainingFileEntry>();
        
        // we need to skip the first line because it contains the feature names and no entries
        List<String> lines = FileUtil.readFileInList(filepath, encoding);
        List<String> featureNames = Arrays.asList(lines.get(0).split("\t"));
        
        for (String line : lines.subList(1, lines.size())) {
            
            if ( line.trim().isEmpty() ) continue;
            
            String[] lineParts = line.split(Constants.FEATURE_FILE_COLUMN_SEPARATOR);
            
            List<Double> featureValues = new ArrayList<Double>();
            for (String feature : Arrays.asList(lineParts).subList(0, lineParts.length - 3)) featureValues.add(Double.valueOf(feature));
            
            Boolean manual = null;
            if ( lineParts[lineParts.length - 3].equals("MANUAL") ) manual = null;
            if ( lineParts[lineParts.length - 3].equals("false") ) manual = false;
            if ( lineParts[lineParts.length - 3].equals("true") ) manual = true;
            
            String patternMappingUri = lineParts[lineParts.length - 2];
            String naturalLanguageRepresentation = lineParts[lineParts.length - 1].replaceAll("^\"", "").replaceAll("\"$", "");
            
            entries.add(MachineLearningTrainingFileFactory.getInstance().getDefaultMachineLearningTrainingFileEntry(
                    patternMappingUri,
                    naturalLanguageRepresentation,
                    featureValues,
                    manual));
        }
        return MachineLearningTrainingFileFactory.getInstance().getDefaultMachineLearningTrainingFile(featureNames, entries);
    }
    
    public static void main(String[] args) {

        NLPediaSetup setup = new NLPediaSetup(true);
        PatternScoreManager psm = new PatternScoreManager();
//        psm.writeNetworkTrainingFile(psm.createNeuronalNetworkTrainingFile(), NLPediaSettings.BOA_BASE_DIRECTORY + "training/ml/en/wiki/new.txt");
    }
}
