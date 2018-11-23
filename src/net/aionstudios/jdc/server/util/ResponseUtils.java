package net.aionstudios.jdc.server.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import net.aionstudios.jdc.content.Cookie;
import net.aionstudios.jdc.content.RequestVariables;
import net.aionstudios.jdc.content.ResponseCode;
import net.aionstudios.jdc.server.content.GeneratorResponse;
import net.aionstudios.jdc.server.content.Website;

public class ResponseUtils {
	
	/**
	 * Generates a response to the client.
	 * 
	 * @param he The HTTPExchange on which to respond.
	 * @param httpResponseCode The HTTP response code.
	 * @param response The response (likely a serialized {@link JSONObject}).
	 * @return True if the response was sent successfully, false otherwise.
	 */
	public static boolean generateHTTPResponse(GeneratorResponse gResponse, HttpExchange he, RequestVariables vars, File page, Website w) {
		String response = gResponse.getResponse();
		ResponseCode rc = gResponse.getResponseCode();
		if(rc.getCode()!=200) {
			try {
				Headers respHeaders = he.getResponseHeaders();
				String encoding = "UTF-8";
				respHeaders.set("Content-Type", "text/html; charset="+encoding);
				String errorResp = w.getErrorContent(rc, he, vars);
				byte[] errRBytes = errorResp.getBytes(StandardCharsets.UTF_8);
				he.sendResponseHeaders(rc.getCode(), errRBytes.length);
				OutputStream os = he.getResponseBody();
				os.write(errRBytes);
				os.close();
				return true;
			} catch (IOException e) {
				return false;
			}
		}
		try {
			if(response!=null&&!response.isEmpty()) {
				Headers respHeaders = he.getResponseHeaders();
				for(Cookie c : vars.getCookieManager().getNewCookies()) {
					respHeaders.add("Set-Cookie", c.makeSetterString());
				}
				String encoding = "UTF-8";
				respHeaders.set("Content-Type", "text/html; charset="+encoding);
				byte[] respBytes = response.getBytes(StandardCharsets.UTF_8);
				he.sendResponseHeaders(200, respBytes.length);
				OutputStream os = he.getResponseBody();
				os.write(respBytes);
				os.close();
				return true;
			} else {
				generateHTTPResponse(new GeneratorResponse("", ResponseCode.NO_CONTENT), he, vars, page, w);
			}
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public static boolean fileHTTPResponse(ResponseCode rc, HttpExchange he, RequestVariables vars, File file, Website w) {
		try {
			if (!file.isFile()) {
				generateHTTPResponse(new GeneratorResponse("", ResponseCode.NOT_FOUND), he, vars, file, w);
	        } else {
	              // Object exists and is a file: accept with response code 200.
	              String mime = "";
	              if(file.getCanonicalPath().endsWith(".html")) mime = "text/html";
	              if(file.getCanonicalPath().endsWith(".htm")) mime = "text/html";
	              if(file.getCanonicalPath().endsWith(".jdc")) mime = "text/html";
	              if(file.getCanonicalPath().endsWith(".js")) mime = "application/javascript";
	              if(file.getCanonicalPath().endsWith(".css")) mime = "text/css";
	              if(file.getCanonicalPath().endsWith(".svg")) mime = "image/svg+xml";

	              Headers h = he.getResponseHeaders();
	              if(mime.length()>0) {
	            	  h.set("Content-Type", mime);
	              }
	              he.sendResponseHeaders(200, 0);              

	              OutputStream os = he.getResponseBody();
	              FileInputStream fs = new FileInputStream(file);
	              final byte[] buffer = new byte[0x10000];
	              int count = 0;
	              while ((count = fs.read(buffer)) >= 0) {
	                os.write(buffer,0,count);
	              }
	              fs.close();
	              os.close();
	              return true;
	            }  
		} catch (IOException e) {
			return false;
		}
		return true;
	}

}
