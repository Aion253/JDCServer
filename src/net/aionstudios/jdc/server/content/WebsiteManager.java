package net.aionstudios.jdc.server.content;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Manages all {@link Website}s exposed by the server.
 * @author Winter Roberts
 */
public class WebsiteManager {
	
	public static Map<String, Website> websites = new HashMap<>();
	
	/**
	 * Adds a {@link Website} to the list available from the server.
	 * @param ws	The {@link Website} to be added.
	 */
	public static void addWebsite(Website ws) {
		websites.put(ws.getName(), ws);
	}
	
	/**
	 * Gets a {@link Website} by its name.
	 * @param name	The name of the {@link Website} to be returned.
	 * @return The named {@link Website}, or null if one isn't found.
	 */
	public static Website getWebsite(String name) {
		return websites.get(name);
	}
	
	/**
	 * Gets a {@link Website} by address.
	 * @param addr	The addresses for which a {@link Website} should be found.
	 * @return The {@link Website} accpeting the address, or null if one isn't found.
	 */
	public static Website getWebsiteByAddress(String addr) {
		for(Entry<String, Website> w : websites.entrySet()) {
			for(String a : w.getValue().getAddresses()) {
				if(a.equals(addr)) {
					return w.getValue();
				}
			}
		}
		return null;
	}
	
	/**
	 * Connects each of the {@link ContentProcessor}s from every {@link Website}.
	 * @see {@link JDCLoader}
	 */
	public static void connectContentProcessors() {
		for(Entry<String, Website> w : websites.entrySet()) {
			for(Entry<String, ContentProcessor> cp : w.getValue().getProcessors().entrySet()) {
				cp.getValue().connectContentProcessor();
			}
		}
	}

}
