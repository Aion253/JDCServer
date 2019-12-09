package net.aionstudios.jdc.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;

import org.fusesource.jansi.AnsiConsole;

/**
 * Manages the application's log file.
 * @author Winter Roberts
 */
public class Logger {
	
	private static File outFile;
	private static FileOutputStream fos;
	private static PrintStream stream;
	private static int logCountToday = 1;
	private static boolean setup = false;
	
	/**
	 * Installs the ANSI Console, creates the correct log file, and opens a file output stream to it.
	 */
	public static void setup(){
		if(!setup){
			AnsiConsole.systemInstall();
			outFile = new File("./logs/"+LogOut.getStreamDate()+"-"+logCountToday+".log");
			outFile.getParentFile().mkdirs();
			correctFileNaming();
			try {
				fos = new FileOutputStream(outFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			stream = new PrintStream(fos);
			setup = true;
		}
	}

	/**
	 * Renames the log file to avoid overwriting existing logs.
	 */
	private static void correctFileNaming() {
		if(hasFile()){
			logCountToday++;
			outFile = new File("./logs/"+LogOut.getStreamDate()+"-"+logCountToday+".log");
			correctFileNaming();
		}
	}

	/**
	 * Gets this logger's .
	 * @return The print stream that prints to the log file.
	 */
	public static PrintStream getStream() {
		return stream;
	}
	
	/**
	 * Checks whether or not the log file exists. Used internally to ensure that a log file won't be overwritten.
	 * @return True if the log file exists, false otherwise.
	 */
	public static boolean hasFile(){
		if(outFile.exists()){
			return true;
		}
		return false;
	}

}
