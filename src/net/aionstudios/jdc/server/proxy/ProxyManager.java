package net.aionstudios.jdc.server.proxy;

import java.util.HashMap;
import java.util.Map;

/**
 * The proxy manager maps request {@link URL}s to outgoing requests per {@link Website}.
 * @author Winter
 *
 */
public class ProxyManager {
	
	private Map<String, String> p = new HashMap<String, String>();
	
	/**
	 * Creates a new proxy manager.
	 */
	public ProxyManager() {
		
	}
	
	/**
	 * Adds a proxy mapping to this manager.
	 * @param context		The context string for which an internal proxy request should be made.
	 * @param proxyUrl		The {@link URL} string to which the request should be proxied.
	 */
	public void putProxy(String context, String proxyUrl) {
		p.put(context, proxyUrl);
	}
	
	/**
	 * Removes a proxy mapping from this manager by name.
	 * @param context		The context of the proxy mapping to be removed.
	 */
	public void removeProxy(String context) {
		p.remove(context);
	}
	
	/**
	 * Gets the proxy url string for a context string if one is mapped.
	 * @param context		The context string for which a proxy url string should be found.
	 * @return		The proxy url string mapped to the context, or null if there is no mapping.
	 */
	public String getProxyUrl(String context) {
		if(context=="/") {
			return null;
		}
		return p.get(context);
	}

}
