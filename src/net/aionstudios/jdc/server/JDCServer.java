package net.aionstudios.jdc.server;

import net.aionstudios.jdc.context.ContextHandler;
import net.aionstudios.jdc.logging.LogOut;
import net.aionstudios.jdc.logging.Logger;
import net.aionstudios.jdc.logging.StandardOverride;
import net.aionstudios.jdc.server.content.PageParser;
import net.aionstudios.jdc.server.content.Website;
import net.aionstudios.jdc.server.content.WebsiteManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

public class JDCServer {
	
	private static HttpServer server;
	
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
	public static void main(String[] args) {
		Logger.setup();
		LogOut.setStreamPrefix("JDC Server");
		StandardOverride.enableOverride();
		JDCServerInfo.readConfigsAtStart();
		//System.out.println(PageParser.parseGeneratePage(w, null, null, null, w.getContentFile("/index.jdc")));
		try {
			server = HttpServer.create(new InetSocketAddress(80), 0);
		} catch (IOException e) {
			System.err.println("Failed to start HTTP Server!");
			e.printStackTrace();
			System.exit(0);
		}
		server.createContext("/", new ContextHandler());
		server.setExecutor(Executors.newCachedThreadPool());
		server.start();
		System.out.println("Server started on port " + 80);
	}

}
