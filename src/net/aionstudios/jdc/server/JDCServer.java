package net.aionstudios.jdc.server;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

import com.nixxcode.jvmbrotli.common.BrotliLoader;
import com.sun.net.httpserver.HttpServer;

import net.aionstudios.jdc.console.JDCConsole;
import net.aionstudios.jdc.console.ListCommand;
import net.aionstudios.jdc.console.ReloadCommand;
import net.aionstudios.jdc.console.StopCommand;
import net.aionstudios.jdc.context.ContextHandler;
import net.aionstudios.jdc.logging.LogOut;
import net.aionstudios.jdc.logging.Logger;
import net.aionstudios.jdc.logging.StandardOverride;
import net.aionstudios.jdc.server.util.FormatUtils;

/**
 * Handles requests to the insecure web port for this server.
 * @author Winter Roberts
 */
public class JDCServer {
	
	private static HttpServer server;
	
	/*
	 * Accesses a series of folders placed next to the server jar file.
	 * 
	 * The head level contains the "websites" folder, which contain folders labeled with no scheme
	 * required, one for each website, a default is provided with the server and comes with
	 * extensive description to assist new users.
	 * 
	 * It contains a folder labeled "content" which holds any files (css, js, html and jdc) that are sent to the user.
	 * 
	 * Also, it contains a folder labeled "java" which contains jar files imported into the 
	 * JVM by the server after reading this website's config file.
	 * 
	 * The JSON config file(s) are placed at the top of each website folder and will contain
	 * all necessary information about the website and importing the user code properly
	 * as well as offering settings.
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
		try {
			server = HttpServer.create(new InetSocketAddress(JDCServerInfo.getHttpPort()), 0);
		} catch (IOException e) {
			System.err.println("Failed to start HTTP Server!");
			e.printStackTrace();
			System.exit(0);
		}
		server.createContext("/", new ContextHandler());
		server.setExecutor(Executors.newCachedThreadPool());
		server.start();
		System.out.println("Server started on port " + JDCServerInfo.getHttpPort());
		new ReloadCommand();
		new ListCommand();
		new StopCommand();
		JDCConsole.getInstance().startConsoleThread();
		startSecureServer();
		if(JDCServerInfo.isEnableBrotli()) BrotliLoader.isBrotliAvailable();
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
				certsJson.put("enable_ssl_server", false);
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
			boolean enabled = certsJson.getBoolean("enable_ssl_server");
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
