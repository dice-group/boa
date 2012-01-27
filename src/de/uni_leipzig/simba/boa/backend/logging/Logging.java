package de.uni_leipzig.simba.boa.backend.logging;

import java.text.DecimalFormat;

import org.apache.log4j.Logger;

/**
 * This is the central place for all log data.
 * 
 * Using this Recorder class helps to ensure, that all log messages
 * are built in the same format. 
 * 
 * A log message consists of the following information:
 * <ul>
 * <li>actual date and time</li>
 * <li>class</li>
 * <li>message</li>
 * </ul>
 */
public class Logging {

	protected static final String PREFIX = "BOA::";
	
	private static final String SEPARATOR = " || ";
	private static final String STACK_OF = ">>> stack of ";
	private static final String CAUSE = ".cause::";
	private static final String N_A = "n/a";

	private static final int stackLength = 11;
	
	protected static final String DEBUG = "DEBUG";
	protected static final String INFO = "INFO";
	protected static final String WARN = "WARN";
	protected static final String ERROR = "ERROR";
	protected static final String FATAL = "FATAL";
	
	private static long debugMsgNum = 0;
	private static long infoMsgNum = 0;
	private static long warnMsgNum = 0;
	private static long errorMsgNum = 0;
	private static long fatalMsgNum = 0; 

	/**
	 * Returns true, if the priority level in the log4j.xml is set to DEBUG.
	 * 
	 * @param callingClass - class where logging was performed
	 * @return true, if debug is enabled
	 */
	public static boolean isDebugEnabled(Class callingClass) {
		
		return Logger.getLogger(callingClass).isDebugEnabled();
	}
	
	/**
	 * @see org.jawa.core.logging.Logging#logDebug(String, Throwable, Class);
	 */
	public static long logDebug(String logMsg, Class callingClass) {
		
		return logDebug(logMsg, null, callingClass);
	}
	
	/**
	 * TODO: description from documentation
	 * 
	 * @param logMsg - message which should be logged
	 * @param e - Throwable that was thrown (exception)
	 * @param callingClass - class where the recorder was called
	 * @return long the number of the fatal message since last reboot
	 */
	public static long logDebug(String logMsg, Throwable e, Class callingClass) {
		
		long msgNum = getDebugMsgNum();
		getLogger(callingClass).debug(createLogEntry(DEBUG, PREFIX, msgNum, callingClass, logMsg, e));
		return msgNum;
	}
	
	/**
	 * @see simba.nlpedia.logging.Logging#logInfo(String, Throwable, Class)
	 */
	public static long logInfo(String logMsg, Class callingClass) {
		
		return logInfo(logMsg, null, callingClass);
	}
	
	/**
	 * TODO: description from documentation
	 *
	 * @param logMsg - message which should be logged
	 * @param e - Throwable that was thrown (exception) 
	 * @param callingClass - class where the recorder was called
	 * @return long the number of the info message since last reboot
	 */
	public static long logInfo(String logMsg, Throwable e, Class callingClass) {
		
		long msgNum = getInfoMsgNum();
		getLogger(callingClass).info(createLogEntry(INFO, PREFIX, msgNum, callingClass, logMsg, e));
		return msgNum;
	}
	
	/**
	 * @see simba.nlpedia.logging.Logging#logWarn(String, Throwable, Class)
	 */
	public static long logWarn(String logMsg, Class callingClass) {
		
		return logWarn(logMsg, null, callingClass);
	}
	
	/**
	 * TODO: description from documentation
	 * 
	 * @param logMsg - message which should be logged
	 * @param e - Throwable that was thrown (exception)
	 * @param callingClass - class where the recorder was called
	 * @return long the number of the warn messages since last reboot
	 */
	public static long logWarn(String logMsg, Throwable e, Class callingClass) {
		
		long msgNum = getWarnMsgNum();
		getLogger(callingClass).warn(createLogEntry(WARN, PREFIX, msgNum, callingClass, logMsg, e));
		return msgNum;
	}
	
	/**
	 * @see simba.nlpedia.logging.Logging#logError(String, Throwable, Class)
	 */
	public static long logError(String logMsg, Class callingClass) {
		
		return logError(logMsg, null, callingClass);
	}
	
	/**
	 * TODO: description from documentation
	 * 
	 * @param logMsg - message which should be logged
	 * @param e - Throwable that was thrown (exception)
	 * @param callingClass - class where the recorder was called
	 * @return long the number of the error message since last reboot
	 */
	public static long logError(String logMsg, Throwable e, Class callingClass) {
		
		long msgNum = getErrorMsgNum();
		getLogger(callingClass).error(createLogEntry(ERROR, PREFIX, msgNum, callingClass, logMsg, e));
		return msgNum;
	}
	
	/**
	 * @see org.jawa.core.logging.Logging#logFatal(String, Throwable, Class);
	 */
	public static long logFatal(String logMsg, Class callingClass) {
		
		return logFatal(logMsg, null, callingClass);
	}
	
	/**
	 * TODO: description from documentation
	 * 
	 * @param logMsg - message which should be logged
	 * @param e - Throwable that was thrown (exception)
	 * @param callingClass - class where the recorder was called
	 * @return long the number of the fatal message since last reboot
	 */
	public static long logFatal(String logMsg, Throwable e, Class callingClass) {
		
		long msgNum = getFatalMsgNum();
		getLogger(callingClass).fatal(createLogEntry(FATAL, PREFIX, msgNum, callingClass, logMsg, e));
		return msgNum;
	}
	
	private static long getDebugMsgNum() {
		
		return ++debugMsgNum;
	}
	
	private static long getInfoMsgNum() {

		return ++infoMsgNum;
	}
	
	private static long getWarnMsgNum() {
	
		return ++warnMsgNum;
	}
	
	private static long getErrorMsgNum() {

		return ++errorMsgNum;
	}
	
	private static long getFatalMsgNum() {
		
		return ++fatalMsgNum;
	}
	
	/**
	 * @param callingClass
	 * @return Logger the log4j logger
	 */
	private static Logger getLogger(Class callingClass) {
		
		return Logger.getLogger(callingClass);
	}
	
	/**
	 * Creates a log message with the help of the parameters.
	 * 
	 * @param msgCategory - kind of the log message (INFO, WARN, ERROR)
	 * @param prefix - PREFIX (usually the name of the application)
	 * @param msgNum - number of the message
	 * @param callingClass - class where recorder was called
	 * @param logMsg - the message to be logged
	 * @param e - the Throwable that was thrown
	 * @return the 'ready to use' log message 
	 */
	private static String createLogEntry(String msgCategory, String prefix, long msgNum, Class callingClass, String logMsg, Throwable e) {
		
		StringBuffer sb = new StringBuffer();
//		sb.append(prefix);
//		sb.append(msgCategory);
		sb.append("_#");
		sb.append(new DecimalFormat("0000").format(msgNum));
//		sb.append(SEPARATOR);
//		sb.append(callingClass == null ? N_A : callingClass.getName());
		sb.append(SEPARATOR);
		sb.append(logMsg == null ? N_A : logMsg.replaceAll("[\\r\\f]", "").replaceAll("[/^]M", "").replaceAll("[\\r\\n]", ""));
		sb.append(SEPARATOR);
		if ( e == null ) {
			if (msgCategory != DEBUG) {
//				sb.append("cause: " + N_A);
			}
		}
		else {
			int i = 1;
			while (e != null && i < 10) {
				sb.append(STACK_OF);
				sb.append(i);
				sb.append(CAUSE);
				sb.append(e.getClass().getName());
				sb.append("::");
				sb.append(e.getMessage());
				sb.append(" -> ");
				sb.append(Logging.getStackTrace(e));
				i++;
				e = e.getCause();
			}
		}
		return sb.toString();
	}
	
	/**
	 * Returns the first few (max stackLength) lines of the stacktrace.
	 * 
	 * @param cause - the throwable containing the StackTrace
	 * @return the stackTrace
	 */
	private static StringBuffer getStackTrace(Throwable cause) {
		
		StackTraceElement[] st = cause.getStackTrace();
		StringBuffer stackTrace = new StringBuffer(500);
		int max = (st.length < stackLength ? st.length : stackLength);
		for (int i = 0; i < max; i++) {
			
			stackTrace.append(" at ");
			stackTrace.append(st[i]);
		}
		return stackTrace;
	}
}