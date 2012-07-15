package de.uni_leipzig.simba.boa.backend.logging;


/**
 * Log file for comfortable logging in one class.
 * Create it as a static field if you want to use it in a static context.
 * <br>
 * For more details see Logging.java in same package.
 */
public class NLPediaLogger /*implements Serializable*/ {
	
	/**
     * 
     */
//    private static final long serialVersionUID = 3687086210560615529L;
    
    private Class loggingClazz;
	
	public NLPediaLogger(Class clazz) {
		
		this.loggingClazz = clazz;
	}
	
	/**
	 * @see simba.nlpedia.logging.Logging#isDebugEnabled(Class)
	 */
	public boolean isDebugEnabled() {
		
		return Logging.isDebugEnabled(this.loggingClazz);
	}
	
	/**
	 * @see simba.nlpedia.logging.Logging#logDebug(String, Class)
	 */
	public void debug(String logMsg) {
		
		Logging.logDebug(logMsg, this.loggingClazz);
	}
	
	/**
	 * @see simba.nlpedia.logging.Logging#logDebug(String, Throwable, Class)
	 */
	public void debug(String logMsg, Throwable cause) {
		
		Logging.logDebug(logMsg, cause, this.loggingClazz);
	}

	/**
	 * @see simba.nlpedia.logging.Logging#logInfo(String, Class)
	 */
	public void info(String logMsg) {
		
		Logging.logInfo(logMsg, this.loggingClazz);
	}
	
	/**
	 * @see simba.nlpedia.logging.Logging#logInfo(String, Throwable, Class)
	 */
	public void info(String logMsg, Throwable cause) {
		
		Logging.logInfo(logMsg, cause, this.loggingClazz);
	}
	
	/**
	 * @see simba.nlpedia.logging.Logging#logWarn(String, Class)
	 */
	public void warn(String logMsg) {
		
		Logging.logWarn(logMsg, this.loggingClazz);
	}
	
	/**
	 * @see simba.nlpedia.logging.Logging#logWarn(String, Throwable, Class)
	 */
	public void warn(String logMsg, Throwable cause) {
		
		Logging.logWarn(logMsg, cause, this.loggingClazz);
	}
	
	/**
	 * @see simba.nlpedia.logging.Logging#logError(String, Class)
	 */
	public void error(String logMsg) {
		
		Logging.logError(logMsg, this.loggingClazz);
	}
	
	/**
	 * @see simba.nlpedia.logging.Logging#logError(String, Throwable, Class)
	 */
	public void error(String logMsg, Throwable cause) {
		
		Logging.logError(logMsg, cause, this.loggingClazz);
	}
	
	/**
	 * @see simba.nlpedia.logging.Logging#logFatal(String, Class)
	 */
	public void fatal(String logMsg) {
		
		Logging.logFatal(logMsg, this.loggingClazz);
	}
	
	/**
	 * @see simba.nlpedia.logging.Logging#logFatal(String, Throwable, Class)
	 */
	public void fatal(String logMsg, Throwable cause) {
		
		Logging.logFatal(logMsg, cause, this.loggingClazz);
	}
}
