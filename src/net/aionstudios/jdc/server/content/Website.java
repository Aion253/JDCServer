package net.aionstudios.jdc.server.content;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import net.aionstudios.jdc.content.RequestVariables;
import net.aionstudios.jdc.content.ResponseCode;
import net.aionstudios.jdc.processor.ElementProcessor;
import net.aionstudios.jdc.processor.Processor;
import net.aionstudios.jdc.processor.ProcessorSet;
import net.aionstudios.jdc.server.JDCServerInfo;
import net.aionstudios.jdc.server.proxy.ProxyManager;
import net.aionstudios.jdc.server.util.ConsoleErrorUtils;
import net.aionstudios.jdc.server.util.FormatUtils;

/**
 * A website retrieves content from a specific folder when requests are made to the server on addresses for which this website accepts.
 * @author Winter Roberts
 */
public class Website {
	
	private String name;
	private String[] addresses;
	private boolean sslOn;
	private Map<String, ContentProcessor> processors = new HashMap<>();
	private Map<ResponseCode, File> errorMappings = new HashMap<>();
	private ProxyManager pm = new ProxyManager();
	
	private File websiteFolder;
	
	private File contentFolder;
	private File javaFolder;
	
	private File processorConfig;
	private File errorsConfig;
	private File proxiesConfig;
	
	private JSONObject processorJson = FormatUtils.getLinkedJsonObject();
	private JSONObject errorJson = FormatUtils.getLinkedJsonObject();
	private JSONObject proxyJson = FormatUtils.getLinkedJsonObject();
	
	/**
	 * Creates a new website reading its configurations and loading its backend code.
	 * @param name		The system name of this website.
	 * @param addresses	The addresses for which a connection to the server should resolve with content on this website.
	 * @param sslOn		Whether of not the website should be made accessible through the {@link JDCSecureServer}.
	 */
	public Website(String name, String[] addresses, boolean sslOn) {
		this.name = name;
		this.addresses = addresses;
		this.sslOn = sslOn;
		websiteFolder = new File("./websites/"+name);
		contentFolder = new File("./websites/"+name+"/content");
		javaFolder = new File("./websites/"+name+"/java");
		processorConfig = new File("./websites/"+name+"/processors.json");
		errorsConfig = new File("./websites/"+name+"/errors.json");
		proxiesConfig = new File("./websites/"+name+"/proxies.json");
		if(!websiteFolder.exists()) {
			websiteFolder.mkdirs();
		}
		if(!contentFolder.exists()) {
			contentFolder.mkdirs();
		}
		if(!javaFolder.exists()) {
			javaFolder.mkdirs();
		}
		readProcessorsConfig();
		readErrorsConfig();
		readProxiesConfig();
		WebsiteManager.addWebsite(this);
	}
	
	/**
	 * Reads the Java {@link JDC} processor configuration file for this website and registers a {@link ContentProcessor} to load external archives into the JVM.
	 * @see {@link JDCLoader}
	 */
	public void readProcessorsConfig() {
		if(processorConfig.exists()) {
			processorJson = JDCServerInfo.readConfig(processorConfig);
		} else {
			try {
				processorConfig.createNewFile();
				JSONArray pa = new JSONArray();
				JSONObject pad = FormatUtils.getLinkedJsonObject();
				pad.put("name", "default");
				pad.put("jar", "default.jar");
				pad.put("jdc_class", "com.default.MainJDC");
				pa.put(pad);
				processorJson.put("processors", pa);
				JDCServerInfo.writeConfig(processorJson, processorConfig);
			} catch (IOException e) {
				System.err.println("Encountered an IOException during config file operations!");
				e.printStackTrace();
			} catch (JSONException e) {
				System.err.println("Encountered an IOException during config file operations!");
				e.printStackTrace();
			}
		}
		JSONArray processorArray = new JSONArray();
		try {
			processorArray = processorJson.getJSONArray("processors");
		} catch (JSONException e1) {
			System.err.println("Failed to interpret processor config!");
			e1.printStackTrace();
		}
		for(int i = 0; i < processorArray.length(); i++) {
			try {
				JSONObject processor = processorArray.getJSONObject(i);
				new ContentProcessor(this, processor.getString("name"), new File("./websites/"+name+"/java/"+processor.getString("jar")), processor.getString("jdc_class"));
			} catch (JSONException e) {
				System.err.println("Failed to interpret processor config!");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Reads the error configuration file for this website and registers them to their relevant error codes.
	 * @see {@link ResponseCode}
	 * @see {@link #makeErrorMapping(ResponseCode, File)}
	 */
	public void readErrorsConfig() {
		if(errorsConfig.exists()) {
			errorJson = JDCServerInfo.readConfig(errorsConfig);
		} else {
			try {
				errorsConfig.createNewFile();
				JSONArray ea = new JSONArray();
				JSONObject ead = FormatUtils.getLinkedJsonObject();
				ead.put("error_code", 404);
				ead.put("enable_override", false);
				ead.put("error_file", "null");
				ea.put(ead);
				errorJson.put("errors", ea);
				JDCServerInfo.writeConfig(errorJson, errorsConfig);
			} catch (IOException e) {
				System.err.println("Encountered an IOException during config file operations!");
				e.printStackTrace();
			} catch (JSONException e) {
				System.err.println("Encountered an IOException during config file operations!");
				e.printStackTrace();
			}
		}
		JSONArray errorArray = new JSONArray();
		try {
			errorArray = errorJson.getJSONArray("errors");
		} catch (JSONException e1) {
			System.err.println("Failed to interpret errors config!");
			e1.printStackTrace();
		}
		for(int i = 0; i < errorArray.length(); i++) {
			try {
				JSONObject error = errorArray.getJSONObject(i);
				if(error.getBoolean("enable_override")) {
					boolean made = false;
					for(ResponseCode r : ResponseCode.values()) {
						if(r.getCode() == error.getInt("error_code")) {
							File errorFile = this.getContentFile(error.getString("error_file"));
							if(errorFile.exists()) {
								makeErrorMapping(r, errorFile);
								made = true;
								break;
							} else {
								System.err.println("Couldn't add error mapping, file '"+errorFile.getPath()+"' doesn't exist.");
								break;
							}
						}
					}
					if(made) {
						continue;
					}
					System.err.println("Error response code '"+error.getInt("error_code")+"' doesn't exist.");
				}
			} catch (JSONException e) {
				System.err.println("Failed to interpret processor config!");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Reads the proxy configuration file for this website and registers them to internally redirect requests.
	 * @see {@link ProxyManager}
	 */
	public void readProxiesConfig() {
		if(proxiesConfig.exists()) {
			proxyJson = JDCServerInfo.readConfig(proxiesConfig);
		} else {
			try {
				proxiesConfig.createNewFile();
				JSONArray ea = new JSONArray();
				JSONObject ead = FormatUtils.getLinkedJsonObject();
				ead.put("context", "/");
				ead.put("proxy_url", "/");
				ea.put(ead);
				proxyJson.put("proxies", ea);
				JDCServerInfo.writeConfig(proxyJson, proxiesConfig);
			} catch (IOException e) {
				System.err.println("Encountered an IOException during config file operations!");
				e.printStackTrace();
			} catch (JSONException e) {
				System.err.println("Encountered an IOException during config file operations!");
				e.printStackTrace();
			}
		}
		JSONArray proxyArray = new JSONArray();
		try {
			proxyArray = proxyJson.getJSONArray("proxies");
		} catch (JSONException e1) {
			System.err.println("Failed to interpret proxies config!");
			e1.printStackTrace();
		}
		for(int i = 0; i < proxyArray.length(); i++) {
			try {
				JSONObject prx = proxyArray.getJSONObject(i);
				pm.putProxy(prx.getString("context"), prx.getString("proxy_url"));
			} catch (JSONException e) {
				System.err.println("Failed to interpret proxies config!");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @return A string, the name of this website, as read from its configuration.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return A string array, each of the host addresses which this website can be reached on.
	 */
	public String[] getAddresses() {
		return addresses;
	}
	
	/**
	 * @return True if this website is configured to allow access through the {@link JDCSecureServer}.
	 */
	public boolean isSslOn() {
		return sslOn;
	}
	
	/**
	 * Adds a new {@link ContentProcessor} to this website.
	 * <p>
	 * An unlimited number of additional archives containing {@link JDC} main classes can be added to a website.
	 * @param processor		The {@link ContentProcessor} to be added.
	 * @see {@link JDCLoader}
	 */
	public void addContentProcessor(ContentProcessor processor) {
		processors.put(processor.getName(), processor);
	}
	
	/**
	 * Locates a processor named by the value of a "javagenerate" attribute in an HTML tag.
	 * @param rc		The {@link ResponseCode} to write an internal server error to if the {@link ElementProcessor} doesn't exist.
	 * @param path		The '.' delimited string naming an {@link ElementProcessor} like [jdc_name].[processor_set].[element_processor] .
	 * @return An {@link ElementProcessor} to process on the tag and generate HTML.
	 */
	public ElementProcessor locateElementProcessor(ResponseCode rc, String path) {
		String[] points = path.split("\\.", 3);
		if(points.length!=3) {
			rc = ResponseCode.INTERNAL_SERVER_ERROR;
			ConsoleErrorUtils.printServerError(rc, path, Thread.currentThread().getStackTrace());
			return null;
		}
		if(processors.containsKey(points[0])) {
			ContentProcessor cp = processors.get(points[0]);
			if(cp.getJDC().getProcessorManager().getProcessorSets().containsKey(points[1])) {
				ProcessorSet ps = cp.getJDC().getProcessorManager().getProcessorSets().get(points[1]);
				if(ps.getElementProcessors().containsKey(points[2])) {
					return ps.getElementProcessors().get(points[2]);
				}
			}
		}
		rc = ResponseCode.INTERNAL_SERVER_ERROR;
		ConsoleErrorUtils.printServerError(rc, path, Thread.currentThread().getStackTrace());
		return null;
	}
	
	/**
	 * Locates a processor named by the value of a "javaexecute" attribute in an HTML tag.
	 * @param rc		The {@link ResponseCode} to write an internal server error to if the {@link Processor} doesn't exist.
	 * @param path		The '.' delimited string naming a {@link Processor} like [jdc_name].[processor_set].[processor] .
	 * @return A {@link Processor} to process without dynamically generating content.
	 */
	public Processor locateProcessor(ResponseCode rc, String path) {
		String[] points = path.split("\\.", 3);
		if(points.length!=3) {
			rc = ResponseCode.INTERNAL_SERVER_ERROR;
			ConsoleErrorUtils.printServerError(rc, path, Thread.currentThread().getStackTrace());
			return null;
		}
		if(processors.containsKey(points[0])) {
			ContentProcessor cp = processors.get(points[0]);
			if(cp.getJDC().getProcessorManager().getProcessorSets().containsKey(points[1])) {
				ProcessorSet ps = cp.getJDC().getProcessorManager().getProcessorSets().get(points[1]);
				if(ps.getProcessors().containsKey(points[2])) {
					return ps.getProcessors().get(points[2]);
				}
			}
		}
		rc = ResponseCode.INTERNAL_SERVER_ERROR;
		ConsoleErrorUtils.printServerError(rc, path, Thread.currentThread().getStackTrace());
		return null;
	}
	
	/**
	 * Searches for an error page on this website by {@link ResponseCode} and returns a {@link GeneratorResponse}.
	 * <p>
	 * If the website has not set a custom error page for the {@link ResponseCode} the default error page is returned.
	 * @param code		The {@link ResponseCode} for which this error should be generated.
	 * @param he		The {@link HttpExchange} handling this request.
	 * @param vars		The {@link RequestVariables} containing request and response data.
	 * @return A string, the content of this error page.
	 */
	public String getErrorContent(ResponseCode code, HttpExchange he, RequestVariables vars) {
		if(errorMappings.containsKey(code)) {
			String er = PageParser.parseGeneratePage(this, he, vars, errorMappings.get(code)).getResponse();
			if(er==null||er.isEmpty()) {
				return code.getDefaultErrorPage(he.getRequestURI().toString());
			} else {
				return er;
			}
		} else {
			return code.getDefaultErrorPage(he.getRequestURI().toString());
		}
	}
	
	/**
	 * Creates a new error mapping, pointing to a file by its website-relative path string.
	 * @param code			The {@link ResponseCode} for which this mapping should be made.
	 * @param relativePath	The website-relative path string of the error file.
	 */
	public void makeErrorMapping(ResponseCode code, String relativePath) {
		errorMappings.put(code, getContentFile(relativePath));
	}
	
	/**
	 * Creates a new error mapping, pointing to a file.
	 * @param code			The {@link ResponseCode} for which this mapping should be made.
	 * @param relativePath	The error file.
	 */
	public void makeErrorMapping(ResponseCode code, File relativePath) {
		errorMappings.put(code, relativePath);
	}
	
	/***
	 * Returns a file via the website-relative path string.
	 * @param path		The website-realtive path string.
	 * @return		The file named by this website-relative path string.
	 */
	public File getContentFile(String path) {
		return new File(contentFolder, path);
	}
	
	/**
	 * @return A list of all {@link ContentProcessor}s registered to this website.
	 */
	public Map<String, ContentProcessor> getProcessors(){
		return processors;
	}

	/**
	 * @return This website's {@link ProxyManager}.
	 */
	public ProxyManager getProxyManager() {
		return pm;
	}

}
