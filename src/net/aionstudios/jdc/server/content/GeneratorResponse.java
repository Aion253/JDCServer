package net.aionstudios.jdc.server.content;

import net.aionstudios.jdc.content.ResponseCode;

/**
 * Part of the response sent back to a user to complete an {@link HttpExchange}.
 * @author Winter
 *
 */
public class GeneratorResponse {
	
	private String response;
	private ResponseCode rc;
	
	/**
	 * Creates a new GeneratorResponse.
	 * @param response		The String response, usually HTML, which having been generated, should be returned the the user.
	 * @param rc			The RFC 7231 HTTP/1.1 standard compliant response code representing the status of this request.
	 */
	public GeneratorResponse(String response, ResponseCode rc) {
		this.rc = rc;
		this.response = response;
	}

	/**
	 * @return A string, the response.
	 */
	public String getResponse() {
		return response;
	}

	/** 
	 * @return A {@link ResponseCode} representing the RFC 7231 HTTP/1.1 standard compliant response code
	 */
	public ResponseCode getResponseCode() {
		return rc;
	}
	
	

}
