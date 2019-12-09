package net.aionstudios.jdc.server.content;

import java.io.File;

import net.aionstudios.jdc.JDC;

/**
 * The bridge between server and website code, enabling {@link JDC} instances and their {@link Processor}s and {@link ElementProcessor}s to run.
 * @author Winter Roberts
 */
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
	/**
	 * Adds a new ContentProcessor to the given website, initializing a {@link JDC} instance by 
	 * reference to an archive to be loaded and the fully qualified name of a {@link JDC} class
	 * within said archive.
	 * @param website The {@link Website} this ContentProcessor should be assigned to.
	 * @param name The String naming this ContentProcessor.
	 * @param javaArchive The JAR {@link File} that should be imported to be used by calls to this
	 * Content Processor
	 * @param mainClass The main class, an instance of {@link JDC}, which should be used to start
	 * running and register {@link ProcessorSet}s and {@link ElementProcessor}s for this
	 * ContentProcessor.
	 * @see {@link JDCLoader}
	 */
	public ContentProcessor(Website website, String name, File javaArchive, String mainClass) {
		this.javaArchive = javaArchive;
		this.mainClass = mainClass;
		this.name = name;
		JDCLoader.addClassLoaderURL(javaArchive);
		website.addContentProcessor(this);
	}
	
	/**
	 * The name of this ContentProcessor.
	 * @return A String, the name of this ContentProcessor.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * The archive which this file adds into the JVM's class loader.
	 * @return A file, added to a {@link Website} by this ContentProcessor.
	 */
	public File getArchive() {
		return javaArchive;
	}
	
	/**
	 * @return The String naming the main class, an instance of {@link JDC}, of this
	 * Content Processor which should be used to start running and register 
	 * {@link ProcessorSet}s and {@link ElementProcessor}s for this
	 * ContentProcessor.
	 */
	public String getMainClass() {
		return mainClass;
	}
	
	/**
	 * Reassigns the {@link JDC} to which this ContentProcessor should be tied.
	 * @param jdc A {@link JDC} instance.
	 */
	public void setJDC(JDC jdc) {
		this.jdc = jdc;
	}
	
	/**
	 * Uses the {@link JDCLoader} to create a new JDC instance, as referenced in this classes constructor by its fully qualified name within the given archive.
	 */
	public void connectContentProcessor() {
		if(!connected) {
			this.jdc = JDCLoader.getJDCFromLibrary(mainClass);
			this.jdc.initialize();
			connected = true;
		}
	}
	
	/**
	 * A {@link JDC} instance started by this ContentProcessor after having been loaded from an external archive.
	 * @return The {@link JDC} instance bound to this ContentProcessor, or null if it couldn't be started.
	 */
	public JDC getJDC() {
		return jdc;
	}
	
	/**
	 * Whether or not the ContentProcessor has loaded and initialized the main {@link JDC} class named in its constructor.
	 * @return True if the class has been loaded and initialized, false otherwise.
	 */
	public boolean isConnected() {
		return connected;
	}

}
