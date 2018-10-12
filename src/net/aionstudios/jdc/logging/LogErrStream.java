package net.aionstudios.jdc.logging;

import java.io.PrintStream;

import org.fusesource.jansi.AnsiConsole;

public class LogErrStream extends PrintStream {

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
