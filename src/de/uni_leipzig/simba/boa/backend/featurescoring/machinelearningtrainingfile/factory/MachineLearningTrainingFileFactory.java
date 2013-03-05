/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.featurescoring.machinelearningtrainingfile.factory;

import java.util.List;

import de.uni_leipzig.simba.boa.backend.featurescoring.machinelearningtrainingfile.MachineLearningTrainingFile;
import de.uni_leipzig.simba.boa.backend.featurescoring.machinelearningtrainingfile.entry.MachineLearningTrainingFileEntry;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.machinelearning.MachineLearningToolFactory;


/**
 * @author gerb
 *
 */
public class MachineLearningTrainingFileFactory {

    private final NLPediaLogger logger = new NLPediaLogger(MachineLearningToolFactory.class);
    
    /**
     * both used for singleton pattern 
     */
    private static MachineLearningTrainingFileFactory INSTANCE = null;
    private MachineLearningTrainingFileFactory() {}
    
    private Class<? extends MachineLearningTrainingFile> defaultMachineLearningTrainingFileClass;
    private Class<? extends MachineLearningTrainingFileEntry> defaultMachineLearningTrainingFileEntryClass;
    
    /**
     * @return the singleton for this factory
     */
    public static MachineLearningTrainingFileFactory getInstance() {
        
        if ( MachineLearningTrainingFileFactory.INSTANCE == null ) {
            
            MachineLearningTrainingFileFactory.INSTANCE = new MachineLearningTrainingFileFactory();
        }
        return MachineLearningTrainingFileFactory.INSTANCE;
    }
    
    public MachineLearningTrainingFileEntry getDefaultMachineLearningTrainingFileEntry(String mappingUri, String pattern, List<Double> features, String pos, Boolean annotated) {
        
        try {
            
            return ((Class<? extends MachineLearningTrainingFileEntry>) this.defaultMachineLearningTrainingFileEntryClass).
                    getDeclaredConstructor(mappingUri.getClass(), pattern.getClass(), List.class, String.class, Boolean.class).
                    newInstance(mappingUri, pattern, features, pos, annotated);
        }
        catch (Exception e) {
            
            e.printStackTrace();
            String error = "Could not load default machine learning training file:" + this.defaultMachineLearningTrainingFileClass;
            this.logger.error(error, e);
            throw new RuntimeException(error, e);
        }
    }
    
    public MachineLearningTrainingFile getDefaultMachineLearningTrainingFile(List<String> featureNames, List<MachineLearningTrainingFileEntry> trainingFileEntries) {
        
        try {
            
            return ((Class<? extends MachineLearningTrainingFile>) this.defaultMachineLearningTrainingFileClass).
                    getDeclaredConstructor(List.class, List.class).
                    newInstance(featureNames, trainingFileEntries);
        }
        catch (Exception e) {
            
            e.printStackTrace();
            String error = "Could not load default machine learning training file:" + this.defaultMachineLearningTrainingFileClass;
            this.logger.error(error, e);
            throw new RuntimeException(error, e);
        }
    }

    
    /**
     * @return the defaultMachineLearningTrainingFileClass
     */
    public Class<? extends MachineLearningTrainingFile> getDefaultMachineLearningTrainingFileClass() {
    
        return defaultMachineLearningTrainingFileClass;
    }

    
    /**
     * @param defaultMachineLearningTrainingFileClass the defaultMachineLearningTrainingFileClass to set
     */
    public void setDefaultMachineLearningTrainingFileClass(Class<? extends MachineLearningTrainingFile> defaultMachineLearningTrainingFileClass) {
    
        this.defaultMachineLearningTrainingFileClass = defaultMachineLearningTrainingFileClass;
    }

    
    /**
     * @return the defaultMachineLearningTrainingFileEntryClass
     */
    public Class<? extends MachineLearningTrainingFileEntry> getDefaultMachineLearningTrainingFileEntryClass() {
    
        return defaultMachineLearningTrainingFileEntryClass;
    }

    
    /**
     * @param defaultMachineLearningTrainingFileEntryClass the defaultMachineLearningTrainingFileEntryClass to set
     */
    public void setDefaultMachineLearningTrainingFileEntryClass(Class<? extends MachineLearningTrainingFileEntry> defaultMachineLearningTrainingFileEntryClass) {
    
        this.defaultMachineLearningTrainingFileEntryClass = defaultMachineLearningTrainingFileEntryClass;
    }
}
