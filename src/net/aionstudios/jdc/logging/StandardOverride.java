package net.aionstudios.jdc.logging;

public class StandardOverride {
	
	private static LogStream serverStream;
	private static LogErrStream serverErrStream;
	
	public static void enableOverride(){
		serverStream = new LogStream();
		serverErrStream = new LogErrStream();
		System.setOut(serverStream);
		System.setErr(serverErrStream);
	}

}
