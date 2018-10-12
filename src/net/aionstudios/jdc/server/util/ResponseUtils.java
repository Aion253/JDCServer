package net.aionstudios.jdc.server.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import com.sun.net.httpserver.HttpExchange;

public class ResponseUtils {
	
	/**
	 * Generates a response to the client.
	 * 
	 * @param he The HTTPExchange on which to respond.
	 * @param httpResponseCode The HTTP response code.
	 * @param response The response (likely a serialized {@link JSONObject}).
	 * @return True if the response was sent successfully, false otherwise.
	 */
	public static boolean generateHTTPResponse(HttpExchange he, int httpResponseCode, String response) {
		try {
			he.sendResponseHeaders(httpResponseCode, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public static boolean fileHTTPResponse(HttpExchange he, File file) {
		try {
			if(file.exists()) {
				he.sendResponseHeaders(200, file.length());
				System.out.println(file.length());
				OutputStream os = he.getResponseBody();
				Files.copy(file.toPath(), os);
				os.close();
			}
		} catch (IOException e) {
			return false;
		}
		return true;
	}

}
