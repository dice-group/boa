package de.uni_leipzig.simba.boa.backend.exception;


public class NLPediaRuntimeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -333093001109699362L;

	/**
	 * 
	 */
	private String name;

	public NLPediaRuntimeException(String name) {
		
		this.name = name;
	}
}
