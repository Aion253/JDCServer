package net.aionstudios.jdc.context;

import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.aionstudios.jdc.content.RequestVariables;
import net.aionstudios.jdc.content.ResponseCode;
import net.aionstudios.jdc.server.content.PageParser;
import net.aionstudios.jdc.server.content.Website;
import net.aionstudios.jdc.server.content.WebsiteManager;
import net.aionstudios.jdc.server.util.RequestUtils;
import net.aionstudios.jdc.server.util.ResponseUtils;

public class SecureContextHandler implements HttpHandler {

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
			requestSplit = new String[1];
			requestSplit[0] = he.getRequestURI().toString();
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
		if(he.getRequestMethod().equalsIgnoreCase("POST")) {
			postQuery = RequestUtils.resolvePostQuery(he);
		}
		Map<String, String> cookies = new HashMap<String, String>();
		cookies = RequestUtils.resolveCookies(he);
		String hostName = he.getRequestHeaders().getFirst("Host").split(":")[0];
		Website wb = WebsiteManager.getWebsiteByAddress(hostName);
		if(!wb.isSslOn()) {
			return;
		}
		RequestVariables vars = new RequestVariables(postQuery, getQuery, cookies);
		if(requestSplit[0].endsWith(".jdc")) {
			ResponseUtils.generateHTTPResponse(PageParser.parseGeneratePage(wb, he, vars, wb.getContentFile(requestSplit[0])), he, vars, wb.getContentFile(requestSplit[0]), wb);
			return;
		} else {
			ResponseUtils.fileHTTPResponse(ResponseCode.OK, he, vars, wb.getContentFile(requestSplit[0]), wb);
			return;
		}
	}

}
