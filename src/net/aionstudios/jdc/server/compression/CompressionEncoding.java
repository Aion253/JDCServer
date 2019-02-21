package net.aionstudios.jdc.server.compression;

public enum CompressionEncoding {

	NONE(0),
	DEFLATE(1),
	GZIP(2);

	private final int value;

	/**
	 * Creates a {@link ResponseStatus} by value.
	 * 
	 * @param newValue An integer representing the value of an enum in this class.
	 */
	CompressionEncoding(final int newValue) {
		value = newValue;
	}
	
	/**
	 * @return The numeric value of a definition.
	 */
	public int getValue() {
		return value;
	}
	
}
