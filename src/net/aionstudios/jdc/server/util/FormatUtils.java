package net.aionstudios.jdc.server.util;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A utility class for JSON formatting.
 * @author Winter
 *
 */
public class FormatUtils {
	
	/**
	 * Empties a {@link JSONObject}.
	 * 
	 * @param j A {@link JSONObject}.
	 */
	public static void clearJsonObject(JSONObject j) {
		while(j.length()>0) {
			j.remove((String) j.keys().next());
		}
	}
	
	/**
	 * Empties a {@link JSONArray}
	 * 
	 * @param j A {@link JSONArray}.
	 */
	public static void clearJsonArray(JSONArray j) {
		while(j.length()>0) {
			j.remove(0);
		}
	}
	
	/**
	 * Creates a new {@link JSONObject}, forcing it to use a LinkedHashMap instead of an unlinked one.
	 * 
	 * @return An empty {@link JSONObject} with modified structure.
	 */
	public static JSONObject getLinkedJsonObject() {
		JSONObject j = new JSONObject();
		Field map;
		try {
			map = j.getClass().getDeclaredField("map");
			map.setAccessible(true);
			map.set(j, new LinkedHashMap<>());
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
		return j;
	}
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
	public static String getLastModifiedAsHTTPString(long time) {
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(new Date(time));
	}
	
	public static String getLastModifiedAsHTTPString(Date d) {
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(d);
	}

}
