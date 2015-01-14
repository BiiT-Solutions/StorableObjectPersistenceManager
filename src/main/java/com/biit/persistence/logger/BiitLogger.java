package com.biit.persistence.logger;

import org.apache.log4j.Logger;

/**
 * Defines basic log behavior. Uses log4j.properties.
 */
public class BiitLogger {
	private static final Logger LOGGER = Logger.getLogger(BiitLogger.class);

	private BiitLogger() {
	}

	/**
	 * Events that have business meaning (i.e. creating category, deleting form,
	 * ...). To follow user actions.
	 * 
	 * @param message
	 */
	private static void info(String message) {
		LOGGER.info(message);
	}

	/**
	 * Events that have business meaning (i.e. creating category, deleting form,
	 * ...). To follow user actions.
	 */
	public static void info(String className, String message) {
		info(className + ": " + message);
	}

	/**
	 * Shows not critical errors. I.e. Email address not found, permissions not
	 * allowed for this user, ...
	 * 
	 * @param message
	 */
	private static void warning(String message) {
		LOGGER.warn(message);
	}

	/**
	 * Shows not critical errors. I.e. Email address not found, permissions not
	 * allowed for this user, ...
	 * 
	 * @param message
	 */
	public static void warning(String className, String message) {
		warning(className + ": " + message);
	}

	/**
	 * For following the trace of the execution. I.e. Knowing if the application
	 * access to a method, opening database connection, etc.
	 * 
	 * @param message
	 */
	private static void debug(String message) {
		if (isDebugEnabled()) {
			LOGGER.debug(message);
		}
	}

	/**
	 * For following the trace of the execution. I.e. Knowing if the application
	 * access to a method, opening database connection, etc.
	 */
	public static void debug(String className, String message) {
		debug(className + ": " + message);
	}

	/**
	 * To log any not expected error that can cause application malfuncionality.
	 * I.e. couldn't open database connection, etc..
	 * 
	 * @param message
	 */
	private static void severe(String message) {
		LOGGER.error(message);
	}

	/**
	 * To log any not expected error that can cause application malfuncionality.
	 * 
	 * @param message
	 */
	public static void severe(String className, String message) {
		severe(className + ": " + message);
	}

	/**
	 * Used for debugging when accessing to a method.
	 * 
	 * @param className
	 * @param method
	 */
	public static void entering(String className, String method) {
		debug(className, "ENTRY (" + method + ")");
	}

	/**
	 * Used for debugging when exiting from a method.
	 * 
	 * @param className
	 * @param method
	 */
	public static void exiting(String className, String method) {
		debug(className, "RETURN (" + method + ")");
	}

	/**
	 * To log java exceptions and log also the stack trace.
	 * 
	 * @param className
	 * @param throwable
	 */
	public static void errorMessage(String className, Throwable throwable) {
		String error = stackTraceToString(throwable);
		severe(className, error);
	}

	public static String stackTraceToString(Throwable e) {
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement element : e.getStackTrace()) {
			sb.append(element.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	public static boolean isDebugEnabled() {
		return LOGGER.isDebugEnabled();
	}
}
