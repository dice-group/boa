/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.machinelearning;

import java.util.List;

import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.machinelearning.neuralnetwork.NeuralNetwork;

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
			
			return (NeuralNetwork) createNewInstance(machineLearningToolClass);
		}
		String error = "Could not load neural network " + machineLearningToolClass;
		this.logger.error(error);
		throw new RuntimeException(error);
	}
	
	/**
	 * Instantiates a machine learning tool tool.
	 * 
	 * @param tool the tool to be instantiated
	 * @return a new instance of the tool
	 * @throw RuntimeException if something wents wrong
	 */
	private MachineLearningTool createNewInstance(Class<? extends MachineLearningTool> tool){
		
		try {
			
			return tool.newInstance();
		}
		catch (InstantiationException e) {

			e.printStackTrace();
			String error = "Could not instantiate class: " + tool;
			this.logger.fatal(error, e);
			throw new RuntimeException(error, e);
		}
		catch (IllegalAccessException e) {
			
			e.printStackTrace();
			String error = "Could not instantiate class: " + tool;
			this.logger.fatal(error, e);
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

    /**
     * @return the default machine learning tool
     */
    @SuppressWarnings("unchecked")
    public MachineLearningTool createDefaultMachineLearningTool() {

        try {
            
            return (MachineLearningTool) createNewInstance(
                    (Class<? extends MachineLearningTool>) Class.forName(defaultMachineLearningTool));
        }
        catch (ClassNotFoundException e) {

            e.printStackTrace();
            String error = "Could not load default machine learning tool:" + defaultMachineLearningTool;
            this.logger.error(error, e);
            throw new RuntimeException(error, e);
        }
    }
}
