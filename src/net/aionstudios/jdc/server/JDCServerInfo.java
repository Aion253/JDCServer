package net.aionstudios.jdc.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.aionstudios.jdc.server.content.ContentLoader;
import net.aionstudios.jdc.server.content.Website;
import net.aionstudios.jdc.server.content.WebsiteManager;
import net.aionstudios.jdc.server.util.FormatUtils;

public class JDCServerInfo {

	public static String JDCS_VER = "1.0.0";
	public static String JDCS_CONFIG = "./sites.json";
	
	private static JSONObject webconfig;
	
	/**
	 * Reads configurable information when the server starts and handles setup if necessary.
	 * Should a config file not exist it will be created and the application terminated.
	 * 
	 * @return True if the config was available and processed, false otherwise.
	 */
	public static boolean readConfigsAtStart() {
		webconfig = FormatUtils.getLinkedJsonObject();
		try {
			File dbcf = new File(JDCS_CONFIG);
			if(!dbcf.exists()) {
				dbcf.getParentFile().mkdirs();
				dbcf.createNewFile();
				System.err.println("No config, using default settings!");
				JSONArray ws = new JSONArray();
				JSONObject defWeb = FormatUtils.getLinkedJsonObject();
				defWeb.put("name", "default");
				JSONArray defWebAddrs = new JSONArray();
				defWebAddrs.put("localhost");
				defWeb.put("addresses", defWebAddrs);
				defWeb.put("ssl_enabled", false);
				ws.put(defWeb);
				webconfig.put("websites", ws);
				writeConfig(webconfig, dbcf);
			} else {
				webconfig = readConfig(dbcf);
			}
			JSONArray sa = webconfig.getJSONArray("websites");
			for(int i = 0; i < sa.length(); i++) {
				JSONObject so = sa.getJSONObject(i);
				String name = so.getString("name");
				boolean sslOn = so.getBoolean("ssl_enabled");
				JSONArray addra = so.getJSONArray("addresses");
				String[] addresses = new String[addra.length()];
				for(int j = 0; j < addra.length(); j++) {
					addresses[j] = addra.getString(j);
				}
				new Website(name, addresses, sslOn);
			}
			ContentLoader.initializeClassLoader();
			WebsiteManager.connectContentProcessors();
			return true;
		} catch (IOException e) {
			System.err.println("Encountered an IOException during config file operations!");
			e.printStackTrace();
			return false;
		} catch (JSONException e) {
			System.err.println("Encountered an JSONException during config file operations!");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Writes the provided {@link JSONObject} to the file system, optimistically as a configuration file.
	 * 
	 * @param j	The {@link JSONObject} to be serialized into the file system.
	 * @param f	The {@link File} object identifying where the {@link JSONObject} should be saved onto the file system.
	 * @return True if the file was written without error, false otherwise.
	 */
	public static boolean writeConfig(JSONObject j, File f) {
		try {
			if(!f.exists()) {
				f.getParentFile().mkdirs();
				f.createNewFile();
				System.out.println("Created config file '"+f.toString()+"'!");
			}
			PrintWriter writer;
			File temp = File.createTempFile("temp_json", null, f.getParentFile());
			writer = new PrintWriter(temp.toString(), "UTF-8");
			writer.println(j.toString(2));
			writer.close();
			Files.deleteIfExists(f.toPath());
			temp.renameTo(f);
			return true;
		} catch (IOException e) {
			System.err.println("Encountered an IOException while writing config: '"+f.toString()+"'!");
			e.printStackTrace();
			return false;
		} catch (JSONException e) {
			System.err.println("Encountered a JSONException while writing config: '"+f.toString()+"'!");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Deserializes a {@link JSONObject} from a file on the file system and returns it.
	 * 
	 * @param f	The {@link File} object, representing a file containing JSON data on the file system.
	 * @return	A {@link JSONObject} representing the file provided or null if it could not be read.
	 */
	public static JSONObject readConfig(File f) {
		if(!f.exists()) {
			System.err.println("Failed reading config: '"+f.toString()+"'. No such file!");
			return null;
		}
		String jsonString = "";
		try (BufferedReader br = new BufferedReader(new FileReader(f.toString()))) {
		    for (String line; (line = br.readLine()) != null;) {
		    	jsonString += line;
		    }
		    br.close();
		    return new JSONObject(jsonString);
		} catch (IOException e) {
			System.err.println("Encountered an IOException while reading config: '"+f.toString()+"'!");
			e.printStackTrace();
			return null;
		} catch (JSONException e) {
			System.err.println("Encountered a JSONException while reading config: '"+f.toString()+"'!");
			e.printStackTrace();
			return null;
		}
	}
	
	public static String readFile(File f) {
		if(!f.exists()) {
			System.err.println("Failed reading file: '"+f.toString()+"'. No such file!");
			return null;
		}
		String jsonString = "";
		try (BufferedReader br = new BufferedReader(new FileReader(f.toString()))) {
		    for (String line; (line = br.readLine()) != null;) {
		    	jsonString += line;
		    }
		    br.close();
		    return jsonString;
		} catch (IOException e) {
			System.err.println("Encountered an IOException while reading file: '"+f.toString()+"'!");
			e.printStackTrace();
			return null;
		}
	}
	
}
