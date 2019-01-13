package net.aionstudios.jdc.logging;

/**
 * Used to override the default std out and error output streams.
 * <p>
 * Sets the System "standard" output stream to a new {@link LogStream}
 * and the System "standard" error output stream to a new {@link LogErrorStream}.
 * @author Winter
 *
 */
public class StandardOverride {
	
	private static LogStream serverStream;
	private static LogErrStream serverErrStream;
	
	/**
	 * Override the outputs of the "standard" output and error streams to alternative {@link PrintStreams}.
	 * <p>
	 * Sets the System "standard" output stream to a new {@link LogStream}
	 * and the System "standard" error output stream to a new {@link LogErrorStream}.
	 */
	public static void enableOverride(){
		serverStream = new LogStream();
		serverErrStream = new LogErrStream();
		System.setOut(serverStream);
		System.setErr(serverErrStream);
	}

}
