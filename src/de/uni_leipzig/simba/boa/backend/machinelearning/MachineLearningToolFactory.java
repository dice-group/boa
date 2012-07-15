/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.machinelearning;

import java.util.List;

import de.uni_leipzig.simba.boa.backend.featurescoring.machinelearningtrainingfile.MachineLearningTrainingFile;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.util.FactoryUtil;

/**
 *@author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public class MachineLearningToolFactory {

	private static MachineLearningToolFactory INSTANCE;
	
	private final NLPediaLogger logger = new NLPediaLogger(MachineLearningToolFactory.class);
	
	private String defaultMachineLearningTool;
	private List<String> machineLearningTools;
	
	/**
	 * @return the machineLearningTools
	 */
	public List<String> getMachineLearningTools() {
		return machineLearningTools;
	}

	/**
	 * @param neuralNetworks the neuralNetworks to set
	 */
	public void setMachineLearningTools(List<String> machineLearningTools) {
		this.machineLearningTools = machineLearningTools;
	}

	/**
	 * Singleton
	 */
	private MachineLearningToolFactory() {}
	
	/**
	 * @return the instance of this singleton
	 */
	public static MachineLearningToolFactory getInstance() {
		
		if ( INSTANCE == null ) {
			
			INSTANCE = new MachineLearningToolFactory();
		}
		return INSTANCE;
	}
	
	/**
	 * Returns a new instance of the specified machineLearningToolClass.
	 * The specific implementations of the machine learning have to be declared
	 * in the machinelearning.xml file.
	 * 
	 * @param machineLearningToolClass - the machineLearningToolClass to be instantiated
	 * @return the instantiated machineLearningToolClass 
	 * @throws RuntimeExcpetion if no machineLearningToolClass could be found
	 */
	public MachineLearningTool createMachineLearningTool(Class<? extends MachineLearningTool> machineLearningToolClass) {

		if ( this.machineLearningTools.contains(machineLearningToolClass.getName()) ) {
			
			return (MachineLearningTool) FactoryUtil.createNewInstance(machineLearningToolClass);
		}
		String error = "Could not load neural network " + machineLearningToolClass;
		this.logger.error(error);
		throw new RuntimeException(error);
	}

    /**
     * @return the default machine learning tool
     */
    public MachineLearningTool createDefaultMachineLearningTool(MachineLearningTrainingFile trainingFile, MachineLearningTrainingFile testFile) {

        try {
            
            return (MachineLearningTool) Class.forName(this.defaultMachineLearningTool).
                    getDeclaredConstructor(MachineLearningTrainingFile.class, MachineLearningTrainingFile.class).newInstance(trainingFile, testFile);
        }
        catch (Exception e) {

            e.printStackTrace();
            String error = "Could not load default machine learning tool:" + defaultMachineLearningTool;
            this.logger.error(error, e);
            throw new RuntimeException(error, e);
        }
    }
    
    /**
     * @param defaultMachineLearningTool the defaultMachineLearningTool to set
     */
    public void setDefaultMachineLearningTool(String defaultMachineLearningTool) {
    
        this.defaultMachineLearningTool = defaultMachineLearningTool;
    }
    
    /**
     * @return the defaultMachineLearningTool
     */
    public String getDefaultMachineLearningTool() {
    
        return defaultMachineLearningTool;
    }
}
