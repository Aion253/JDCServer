package net.aionstudios.jdc.server.content;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.aionstudios.jdc.processor.ElementProcessor;
import net.aionstudios.jdc.processor.ProcessorSet;
import net.aionstudios.jdc.server.JDCServerInfo;

public class Website {
	
	private String name;
	private String[] addresses;
	private boolean sslOn;
	private List<ContentProcessor> processors = new ArrayList<ContentProcessor>();
	
	private File websiteFolder;
	
	private File contentFolder;
	private File javaFolder;
	
	private File processorConfig;
	
	private JSONObject processorJson = new JSONObject();
	
	public Website(String name, String[] addresses, boolean sslOn) {
		this.name = name;
		this.addresses = addresses;
		this.sslOn = sslOn;
		websiteFolder = new File("./websites/"+name);
		contentFolder = new File("./websites/"+name+"/content");
		javaFolder = new File("./websites/"+name+"/java");
		processorConfig = new File("./websites/"+name+"/processors.json");
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
				JSONObject processor = (JSONObject) processorArray.get(i);
				new ContentProcessor(this, processor.getString("name"), new File("./websites/"+name+"/java/"+processor.getString("jar")), processor.getString("jdc_class"));
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
	
	public ElementProcessor locateElementProcessor(String path) {
		String[] points = path.split("\\.", 3);
		if(points.length!=3) {
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
		return null;
	}
	
	public File getContentFile(String path) {
		return new File(contentFolder, path);
	}
	
	public List<ContentProcessor> getProcessors(){
		return processors;
	}

}
