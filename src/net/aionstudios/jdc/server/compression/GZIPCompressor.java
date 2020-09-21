package net.aionstudios.jdc.server.compression;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * A {@link CompressionEncoding} that compresses using GZIP.
 * @author Winter Roberts
 */
public class GZIPCompressor {
	
	/**
	 * Compresses a String using GZIP.
	 * @param str The String to be compressed.
	 * @return A byte array, the compressed String.
	 * @throws IOException If the {@link GZIPOutputStream} fails an IO operation.
	 */
	public static byte[] compress(String str) throws IOException {
	    if ((str == null) || (str.length() == 0)) {
	      return null;
	    }
	    ByteArrayOutputStream obj = new ByteArrayOutputStream();
	    GZIPOutputStream gzip = new GZIPOutputStream(obj);
	    gzip.write(str.getBytes(StandardCharsets.UTF_8));
	    gzip.flush();
	    gzip.close();
	    return obj.toByteArray();
	}

}
