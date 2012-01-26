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
	
	private List<String> neuralNetworks;
	
	/**
	 * @return the neuralNetworks
	 */
	public List<String> getNeuralNetworks() {
		return neuralNetworks;
	}

	/**
	 * @param neuralNetworks the neuralNetworks to set
	 */
	public void setNeuralNetworks(List<String> neuralNetworks) {
		this.neuralNetworks = neuralNetworks;
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
	 * Returns a new instance of the specified neuralNetworkClass.
	 * The specific implementations of the neural networks have to be declared
	 * in the nlpedia_setup.xml file.
	 * 
	 * @param neuralNetworkClass - the neuralNetworkClass to be instantiated
	 * @return the instantiated neuralNetworkClass 
	 * @throws RuntimeExcpetion if no neuralNetworkClass could be found
	 */
	public NeuralNetwork createNeuralNetwork(Class<? extends NeuralNetwork> neuralNetworkClass) {

		if ( this.neuralNetworks.contains(neuralNetworkClass.getName()) ) {
			
			return (NeuralNetwork) createNewInstance(neuralNetworkClass);
		}
		this.logger.error("Could not load neural network " + neuralNetworkClass);
		throw new RuntimeException("Could not load neural network " + neuralNetworkClass);
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
			this.logger.fatal("Could not instantiate class: " + tool, e);
			throw new RuntimeException("Could not instantiate class: " + tool, e);
		}
		catch (IllegalAccessException e) {
			
			e.printStackTrace();
			this.logger.fatal("Could not instantiate class: " + tool, e);
			throw new RuntimeException("Could not instantiate class: " + tool, e);
		}
	}

}
