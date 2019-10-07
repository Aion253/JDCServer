package net.aionstudios.jdc.server.compression;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.nixxcode.jvmbrotli.enc.BrotliOutputStream;

public class BrotliCompressor {
	
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
