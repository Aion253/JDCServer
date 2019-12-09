package net.aionstudios.jdc.logging;

import java.io.PrintStream;

/**
 * Logs messages to the {@link Logger}'s {@link PrintStream}.
 * @author Winter Roberts
 */
public class LogStream extends PrintStream {

	/**
	 * Creates a new stream which streams messages to a log file.
	 */
	public LogStream() {
		super(Logger.getStream(), true);
	}
	
	@Override
    public final void print(String s) {
        LogOut.print(s);
    }
	
	@Override
    public final void println(String s) {
        LogOut.println(s);
    }

}
