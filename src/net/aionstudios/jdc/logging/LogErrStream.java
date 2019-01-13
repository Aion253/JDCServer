package net.aionstudios.jdc.logging;

import java.io.PrintStream;

import org.fusesource.jansi.AnsiConsole;

/**
 * Logs errors to the {@link Logger}'s {@link PrintStream}.
 * @author Winter
 *
 */
public class LogErrStream extends PrintStream {

	/**
	 * Creates a new error stream which streams errors to a log file.
	 */
	public LogErrStream() {
		super(Logger.getStream(), true);
	}
	
	@Override
    public final void print(String s)
    {//do what ever you like
        LogOut.errp(s);
    }
	
	@Override
    public final void println(String s)
    {//do what ever you like
        LogOut.errpl(s);
    }

}
