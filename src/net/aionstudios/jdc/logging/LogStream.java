package net.aionstudios.jdc.logging;

import java.io.PrintStream;

public class LogStream extends PrintStream {

	public LogStream() {
		super(Logger.getStream(), true);
	}
	
	@Override
    public final void print(String s)
    {//do what ever you like
        LogOut.print(s);
    }
	
	@Override
    public final void println(String s)
    {//do what ever you like
        LogOut.println(s);
    }

}
