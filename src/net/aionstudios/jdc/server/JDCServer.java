package net.aionstudios.jdc.server;

import net.aionstudios.jdc.content.ResponseCode;
import net.aionstudios.jdc.context.ContextHandler;
import net.aionstudios.jdc.logging.LogOut;
import net.aionstudios.jdc.logging.Logger;
import net.aionstudios.jdc.logging.StandardOverride;
import net.aionstudios.jdc.server.content.PageParser;
import net.aionstudios.jdc.server.content.Website;
import net.aionstudios.jdc.server.content.WebsiteManager;
import net.aionstudios.jdc.server.util.FormatUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpServer;

public class JDCServer {
	
	private static HttpServer server;
	private static int port = 80;
	
	/*
	 * Accesses a series of folders placed next to the server jar file.
	 * 
	 * The head level contains the "websites" folder, which contain folders labeled with no scheme
	 * required, one for each website, a default is provided with the server and comes with
	 * extensive description to assist new users.
	 * 
	 * The "computed" folder inside of this holds all of the precomputed jdc pages and leaves
	 * the non-cron based page updates to be processed per request in order to save server resources.
	 * 
	 * It also contains a folder labeled "content" which holds any files (css, js, html and jdc) that are sent to the user.
	 * 
	 * Finally, it contains a folder labeled "java" which contains jar files imported into the 
	 * JVM by the server after reading this website's config file.
	 * 
	 * The JSON config file(s) are placed at the top of each website folder and will contain
	 * all necessary information about the website and importing the user code properly
	 * as well as offering settings for htaccess like stuffs.
	 * 
	 * Personally, as I'm using forefront to route, I have a separate website for each subdomain
	 * but the configs will offer settings for this.
	 */
	/**
	 * Starts a new {@link HttpServer} as well as the server's {@link Logger} and loads config files.
	 * @param args		Arguments passed by the command line.
	 */
	public static void main(String[] args) {
		Logger.setup();
		LogOut.setStreamPrefix("JDC Server");
		StandardOverride.enableOverride();
		JDCServerInfo.readConfigsAtStart();
		//System.out.println(PageParser.parseGeneratePage(w, null, null, null, w.getContentFile("/index.jdc")));
		try {
			server = HttpServer.create(new InetSocketAddress(port), 0);
		} catch (IOException e) {
			System.err.println("Failed to start HTTP Server!");
			e.printStackTrace();
			System.exit(0);
		}
		server.createContext("/", new ContextHandler());
		server.setExecutor(Executors.newCachedThreadPool());
		server.start();
		System.out.println("Server started on port " + port);
		startSecureServer();
	}
	
	/**
	 * Initializes a {@link JDCSecureServer} if config files dictate to do so. HTTPS is disabled by default.
	 */
	private static void startSecureServer() {
		File certsConfig = new File("./certs.json");
		JSONObject certsJson = FormatUtils.getLinkedJsonObject();
		if(certsConfig.exists()) {
			certsJson = JDCServerInfo.readConfig(certsConfig);
		} else {
			try {
				certsConfig.createNewFile();
				certsJson.put("enable_sll_server", false);
				certsJson.put("jks_certificate", "cert.jks");
				certsJson.put("store_password", "changeit");
				certsJson.put("key_password", "changeit");
				certsJson.put("cert_alias", "certificate");
				JDCServerInfo.writeConfig(certsJson, certsConfig);
			} catch (IOException e) {
				System.err.println("Encountered an IOException during config file operations!");
				e.printStackTrace();
			} catch (JSONException e) {
				System.err.println("Encountered an IOException during config file operations!");
				e.printStackTrace();
			}
		}
		try {
			boolean enabled = certsJson.getBoolean("enable_sll_server");
			if(enabled) {
				String jksCertificate = certsJson.getString("jks_certificate");
				String storePassword = certsJson.getString("store_password");
				String keyPassword = certsJson.getString("key_password");
				String certAlias = certsJson.getString("cert_alias");
				JDCSecureServer.startServer(jksCertificate, storePassword, keyPassword, certAlias);
			}
		} catch (JSONException e1) {
			System.err.println("Failed to interpret processor config!");
			e1.printStackTrace();
		}
	}

}
