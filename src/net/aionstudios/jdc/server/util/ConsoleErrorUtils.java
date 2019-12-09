package net.aionstudios.jdc.server.util;

import com.sun.net.httpserver.HttpExchange;

import net.aionstudios.jdc.content.ResponseCode;

/**
 * A utility class designed to print information about {@link JDC} errors that occur in processing.
 * @author Winter Roberts
 */
public class ConsoleErrorUtils {
	
	/**
	 * Prints a server error including the error response code, request context, processor and complete stack trace.
	 * @param rc				The {@link ResponseCode} describing this error.
	 * @param processorName		The name of the processor that was being used when the error occured.
	 * @param he				The {@link HttpExchange} which is handling the request.
	 * @param stea				The array of {@link StackTraceElement}s to be printed as one complete stack trace.
	 */
	public static void printServerError(ResponseCode rc, String processorName, HttpExchange he, StackTraceElement[] stea) {
		String newLine = "\r\n";
		System.err.println("An error of type \""+rc.getCodeName()+"\" occured while processing a request"+newLine
				+"	Request at: "+he.getRequestHeaders().getFirst("Host")+he.getRequestURI()+newLine
				+"	Using processor: "+processorName+newLine
				+"	Stack trace: "+stackTraceToStringRemoveTrace(stea)+newLine);
	}
	
	/**
	 * Prints a server error including the error response code, processor and complete stack trace.
	 * @param rc				The {@link ResponseCode} describing this error.
	 * @param processorName		The name of the processor that was being used when the error occured.
	 * @param stea				The array of {@link StackTraceElement}s to be printed as one complete stack trace.
	 */
	public static void printServerError(ResponseCode rc, String processorName, StackTraceElement[] stea) {
		String newLine = "\r\n";
		System.err.println("An error of type \""+rc.getCodeName()+"\" occured while processing a request"+newLine
				+"	Using processor: "+processorName+newLine
				+"	Stack trace: "+stackTraceToStringRemoveTrace(stea)+newLine);
	}
	
	/**
	 * Prints a server error including the error response code, request context and complete stack trace.
	 * @param rc				The {@link ResponseCode} describing this error.
	 * @param he				The {@link HttpExchange} which is handling the request.
	 * @param stea				The array of {@link StackTraceElement}s to be printed as one complete stack trace.
	 */
	public static void printServerError(ResponseCode rc, HttpExchange he, StackTraceElement[] stea) {
		String newLine = "\r\n";
		System.err.println("An error of type \""+rc.getCodeName()+"\" occured while processing a request"+newLine
				+"	Request at: "+he.getRequestHeaders().getFirst("Host")+he.getRequestURI()+newLine
				+"	Stack trace: "+stackTraceToStringRemoveTrace(stea)+newLine);
	}
	
	/**
	 * Prints a server error including the error response code and complete stack trace.
	 * @param rc				The {@link ResponseCode} describing this error.
	 * @param stea				The array of {@link StackTraceElement}s to be printed as one complete stack trace.
	 */
	public static void printServerError(ResponseCode rc, StackTraceElement[] stea) {
		String newLine = "\r\n";
		System.err.println("An error of type \""+rc.getCodeName()+"\" occured while processing a request"+newLine
				+"	Stack trace: "+stackTraceToStringRemoveTrace(stea)+newLine);
	}
	
	/**
	 * Prints a server error including the error response code, processor and exception.
	 * @param rc				The {@link ResponseCode} describing this error.
	 * @param processorName		The name of the processor that was being used when the error occured.
	 * @param e					The {@link Exception} that occured.
	 */
	public static void printServerError(ResponseCode rc, String processorName, Exception e) {
		String newLine = "\r\n";
		System.err.println("An error of type \""+rc.getCodeName()+"\" occured while processing a request"+newLine
				+"	Using processor: "+processorName+newLine
				+"	Exception: '"+e.getMessage()+"' at "+stackTraceToString(e.getStackTrace())+newLine);
	}
	
	/**
	 * Prints a server error including the error response code and exception.
	 * @param rc				The {@link ResponseCode} describing this error.
	 * @param e					The {@link Exception} that occured.
	 */
	public static void printServerError(ResponseCode rc, Exception e) {
		String newLine = "\r\n";
		System.err.println("An error of type \""+rc.getCodeName()+"\" occured while processing a request"+newLine
				+"	Exception: '"+e.getMessage()+"' at "+stackTraceToString(e.getStackTrace())+newLine);
	}
	
	/**
	 * Converts an array of {@link StackTraceElement}s to a representative string as would normally be printed by an {@link Exception} stack trace.
	 * @param stea		An array of {@link StackTraceElement}s leading up to the error that occurred.
	 * @return The complete stack trace array as a string.
	 */
	public static String stackTraceToString(StackTraceElement[] stea) {
		String stacktrace = stea[0].toString();
		for(int i = 1; i < stea.length; i++) {
			stacktrace = stacktrace+"\r\n\t\t"+stea[i];
		}
		return stacktrace;
	}
	
	/**
	 * Converts an array of {@link StackTraceElement}s to a representative string as would normally be printed by an {@link Exception} stack trace.
	 * This version of the method is intended to be used when the Stack Trace is generated with intent by the application
	 * (The application does not throw an error but intentionally calls the method).
	 * @param stea		An array of {@link StackTraceElement}s leading up to the error that occurred.
	 * @return The complete stack trace array as a string.
	 */
	public static String stackTraceToStringRemoveTrace(StackTraceElement[] stea) {
		String stacktrace = stea[1].toString();
		for(int i = 2; i < stea.length; i++) {
			stacktrace = stacktrace+"\r\n\t\t"+stea[i];
		}
		return stacktrace;
	}

}
