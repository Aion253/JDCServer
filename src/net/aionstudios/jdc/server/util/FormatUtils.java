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
 * @author Winter Roberts
 */
public class FormatUtils {
	
	/**
	 * Empties a {@link JSONObject}.
	 * @param j A {@link JSONObject}.
	 */
	public static void clearJsonObject(JSONObject j) {
		while(j.length()>0) {
			j.remove((String) j.keys().next());
		}
	}
	
	/**
	 * Empties a {@link JSONArray}
	 * @param j A {@link JSONArray}.
	 */
	public static void clearJsonArray(JSONArray j) {
		while(j.length()>0) {
			j.remove(0);
		}
	}
	
	/**
	 * Creates a new {@link JSONObject}, forcing it to use a LinkedHashMap instead of an unlinked one.
	 * @return An empty {@link JSONObject} with modified structure.
	 */
	public static JSONObject getLinkedJsonObject() {
		return new LinkedJSONObject();
	}
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
	/**
	 * Converts a Unix epoch time to the HTTP GMT format,
	 * required for cache control.
	 * @param time A long, prospectively the system's current time.
	 * @return A String representing the Unix epoch time passed to this method
	 * in HTTP standard.
	 */
	public static String getLastModifiedAsHTTPString(long time) {
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(new Date(time));
	}
	
	/**
	 * Converts a {@link Date} to the HTTP GMT format,
	 * required for cache control.
	 * @param time A {@link Date}, prospectively the system's current time.
	 * @return A String representing the {@link Date} passed to this method
	 * in HTTP standard.
	 */
	public static String getLastModifiedAsHTTPString(Date d) {
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(d);
	}
	
	public static String cap(String s, int maxLength, boolean ellipse) {
		if (s.length() > maxLength) {
			return ellipse?s.substring(0, maxLength-3)+"...":s.substring(0, maxLength);
		}
		return s;
	}

}
