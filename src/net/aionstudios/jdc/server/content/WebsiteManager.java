package net.aionstudios.jdc.server.content;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all {@link Website}s exposed by the server.
 * @author Winter Roberts
 */
public class WebsiteManager {
	
	public static List<Website> websites = new ArrayList<Website>();
	
	/**
	 * Adds a {@link Website} to the list available from the server.
	 * @param ws	The {@link Website} to be added.
	 */
	public static void addWebsite(Website ws) {
		for(Website w : websites) {
			if(w.getName() == ws.getName()) {
				websites.remove(w);
				websites.add(ws);
				return;
			}
		}
		websites.add(ws);
	}
	
	/**
	 * Gets a {@link Website} by its name.
	 * @param name	The name of the {@link Website} to be returned.
	 * @return The named {@link Website}, or null if one isn't found.
	 */
	public static Website getWebsite(String name) {
		for(Website w : websites) {
			if(w.getName().equals(name)) {
				return w;
			}
		}
		return null;
	}
	
	/**
	 * Gets a {@link Website} by address.
	 * @param addr	The addresses for which a {@link Website} should be found.
	 * @return The {@link Website} accpeting the address, or null if one isn't found.
	 */
	public static Website getWebsiteByAddress(String addr) {
		for(Website w : websites) {
			for(String a : w.getAddresses()) {
				if(a.equals(addr)) {
					return w;
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
		for(Website w : websites) {
			for(ContentProcessor cp : w.getProcessors()) {
				cp.connectContentProcessor();
				//TODO register JDC instances so they can be called by java-execute in pages.
				/*
				 * Additional note because I know I'll check here later.
				 * 
				 * See about implementing an on-demand page (part-of-page) generating service
				 * to avoid running big queries with results that don't change often.
				 */
			}
		}
	}

}
