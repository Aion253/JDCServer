package net.aionstudios.jdc.server.compression;

public enum CompressionEncoding {

	NONE,
	DEFLATE,
	GZIP,
	BR;

	/**
	 * Creates a {@link ResponseStatus} by value.
	 * 
	 * @param newValue An integer representing the value of an enum in this class.
	 */
	CompressionEncoding() {}
	
}
