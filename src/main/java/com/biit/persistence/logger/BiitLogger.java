package com.biit.persistence.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.log4j.Logger;

/**
 * Defines basic log behavior. Uses log4j.properties.
 */
public class BiitLogger {
	private static final Logger logger = Logger.getLogger(BiitLogger.class);

	private BiitLogger() {
	}

	/**
	 * Events that have business meaning (i.e. creating category, deleting form, ...). To follow user actions.
	 *
	 * @param message
	 */
	private static void info(String message) {
		logger.info(message);
	}

	/**
	 * Events that have business meaning (i.e. creating category, deleting form, ...). To follow user actions.
	 */
	public static void info(String className, String message) {
		info(className + ": " + message);
	}

	/**
	 * Shows not critical errors. I.e. Email address not found, permissions not allowed for this user, ...
	 *
	 * @param message
	 */
	private static void warning(String message) {
		logger.warn(message);
	}

	/**
	 * Shows not critical errors. I.e. Email address not found, permissions not allowed for this user, ...
	 *
	 * @param message
	 */
	public static void warning(String className, String message) {
		warning(className + ": " + message);
	}

	/**
	 * For following the trace of the execution. I.e. Knowing if the application access to a method, opening database
	 * connection, etc.
	 *
	 * @param message
	 */
	private static void debug(String message) {
		if (isDebugEnabled()) {
			logger.debug(message);
		}
	}

	/**
	 * For following the trace of the execution. I.e. Knowing if the application access to a method, opening database
	 * connection, etc.
	 */
	public static void debug(String className, String message) {
		debug(className + ": " + message);
	}

	/**
	 * To log any not expected error that can cause application malfuncionality. I.e. couldn't open database connection,
	 * etc..
	 *
	 * @param message
	 */
	private static void severe(String message) {
		logger.error(message);
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
		String error = getStackTrace(throwable);
		severe(className, error);
	}

	private static String getStackTrace(Throwable throwable) {
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		throwable.printStackTrace(printWriter);
		return writer.toString();
	}

	public static boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}
}
