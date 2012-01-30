package de.uni_leipzig.simba.boa.backend.featurescoring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import de.danielgerber.file.BufferedFileWriter;
import de.danielgerber.file.FileUtil;
import de.danielgerber.file.BufferedFileWriter.WRITER_WRITE_MODE;
import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.persistance.serialization.SerializationManager;

/**
 * 
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public class PatternScoreManager {

    /**
     * 
     * @param filepath
     */
    public void writeNetworkTrainingFile(NeuronalNetworkTrainingFile networkTrainingFile, String filepath) {

        BufferedFileWriter writer = FileUtil.openWriter(filepath, "UTF-8", WRITER_WRITE_MODE.OVERRIDE);
        writer.writeLineNoNewLine(networkTrainingFile.toString());
        writer.close();
    }
    
    /**
     * 
     * @param mappings
     * @return
     */
    public NeuronalNetworkTrainingFile createNeuronalNetworkTrainingFile(Set<PatternMapping> mappings) {
        
        List<NeuronalNetworkTrainingFileEntry> trainingFileEntries = new ArrayList<NeuronalNetworkTrainingFileEntry>();

        // collect all the patterns
        for (PatternMapping mapping : mappings) {
            for (Pattern pattern : mapping.getPatterns() ) {
                
                trainingFileEntries.add(new NeuronalNetworkTrainingFileEntry(
                                            mapping.getProperty().getUri(),
                                            pattern.getNaturalLanguageRepresentation(),
                                            pattern.buildFeatureVector(mapping),
                                            null));
            }
            // shuffle rather more then less
            Collections.shuffle(trainingFileEntries);
        }
        
        // create a file which contains all possible patterns, but there are shuffled so that we don't evaluate to much pattern of the same mapping
        return new NeuronalNetworkTrainingFile(trainingFileEntries);
    }
    
    /**
     * 
     * @param patternMappings
     * @param neuronalNetworkTrainingFile
     * @return
     */
    public NeuronalNetworkTrainingFile updateNetworkTrainingFile(Set<PatternMapping> patternMappings, NeuronalNetworkTrainingFile neuronalNetworkTrainingFile) {
        
        for (PatternMapping mapping : patternMappings) {
            for (Pattern pattern : mapping.getPatterns()) {
                
                NeuronalNetworkTrainingFileEntry entry = neuronalNetworkTrainingFile.getEntry(mapping.getProperty().getUri(), pattern.getNaturalLanguageRepresentation());
                
                if ( entry != null )                  
                    entry.setFeatures(pattern.buildFeatureVector(mapping));
                else 
                    neuronalNetworkTrainingFile.addEntry(new NeuronalNetworkTrainingFileEntry(
                                                            mapping.getProperty().getUri(),
                                                            pattern.getNaturalLanguageRepresentation(),
                                                            pattern.buildFeatureVector(mapping),
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
    public NeuronalNetworkTrainingFile readNetworkTrainingFile(String filepath, String encoding) {
        
        List<NeuronalNetworkTrainingFileEntry> entries = new ArrayList<NeuronalNetworkTrainingFileEntry>();
        
        for (String line : FileUtil.readFileInList(filepath, encoding)) {
            if ( line.trim().isEmpty() ) continue;
            
            String[] lineParts = line.split(Constants.FEATURE_FILE_COLUMN_SEPARATOR);
            
            List<Double> featureValues = new ArrayList<Double>();
            for (String feature : Arrays.asList(lineParts).subList(0, lineParts.length - 3)) featureValues.add(Double.valueOf(feature));
            
            Boolean manual = null;
            if ( lineParts[lineParts.length - 3].equals("MANUAL") ) manual = null;
            if ( lineParts[lineParts.length - 3].equals("0") ) manual = false;
            if ( lineParts[lineParts.length - 3].equals("1") ) manual = true;
            
            String patternMappingUri = lineParts[lineParts.length - 2];
            String naturalLanguageRepresentation = lineParts[lineParts.length - 1];
            
            entries.add(new NeuronalNetworkTrainingFileEntry(
                                            patternMappingUri,
                                            naturalLanguageRepresentation,
                                            featureValues,
                                            manual));
        }
        return new NeuronalNetworkTrainingFile(entries);
    }
    
    public static void main(String[] args) {

        NLPediaSetup setup = new NLPediaSetup(true);
        PatternScoreManager psm = new PatternScoreManager();
//        psm.writeNetworkTrainingFile(psm.createNeuronalNetworkTrainingFile(), NLPediaSettings.BOA_BASE_DIRECTORY + "training/ml/en/wiki/new.txt");
    }
}
