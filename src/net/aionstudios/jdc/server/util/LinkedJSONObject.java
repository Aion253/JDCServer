package net.aionstudios.jdc.server.util;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;

import org.json.JSONException;
import org.json.JSONObject;

public class LinkedJSONObject extends JSONObject {
	
	public LinkedJSONObject() {
		Field map;
		try {
			map = JSONObject.class.getDeclaredField("map");
			map.setAccessible(true);
			map.set(this, new LinkedHashMap<>());
			map.setAccessible(false);
		} catch (NoSuchFieldException e) {
			System.err.println("JSONObject re-link failed!");
			e.printStackTrace();
		} catch (SecurityException e) {
			System.err.println("JSONObject re-link failed!");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.err.println("JSONObject re-link failed!");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.err.println("JSONObject re-link failed!");
			e.printStackTrace();
		}
	}
	
	public LinkedJSONObject(String s) throws JSONException {
		super(s);
	}
	
	public LinkedJSONObject(JSONObject j) throws JSONException {
		super(j);
	}

}
