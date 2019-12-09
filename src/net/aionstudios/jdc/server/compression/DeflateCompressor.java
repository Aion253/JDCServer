package net.aionstudios.jdc.server.compression;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.DeflaterOutputStream;

import com.nixxcode.jvmbrotli.enc.BrotliOutputStream;

/**
 * A {@link CompressionEncoding} that compresses using Deflate.
 * @author Winter Roberts
 */
public class DeflateCompressor {

	/**
	 * Compresses a String using Deflate.
	 * @param str The String to be compressed.
	 * @return A byte array, the compressed String.
	 * @throws IOException If the {@link DeflaterOutputStream} fails an IO operation.
	 */
	public static byte[] compress(String str) throws IOException {
	    if ((str == null) || (str.length() == 0)) {
	      return null;
	    }
	    ByteArrayOutputStream obj = new ByteArrayOutputStream();
	    DeflaterOutputStream deflate = new DeflaterOutputStream(obj);
	    deflate.write(str.getBytes(StandardCharsets.UTF_8));
	    deflate.flush();
	    deflate.close();
	    return obj.toByteArray();
	}
	
}
