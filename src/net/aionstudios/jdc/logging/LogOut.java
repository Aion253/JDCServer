package net.aionstudios.jdc.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

import org.fusesource.jansi.AnsiConsole;

public class LogOut {
	
	private static String streamPrefix = "";
	private static Logger logger;
	private static String n = System.getProperty("line.separator");
	
	private static ReentrantLock lock = new ReentrantLock();
	
	public static void println(String text){
		if(streamPrefix != ""){
			text = "["+getStreamTime()+" INFO]: ["+streamPrefix+"] "+text;
		} else {
			text = "["+getStreamTime()+" INFO]: "+text;
		}
		lock.lock();
		Logger.getStream().println(text.replaceAll("\u001B\\[[;\\d]*[ -/]*[@-~]", "").replaceAll("\\n", n));
		AnsiConsole.out.println(text);
		lock.unlock();
	}
	
	public static void print(String text){
		lock.lock();
		Logger.getStream().print(text.replaceAll("\u001B\\[[;\\d]*[ -/]*[@-~]", "").replaceAll("\\n", n));
		AnsiConsole.out.print(text);
		lock.unlock();
	}
	
	public static void errpl(String text){
		text = "["+getStreamTime()+" SERVER ERROR]: "+text;
		lock.lock();
		Logger.getStream().println(text.replaceAll("\\n", n));
		AnsiConsole.err.println(text);
		lock.unlock();
	}
	
	public static void errp(String text){
		lock.lock();
		Logger.getStream().print(text.replaceAll("\\n", n));
		AnsiConsole.err.print(text);
		lock.unlock();
	}
	
	public static String getStreamTime(){
		Date dNow = new Date( );
	    SimpleDateFormat ft = new SimpleDateFormat ("HH:mm:ss");
	    return ft.format(dNow);
	}
	
	public static String getStreamDate(){
		Date dNow = new Date( );
	    SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
	    return ft.format(dNow);
	}

	public static String getStreamPrefix() {
		return streamPrefix;
	}

	public static void setStreamPrefix(String streamPrefix1) {
		streamPrefix = streamPrefix1;
	}
	
	public static void clearStreamPrefix(){
		streamPrefix = "";
	}

	public static Logger getLogger() {
		return logger;
	}

	public static void setLogger(Logger logger) {
		LogOut.logger = logger;
	}
	
}
