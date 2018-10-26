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
import net.aionstudios.jdc.server.util.ConsoleErrorUtils;

public class Website {
	
	private String name;
	private String[] addresses;
	private boolean sslOn;
	private List<ContentProcessor> processors = new ArrayList<ContentProcessor>();
	private Map<ResponseCode, File> errorMappings = new HashMap<ResponseCode, File>();
	
	private File websiteFolder;
	
	private File contentFolder;
	private File javaFolder;
	
	private File processorConfig;
	private File errorsConfig;
	
	private JSONObject processorJson = new JSONObject();
	private JSONObject errorJson = new JSONObject();
	
	public Website(String name, String[] addresses, boolean sslOn) {
		this.name = name;
		this.addresses = addresses;
		this.sslOn = sslOn;
		websiteFolder = new File("./websites/"+name);
		contentFolder = new File("./websites/"+name+"/content");
		javaFolder = new File("./websites/"+name+"/java");
		processorConfig = new File("./websites/"+name+"/processors.json");
		errorsConfig = new File("./websites/"+name+"/errors.json");
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
		WebsiteManager.addWebsite(this);
	}
	
	public void readProcessorsConfig() {
		if(processorConfig.exists()) {
			processorJson = JDCServerInfo.readConfig(processorConfig);
		} else {
			try {
				processorConfig.createNewFile();
				JSONArray pa = new JSONArray();
				JSONObject pad = new JSONObject();
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
	
	public void readErrorsConfig() {
		if(errorsConfig.exists()) {
			errorJson = JDCServerInfo.readConfig(errorsConfig);
		} else {
			try {
				errorsConfig.createNewFile();
				JSONArray ea = new JSONArray();
				JSONObject ead = new JSONObject();
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
			System.err.println("Failed to interpret processor config!");
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
	
	public String getName() {
		return name;
	}
	
	public String[] getAddresses() {
		return addresses;
	}
	
	public boolean isSslOn() {
		return sslOn;
	}
	
	public void addContentProcessor(ContentProcessor processor) {
		for(ContentProcessor cp : processors) {
			if(cp.getName().equals(processor.getName())) {
				processors.remove(cp);
				processors.add(processor);
				return;
			}
		}
		processors.add(processor);
	}
	
	public ElementProcessor locateElementProcessor(ResponseCode rc, String path) {
		String[] points = path.split("\\.", 3);
		if(points.length!=3) {
			rc = ResponseCode.INTERNAL_SERVER_ERROR;
			ConsoleErrorUtils.printServerError(rc, path, Thread.currentThread().getStackTrace());
			return null;
		}
		for(ContentProcessor cp : processors) {
			if(cp.getName().equals(points[0])) {
				for(ProcessorSet ps : cp.getJDC().getProcessorManager().getProcessorSets()) {
					if(ps.getName().equals(points[1])) {
						for(ElementProcessor ep : ps.getElementProcessors()) {
							if(ep.getName().equals(points[2])) {
								return ep;
							}
						}
					}
				}
			}
		}
		rc = ResponseCode.INTERNAL_SERVER_ERROR;
		ConsoleErrorUtils.printServerError(rc, path, Thread.currentThread().getStackTrace());
		return null;
	}
	
	public Processor locateProcessor(ResponseCode rc, String path) {
		String[] points = path.split("\\.", 3);
		if(points.length!=3) {
			rc = ResponseCode.INTERNAL_SERVER_ERROR;
			ConsoleErrorUtils.printServerError(rc, path, Thread.currentThread().getStackTrace());
			return null;
		}
		for(ContentProcessor cp : processors) {
			if(cp.getName().equals(points[0])) {
				for(ProcessorSet ps : cp.getJDC().getProcessorManager().getProcessorSets()) {
					if(ps.getName().equals(points[1])) {
						for(Processor ep : ps.getProcessors()) {
							if(ep.getName().equals(points[2])) {
								return ep;
							}
						}
					}
				}
			}
		}
		rc = ResponseCode.INTERNAL_SERVER_ERROR;
		ConsoleErrorUtils.printServerError(rc, path, Thread.currentThread().getStackTrace());
		return null;
	}
	
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
	
	public void makeErrorMapping(ResponseCode code, String relativePath) {
		errorMappings.put(code, getContentFile(relativePath));
	}
	
	public void makeErrorMapping(ResponseCode code, File relativePath) {
		errorMappings.put(code, relativePath);
	}
	
	public File getContentFile(String path) {
		return new File(contentFolder, path);
	}
	
	public List<ContentProcessor> getProcessors(){
		return processors;
	}

}
