package net.aionstudios.jdc.server.content;

import java.util.ArrayList;
import java.util.List;

public class WebsiteManager {
	
	public static List<Website> websites = new ArrayList<Website>();
	
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
	
	public static Website getWebsite(String name) {
		for(Website w : websites) {
			if(w.getName().equals(name)) {
				return w;
			}
		}
		return null;
	}
	
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
