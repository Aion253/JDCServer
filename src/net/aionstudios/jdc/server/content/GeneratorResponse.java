package net.aionstudios.jdc.server.content;

import net.aionstudios.jdc.content.ResponseCode;

public class GeneratorResponse {
	
	private String response;
	private ResponseCode rc;
	
	public GeneratorResponse(String response, ResponseCode rc) {
		this.rc = rc;
		this.response = response;
	}

	public String getResponse() {
		return response;
	}

	public ResponseCode getResponseCode() {
		return rc;
	}
	
	

}
