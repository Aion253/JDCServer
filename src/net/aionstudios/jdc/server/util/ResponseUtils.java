package net.aionstudios.jdc.server.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

import com.nixxcode.jvmbrotli.enc.BrotliOutputStream;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import net.aionstudios.jdc.content.Cookie;
import net.aionstudios.jdc.content.RequestVariables;
import net.aionstudios.jdc.content.ResponseCode;
import net.aionstudios.jdc.server.compression.BrotliCompressor;
import net.aionstudios.jdc.server.compression.CompressionEncoding;
import net.aionstudios.jdc.server.compression.DeflateCompressor;
import net.aionstudios.jdc.server.compression.GZIPCompressor;
import net.aionstudios.jdc.server.content.GeneratorResponse;
import net.aionstudios.jdc.server.content.Website;
import net.aionstudios.jdc.server.stream.StreamRange;

/**
 * A utility class which handles HTTP errors, and file and JDC compression
 * and transport, 
 * @author Winter Roberts
 */
public class ResponseUtils {
	
	/**
	 * Generates a response to the client.
	 * @param he The HTTPExchange on which to respond.
	 * @param httpResponseCode The HTTP response code.
	 * @param response The response (likely a serialized {@link JSONObject}).
	 * @return True if the response was sent successfully, false otherwise.
	 */
	public static boolean generateHTTPResponse(GeneratorResponse gResponse, HttpExchange he, RequestVariables vars, File page, Website w, CompressionEncoding ce) {
		if(vars.getStreamFile()!=null) {
			return fileHTTPResponse(he, vars, vars.getStreamFile(), w, ce);
		}
		String response = gResponse.getResponse();
		ResponseCode rc = gResponse.getResponseCode();
		String redirect = vars!=null ? vars.getRedirect() : null;
		vars.setResponseCode(rc);
		if(!(rc.getCode() >= 100)) {
			rc = ResponseCode.OK;
		}
		if((rc.getCode()>200&&rc.getCode()<300)||rc.getCode()>=400) {
			try {
				Headers respHeaders = he.getResponseHeaders();
				respHeaders.set("Content-Type", vars.getContentType());
				respHeaders.set("Last-Modified", FormatUtils.getLastModifiedAsHTTPString(System.currentTimeMillis()));
				String errorResp = w.getErrorContent(rc, he, vars);
				byte[] errRBytes;
				if(ce==CompressionEncoding.BR) {
					respHeaders.set("Content-Encoding", "br");
					errRBytes = BrotliCompressor.compress(errorResp);
				} else if(ce==CompressionEncoding.GZIP) {
					respHeaders.set("Content-Encoding", "gzip");
					errRBytes = GZIPCompressor.compress(errorResp);
				} else if (ce==CompressionEncoding.DEFLATE) {
					respHeaders.set("Content-Encoding", "deflate");
					errRBytes = DeflateCompressor.compress(errorResp);
				} else {
					errRBytes = errorResp.getBytes(StandardCharsets.UTF_8);
				}
				he.sendResponseHeaders(rc.getCode(), errRBytes.length);
				OutputStream os = he.getResponseBody();
				os.write(errRBytes);
				safeCloseStream(os);
				return true;
			} catch (IOException e) {
				return false;
			}
		}
		try {
			if(response!=null&&!response.isEmpty()) {
				Headers respHeaders = he.getResponseHeaders();
				if(vars!=null) {
					for(Cookie c : vars.getCookieManager().getNewCookies()) {
						respHeaders.add("Set-Cookie", c.makeSetterString());
					}
				}
				if(redirect!=null) {
					rc = ResponseCode.FOUND_REDIRECT;
					respHeaders.set("Location", vars.getRedirect());
				}
				respHeaders.set("Content-Type", vars.getContentType());
				respHeaders.set("Last-Modified", FormatUtils.getLastModifiedAsHTTPString(System.currentTimeMillis()));
				byte[] respBytes;
				if(ce==CompressionEncoding.BR) {
					respHeaders.set("Content-Encoding", "br");
					respBytes = BrotliCompressor.compress(response);
				} else if(ce==CompressionEncoding.GZIP) {
					respHeaders.set("Content-Encoding", "gzip");
					respBytes = GZIPCompressor.compress(response);
				} else if (ce==CompressionEncoding.DEFLATE) {
					respHeaders.set("Content-Encoding", "deflate");
					respBytes = DeflateCompressor.compress(response);
				} else {
					respBytes = response.getBytes(StandardCharsets.UTF_8);
				}
				he.sendResponseHeaders(rc.getCode(), respBytes.length);
				OutputStream os = he.getResponseBody();
				os.write(respBytes);
				safeCloseStream(os);
				return true;
			} else {
				generateHTTPResponse(new GeneratorResponse("", ResponseCode.NO_CONTENT), he, vars, page, w, ce);
			}
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Responds with a file over HTTP.
	 * @param rc		The HTTP {@link ResponseCode}.
	 * @param he		The {@link HttpExchange} handling this request.
	 * @param vars		The {@link RequestVariables} containing information about the request and response.
	 * @param file		The file to be streamed in response to the user.
	 * @param w			The {@link Website} on which the request was made.
	 * @return			True if the file was successfully transferred, false otherwise.
	 */
	public static boolean fileHTTPResponse(HttpExchange he, RequestVariables vars, File file, Website w, CompressionEncoding ce) {
		try {
			if (!file.isFile()) {
				vars.setStreamFile(null);
				vars.setResponseCode(ResponseCode.NOT_FOUND);
				generateHTTPResponse(new GeneratorResponse("", ResponseCode.NOT_FOUND), he, vars, file, w, ce);
	        } else {
	        	long len = file.length();
	        	StreamRange sr = he.getRequestHeaders().containsKey("Range") ? new StreamRange(he.getRequestHeaders().getFirst("Range"), len) : null;
	        	if (sr!=null&&!sr.validateRanges()) {
	        		vars.setStreamFile(null);
	        		vars.setResponseCode(ResponseCode.REQUEST_RANGE_NOT_SATISFIABLE);
	        		generateHTTPResponse(new GeneratorResponse("", ResponseCode.REQUEST_RANGE_NOT_SATISFIABLE), he, vars, file, w, ce);
	        	}
	        	String[] fileParts = file.getCanonicalPath().split("\\.");
	        	String mime = MimeUtils.getInstance().getMimeString(fileParts[fileParts.length-1]);

	        	Headers h = he.getResponseHeaders();
	        	if(mime.length()>0) {
	        		h.set("Content-Type", mime);
	        	}
	        	if(ce==CompressionEncoding.BR) {
	        		h.set("Content-Encoding", "br");
	        	} else if(ce==CompressionEncoding.GZIP) {
	        		h.set("Content-Encoding", "gzip");
	        	} else if (ce==CompressionEncoding.DEFLATE) {
	        		h.set("Content-Encoding", "deflate");
	        	}
	        	h.set("Accept-Ranges", "bytes");
	        	h.set("Last-Modified", FormatUtils.getLastModifiedAsHTTPString(file.lastModified()));
	        	
	        	Calendar date = Calendar.getInstance();
	        	date.setTime(new Date());
	        	date.add(Calendar.YEAR,1);
	        	h.set("Expires", FormatUtils.getLastModifiedAsHTTPString(date.getTime()));
	        	
	        	boolean ignoreSR = false;
	        	if (sr!=null && !sr.getRanges().isEmpty()) {
	        		Long[] s = sr.getRanges().get(0);
	        		ignoreSR = (s[0]!=null&&s[0]==0&&s[1]==null);
	        	}

	        	if(ce==CompressionEncoding.BR) {
	        		final byte[] buffer = new byte[1024];
	        		BrotliOutputStream os = new BrotliOutputStream(he.getResponseBody());
	        		streamFile(he, buffer, os, file, sr, ignoreSR);
	        		safeCloseStream(os);
	        	} else if(ce==CompressionEncoding.GZIP) {
	        		final byte[] buffer = new byte[1024];
	        		GZIPOutputStream os = new GZIPOutputStream(he.getResponseBody());
	        		streamFile(he, buffer, os, file, sr, ignoreSR);
	        		safeCloseStream(os);
	        	} else if (ce==CompressionEncoding.DEFLATE) {
	        		final byte[] buffer = new byte[1024];
	        		DeflaterOutputStream os = new DeflaterOutputStream(he.getResponseBody());
	        		streamFile(he, buffer, os, file, sr, ignoreSR);
	        		safeCloseStream(os);
	        	} else {
	        		final byte[] buffer = new byte[1024];
	        		OutputStream os = he.getResponseBody();
	        		streamFile(he, buffer, os, file, sr, ignoreSR);
	        		safeCloseStream(os);
	        	}
	        	return true;
	        }  
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static void outputFile(HttpExchange he, byte[] buffer, OutputStream os, File file) throws FileNotFoundException {
		FileInputStream fs = new FileInputStream(file);
		try {
			he.sendResponseHeaders(200, 0); 
			int count;
	        while ((count = fs.read(buffer)) > 0) {
	          os.write(buffer,0,count);
	        }
		} catch (Exception e) {
			//ignore
		}
		try {
			fs.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void streamFile(HttpExchange he, byte[] buffer, OutputStream os, File file, StreamRange sr, boolean ignoreSR) throws FileNotFoundException {
		if (sr == null || ignoreSR) {
			outputFile(he, buffer, os, file);
			return;
		}
		RandomAccessFile rf = new RandomAccessFile(file,"r");
		try {
			int count = 0;
			he.getResponseHeaders().set("Content-Range", sr.generateContentRangeString());
			he.sendResponseHeaders(206, 0);  
			long len = sr.getLength();
			for(Long[] r : sr.getRanges()) {
				long rangeBase = r[0]==null?(len-r[1]):r[0];
				long rangeEnd = r[1]==null?len:(r[0]==null?len:r[1]);
				while(rangeEnd-rangeBase>0) {
					//System.out.println(len + " " + rangeBase + " " + (rangeBase+1023) + " " + rangeEnd);
					rf.seek(rangeBase);
					int readcount = rf.read(buffer, count, (int)Math.min(rangeEnd-rangeBase, 1024-count));
					count += readcount;
					if(count==1024) {
						os.write(buffer,0,count);
						count = 0;
					}
					rangeBase += readcount;
				}
			}
			if(count>0) {
				os.write(buffer,0,count);
			}
		} catch (Exception e) {
			//ignore
		}
		try {
			rf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void safeCloseStream(OutputStream os) {
		try {
			os.flush();
		} catch (Exception e) {
			//ignore
		}
		try {
			os.close();
		} catch (Exception e) {
			//ignore
		}
	}

}
