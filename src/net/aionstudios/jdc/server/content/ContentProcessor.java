package net.aionstudios.jdc.server.content;

import java.io.File;

import net.aionstudios.jdc.JDC;

public class ContentProcessor {
	
	private File javaArchive;
	private String mainClass;
	private String name;
	
	//per jar file were multiple processors can be registered.
	//The main file in a JDC instance will contain a list of sets and is cast from a general
	//class to a JDC class by the URLClassLoader to load the instance more easily.
	//private List<ProcessorSet> psets = new ArrayList<ProcessorSet>();
	private JDC jdc;
	private boolean connected = false;
	
	/* Calling javaexecute from a jdc file will find the ContentProcessor attached to that page
	 * and via its argument locate a ProcessorSet, as exposed by the JDCLib by name and runs an
	 * ElementProcessor (which may specify if it should be run via Cron or at every page load)
	 * within it to generate content by passing the Element that spawned it and other relevant data.
	 */
	
	public ContentProcessor(Website website, String name, File javaArchive, String mainClass) {
		this.javaArchive = javaArchive;
		this.mainClass = mainClass;
		this.name = name;
		ContentLoader.addClassLoaderURL(javaArchive);
		website.addContentProcessor(this);
	}
	
	public String getName() {
		return name;
	}
	
	public File getArchive() {
		return javaArchive;
	}
	
	public void connectContentProcessor() {
		if(!connected) {
			this.jdc = ContentLoader.getJDCFromLibrary(mainClass);
			connected = true;
		}
	}
	
	public JDC getJDC() {
		return jdc;
	}
	
	public boolean isConnected() {
		return connected;
	}

}
