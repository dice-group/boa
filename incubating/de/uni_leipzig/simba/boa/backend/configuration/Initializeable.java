package de.uni_leipzig.simba.boa.backend.configuration;

/**
 * 
 * @author Daniel Gerber
 */
public abstract class Initializeable {

	public boolean isInitialized = false;
	
	/**
	 * Use this method to initialize instance variables. Use this method
	 * instead of doing it with the constructor. Note, if you use this method
	 * you can reset the objects state to when it was first created instead
	 * of creating a new object.
	 */
	public abstract void initialize();
	
	/**
	 * @param isInitialized - the initialization status of this object
	 */
	public void setIsInitialized(boolean isInitialized) {
		
		this.isInitialized = isInitialized;
	}
	
	/**
	 * @return the initialization status of this object
	 */
	public boolean isInitialized() {
		
		return this.isInitialized;
	}
}
