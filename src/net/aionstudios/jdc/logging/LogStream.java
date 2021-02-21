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
	
	@Override
	public final PrintStream printf(String format, Object... args) {
		LogOut.print(String.format(format, args));
		return this;
	}
	
	@Override
	public final PrintStream format(String format, Object... args) {
		return printf(format, args);
	}

}
