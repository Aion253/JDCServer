package net.aionstudios.jdc.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

import org.fusesource.jansi.AnsiConsole;

/**
 * Handles writing to both the log streams and live console if one is visible.
 * Adds time, type and stream prefix to messages.
 * @author Winter Roberts
 */
public class LogOut {
	
	private static String streamPrefix = "";
	private static Logger logger;
	private static String n = System.getProperty("line.separator");
	
	private static ReentrantLock lock = new ReentrantLock();
	
	/**
	 * Prints and logs a message to the "standard" output streams, following it with a system-specific return character.
	 * <p>
	 * Adds both a stream time via the {@link #getStreamTime()} method and a prefix, if one was set with {@link #setStreamPrefix(String)}.
	 * @param text		The text to be printed.
	 */
	public static void println(String text){
		if(streamPrefix != ""){
			text = "["+getStreamTime()+" INFO]: ["+streamPrefix+"] "+text;
		} else {
			text = "["+getStreamTime()+" INFO]: "+text;
		}
		lock.lock();
		Logger.getStream().println(text.replaceAll("\u001B\\[[;\\d]*[ -/]*[@-~]", "").replaceAll("\\r\\n|\\r|\\n", n));
		AnsiConsole.out.println(text);
		lock.unlock();
	}
	
	/**
	 * Prints and logs a message to the "standard" output streams.
	 * @param text		The text to be printed.
	 */
	public static void print(String text){
		lock.lock();
		Logger.getStream().print(text.replaceAll("\u001B\\[[;\\d]*[ -/]*[@-~]", "").replaceAll("\\r\\n|\\r|\\n", n));
		AnsiConsole.out.print(text);
		lock.unlock();
	}
	
	/**
	 * Prints and logs a message to the "standard" error output streams, following it with a system-specific return character.
	 * <p>
	 * Adds a stream time via the {@link #getStreamTime()} method.
	 * @param text		The text to be printed.
	 */
	public static void errpl(String text){
		text = "["+getStreamTime()+" SERVER ERROR]: "+text;
		lock.lock();
		Logger.getStream().println(text.replaceAll("\\r\\n|\\r|\\n", n));
		AnsiConsole.err.println(text);
		lock.unlock();
	}
	
	/**
	 * Prints and logs a message to the "standard" error output streams.
	 * @param text		The text to be printed.
	 */
	public static void errp(String text){
		lock.lock();
		Logger.getStream().print(text.replaceAll("\\r\\n|\\r|\\n", n));
		AnsiConsole.err.print(text);
		lock.unlock();
	}
	
	/**
	 * Returns a string representation of the current time from a {@link SimpleDateFormat} in form of "HH:mm:ss".
	 * @return A representation of the current time as a string.
	 */
	public static String getStreamTime(){
		Date dNow = new Date( );
	    SimpleDateFormat ft = new SimpleDateFormat ("HH:mm:ss");
	    return ft.format(dNow);
	}
	
	/**
	 * Returns a reliably sortable string representation of the current date from a {@link SimpleDateFormat} in form of "yyyy-MM-dd".
	 * @return A representation of the current date as a string.
	 */
	public static String getStreamDate(){
		Date dNow = new Date( );
	    SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
	    return ft.format(dNow);
	}

	/**
	 * Returns the stream prefix, a short bit of text to be displayed before println calls.
	 * @return The stream prefix, or null if not set.
	 */
	public static String getStreamPrefix() {
		return streamPrefix;
	}

	/**
	 * Sets the stream prefix, a short bit of text to be displayed before println calls.
	 * @param streamPrefix1		The stream prefix.
	 */
	public static void setStreamPrefix(String streamPrefix1) {
		streamPrefix = streamPrefix1;
	}
	
	/**
	 * Empties the stream prefix so one wont be printed.
	 * 
	 * @see {@link #setStreamPrefix(String)}.
	 */
	public static void clearStreamPrefix(){
		streamPrefix = "";
	}

	/**
	 * Returns the {@link Logger} which is currently bound to be printed to by this class.
	 * @return A {@link Logger}, belonging to this class.
	 */
	public static Logger getLogger() {
		return logger;
	}

	/**
	 * Changes the {@link Logger} that is bound to this class.
	 * @param logger
	 */
	public static void setLogger(Logger logger) {
		LogOut.logger = logger;
	}
	
}
