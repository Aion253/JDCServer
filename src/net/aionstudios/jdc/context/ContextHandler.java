package net.aionstudios.jdc.context;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.aionstudios.jdc.content.MultipartFile;
import net.aionstudios.jdc.content.OutgoingRequest;
import net.aionstudios.jdc.content.RequestVariables;
import net.aionstudios.jdc.content.ResponseCode;
import net.aionstudios.jdc.server.compression.CompressionEncoding;
import net.aionstudios.jdc.server.content.GeneratorResponse;
import net.aionstudios.jdc.server.content.PageParser;
import net.aionstudios.jdc.server.content.Website;
import net.aionstudios.jdc.server.content.WebsiteManager;
import net.aionstudios.jdc.server.util.RequestUtils;
import net.aionstudios.jdc.server.util.ResponseUtils;
import net.aionstudios.jdc.service.OutgoingRequestService;

/**
 * Handles standard HTTP Contexts.
 * @author Winter
 *
 */
public class ContextHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange he) {
		long nanoStart = System.nanoTime();
		/*TODO
		 * extend information stored about websites in the website file
		 * to include the url(s) at which a website should accept calls.
		 * 
		 * Direct server exchanges in this file in order to locate the
		 * desired website and execute JDC as necessary.
		 * 
		 * Cron, Call, Live
		 */
		String[] requestSplit;
		if(he.getRequestURI().toString().contains("?")) {
			requestSplit = he.getRequestURI().toString().split("\\?", 2);
		} else {
			requestSplit = new String[2];
			requestSplit[0] = he.getRequestURI().toString();
			requestSplit[1] = "";
		}
		String requestContext = "";
		if(requestSplit[0].endsWith("/")) {
			requestSplit[0] = requestSplit[0] + "index.jdc";
		}
		Map<String, String> getQuery = new HashMap<String, String>();
		if(requestSplit.length>1) {
			requestContext = requestSplit[0];
			getQuery = RequestUtils.resolveGetQuery(requestSplit[1]);
		}
		Map<String, String> postQuery = new HashMap<String, String>();
		Map<String, String> cookies = new HashMap<String, String>();
		cookies = RequestUtils.resolveCookies(he);
		String hostName = he.getRequestHeaders().getFirst("Host").split(":")[0];
		CompressionEncoding ce = CompressionEncoding.NONE;
		if(he.getRequestHeaders().containsKey("Accept-Encoding")) {
			String accept = he.getRequestHeaders().getFirst("Accept-Encoding");
			if(accept.contains("br")&&!accept.contains("br;q=0")&&!accept.contains("br; q=0")){
				ce = CompressionEncoding.BR;
			} else if(accept.contains("gzip")&&!accept.contains("gzip;q=0")&&!accept.contains("gzip; q=0")){
				ce = CompressionEncoding.GZIP;
			} else if (accept.contains("deflate")&&!accept.contains("deflate;q=0")&&!accept.contains("deflate; q=0")) {
				ce = CompressionEncoding.DEFLATE;
			}
		}
		Website wb = WebsiteManager.getWebsiteByAddress(hostName);
		String proxyUrl = wb.getProxyManager().getProxyUrl(requestSplit[0]);
		if(proxyUrl!=null) {
			OutgoingRequest or = new OutgoingRequest("", null);
			String rp = OutgoingRequestService.executePost(proxyUrl+ (requestSplit[1].length()>0 ? "?" : "") + requestSplit[1], OutgoingRequestService.postMapToString(postQuery), or);
			RequestVariables v = new RequestVariables(null, null, null, null, null);
			or.getLastHeader("Content-Type");
			v.setContentType(or.getLastHeader("Content-Type"));
			v.setRedirect(or.getLastHeader("Location"));
			ResponseUtils.generateHTTPResponse(new GeneratorResponse(or.getContent(), v.getResponseCode()), he, v, null, wb, ce);
			return;
		}
		//File Uploads
		List<MultipartFile> mfs = new ArrayList<MultipartFile>();
		List<FileItem> deleteLater = new ArrayList<>();
		String cT = he.getRequestHeaders().containsKey("Content-Type") ? he.getRequestHeaders().getFirst("Content-Type") : "text/html";
		if(cT.contains("multipart/form-data")||cT.contains("multipart/stream")) {
			DiskFileItemFactory d = new DiskFileItemFactory();
			try {
				ServletFileUpload up = new ServletFileUpload(d);
				List<FileItem> result = up.parseRequest(new RequestContext() {

					@Override
					public String getCharacterEncoding() {
						return "UTF-8";
					}

					@Override
					public int getContentLength() {
						return 0; //tested to work with 0 as return
					}

					@Override
					public String getContentType() {
						return cT;
					}

					@Override
					public InputStream getInputStream() throws IOException {
						return he.getRequestBody();
					}

				});
				System.out.println("B");
				for(FileItem fi : result) {
					if(!fi.isFormField()) {
			        	mfs.add(new MultipartFile(fi.getFieldName(), fi.getName(), fi.getContentType(), fi.getInputStream(), fi.getSize()));
			        	deleteLater.add(fi);
			        } else {
			        	postQuery.put(fi.getFieldName(), fi.getString());
			        }
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			if(he.getRequestMethod().equalsIgnoreCase("POST")) {
				postQuery = RequestUtils.resolvePostQuery(he);
			}
		}
        RequestVariables vars = new RequestVariables(postQuery, getQuery, cookies, requestSplit[0], mfs);
		if(requestSplit[0].endsWith(".jdc")) {
			ResponseUtils.generateHTTPResponse(PageParser.parseGeneratePage(wb, he, vars, wb.getContentFile(requestSplit[0])), he, vars, wb.getContentFile(requestSplit[0]), wb, ce);
			for(FileItem fi : deleteLater) {
            	fi.delete();
            }
			return;
		} else {
			ResponseUtils.fileHTTPResponse(he, vars, wb.getContentFile(requestSplit[0]), wb, ce);
			for(FileItem fi : deleteLater) {
            	fi.delete();
            }
			return;
		}
	}

}
