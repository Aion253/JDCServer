package net.aionstudios.jdc.server.util;

import com.sun.net.httpserver.HttpExchange;

import net.aionstudios.jdc.content.ResponseCode;

public class ConsoleErrorUtils {
	
	public static void printServerError(ResponseCode rc, String processorName, HttpExchange he, StackTraceElement[] stea) {
		String newLine = System.getProperty("line.separator");
		System.err.println("An error of type \""+rc.getCodeName()+"\" occured while processing a request"+newLine
				+"	Request at: "+he.getRequestHeaders().getFirst("Host")+he.getRequestURI()+newLine
				+"	Using processor: "+processorName+newLine
				+"	Stack trace: "+stackTraceToString(stea)+newLine);
	}
	
	public static void printServerError(ResponseCode rc, String processorName, StackTraceElement[] stea) {
		String newLine = System.getProperty("line.separator");
		System.err.println("An error of type \""+rc.getCodeName()+"\" occured while processing a request"+newLine
				+"	Using processor: "+processorName+newLine
				+"	Stack trace: "+stackTraceToString(stea)+newLine);
	}
	
	public static void printServerError(ResponseCode rc, HttpExchange he, StackTraceElement[] stea) {
		String newLine = System.getProperty("line.separator");
		System.err.println("An error of type \""+rc.getCodeName()+"\" occured while processing a request"+newLine
				+"	Request at: "+he.getRequestHeaders().getFirst("Host")+he.getRequestURI()+newLine
				+"	Stack trace: "+stackTraceToString(stea)+newLine);
	}
	
	public static void printServerError(ResponseCode rc, StackTraceElement[] stea) {
		String newLine = System.getProperty("line.separator");
		System.err.println("An error of type \""+rc.getCodeName()+"\" occured while processing a request"+newLine
				+"	Stack trace: "+stackTraceToString(stea)+newLine);
	}
	
	public static void printServerError(ResponseCode rc, String processorName, Exception e) {
		String newLine = System.getProperty("line.separator");
		System.err.println("An error of type \""+rc.getCodeName()+"\" occured while processing a request"+newLine
				+"	Using processor: "+processorName+newLine
				+"	Exception: '"+e.getLocalizedMessage()+"' at "+stackTraceToString(e.getStackTrace())+newLine);
	}
	
	public static void printServerError(ResponseCode rc, Exception e) {
		String newLine = System.getProperty("line.separator");
		System.err.println("An error of type \""+rc.getCodeName()+"\" occured while processing a request"+newLine
				+"	Exception: '"+e.getLocalizedMessage()+"' at "+stackTraceToString(e.getStackTrace())+newLine);
	}
	
	public static String stackTraceToString(StackTraceElement[] stea) {
		String newLine = System.getProperty("line.separator");
		String stacktrace = stea[0].toString();
		for(int i = 1; i < stea.length; i++) {
			stacktrace = stacktrace+newLine+"		"+stea[i];
		}
		return stacktrace;
	}

}
