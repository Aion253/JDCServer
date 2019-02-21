package net.aionstudios.jdc.server.compression;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.DeflaterOutputStream;

public class DeflateCompressor {

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
