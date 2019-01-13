package net.aionstudios.jdc.server.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

/**
 * A utility class to standardize and ease access to header variables.
 * @author Winter
 *
 */
public class RequestUtils {
	
	/**
	 * Gets the IP address of a client if it traveled through a normal proxy or other system that provides the X-Forwarded-For header.
	 * 
	 * @param he The HTTP request.
	 * @return The IP address of the request origin.
	 */
	public static String getRequestIP(HttpExchange he) {
		if(he.getRequestHeaders().containsKey("X-Forwarded-For")) {
			return he.getRequestHeaders().getFirst("X-Forwarded-For");
		}
		return he.getRemoteAddress().getAddress().getHostAddress();
	}
	
	/**
	 * Maps elements of a get query for easy processing.
	 * 
	 * @param qs A get query from url.
	 * @return A HashMap of get parameters and their values.
	 */
	public static Map<String, String> resolveGetQuery(String qs) {
	    Map<String, String> result = new HashMap<>();
	    if (qs == null)
	        return result;

	    int last = 0, next, l = qs.length();
	    while (last < l) {
	        next = qs.indexOf('&', last);
	        if (next == -1)
	            next = l;

	        if (next > last) {
	            int eqPos = qs.indexOf('=', last);
	            try {
	                if (eqPos < 0 || eqPos > next)
	                    result.put(URLDecoder.decode(qs.substring(last, next), "UTF-8"), "");
	                else
	                    result.put(URLDecoder.decode(qs.substring(last, eqPos), "UTF-8"), URLDecoder.decode(qs.substring(eqPos + 1, next), "UTF-8"));
	            } catch (UnsupportedEncodingException e) {
	                throw new RuntimeException(e); // will never happen, utf-8 support is mandatory for java
	            }
	        }
	        last = next + 1;
	    }
	    return result;
	}
	
	/**
	 * Maps elements of a get query for easy processing.
	 * 
	 * @param qs A get query from url.
	 * @return A HashMap of get parameters and their values.
	 */
	public static Map<String, String> resolveCookies(HttpExchange he) {
		if(he.getRequestHeaders().containsKey("Cookie")) {
			String qs = he.getRequestHeaders().getFirst("Cookie");
			Map<String, String> cookies = new HashMap<String, String>();
			for(String s : qs.split("; ")) {
				String[] nv = s.split("=", 2);
				cookies.put(nv[0], nv[1]);
			}
			return cookies;
		}
		return null;
	}
	
	/**
	 * Maps elements of a post query for easy processing.
	 * 
	 * @param he The HTTP request.
	 * @return A HashMap of post parameters and their values.
	 */
	public static Map<String, String> resolvePostQuery(HttpExchange httpExchange) {
		  Map<String, String> parameters = new HashMap<>();
		  InputStream inputStream = httpExchange.getRequestBody();
		  ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		  byte[] buffer = new byte[2048];
		  int read = 0;
		  try {
			while ((read = inputStream.read(buffer)) != -1) {
				  byteArrayOutputStream.write(buffer, 0, read);
			  }
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		  String[] keyValuePairs = byteArrayOutputStream.toString().split("&");
		  for (String keyValuePair : keyValuePairs) {
		    String[] keyValue = keyValuePair.split("=");
		    if (keyValue.length != 2) {
		      continue;
		    }
		    try {
				parameters.put(keyValue[0], URLDecoder.decode(keyValue[1], "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		  }
		  return parameters;
	}

}
