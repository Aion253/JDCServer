package net.aionstudios.jdc.server.proxy;

import java.util.HashMap;
import java.util.Map;

public class ProxyManager {
	
	private Map<String, String> p = new HashMap<String, String>();
	
	public ProxyManager() {
		
	}
	
	public void putProxy(String context, String proxyUrl) {
		p.put(context, proxyUrl);
	}
	
	public void removeProxy(String context) {
		p.remove(context);
	}
	
	public String getProxyUrl(String context) {
		if(context=="/") {
			return null;
		}
		return p.get(context);
	}

}
