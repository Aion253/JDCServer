package net.aionstudios.jdc.server.compression;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.nixxcode.jvmbrotli.enc.BrotliOutputStream;

/**
 * A {@link CompressionEncoding} that compresses using Google's Brotli.
 * <p>
 * This encoding is not currently supported by all major browsers and servers,
 * and will not work on JDC builds based on Java 7 or earlier.
 * @author Winter Roberts
 */
public class BrotliCompressor {
	
	/**
	 * Compresses a String using Brotli.
	 * @param str The String to be compressed.
	 * @return A byte array, the compressed String.
	 * @throws IOException If the {@link BrotliOutputStream} fails an IO operation.
	 */
	public static byte[] compress(String str) throws IOException {
	    if ((str == null) || (str.length() == 0)) {
	      return null;
	    }
	    ByteArrayOutputStream obj = new ByteArrayOutputStream();
	    BrotliOutputStream br = new BrotliOutputStream(obj);
	    br.write(str.getBytes(StandardCharsets.UTF_8));
	    br.flush();
	    br.close();
	    return obj.toByteArray();
	}

}
